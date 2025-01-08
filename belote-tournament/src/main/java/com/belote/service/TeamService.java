package com.belote.service;


import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.repository.TeamRepository;
import com.belote.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeamService {
	@Autowired
    private TeamRepository teamRepository;
	
	@Autowired
    private TournamentRepository tournamentRepository;

	@Transactional
    public Team addTeam(Long tournamentId, String player1Name, String player2Name) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (tournament.getStatus() != TournamentStatus.REGISTRATION) {
            throw new RuntimeException("Tournament is not in registration phase");
        }

        // Get next team number
        List<Team> existingTeams = teamRepository.findByTournamentId(tournamentId);
        int nextTeamNumber = existingTeams.isEmpty() ? 1 : 
            existingTeams.stream()
                .mapToInt(Team::getTeamNumber)
                .max()
                .getAsInt() + 1;

        Team team = new Team();
        team.setPlayer1Name(player1Name);
        team.setPlayer2Name(player2Name);
        team.setTournament(tournament);
        team.setTeamNumber(nextTeamNumber);

        return teamRepository.save(team);
    }


    public List<Team> getTeamsByTournament(Long tournamentId) {
        return teamRepository.findByTournamentId(tournamentId);
    }
    
    @Transactional
    public Team updateTeam(Long teamId, String player1Name, String player2Name) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found"));
            
        // Only allow editing if tournament is in REGISTRATION
        if (team.getTournament().getStatus() != TournamentStatus.REGISTRATION) {
            throw new IllegalStateException("Can only edit teams during registration phase");
        }
        
        team.setPlayer1Name(player1Name);
        team.setPlayer2Name(player2Name);
        
        return teamRepository.save(team);
    }
    
    @Transactional
    public void deleteTeam(Long teamId, Long tournamentId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found"));
            
        if (team.getTournament().getStatus() != TournamentStatus.REGISTRATION) {
            throw new RuntimeException("Can only delete teams during registration");
        }

        // Get the team number being deleted
        int deletedTeamNumber = team.getTeamNumber();

        // Delete the team
        teamRepository.delete(team);

        // Reorder remaining teams
        List<Team> teamsToUpdate = teamRepository.findByTournamentIdAndTeamNumberGreaterThan(
            tournamentId, deletedTeamNumber);
        
        teamsToUpdate.forEach(t -> {
            t.setTeamNumber(t.getTeamNumber() - 1);
            teamRepository.save(t);
        });
    }
    
    @Transactional
    public void reorderTeamNumbers(Long tournamentId) {
        List<Team> teams = teamRepository.findByTournamentIdOrderByTeamNumber(tournamentId);
        int newNumber = 1;
        for (Team team : teams) {
            team.setTeamNumber(newNumber++);
            teamRepository.save(team);
        }
    }
    
}