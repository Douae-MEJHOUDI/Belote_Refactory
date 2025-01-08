package com.belote.dto;

import lombok.Data;

@Data
public class TeamStanding {
    private Long teamId;
    private Integer teamNumber;
    private String player1Name;
    private String player2Name;
    private Integer matchesPlayed;
    private Integer matchesWon;
    private Integer totalScore;
	public Long getTeamId() {
		return teamId;
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
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
	public Integer getMatchesPlayed() {
		return matchesPlayed;
	}
	public void setMatchesPlayed(Integer matchesPlayed) {
		this.matchesPlayed = matchesPlayed;
	}
	public Integer getMatchesWon() {
		return matchesWon;
	}
	public void setMatchesWon(Integer matchesWon) {
		this.matchesWon = matchesWon;
	}
	public Integer getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}
    
    
    
}
