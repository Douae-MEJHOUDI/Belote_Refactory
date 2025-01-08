package com.belote.controller.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.belote.dto.TeamStanding;
import com.belote.entity.Match;
import com.belote.entity.Team;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.service.MatchService;
import com.belote.service.TeamService;
import com.belote.service.TournamentService;

@Controller
public class WebTournamentController {

    @Autowired
    private TournamentService tournamentService;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private MatchService matchService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        return "home";
    }
    
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tournaments/new")
    public String newTournamentForm() {
        return "tournament/create";
    }
    
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tournaments")
    public String createTournament(@RequestParam("name") String name) {
        Tournament tournament = tournamentService.createTournament(name);
        return "redirect:/tournaments/" + tournament.getId();
    }
    
    @GetMapping("/tournaments/{id}")
    public String viewTournament(@PathVariable("id") Long id, Model model) {
        Tournament tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);
        return "tournament/view";
    }

    @GetMapping("/tournaments/{id}/teams")
    public String viewTeams(@PathVariable("id") Long id, Model model) {
        Tournament tournament = tournamentService.getTournamentById(id);
        List<Team> teams = teamService.getTeamsByTournament(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);
        return "tournament/teams";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tournaments/{id}/teams")
    public String addTeam(@PathVariable("id") Long id, 
                         @RequestParam("player1Name") String player1Name,
                         @RequestParam("player2Name") String player2Name) {
        teamService.addTeam(id, player1Name, player2Name);
        return "redirect:/tournaments/" + id + "/teams";
    }
    
    @GetMapping("/tournaments/{id}/matches")
    public String viewMatches(@PathVariable("id") Long id, Model model) {
        Tournament tournament = tournamentService.getTournamentById(id);
        List<Match> matches = matchService.getMatchesByTournament(id);
        
        // Group matches by round
        Map<Integer, List<Match>> matchesByRound = matches.stream()
            .collect(Collectors.groupingBy(Match::getRoundNumber));
        
        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByRound", matchesByRound);
        return "tournament/matches";
    }

    @PostMapping("/tournaments/{id}/matches/next-round")
    public String generateNextRound(@PathVariable("id") Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        Integer currentRound = matchService.getCurrentRound(id);
        matchService.generateMatchesForRound(tournament, currentRound + 1);
        return "redirect:/tournaments/" + id + "/matches";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tournaments/{tournamentId}/matches/{matchId}/score")
    public String updateScore(@PathVariable("tournamentId") Long tournamentId, 
                             @PathVariable("matchId") Long matchId,
                             @RequestParam("score1") Integer score1,
                             @RequestParam("score2") Integer score2) {
        matchService.updateMatchScore(matchId, score1, score2);
        return "redirect:/tournaments/" + tournamentId + "/matches";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tournaments/{id}/status")
    public String updateTournamentStatus(@PathVariable("id") Long id, @RequestParam("status") TournamentStatus status) {
        tournamentService.updateStatus(id, status);
        return "redirect:/tournaments/" + id;
    }
    
    
    @GetMapping("/tournaments/{id}/standings")
    public String viewStandings(@PathVariable("id") Long id, Model model) {
        Tournament tournament = tournamentService.getTournamentById(id);
        List<TeamStanding> standings = tournamentService.getStandings(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("standings", standings);
        return "tournament/standings";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tournaments/{tournamentId}/teams/{teamId}/delete")
    public String deleteTeam(@PathVariable("tournamentId") Long tournamentId, @PathVariable("teamId") Long teamId) {
        teamService.deleteTeam(teamId, tournamentId);
        return "redirect:/tournaments/" + tournamentId + "/teams";
    }
}