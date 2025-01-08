package com.belote.service;

import com.belote.entity.Match;
import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.repository.MatchRepository;
import com.belote.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {
	
	@Autowired
    private MatchRepository matchRepository;
	
	@Autowired 
    private TeamRepository teamRepository;
	
	@Autowired
    private TournamentCompletionService completionService;

    public List<Match> getMatchesByTournament(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public List<Match> getMatchesByTournamentAndRound(Long tournamentId, Integer roundNumber) {
        return matchRepository.findByTournamentIdAndRoundNumber(tournamentId, roundNumber);
    }

    @Transactional
    public Match updateMatchScore(Long matchId, Integer score1, Integer score2) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new RuntimeException("Match not found"));
                
        match.setScore1(score1);
        match.setScore2(score2);
        match.setCompleted(true);
        
        Match savedMatch = matchRepository.save(match);
        
        // Check if tournament is completed after updating match
        completionService.checkAndUpdateTournamentCompletion(match.getTournament().getId());
        
        return savedMatch;
    }

    @Transactional
    public void generateMatchesForRound(Tournament tournament, int roundNumber) {
    	// Add validations
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        // Check if previous round is complete (if not first round)
        if (roundNumber > 1) {
            List<Match> previousRoundMatches = matchRepository
                .findByTournamentIdAndRoundNumber(tournament.getId(), roundNumber - 1);
            boolean allCompleted = previousRoundMatches.stream()
                .allMatch(Match::getCompleted);
            if (!allCompleted) {
                throw new IllegalStateException("Previous round must be completed before generating new matches");
            }
        }

        // Check if matches already exist for this round
        List<Match> existingMatches = matchRepository
            .findByTournamentIdAndRoundNumber(tournament.getId(), roundNumber);
        if (!existingMatches.isEmpty()) {
            throw new IllegalStateException("Matches for this round already exist");
        }
    	
        List<Team> teams = teamRepository.findByTournamentId(tournament.getId());
        
        // If odd number of teams, add a dummy team
        boolean hasVirtualTeam = false;
        if (teams.size() % 2 != 0) {
            hasVirtualTeam = true;
            Team virtualTeam = new Team();
            virtualTeam.setId(-1L); // Virtual team identifier
            virtualTeam.setTeamNumber(-1);
            teams.add(virtualTeam);
        }

        int nbTeams = teams.size();
        List<Match> roundMatches = new ArrayList<>();
        
        // Copy team positions for rotation
        Team[] teamPositions = new Team[nbTeams];
        for (int i = 0; i < nbTeams; i++) {
            teamPositions[i] = teams.get(i);
        }

        // If not first round, rotate teams
        if (roundNumber > 1) {
            Team temp = teamPositions[nbTeams - 2];
            for (int i = nbTeams - 2; i > 0; i--) {
                teamPositions[i] = teamPositions[i-1];
            }
            teamPositions[0] = temp;
        }

        // Generate matches for this round
        for (int i = 0; i < nbTeams / 2; i++) {
            Team team1 = teamPositions[i];
            Team team2 = teamPositions[nbTeams - 1 - i];

            // Skip if either team is the virtual team
            if (hasVirtualTeam && (team1.getId() == -1L || team2.getId() == -1L)) {
                continue;
            }

            Match match = new Match();
            match.setTournament(tournament);
            match.setTeam1(team1);
            match.setTeam2(team2);
            match.setRoundNumber(roundNumber);
            match.setScore1(0);
            match.setScore2(0);
            match.setCompleted(false);

            roundMatches.add(match);
        }

        // Save all matches for this round
        matchRepository.saveAll(roundMatches);
    	
    }
    
    public Integer getCurrentRound(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId)
            .stream()
            .mapToInt(Match::getRoundNumber)
            .max()
            .orElse(0);
    }
    
    public boolean areAllMatchesCompleted(Long tournamentId) {
        List<Match> matches = matchRepository.findByTournamentId(tournamentId);
        return !matches.isEmpty() && matches.stream().allMatch(Match::getCompleted);
    }
}