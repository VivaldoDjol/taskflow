package com.gozzerks.taskflow.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
docs(api): annotate controllers and DTOs with OpenAPI metadataimport io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Uniform error payload returned from 4xx and 5xx responses.")
public record ErrorResponse(
        @Schema(description = "HTTP status code.", example = "404")
        int status,

        @Schema(description = "Short human-readable message.", example = "Task list not found")
        String message,

        @Schema(description = "Request path or additional detail, depending on the error.", example = "/api/task-lists/11111111-1111-1111-1111-111111111111")
        String details,

        @Schema(description = "Field-level validation errors. Present only on 400 responses from Bean Validation.")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<FieldError> errors
) {

    public ErrorResponse(int status, String message, String details) {
        this(status, message, details, null);
    }

    @Schema(description = "A single field validation failure.")
    public record FieldError(
            @Schema(description = "Offending field name.", example = "title")
            String field,

            @Schema(description = "Human-readable validation message.", example = "Title must not be blank")
            String message
    ) {
    }
}
