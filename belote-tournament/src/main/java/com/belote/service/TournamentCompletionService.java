package com.belote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belote.entity.Match;
import com.belote.entity.Tournament;
import com.belote.entity.TournamentStatus;
import com.belote.repository.MatchRepository;
import com.belote.repository.TournamentRepository;

import jakarta.transaction.Transactional;

@Service
public class TournamentCompletionService {
    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Autowired
    private MatchRepository matchRepository;

    @Transactional
    public void checkAndUpdateTournamentCompletion(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));
            
        if (tournament.getStatus() == TournamentStatus.IN_PROGRESS) {
            List<Match> matches = matchRepository.findByTournamentId(tournamentId);
            boolean allMatchesCompleted = !matches.isEmpty() && 
                matches.stream().allMatch(Match::getCompleted);
                
            if (allMatchesCompleted) {
                tournament.setStatus(TournamentStatus.COMPLETED);
                tournamentRepository.save(tournament);
            }
        }
    }
}
