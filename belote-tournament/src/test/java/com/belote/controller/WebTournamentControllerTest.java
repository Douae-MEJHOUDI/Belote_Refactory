package com.belote.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.belote.controller.web.WebTournamentController;
import com.belote.dto.TeamStanding;
import com.belote.entity.Match;
import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.service.MatchService;
import com.belote.service.TeamService;
import com.belote.service.TournamentService;

public class WebTournamentControllerTest {

    @Mock
    private TournamentService tournamentService;

    @Mock
    private TeamService teamService;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private WebTournamentController webTournamentController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webTournamentController).build();
    }

    @Test
    public void testHome() throws Exception {
        when(tournamentService.getAllTournaments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("tournaments"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testNewTournamentForm() throws Exception {
        mockMvc.perform(get("/tournaments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament/create"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTournament() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        when(tournamentService.createTournament("Test Tournament")).thenReturn(tournament);

        mockMvc.perform(post("/tournaments")
                .param("name", "Test Tournament"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/1"));
    }

    @Test
    public void testViewTournament() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);

        mockMvc.perform(get("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament/view"))
                .andExpect(model().attributeExists("tournament"));
    }

    @Test
    public void testViewTeams() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        List<Team> teams = Arrays.asList(new Team(), new Team());
        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);
        when(teamService.getTeamsByTournament(1L)).thenReturn(teams);

        mockMvc.perform(get("/tournaments/1/teams"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament/teams"))
                .andExpect(model().attributeExists("tournament", "teams"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddTeam() throws Exception {
        Long tournamentId = 1L;
        String player1Name = "Player1";
        String player2Name = "Player2";
        
        Team team = new Team();
        team.setId(1L);
        team.setPlayer1Name(player1Name);
        team.setPlayer2Name(player2Name);
        
        when(teamService.addTeam(tournamentId, player1Name, player2Name)).thenReturn(team);

        mockMvc.perform(post("/tournaments/{id}/teams", tournamentId)
                .param("player1Name", player1Name)
                .param("player2Name", player2Name)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/" + tournamentId + "/teams"));
                
        verify(teamService).addTeam(tournamentId, player1Name, player2Name);
    }

    @Test
    public void testViewMatches() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        Match match = new Match();
        match.setRoundNumber(1);
        List<Match> matches = Arrays.asList(match);
        Map<Integer, List<Match>> matchesByRound = Map.of(1, matches);

        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);
        when(matchService.getMatchesByTournament(1L)).thenReturn(matches);

        mockMvc.perform(get("/tournaments/1/matches"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament/matches"))
                .andExpect(model().attributeExists("tournament", "matchesByRound"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGenerateNextRound() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);
        when(matchService.getCurrentRound(1L)).thenReturn(1);

        mockMvc.perform(post("/tournaments/1/matches/next-round"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/1/matches"));

        verify(matchService, times(1)).generateMatchesForRound(tournament, 2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateScore() throws Exception {
        Long tournamentId = 1L;
        Long matchId = 1L;
        Integer score1 = 10;
        Integer score2 = 5;
        
        Match match = new Match();
        match.setId(matchId);
        match.setScore1(score1);
        match.setScore2(score2);
        
        when(matchService.updateMatchScore(matchId, score1, score2)).thenReturn(match);
        
        mockMvc.perform(post("/tournaments/{tournamentId}/matches/{matchId}/score", tournamentId, matchId)
                .param("score1", score1.toString())
                .param("score2", score2.toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/" + tournamentId + "/matches"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteTeam() throws Exception {
        doNothing().when(teamService).deleteTeam(1L, 1L);

        mockMvc.perform(post("/tournaments/1/teams/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/1/teams"));
    }

    @Test
    public void testViewStandings() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        List<TeamStanding> standings = Arrays.asList(new TeamStanding());

        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);
        when(tournamentService.getStandings(1L)).thenReturn(standings);

        mockMvc.perform(get("/tournaments/1/standings"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament/standings"))
                .andExpect(model().attributeExists("tournament", "standings"));
    }
}