package com.belote.repository;

import com.belote.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId);
    List<Match> findByTournamentIdAndRoundNumber(Long tournamentId, Integer roundNumber);
    Integer countByTournamentIdAndCompleted(Long tournamentId, Boolean completed);
}
