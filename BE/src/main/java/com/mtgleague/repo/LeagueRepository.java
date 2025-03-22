package com.mtgleague.repo;


import com.mtgleague.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    List<League> findAllByOrderByNameAsc();
    @Query("SELECT l FROM League l JOIN l.players p WHERE p.id = :playerId")
    List<League> findPlayerLeagues(@Param("playerId") Long playerId);

}