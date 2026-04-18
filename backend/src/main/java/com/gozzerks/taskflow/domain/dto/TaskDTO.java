package com.gozzerks.taskflow.domain.dto;

import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "A task belonging to a task list.")
public record TaskDTO(
        @Schema(description = "Server-assigned identifier. Omit when creating.", accessMode = Schema.AccessMode.READ_ONLY)
        UUID id,

        @Schema(description = "Human-readable title.", example = "Write unit tests")
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Schema(description = "Optional free-form details.", example = "Cover both happy and error paths for AuthService.")
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Schema(description = "Optional due date and time.", example = "2026-05-01T17:00:00")
        LocalDateTime dueDate,

        @Schema(description = "Priority bucket for sorting and filtering.")
        TaskPriority priority,

        @Schema(description = "Open or closed status.")
        TaskStatus status
) {


}
