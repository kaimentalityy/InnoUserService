package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing standardized error responses returned by the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standardized error response")
public class ErrorDto {

    /**
     * Human-readable error message.
     */
    @Schema(description = "Human-readable error message", example = "User not found")
    private String message;

    /**
     * Corresponding HTTP status code.
     */
    @Schema(description = "HTTP status code", example = "404")
    private int status;
}
