package com.tasksphere.dto;

import com.tasksphere.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;



public class TaskRequestDto {

    @NotBlank(message = "Title cannot be empty!")
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public TaskStatus getStatus(){
        return status;
    }

    public void setStatus(TaskStatus status){
        this.status = status;
    }
}
