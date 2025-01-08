package com.belote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.belote.dto.TeamStanding;
import com.belote.entity.Match;
import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.repository.MatchRepository;
import com.belote.repository.TournamentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament tournament;
    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");
        tournament.setStatus(TournamentStatus.REGISTRATION);

        
        team1 = new Team();
        team1.setId(1L);
        team1.setTeamNumber(1);
        team1.setPlayer1Name("Player1");
        team1.setPlayer2Name("Player2");
        team1.setTournament(tournament);

        team2 = new Team();
        team2.setId(2L);
        team2.setTeamNumber(2);
        team2.setPlayer1Name("Player3");
        team2.setPlayer2Name("Player4");
        team2.setTournament(tournament);
    }

    @Test
    void createTournament_WithUniqueName_ShouldCreateTournament() {
        
        String tournamentName = "New Tournament";
        when(tournamentRepository.existsByName(tournamentName)).thenReturn(false);
        
        Tournament newTournament = new Tournament();
        newTournament.setId(1L);
        newTournament.setName(tournamentName);
        newTournament.setStatus(TournamentStatus.REGISTRATION);
        
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(newTournament);

        
        Tournament result = tournamentService.createTournament(tournamentName);

        
        assertNotNull(result);
        assertEquals(tournamentName, result.getName());
        assertEquals(TournamentStatus.REGISTRATION, result.getStatus());
    }

    @Test
    void createTournament_WithDuplicateName_ShouldThrowException() {
        
        String tournamentName = "Existing Tournament";
        when(tournamentRepository.existsByName(tournamentName)).thenReturn(true);

        
        assertThrows(RuntimeException.class, () -> 
            tournamentService.createTournament(tournamentName));
    }

    @Test
    void updateStatus_ToInProgress_WithEnoughTeams_ShouldUpdateStatus() {
        
        tournament.setTeams(Arrays.asList(team1, team2)); // Use properly initialized teams
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.updateStatus(1L, TournamentStatus.IN_PROGRESS);

        assertEquals(TournamentStatus.IN_PROGRESS, result.getStatus());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void updateStatus_ToInProgress_WithInsufficientTeams_ShouldThrowException() {

        tournament.setTeams(Arrays.asList(team1)); // Only one team
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(IllegalStateException.class, () -> 
            tournamentService.updateStatus(1L, TournamentStatus.IN_PROGRESS));
    }

    @Test
    void getStandings_ShouldReturnCorrectStandings() {
        tournament.setTeams(Arrays.asList(team1, team2));
        
        Match match = new Match();
        match.setId(1L);
        match.setTournament(tournament);
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setScore1(10);
        match.setScore2(5);
        match.setCompleted(true);
        
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(matchRepository.findByTournamentId(1L)).thenReturn(Arrays.asList(match));

        List<TeamStanding> standings = tournamentService.getStandings(1L);

        assertNotNull(standings);
        assertFalse(standings.isEmpty());
        assertEquals(2, standings.size());
    }
}