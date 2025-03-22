package com.mtgleague.repo;

import com.mtgleague.model.Event;
import com.mtgleague.model.EventSubRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventSubReqRepository extends JpaRepository<EventSubRequest, Long> {

    @Query("SELECT r FROM EventSubRequest r WHERE r.player.id = :playerId AND r.event.id = :eventId")
    Optional<EventSubRequest> findByPlayerAndEventId(@Param("playerId") Long playerId, @Param("eventId") Long eventId);

    @Query("SELECT r.event FROM EventSubRequest r WHERE r.player.id = :playerId AND r.event.date >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') ORDER BY r.event.date ASC")
    List<Event> findPlayerSubbedEvents(@Param("playerId") Long playerId);

}