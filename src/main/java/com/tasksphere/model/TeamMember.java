package com.tasksphere.model;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id" , "user_id"})
        }
)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Instant joinedAt = Instant.now();

    public TeamMember() {}

    public Long getId() { return id; }

    public Team getTeam() { return team; }

    public User getUser() { return user; }

    public String getRole() { return role; }

    public Instant getJoinedAt() { return joinedAt; }

    public void setTeam(Team team) { this.team = team; }

    public void setUser(User user) { this.user = user; }

    public void setRole(String role) { this.role = role; }

}
