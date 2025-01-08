package com.belote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.repository.TeamRepository;
import com.belote.repository.TournamentRepository;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TeamService teamService;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");
        tournament.setStatus(TournamentStatus.REGISTRATION);
    }

    @Test
    void addTeam_WhenTournamentInRegistration_ShouldAddTeam() {

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findByTournamentId(1L)).thenReturn(Arrays.asList());
        
        Team newTeam = new Team();
        newTeam.setId(1L);
        newTeam.setPlayer1Name("Player1");
        newTeam.setPlayer2Name("Player2");
        newTeam.setTeamNumber(1);
        newTeam.setTournament(tournament);
        
        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        Team result = teamService.addTeam(1L, "Player1", "Player2");

        assertNotNull(result);
        assertEquals("Player1", result.getPlayer1Name());
        assertEquals("Player2", result.getPlayer2Name());
        assertEquals(1, result.getTeamNumber());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void addTeam_WhenTournamentNotInRegistration_ShouldThrowException() {

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(RuntimeException.class, () -> 
            teamService.addTeam(1L, "Player1", "Player2"));
    }

    @Test
    void deleteTeam_WhenTournamentInRegistration_ShouldDeleteAndReorderTeams() {

        Team team1 = new Team();
        team1.setId(1L);
        team1.setTeamNumber(1);
        team1.setTournament(tournament);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1));
        when(teamRepository.findByTournamentIdAndTeamNumberGreaterThan(1L, 1))
            .thenReturn(Arrays.asList());

        teamService.deleteTeam(1L, 1L);

        verify(teamRepository).delete(team1);
    }
}