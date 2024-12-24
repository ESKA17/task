package kz.homework.task.exception;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Represents an error response with details about the issue that occurred in the system.")
public class ErrorResponse {

    @Schema(description = "A unique code representing the error.", example = "not_found")
    private String code;

    @Schema(description = "A description of the error that occurred.", example = "The requested resource was not found.")
    private String description;

    @Schema(description = "The origin of the error, which can help in tracing where the error originated from.", example = "tasks-service")
    private String origin;

    @Schema(description = "Timestamp when the error occurred.", example = "2024-12-24T10:00:00Z")
    private LocalDateTime timestamp;

    @Schema(description = "Additional data related to the error (if applicable).", example = "{\"field\":\"username\", \"error\":\"must not be blank\"}")
    private Map<String, Object> data;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String description) {
        this.code = code.toLowerCase();
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}

