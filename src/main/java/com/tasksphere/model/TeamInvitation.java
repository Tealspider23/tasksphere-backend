package com.tasksphere.model;


import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.Instant;

@Entity
@Table(
        name = "team_invitations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id" , "email"})
        }
)
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role = "MEMBER";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public TeamInvitation() {}

    public Long getId() { return id; }

    public Team getTeam() { return team; }

    public String getEmail() { return email; }

    public String getRole() { return role; }

    public Instant getCreatedAt() { return createdAt; }

    public void setTeam(Team team) { this.team = team; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(String role) { this.role = role; }

}
