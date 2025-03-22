package com.mtgleague.repo;

import com.mtgleague.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayersRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByEmail(String email);

    @Query("SELECT p FROM Player p JOIN p.owner s WHERE s.id = :leagueId")
    List<Player> getLeagueAdmins(@Param("leagueId")Long leagueId);

    @Transactional
    @Modifying
    @Query("Update Player p set p.password = :password where p.email = :email")
    void updatePassword(String email, String password);

}