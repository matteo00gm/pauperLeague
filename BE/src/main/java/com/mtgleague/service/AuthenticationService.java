package com.mtgleague.service;

import com.mtgleague.config.JwtService;
import com.mtgleague.dto.auth.request.LoginRequest;
import com.mtgleague.dto.auth.request.SignupRequest;
import com.mtgleague.dto.auth.response.AuthenticationResponse;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.Player;
import com.mtgleague.repo.PlayersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PlayersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse signup(SignupRequest request) {
        var player = Player.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        try {
            repository.save(player);
        } catch (Exception ex) {
            throw new GenericException("L'email inserita è già stata presa!");
        }


        var jwtTokenDTO = jwtService.generateToken(player, player.getId().toString());

        return AuthenticationResponse.builder()
                .token(jwtTokenDTO.getToken())
                .userId(player.getId())
                .expiresIn(jwtTokenDTO.getExpirationDate())
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        var playerOptional = repository.findByEmail(request.getEmail());

        Player player = playerOptional.orElseThrow(() -> new GenericException("Credenziali errate!"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(player.getEmail(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new GenericException("Credenziali errate!");
        }

        var jwtTokenDTO = jwtService.generateToken(player, player.getId().toString());

        return AuthenticationResponse.builder()
                .token(jwtTokenDTO.getToken())
                .userId(player.getId())
                .expiresIn(jwtTokenDTO.getExpirationDate())
                .build();
    }
}
