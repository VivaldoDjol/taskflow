package com.gozzerks.taskflow.domain.dto;

import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status
) {


}