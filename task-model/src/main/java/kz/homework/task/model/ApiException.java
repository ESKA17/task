package kz.homework.task.model;

import java.util.HashMap;
import java.util.Map;

public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 6803391683871204802L;
    private final ApiError apiError;
    private final ErrorResponse errorResponse;

    public ApiException(ApiError apiError, String message) {
        super(apiError.name() + ": " + message);
        this.errorResponse = new ErrorResponse(apiError.name(), message);
        this.apiError = apiError;
    }

    public ApiException(ApiError apiError, String message, boolean addToErrorsData) {
        this(apiError, message);
        if (addToErrorsData) {
            this.set(apiError.name(), message);
        }

    }

    public ApiException(ApiError apiError) {
        super(apiError.name() + ": " + apiError.getMessage());
        this.errorResponse = new ErrorResponse(apiError.name(), apiError.getMessage());
        this.apiError = apiError;
    }

    public ApiException(ApiError apiError, ErrorResponse errorResponse) {
        super(apiError.name() + ": " + errorResponse.getDescription());
        this.errorResponse = errorResponse;
        this.apiError = apiError;
    }

    public void set(String key, Object value) {
        if (this.errorResponse.getData() == null) {
            this.errorResponse.setData(new HashMap<>());
        }

        this.errorResponse.getData().put(key, value);
    }

    public void setErrors(Map<String, Object> data) {
        this.errorResponse.setData(data);
    }

    public ApiError getApiError() {
        return this.apiError;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    public String getMessage() {
        return this.errorResponse == null ? null : this.errorResponse.getCode() + ": " + this.errorResponse.getDescription();
    }

    public static ApiExceptionBuilder builder() {
        return new ApiExceptionBuilder();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiException)) {
            return false;
        } else {
            ApiException other = (ApiException) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object thisApiError = this.getApiError();
                Object otherApiError = other.getApiError();
                if (thisApiError == null) {
                    if (otherApiError != null) {
                        return false;
                    }
                } else if (!thisApiError.equals(otherApiError)) {
                    return false;
                }

                Object thisErrorResponse = this.getErrorResponse();
                Object otherErrorResponse = other.getErrorResponse();
                if (thisErrorResponse == null) {
                    return otherErrorResponse == null;
                } else return thisErrorResponse.equals(otherErrorResponse);
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiException;
    }

    public int hashCode() {
        int result = 1;
        Object apiErrorIn = this.getApiError();
        result = result * 59 + (apiErrorIn == null ? 43 : apiErrorIn.hashCode());
        Object errorResponseIn = this.getErrorResponse();
        result = result * 59 + (errorResponseIn == null ? 43 : errorResponseIn.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ApiException(apiError=" + this.getApiError() + ", errorResponse=" + this.getErrorResponse() + ")";
    }

    public static class ApiExceptionBuilder {
        private ApiError apiError;
        private ErrorResponse errorResponse;

        ApiExceptionBuilder() {
        }

        public ApiExceptionBuilder apiError(ApiError apiError) {
            this.apiError = apiError;
            return this;
        }

        public ApiExceptionBuilder errorResponse(ErrorResponse errorResponse) {
            this.errorResponse = errorResponse;
            return this;
        }

        public ApiException build() {
            return new ApiException(this.apiError, this.errorResponse);
        }

        public String toString() {
            return "ApiException.ApiExceptionBuilder(apiError=" + this.apiError + ", errorResponse=" + this.errorResponse + ")";
        }
    }
}
