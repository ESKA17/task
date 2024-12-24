package kz.homework.task.service.impl;

import kz.homework.task.entity.Task;
import kz.homework.task.exception.ApiError;
import kz.homework.task.exception.ApiException;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskRequest;
import kz.homework.task.model.TaskResponse;
import kz.homework.task.repository.TaskRepository;
import kz.homework.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest createTaskRequest) {
        log.info("Creating task with title: {}", createTaskRequest.getTitle());
        Task task = new Task();
        task.setTitle(createTaskRequest.getTitle());
        task.setDescription(createTaskRequest.getDescription());
        if (createTaskRequest.getStatus() != null) {
            task.setStatus(createTaskRequest.getStatus());
        }
        Task savedTask = taskRepository.save(task);
        TaskResponse taskResponse = new TaskResponse(createTaskRequest);
        taskResponse.setCreatedAt(savedTask.getCreatedAt());
        taskResponse.setId(savedTask.getId());

        log.info("Task created with title \"{}\" created with ID: {}", createTaskRequest.getTitle(), savedTask.getId());
        return taskResponse;
    }

    public List<TaskResponse> getTasks(Status status, LocalDate createdAt) {
        log.info("Fetching tasks with status: {} and createdAt: {}", status, createdAt);
        List<TaskResponse> taskResponses;
        if (status == null && createdAt == null) {
            taskResponses = mapToTaskResponse(taskRepository.findAll());
        } else if (status != null && createdAt != null) {
            taskResponses = mapToTaskResponse(taskRepository.findByStatusAndCreatedAt(status, createdAt));
        } else if (status != null) {
            taskResponses = mapToTaskResponse(taskRepository.findByStatus(status));
        } else {
            taskResponses = mapToTaskResponse(taskRepository.findByCreatedAt(createdAt));
        }
        log.info("Found {} tasks", taskResponses.size());
        return taskResponses;
    }

    public TaskResponse getTaskResponseById(Integer id) {
        log.info("Fetching task with ID: {}", id);
        return mapToTaskResponse(getTaskById(id));
    }

    private Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.RESOURCE_NOT_FOUND));
    }

    public TaskResponse updateTask(Integer id, TaskRequest taskRequest) {
        log.info("Updating task with ID: {}", id);
        Task task = getTaskById(id);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        taskRepository.save(task);

        TaskResponse taskResponse = new TaskResponse(taskRequest);
        taskResponse.setId(task.getId());
        taskResponse.setCreatedAt(task.getCreatedAt());
        log.info("Task with ID: {} updated successfully", task.getId());
        return taskResponse;
    }

    public void deleteTask(Integer id) {
        log.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Task with ID: {} not found", id);
            throw new ApiException(ApiError.RESOURCE_NOT_FOUND);
        }
        taskRepository.deleteById(id);
        log.info("Task with ID: {} deleted successfully", id);
    }

    private List<TaskResponse> mapToTaskResponse(List<Task> tasks) {
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setCreatedAt(task.getCreatedAt());
        return taskResponse;
    }

}
