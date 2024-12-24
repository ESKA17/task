package kz.homework.task.service;

import kz.homework.task.model.Status;
import kz.homework.task.model.TaskRequest;
import kz.homework.task.model.TaskResponse;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest taskRequest);

    List<TaskResponse> getTasks(Status status, LocalDate createdAt);

    TaskResponse getTaskResponseById(Integer id);

    TaskResponse updateTask(Integer id, TaskRequest taskRequest);

    void deleteTask(Integer id);
}
