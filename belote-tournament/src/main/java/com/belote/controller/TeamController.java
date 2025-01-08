package com.belote.controller;


import com.belote.entity.Team;
import com.belote.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;
    
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeam(@PathVariable("teamId") Long teamId,
                                         @RequestParam("player1Name") String player1Name,
                                         @RequestParam("player2Name") String player2Name) {
        try {
            Team updatedTeam = teamService.updateTeam(teamId, player1Name, player2Name);
            return ResponseEntity.ok(updatedTeam);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
