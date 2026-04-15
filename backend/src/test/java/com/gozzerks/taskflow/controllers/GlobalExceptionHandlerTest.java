package com.gozzerks.taskflow.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/illegal-argument")
        public String throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/test/illegal-argument-custom")
        public String throwIllegalArgumentCustom() {
            throw new IllegalArgumentException("Task list not found with id: 123e4567-e89b-12d3-a456-426614174000");
        }

        @GetMapping("/test/illegal-argument-empty")
        public String throwIllegalArgumentEmpty() {
            throw new IllegalArgumentException("");
        }

        @GetMapping("/test/illegal-argument-null")
        public String throwIllegalArgumentNull() {
            throw new IllegalArgumentException((String) null);
        }

        @GetMapping("/test/illegal-argument-unicode")
        public String throwIllegalArgumentUnicode() {
            throw new IllegalArgumentException("Invalid task: Café ☀️ naïve résumé");
        }

        @GetMapping("/test/illegal-argument-long")
        public String throwIllegalArgumentLong() {
            throw new IllegalArgumentException("This is a very long error message that exceeds normal length ".repeat(10));
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handling")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("Should handle IllegalArgumentException with 400 status")
        void shouldHandleIllegalArgumentException() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid argument provided")))
                    .andExpect(jsonPath("$.details", containsString("/test/illegal-argument")));
        }

        @Test
        @DisplayName("Should return ErrorResponse with correct structure")
        void shouldReturnErrorResponseWithCorrectStructure() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.details").isString());
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException with custom message")
        void shouldHandleCustomExceptionMessage() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument-custom")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", containsString("Task list not found")))
                    .andExpect(jsonPath("$.message", containsString("123e4567-e89b-12d3-a456-426614174000")));
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException with empty message")
        void shouldHandleEmptyExceptionMessage() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument-empty")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", is("")))
                    .andExpect(jsonPath("$.details", containsString("/test/illegal-argument-empty")));
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException with null message")
        void shouldHandleNullExceptionMessage() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument-null")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message").value(nullValue()));
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException with special characters and emojis")
        void shouldHandleSpecialCharactersAndEmojis() throws Exception {
            // Arrange
            String messageWithSpecialChars = "Invalid task: Café ☀️ naïve résumé";

            // Act & Assert
            mockMvc.perform(get("/test/illegal-argument-unicode")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", is(messageWithSpecialChars)));
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException with very long message")
        void shouldHandleLongExceptionMessage() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument-long")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", containsString("This is a very long error message")));
        }
    }

    @Nested
    @DisplayName("Error Response Content Validation")
    class ErrorResponseContentTests {

        @Test
        @DisplayName("Should include correct HTTP status code in response body")
        void shouldIncludeCorrectStatusCodeInBody() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        @DisplayName("Should include exception message in response body")
        void shouldIncludeExceptionMessageInBody() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(jsonPath("$.message", notNullValue()))
                    .andExpect(jsonPath("$.message", is("Invalid argument provided")));
        }

        @Test
        @DisplayName("Should include request path in response body")
        void shouldIncludeRequestPathInBody() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(jsonPath("$.details", notNullValue()))
                    .andExpect(jsonPath("$.details", containsString("uri=/test/illegal-argument")));
        }

        @Test
        @DisplayName("Should return JSON content type")
        void shouldReturnJsonContentType() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should have all required fields in error response")
        void shouldHaveAllRequiredFields() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.details").exists())
                    .andExpect(jsonPath("$.*", hasSize(3)));
        }
    }

    @Nested
    @DisplayName("HTTP Response Validation")
    class HttpResponseTests {

        @Test
        @DisplayName("Should return 400 Bad Request status")
        void shouldReturn400Status() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("Should handle multiple consecutive exceptions")
        void shouldHandleMultipleExceptions() throws Exception {
            // Arrange & Act & Assert - First request
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Act & Assert - Second request
            mockMvc.perform(get("/test/illegal-argument-custom")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Act & Assert - Third request
            mockMvc.perform(get("/test/illegal-argument-empty")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should not expose stack trace in response")
        void shouldNotExposeStackTrace() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.stackTrace").doesNotExist())
                    .andExpect(jsonPath("$.trace").doesNotExist());
        }

        @Test
        @DisplayName("Should not include sensitive server information")
        void shouldNotIncludeSensitiveInformation() throws Exception {
            // Arrange & Act & Assert
            mockMvc.perform(get("/test/illegal-argument")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.exception").doesNotExist())
                    .andExpect(jsonPath("$.error").doesNotExist());
        }
    }
}