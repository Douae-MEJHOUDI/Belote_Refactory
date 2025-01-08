package com.belote.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @Column(name = "number_of_teams")
    private Integer numberOfTeams;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Team> teams;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TournamentStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentStatus status) {
		this.status = status;
	}

	public Integer getNumberOfTeams() {
		return numberOfTeams;
	}

	public void setNumberOfTeams(Integer numberOfTeams) {
		this.numberOfTeams = numberOfTeams;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
    
    
}