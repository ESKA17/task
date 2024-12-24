package kz.homework.task.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO representing a task in the system. This DTO is used as a response.")
public class TaskResponse extends TaskRequest {

    @Schema(description = "Id of the task", example = "1")
    private Integer id;

    @Schema(description = "Creation date")
    private LocalDate createdAt;

    public TaskResponse(TaskRequest taskResponse) {
        BeanUtils.copyProperties(taskResponse, this);
    }
}

