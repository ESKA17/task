package kz.homework.task.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Schema(description = "DTO representing a task in the system. This DTO is used for task creation and update.")
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @NotBlank
    @Size(min = 1, max = 255)
    @Schema(description = "The title of the task", example = "Task Title", maxLength = 255)
    private String title;

    @Size(max = 1000)
    @Schema(description = "A detailed description of the task", example = "This is a description of the task.", maxLength = 1000)
    private String description;

    @Nullable
    @Schema(description = "The current status of the task", example = "IN_PROGRESS")
    private Status status;


}
