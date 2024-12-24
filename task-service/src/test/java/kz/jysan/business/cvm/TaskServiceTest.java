package kz.jysan.business.cvm;

import kz.homework.task.entity.Task;
import kz.homework.task.model.Status;
import kz.homework.task.model.TaskDTO;
import kz.homework.task.repository.TaskRepository;
import kz.homework.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        taskDTO = new TaskDTO();
        taskDTO.setTitle("Sample Task");
        taskDTO.setDescription("Sample Description");
        taskDTO.setStatus(Status.NEW);

        task = new Task();
        task.setId(1);
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals(task.getTitle(), createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testGetTasksWithoutFilters() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getTasks(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTasksByStatus() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByStatus(task.getStatus())).thenReturn(tasks);

        List<Task> result = taskService.getTasks(task.getStatus(), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByStatus(task.getStatus());
    }

    @Test
    void testGetTasksByCreatedAt() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByCreatedAt(any(LocalDate.class))).thenReturn(tasks);

        List<Task> result = taskService.getTasks(null, task.getCreatedAt());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByCreatedAt(any(LocalDate.class));
    }

    @Test
    void testGetTasksByStatusAndCreatedAt() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByStatusAndCreatedAt(task.getStatus(), task.getCreatedAt().toLocalDate())).thenReturn(tasks);

        List<Task> result = taskService.getTasks(task.getStatus(), task.getCreatedAt());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByStatusAndCreatedAt(task.getStatus(), task.getCreatedAt().toLocalDate());
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task.getId(), foundTask.getId());
        verify(taskRepository, times(1)).findById(task.getId());
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.getTaskById(task.getId()));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(task.getId());
    }

    @Test
    void testUpdateTask() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task");
        updatedTaskDTO.setDescription("Updated Description");
        updatedTaskDTO.setStatus(Status.DONE);

        Task updatedTask = taskService.updateTask(task.getId(), updatedTaskDTO);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        verify(taskRepository, times(1)).findById(task.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTask() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(any(Task.class));

        taskService.deleteTask(task.getId());

        verify(taskRepository, times(1)).findById(task.getId());
        verify(taskRepository, times(1)).delete(task);
    }
}
