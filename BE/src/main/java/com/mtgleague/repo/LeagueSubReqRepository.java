package com.mtgleague.repo;

import com.mtgleague.model.LeagueSubRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LeagueSubReqRepository extends JpaRepository<LeagueSubRequest, Long> {

    @Query("SELECT r FROM LeagueSubRequest r WHERE r.player.id = :playerId AND r.league.id = :leagueId")
    Optional<LeagueSubRequest> findByPlayerAndLeagueId(@Param("playerId") Long playerId, @Param("leagueId") Long leagueId);

    @Query("SELECT r FROM LeagueSubRequest r WHERE r.league.id = :leagueId")
    List<LeagueSubRequest> findByLeagueId(@Param("leagueId") Long leagueId);

}