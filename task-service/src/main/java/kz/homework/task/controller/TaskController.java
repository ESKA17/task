package kz.homework.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import kz.homework.task.entity.Task;
import kz.homework.task.model.ErrorResponse;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskDTO;
import kz.homework.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Создать задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Операция завершена успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации одного из параметров запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task newTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @Operation(summary = "Получение списка задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция завершена успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации одного из параметров запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) Status status,
                                  @RequestParam(required = false) LocalDateTime createdAt) {
        return taskService.getTasks(status, createdAt);
    }

    @Operation(summary = "Получение задачи по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция завершена успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации одного из параметров запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Обновление задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция завершена успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации одного из параметров запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id, @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @Operation(summary = "Удаление задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция завершена успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации одного из параметров запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
