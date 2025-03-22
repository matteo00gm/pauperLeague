package com.mtgleague.controller;

import com.mtgleague.dto.request.RoundRequestDTO;
import com.mtgleague.dto.response.RoundResponseDTO;
import com.mtgleague.service.EventsService;
import com.mtgleague.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rounds")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;
    private final EventsService eventsService;

    @CacheEvict(cacheNames = "round", allEntries = true)
    @Transactional
    @PostMapping("/save")
    public ResponseEntity<?> confirmScore(@RequestBody RoundRequestDTO request) {
        Long eventId = roundService.confirmScore(request);
        if (roundService.isRoundEnded(eventId)) {
            eventsService.calculatePairings(eventId);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Cacheable(cacheNames = "round", key = "#request.playerId")
    @PostMapping("/current")
    public ResponseEntity<RoundResponseDTO> getCurrentRound(@RequestBody RoundRequestDTO request){
        RoundResponseDTO currentRound= roundService.getCurrentRound(request.getPlayerId());
        return new ResponseEntity<>(currentRound, HttpStatus.OK);
    }
}
