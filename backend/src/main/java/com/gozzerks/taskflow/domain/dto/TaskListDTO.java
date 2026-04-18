package com.gozzerks.taskflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "A task list owned by a user. Derived fields (count, progress, tasks) are populated on reads and ignored on writes.")
public record TaskListDTO(
        @Schema(description = "Server-assigned identifier. Omit when creating.", accessMode = Schema.AccessMode.READ_ONLY)
        UUID id,

        @Schema(description = "Human-readable title.", example = "Sprint 12")
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Schema(description = "Optional free-form details.", example = "Work items for the sprint ending 2026-05-02.")
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Schema(description = "Total number of tasks in this list.", accessMode = Schema.AccessMode.READ_ONLY)
        Integer count,

        @Schema(description = "Fraction of tasks in CLOSED status, between 0 and 1.", accessMode = Schema.AccessMode.READ_ONLY, example = "0.75")
        Double progress,

        @Schema(description = "Tasks in this list (present on single-list reads).", accessMode = Schema.AccessMode.READ_ONLY)
        List<TaskDTO> tasks
) {
}
