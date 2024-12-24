package kz.homework.task.service;

import kz.homework.task.entity.Task;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {

    Task createTask(TaskDTO taskDTO);

    List<Task> getTasks(Status status, LocalDateTime createdAt);

    Task getTaskById(Integer id);

    Task updateTask(Integer id, TaskDTO taskDTO);

    void deleteTask(Integer id);
}
