package com.mtgleague.repo;

import com.mtgleague.model.ForgotPassword;
import com.mtgleague.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("Select fp from ForgotPassword fp where fp.otp = :otp and fp.player = :player")
    Optional<ForgotPassword> findByOtpAndPlayer(Integer otp, Player player);

    @Query("Select fp from ForgotPassword fp where fp.player = :player")
    Optional<ForgotPassword> findByPlayer(Player player);
}
