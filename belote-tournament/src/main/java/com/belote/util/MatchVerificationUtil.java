package com.belote.util;

import com.belote.entity.Match;
import com.belote.entity.Team;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MatchVerificationUtil {
    
    public static boolean hasTeamPlayedInRound(Team team, List<Match> roundMatches) {
        return roundMatches.stream()
            .anyMatch(m -> m.getTeam1().getId().equals(team.getId()) 
                      || m.getTeam2().getId().equals(team.getId()));
    }
    
    public static boolean haveTeamsPlayed(Team team1, Team team2, List<Match> allMatches) {
        return allMatches.stream()
            .anyMatch(m -> (m.getTeam1().getId().equals(team1.getId()) && m.getTeam2().getId().equals(team2.getId()))
                      || (m.getTeam1().getId().equals(team2.getId()) && m.getTeam2().getId().equals(team1.getId())));
    }
    
    public static boolean isValidMatch(Team team1, Team team2, List<Match> roundMatches, List<Match> allMatches) {
        // Teams can't play against themselves
        if (team1.getId().equals(team2.getId())) {
            return false;
        }
        
        // Teams can't play twice in same round
        if (hasTeamPlayedInRound(team1, roundMatches) || hasTeamPlayedInRound(team2, roundMatches)) {
            return false;
        }
        
        // Teams can't play against each other more than once
        if (haveTeamsPlayed(team1, team2, allMatches)) {
            return false;
        }
        
        return true;
    }
}
