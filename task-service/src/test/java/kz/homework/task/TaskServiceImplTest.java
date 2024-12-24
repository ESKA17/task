package kz.homework.task;

import kz.homework.task.entity.Task;
import kz.homework.task.exception.ApiError;
import kz.homework.task.exception.ApiException;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskRequest;
import kz.homework.task.model.TaskResponse;
import kz.homework.task.repository.TaskRepository;
import kz.homework.task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;


    @Test
    void createTask_ShouldCreateAndReturnTaskResponse() {
        TaskRequest taskRequest = new TaskRequest("Test Title", "Test Description", Status.NEW);
        Task task = new Task();
        task.setId(1);
        task.setTitle("Test Title");
        task.setDescription("Test Description");
        task.setStatus(Status.NEW);
        task.setCreatedAt(LocalDate.now());

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(Status.NEW, response.getStatus());
        assertEquals(LocalDate.now(), response.getCreatedAt());
        assertEquals(1, response.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTasks_ShouldReturnAllTasks_WhenStatusAndCreatedAtAreNull() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Task Description");

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getTasks(null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTasks_ShouldReturnTasks_WhenStatusNotNull() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Testing Task");
        task.setStatus(Status.NEW);
        task.setDescription("Testing Task Description");

        when(taskRepository.findByStatus(any(Status.class))).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getTasks(Status.NEW, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(taskRepository, times(1)).findByStatus(any(Status.class));
    }

    @Test
    void getTasks_ShouldReturnTasks_WhenCreatedAtNotNull() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Testing Task");
        task.setStatus(Status.NEW);
        task.setDescription("Testing Task Description");

        when(taskRepository.findByCreatedAt(any(LocalDate.class))).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getTasks(null, LocalDate.now());

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(taskRepository, times(1)).findByCreatedAt(any(LocalDate.class));
    }

    @Test
    void getTasks_ShouldReturnTasksFilteredByStatusAndCreatedAt() {
        Status status = Status.NEW;
        LocalDate createdAt = LocalDate.now();
        Task task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Task Description");
        task.setStatus(status);
        task.setCreatedAt(LocalDate.now());

        when(taskRepository.findByStatusAndCreatedAt(status, createdAt)).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getTasks(status, LocalDate.now());

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(taskRepository, times(1)).findByStatusAndCreatedAt(status, createdAt);
    }

    @Test
    void getTaskResponseById_ShouldReturnTaskResponse_WhenTaskExists() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setStatus(Status.NEW);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskResponseById(1);

        assertNotNull(response);
        assertEquals("Task Title", response.getTitle());
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void getTaskResponseById_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> taskService.getTaskResponseById(1));

        assertEquals(ApiError.RESOURCE_NOT_FOUND, exception.getApiError());
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenTaskExists() {
        TaskRequest taskRequest = new TaskRequest("Updated Title", "Updated Description", Status.IN_PROGRESS);
        Task task = new Task();
        task.setId(1);
        task.setTitle("Original Title");
        task.setDescription("Original Description");
        task.setStatus(Status.NEW);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.updateTask(1, taskRequest);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.existsById(1)).thenReturn(true);

        taskService.deleteTask(1);

        verify(taskRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(1)).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> taskService.deleteTask(1));

        assertEquals(ApiError.RESOURCE_NOT_FOUND, exception.getApiError());
        verify(taskRepository, times(1)).existsById(1);
    }
}
