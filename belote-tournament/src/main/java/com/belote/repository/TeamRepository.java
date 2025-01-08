package com.belote.repository;


import com.belote.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTournamentId(Long tournamentId);
    Integer countByTournamentId(Long tournamentId);
    List<Team> findByTournamentIdOrderByTeamNumber(Long tournamentId);
    List<Team> findByTournamentIdAndTeamNumberGreaterThan(Long tournamentId, int teamNumber);
}
