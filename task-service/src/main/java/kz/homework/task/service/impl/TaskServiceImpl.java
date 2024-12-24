package kz.homework.task.service.impl;

import kz.homework.task.entity.Task;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskDTO;
import kz.homework.task.repository.TaskRepository;
import kz.homework.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public List<Task> getTasks(Status status, LocalDateTime createdAt) {
        if (status == null && createdAt == null) {
            return taskRepository.findAll();
        } else if (status != null && createdAt != null) {
            return taskRepository.findByStatusAndCreatedAt(status, createdAt.toLocalDate());
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else {
            return taskRepository.findByCreatedAt(createdAt.toLocalDate());
        }
    }

    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task updateTask(Integer id, TaskDTO taskDTO) {
        Task task = getTaskById(id);
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
