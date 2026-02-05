package com.tasksphere.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tasksphere.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max=100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max=500 , message = "Description must be at most 500 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;  // if task is ever converted to JSON , ignore the user field (@JSONIGNORE working)

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


    public Task(){} // for conversion from java object to json

    public Long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Team getTeam() {return team;}

    public void setTeam(Team team) {this.team = team; }
}

