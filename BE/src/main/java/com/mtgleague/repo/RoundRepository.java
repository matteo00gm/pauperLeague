package com.mtgleague.repo;

import com.mtgleague.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    @Query("SELECT r FROM Round r WHERE r.event.ended = false AND (r.idP1 = :playerId OR r.idP2 = :playerId) ORDER BY r.turn DESC LIMIT 1")
    Optional<Round> findCurrentByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT r FROM Round r WHERE r.event.id = :eventId AND not ended")
    List<Round> findCurrentByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r FROM Round r WHERE r.event.id = :eventId")
    List<Round> findByEventId(@Param("eventId") Long eventId);

    @Query(value = "SELECT r.* FROM round r " +
            "JOIN event e ON r.event_id = e.id " +
            "WHERE " +
            "(CURRENT_DATE BETWEEN '2024-06-02' AND '2024-11-10' AND e.date BETWEEN '2024-06-02' AND '2024-11-10') OR " +
            "(CURRENT_DATE BETWEEN '2024-11-11' AND '2025-02-23' AND e.date BETWEEN '2024-11-11' AND '2025-02-23') OR " +
            "(CURRENT_DATE BETWEEN '2024-02-24' AND '2024-06-01' AND e.date BETWEEN '2024-02-24' AND '2024-06-01')",
            nativeQuery = true)
    List<Round> findRoundsInCurrentSeason();

}