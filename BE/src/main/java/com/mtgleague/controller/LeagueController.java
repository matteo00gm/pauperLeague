package com.mtgleague.controller;

import com.mtgleague.dto.request.player.PlayerLeagueRequestDTO;
import com.mtgleague.dto.request.league.CreateLeagueRequestDTO;
import com.mtgleague.dto.response.GenericEntityListDTO;
import com.mtgleague.dto.response.event.LeagueEventResponseDTO;
import com.mtgleague.dto.response.event.LeagueProgramResponseDTO;
import com.mtgleague.dto.response.league.CreateLeagueResponseDTO;
import com.mtgleague.dto.response.league.LeagueResponseDTO;
import com.mtgleague.dto.response.league.SelectedLeagueResponseDTO;
import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.model.League;
import com.mtgleague.model.Player;
import com.mtgleague.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final PlayersService playersService;
    private final RankingService rankingService;
    private final EventsService eventsService;
    private final SubReqService subReqService;

    @Cacheable(cacheNames = "leagues")
    @GetMapping()
    public ResponseEntity<GenericEntityListDTO<LeagueResponseDTO>> getLeagues() {
        GenericEntityListDTO<LeagueResponseDTO> leagues = new GenericEntityListDTO<>(leagueService.findAll());
        return new ResponseEntity<>(leagues, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "league", key = "#leagueId")
    @GetMapping("/{leagueId}")
    public ResponseEntity<SelectedLeagueResponseDTO> findLeague(@PathVariable("leagueId") Long leagueId) {
        SelectedLeagueResponseDTO league = leagueService.getLeagueResponseById(leagueId);
        return new ResponseEntity<>(league, HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leagues", allEntries = true),
            @CacheEvict(cacheNames = "myLeagues", key = "#createLeagueRequestDTO.ownerId")
    })
    @PostMapping("/new")
    public ResponseEntity<CreateLeagueResponseDTO> createLeague(@RequestBody CreateLeagueRequestDTO createLeagueRequestDTO) {
        CreateLeagueResponseDTO league = leagueService.addLeague(createLeagueRequestDTO);
        return new ResponseEntity<>(league, HttpStatus.CREATED);
    }

    @Cacheable(cacheNames = "myLeagues", key = "#playerId.playerId")
    @PostMapping("/my-subs") //returning entity, because I need both players and admins
    public ResponseEntity<GenericEntityListDTO<League>> findMySubbedLeagues(@RequestBody PlayerLeagueRequestDTO playerId){
        GenericEntityListDTO<League> leagues= new GenericEntityListDTO(leagueService.findPlayerLeagues(playerId.getPlayerId()));
        return new ResponseEntity<>(leagues, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "leagueRank", key = "#leagueId")
    @GetMapping("/{leagueId}/ranking")
    public ResponseEntity<GenericEntityListDTO<PlayerResponseDTO>> getLeagueRanking(@PathVariable("leagueId") Long leagueId) {
        GenericEntityListDTO<PlayerResponseDTO> players= new GenericEntityListDTO(rankingService.getLatestLeagueRanking(leagueId));
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "leagueEvents", key = "#leagueId")
    @GetMapping("/{leagueId}/events")
    public ResponseEntity<GenericEntityListDTO<LeagueEventResponseDTO>> getLeagueEvents(@PathVariable("leagueId") Long leagueId){
        GenericEntityListDTO<LeagueEventResponseDTO> events = new GenericEntityListDTO<>(eventsService.findAll(leagueId));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "leagueProgram", key = "#leagueId")
    @GetMapping("/{leagueId}/program")
    public ResponseEntity<GenericEntityListDTO<LeagueProgramResponseDTO>> getProgrammedEvents(@PathVariable("leagueId") Long leagueId){
        GenericEntityListDTO<LeagueProgramResponseDTO> events= new GenericEntityListDTO(eventsService.findUpcomingEvents(leagueId));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leagueSubs", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "league", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "leagues", allEntries = true),
    })
    @PostMapping("/register")
    public ResponseEntity<?> saveLeagueSubReq(@RequestBody PlayerLeagueRequestDTO request){
        subReqService.saveLeagueSubReq(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @CacheEvict(cacheNames = {"myLeagues", "leagues"}, allEntries = true)
    @PostMapping("/removeMember")
    public ResponseEntity<?> removePlayer(@RequestBody PlayerLeagueRequestDTO request) {
        Player playerToRemove = playersService.findById(request.getPlayerId());
        League league = leagueService.findById(request.getLeagueId());
        leagueService.removeMemberFromLeague(league, playerToRemove);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @CacheEvict(cacheNames = {"myLeagues", "leagues"}, allEntries = true)
    @PostMapping("/leave")
    public ResponseEntity<?> leaveLeague(@RequestBody PlayerLeagueRequestDTO request){
        Player playerToRemove= playersService.findById(request.getPlayerId());
        League league = leagueService.findById(request.getLeagueId());
        leagueService.removePlayerFromLeague(league, playerToRemove);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}