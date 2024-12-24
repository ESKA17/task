package kz.homework.task.model;

public enum ApiError {
    ARGUMENT_MISSING(400, "Body argument missing"),
    BAD_RESOURCE_ID(400, "Bad task id"),
    RESOURCE_NOT_FOUND(404, "No task found"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal server error");

    private final int status;
    private final String message;

    private ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
