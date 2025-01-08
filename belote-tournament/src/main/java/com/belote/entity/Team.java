
package com.belote.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_number")
    private Integer teamNumber;

    @Column(name = "player1_name")
    private String player1Name;

    @Column(name = "player2_name")
    private String player2Name;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(Integer teamNumber) {
		this.teamNumber = teamNumber;
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public void setPlayer1Name(String player1Name) {
		this.player1Name = player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}
    
    
}