package com.mtgleague.repo;

import com.mtgleague.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId AND er.player.id = :playerId")
    Optional<EventRegistration> findByEventIdAndPlayerId(@Param("eventId") Long eventId, @Param("playerId") Long playerId);


}