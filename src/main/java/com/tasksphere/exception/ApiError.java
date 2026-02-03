package com.tasksphere.exception;


import java.time.LocalDateTime;

public class ApiError {

    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;


    public ApiError(boolean success , String message , int status){
        this.success = success;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
