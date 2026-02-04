package com.tasksphere.model;

import jakarta.persistence.*;

@Entity
@Table(name ="teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Team(){}

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public User getOwner(){
        return owner;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }
}
