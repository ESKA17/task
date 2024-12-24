package kz.homework.task.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import kz.homework.task.model.ApiError;
import kz.homework.task.model.ApiException;
import kz.homework.task.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private static final String TASKS_SERVICE = "tasks-service";
    private static final String INVALID_VALUE = "Invalid value: '%s'";

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleMapperErrors(HttpMessageNotReadableException exception) throws IOException {
        log.warn(exception.getLocalizedMessage());
        ApiError error = ApiError.BAD_REQUEST;
        ErrorResponse errorResponse;

        String exceptionMessage = exception.getMessage();
        String value = StringUtils.substringBetween(exceptionMessage, "\"", "\"");
        String fieldName = StringUtils.substringBetween(exceptionMessage, "$", "`");

        Throwable cause = exception.getCause();
        if (exceptionMessage.contains("not one of the values accepted for Enum class")) {
            errorResponse = new ErrorResponse(error.name(), "invalid value " + value + " for a field " + fieldName);
        } else if (cause instanceof JsonMappingException jsonMappingException) {
            String invalidDataFieldNames = jsonMappingException.getPath().stream().map(JsonMappingException.Reference::getFieldName).filter(Objects::nonNull).collect(Collectors.joining("."));
            errorResponse = new ErrorResponse(error.name(), String.format("invalid value for a field: '%s'", invalidDataFieldNames));
        } else if (cause instanceof JsonParseException jsonParseException) {
            String invalidDataFieldNames = jsonParseException.getProcessor().currentName();
            errorResponse = new ErrorResponse(error.name(), String.format("invalid value for a field: '%s'", invalidDataFieldNames));
        } else {
            errorResponse = new ErrorResponse(error.name(), exception.getMessage());
        }

        if (errorResponse.getOrigin() == null || errorResponse.getOrigin().isEmpty()) {
            errorResponse.setOrigin(TASKS_SERVICE);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ErrorResponse> handleApiExceptions(ApiException exception) {
        log.error(exception.getLocalizedMessage(), exception);

        ApiError apiError = exception.getApiError();
        ErrorResponse errorResponse = exception.getErrorResponse();

        if (errorResponse.getOrigin() == null || errorResponse.getOrigin().isEmpty()) {
            errorResponse.setOrigin(TASKS_SERVICE);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(apiError.getStatus()));
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(MissingRequestHeaderException exception) {
        log.error(exception.getLocalizedMessage(), exception);
        ApiError error = ApiError.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(error.name(), exception.getMessage());

        if (errorResponse.getOrigin() == null || errorResponse.getOrigin().isEmpty()) {
            errorResponse.setOrigin(TASKS_SERVICE);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleInterServerError(Exception exception) {
        log.error(exception.getLocalizedMessage(), exception);
        ApiError error = ApiError.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(error.name(), "server error");
        errorResponse.setOrigin(TASKS_SERVICE);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoResourceError(NoResourceFoundException exception) {
        log.error(exception.getLocalizedMessage(), exception);
        ApiError error = ApiError.RESOURCE_NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(error.name(), exception.getMessage());
        errorResponse.setOrigin(TASKS_SERVICE);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(error.getStatus()));
    }


    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception exception) {
        log.error(exception.getLocalizedMessage(), exception);
        Map<String, Object> errors = convert(exception);
        return getErrorResponse(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error(exception.getLocalizedMessage(), exception);
        Map<String, Object> errors = convert(exception);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setData(errors);
        errorResponse.setCode(ApiError.BAD_REQUEST.name());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setDescription(errors.isEmpty() ? exception.getMessage() : "invalid data");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.warn(exception.getLocalizedMessage(), exception);
        Map<String, Object> errors = new HashMap<>();
        errors.put(exception.getName(), String.format(INVALID_VALUE, exception.getValue()));
        return getErrorResponse(errors);
    }

    @ExceptionHandler({InvalidPersistentPropertyPath.class})
    public ResponseEntity<ErrorResponse> handleInvalidPersistentPropertyPath(InvalidPersistentPropertyPath exception) {
        log.error(exception.getLocalizedMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ApiError.BAD_REQUEST.name());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setDescription(String.format("unknown value: %s", exception.getUnresolvableSegment()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> convert(ConstraintViolationException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(exception.getConstraintViolations())) {
            exception.getConstraintViolations().stream().filter(constraintViolation -> constraintViolation.getPropertyPath() != null).forEach(constraintViolation -> {
                String name = StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false).filter(v -> v.getKind() == ElementKind.PARAMETER || v.getKind() == ElementKind.PROPERTY).map(Path.Node::getName).collect(Collectors.joining("."));
                errorMap.put(name, constraintViolation.getMessage());
            });
        }

        return errorMap;
    }


    private Map<String, Object> convert(Exception exception) {
        Errors errors;
        Map<String, Object> errorMap = new HashMap<>();
        switch (exception) {
            case MethodArgumentNotValidException methodArgumentNotValidException ->
                    errors = methodArgumentNotValidException.getBindingResult();
            case BindException bindException -> errors = bindException.getBindingResult();
            default -> {
                return errorMap;
            }
        }

        for (ObjectError error : errors.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                errorMap.put(fieldError.getField(), error.getDefaultMessage());
                String defaultMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse(String.format(INVALID_VALUE, fieldError.getRejectedValue()));
                // in case of 'typeMismatch'
                if (defaultMessage.contains("failed to convert")) {
                    defaultMessage = String.format(INVALID_VALUE, fieldError.getRejectedValue());
                }
                errorMap.put(fieldError.getField(), defaultMessage);
            } else {
                errorMap.put(error.getCode(), error.getDefaultMessage());
            }
        }
        return errorMap;
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(Map<String, Object> errors) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setData(errors);
        errorResponse.setCode(ApiError.BAD_REQUEST.name());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setDescription("invalid data");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}