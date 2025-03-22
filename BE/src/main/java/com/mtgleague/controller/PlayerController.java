package com.mtgleague.controller;

import com.mtgleague.dto.request.player.PlayerIdRequestDTO;
import com.mtgleague.dto.response.player.PlayerProfileResponseDTO;
import com.mtgleague.service.PlayersService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayersService playersService;

    @Cacheable(cacheNames = "profile", key = "#player.playerId")
    @PostMapping("/profile")
    public ResponseEntity<PlayerProfileResponseDTO> findSubbedLeagues(@RequestBody PlayerIdRequestDTO player){
        PlayerProfileResponseDTO leagues= playersService.findPlayer(player.getPlayerId());
        return new ResponseEntity<>(leagues, HttpStatus.OK);
    }
}
