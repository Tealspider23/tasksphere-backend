package com.tasksphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,message = "password must be atleast 6 characters long" )
    private String password;

    public RegisterRequest(){}

    public RegisterRequest(String email , String password){
        this.email = email;
        this.password = password;
    }

    // Getters and setters

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(){
        this.password = password;
    }

}
