package com.belote.repository;

import com.belote.entity.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByName(String name);
    boolean existsByName(String name);
}
