package com.mtgleague.controller;

import com.mtgleague.dto.response.GenericEntityListDTO;
import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Cacheable(cacheNames = "generalRank")
    @GetMapping()
    public ResponseEntity<GenericEntityListDTO<PlayerResponseDTO>> getGeneralRanking() {
        GenericEntityListDTO<PlayerResponseDTO> players= new GenericEntityListDTO(rankingService.getLatestGeneralRanking());
        return new ResponseEntity<>(players, HttpStatus.OK);
    }
}
