package com.belote.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private Team team2;

    private Integer score1;
    private Integer score2;
    private Integer roundNumber;
    private Boolean completed = false;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Tournament getTournament() {
		return tournament;
	}
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}
	public Team getTeam1() {
		return team1;
	}
	public void setTeam1(Team team1) {
		this.team1 = team1;
	}
	public Team getTeam2() {
		return team2;
	}
	public void setTeam2(Team team2) {
		this.team2 = team2;
	}
	public Integer getScore1() {
		return score1;
	}
	public void setScore1(Integer score1) {
		this.score1 = score1;
	}
	public Integer getScore2() {
		return score2;
	}
	public void setScore2(Integer score2) {
		this.score2 = score2;
	}
	public Integer getRoundNumber() {
		return roundNumber;
	}
	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
    
    
}
