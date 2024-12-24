package kz.homework.task.repository;

import kz.homework.task.entity.Task;
import kz.homework.task.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByStatus(Status status);

    List<Task> findByCreatedAt(LocalDate createdAt);

    List<Task> findByStatusAndCreatedAt(Status status, LocalDate createdAt);
}
