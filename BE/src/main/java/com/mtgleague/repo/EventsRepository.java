package com.mtgleague.repo;


import com.mtgleague.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {

    @Query("SELECT r FROM Event r WHERE r.league.id = :leagueId ORDER by date ASC")
    List<Event> findAllOrdered(@Param("leagueId") Long leagueId);

    @Query("SELECT r FROM Event r WHERE r.league.id = :leagueId AND r.ended = false ORDER by date ASC")
    List<Event> findUpcomingEvents(@Param("leagueId") Long leagueId);

    @Query("SELECT r.event FROM EventRegistration r WHERE r.player.id = :playerId AND r.event.date >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') ORDER BY r.event.date ASC")
    List<Event> findPlayerEvents(@Param("playerId") Long playerId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.eventRegistrations r LEFT JOIN FETCH r.player WHERE e.id = :eventId")
    Optional<Event> findByIdWithPlayers(@Param("eventId") Long eventId);

}