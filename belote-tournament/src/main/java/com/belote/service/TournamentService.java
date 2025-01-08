package com.belote.service;

import com.belote.dto.TeamStanding;
import com.belote.entity.Match;
import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.repository.MatchRepository;
import com.belote.repository.TournamentRepository;
import com.belote.util.MatchVerificationUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TournamentService {
	@Autowired
    private TournamentRepository tournamentRepository;
	
	@Autowired
    private MatchRepository matchRepository;
	
	

    @Transactional
    public Tournament createTournament(String name) {
        if (tournamentRepository.existsByName(name)) {
            throw new RuntimeException("Tournament with this name already exists");
        }

        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setStatus(TournamentStatus.REGISTRATION);
        return tournamentRepository.save(tournament);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));
    }
    
    @Transactional
    public Tournament updateStatus(Long id, TournamentStatus status) {
        Tournament tournament = getTournamentById(id);
        
        // If moving to IN_PROGRESS, generate all matches
        if (status == TournamentStatus.IN_PROGRESS) {
            // Check if enough teams (at least 2)
            if (tournament.getTeams().size() < 2) {
                throw new IllegalStateException("Need at least 2 teams to start tournament");
            }
            
            // Generate all matches for all rounds
            generateAllMatches(tournament);
        }
        
        tournament.setStatus(status);
        return tournamentRepository.save(tournament);
    }
    
    @Transactional
    private void generateAllMatches(Tournament tournament) {
        List<Team> teams = tournament.getTeams();
        int numberOfTeams = teams.size();
        
        // If odd number of teams, add a virtual team
        boolean hasVirtualTeam = false;
        if (numberOfTeams % 2 != 0) {
            hasVirtualTeam = true;
            numberOfTeams++;
        }
        
        // Number of rounds is (numberOfTeams - 1)
        int numberOfRounds = numberOfTeams - 1;
        
        // Array to track team positions
        Team[] teamPositions = new Team[numberOfTeams];
        for (int i = 0; i < teams.size(); i++) {
            teamPositions[i] = teams.get(i);
        }
        if (hasVirtualTeam) {
            teamPositions[numberOfTeams - 1] = null; // Virtual team
        }
        
        List<Match> allMatches = new ArrayList<>();
        
        // Generate matches for each round
        for (int round = 1; round <= numberOfRounds; round++) {
            List<Match> roundMatches = new ArrayList<>();
            
            // If not first round, rotate teams
            if (round > 1) {
                Team temp = teamPositions[numberOfTeams - 2];
                for (int i = numberOfTeams - 2; i > 0; i--) {
                    teamPositions[i] = teamPositions[i-1];
                }
                teamPositions[0] = temp;
            }
            
            // Create matches for this round
            for (int i = 0; i < numberOfTeams / 2; i++) {
                Team team1 = teamPositions[i];
                Team team2 = teamPositions[numberOfTeams - 1 - i];
                
                // Skip if either team is virtual
                if (team1 == null || team2 == null) {
                    continue;
                }
                
                // Verify match validity
                if (!MatchVerificationUtil.isValidMatch(team1, team2, roundMatches, allMatches)) {
                    throw new IllegalStateException("Invalid match pairing generated");
                }
                
                Match match = new Match();
                match.setTournament(tournament);
                match.setTeam1(team1);
                match.setTeam2(team2);
                match.setRoundNumber(round);
                match.setScore1(0);
                match.setScore2(0);
                match.setCompleted(false);
                
                roundMatches.add(match);
                allMatches.add(match);
            }
        }
        
        // Verify total number of matches
        int expectedMatches = (numberOfTeams * (numberOfTeams - 1)) / 2;
        if (!hasVirtualTeam && allMatches.size() != expectedMatches) {
            throw new IllegalStateException("Incorrect number of matches generated");
        }
        
        // Save all matches
        matchRepository.saveAll(allMatches);
    }
    
    public List<TeamStanding> getStandings(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        List<Match> matches = matchRepository.findByTournamentId(tournamentId);
        
        Map<Long, TeamStanding> standingsMap = new HashMap<>();
        
        // Initialize standings for all teams
        tournament.getTeams().forEach(team -> {
            TeamStanding standing = new TeamStanding();
            standing.setTeamId(team.getId());
            standing.setTeamNumber(team.getTeamNumber());
            standing.setPlayer1Name(team.getPlayer1Name());
            standing.setPlayer2Name(team.getPlayer2Name());
            standing.setMatchesPlayed(0);
            standing.setMatchesWon(0);
            standing.setTotalScore(0);
            standingsMap.put(team.getId(), standing);
        });
        
        // Calculate statistics from matches
        matches.stream()
            .filter(Match::getCompleted)
            .forEach(match -> {
                // Update team 1 stats
                TeamStanding standing1 = standingsMap.get(match.getTeam1().getId());
                standing1.setMatchesPlayed(standing1.getMatchesPlayed() + 1);
                standing1.setTotalScore(standing1.getTotalScore() + match.getScore1());
                if (match.getScore1() > match.getScore2()) {
                    standing1.setMatchesWon(standing1.getMatchesWon() + 1);
                }
                
                // Update team 2 stats
                TeamStanding standing2 = standingsMap.get(match.getTeam2().getId());
                standing2.setMatchesPlayed(standing2.getMatchesPlayed() + 1);
                standing2.setTotalScore(standing2.getTotalScore() + match.getScore2());
                if (match.getScore2() > match.getScore1()) {
                    standing2.setMatchesWon(standing2.getMatchesWon() + 1);
                }
            });
        
        // Sort by matches won (descending), then by total score (descending)
        return standingsMap.values().stream()
            .sorted(Comparator
                .<TeamStanding>comparingInt(s -> -s.getMatchesWon()) // Negative to sort descending
                .thenComparingInt(s -> -s.getTotalScore()))          // Negative to sort descending
            .collect(Collectors.toList());
    }
    
    

    @Transactional
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }
}