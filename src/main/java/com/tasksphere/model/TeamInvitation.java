package com.tasksphere.model;


import com.tasksphere.enums.TeamRole;
import jakarta.persistence.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public TeamInvitation() {}

    public Long getId() { return id; }

    public Team getTeam() { return team; }

    public String getEmail() { return email; }

    public TeamRole getRole() { return role; }

    public Instant getCreatedAt() { return createdAt; }

    public void setTeam(Team team) { this.team = team; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(TeamRole role) { this.role = role; }

}
