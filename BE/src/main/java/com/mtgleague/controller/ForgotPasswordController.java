package com.mtgleague.controller;


import com.mtgleague.dto.MailBody;
import com.mtgleague.dto.request.MailRequestDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.ForgotPassword;
import com.mtgleague.model.Player;
import com.mtgleague.repo.ForgotPasswordRepository;
import com.mtgleague.repo.PlayersRepository;
import com.mtgleague.service.EmailService;
import com.mtgleague.utils.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final PlayersRepository playersRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verifyMail")
    public ResponseEntity<String> verifyEmail(@RequestBody MailRequestDTO request) {
        Player player = playersRepository.findByEmail(request.getMail())
                .orElseThrow(() -> new GenericException("Giocatore non trovato con email " + request.getMail()));

        int otp = otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(request.getMail())
                .subject("Password Reset")
                .text("""
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333; text-align: center;">
                        <h2>Richiesta reset della Password</h2>
                        <p>Clicca qui per aggiornare la tua password:</p>
                        <a href="https://pauperleague.com/password-reset?email=%s&otp=%d"
                            style="
                                display: inline-block;
                                background-color: #4CAF50; 
                                color: white; 
                                text-decoration: none; 
                                padding: 10px 20px; 
                                font-size: 16px; 
                                border-radius: 5px;">
                            Reset Password
                        </a>
                        <p>Se non sei stato tu a richiederlo, ignora questa mail.</p>
                    </body>
                </html>
                """.formatted(request.getMail(), otp))
                            .build();


        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .player(player)
                .build();

        Optional<ForgotPassword> oldFP = forgotPasswordRepository.findByPlayer(player);
        if(oldFP.isPresent()){
            if(!oldFP.get().getExpirationTime().before(Date.from(Instant.now()))) {
                throw new GenericException("Hai già effettuato una richiesta di reset. Controlla la posta. Se non la vedi, verifica la cartella spam. Altrimenti, riprova tra qualche minuto.");
            } else {
                forgotPasswordRepository.deleteById(oldFP.get().getFpid());
            }
        }

        forgotPasswordRepository.save(fp);
        emailService.sendHtmlMessage(mailBody);

        return ResponseEntity.ok("Email di verifica inviata!");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(
            @RequestBody ChangePassword changePassword, @PathVariable String email) {
        if(!Objects.equals(changePassword.newPassword(), changePassword.confirmPassword())) {
            return new ResponseEntity<>("Le password sono diverse!", HttpStatus.EXPECTATION_FAILED);
        }

        //checking if otp exists
        Player player = playersRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException("Giocatore non trovato con email " + email));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndPlayer(changePassword.otp(), player)
                .orElseThrow(() -> new GenericException("OTP non valido per email " + email));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP scaduto!", HttpStatus.EXPECTATION_FAILED);
        }
        forgotPasswordRepository.deleteById(fp.getFpid());

        //updating password
        String encodedPassword = passwordEncoder.encode(changePassword.newPassword());

        playersRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password modificata!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}