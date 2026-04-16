package com.gozzerks.taskflow.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record ErrorResponse(int status,
                            String message,
                            String details,
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            List<FieldError> errors
) {

    public ErrorResponse(int status, String message, String details) {
        this(status, message, details, null);
    }

    public record FieldError(String field, String message) {
    }
}