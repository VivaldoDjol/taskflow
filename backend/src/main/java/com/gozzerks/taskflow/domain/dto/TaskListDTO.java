package com.gozzerks.taskflow.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record TaskListDTO(
        UUID id,
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,
        Integer count,
        Double progress,
        List<TaskDTO> tasks
) {
}
