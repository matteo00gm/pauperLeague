package com.mtgleague.controller;

import com.mtgleague.dto.request.SubReqRequestDTO;
import com.mtgleague.dto.request.league.LeagueIdRequestDTO;
import com.mtgleague.dto.request.player.PlayerIdRequestDTO;
import com.mtgleague.dto.response.GenericEntityListDTO;
import com.mtgleague.dto.response.SubReqResponseDTO;
import com.mtgleague.dto.response.subs.EventSubsResponseDTO;
import com.mtgleague.model.Event;
import com.mtgleague.model.League;
import com.mtgleague.model.Player;
import com.mtgleague.service.EventsService;
import com.mtgleague.service.LeagueService;
import com.mtgleague.service.PlayersService;
import com.mtgleague.service.SubReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subs")
@RequiredArgsConstructor
public class SubRequestsController {

    private final SubReqService subReqService;
    private final PlayersService playersService;
    private final LeagueService leagueService;
    private final EventsService eventsService;

    @Cacheable(cacheNames = "leagueSubs", key = "#leagueId.leagueId")
    @PostMapping("/league")
    public ResponseEntity<GenericEntityListDTO<List<SubReqResponseDTO>>> findSubsByLeagueId(@RequestBody LeagueIdRequestDTO leagueId){
        GenericEntityListDTO<List<SubReqResponseDTO>> league= new GenericEntityListDTO(subReqService.findByLeagueId(leagueId.getLeagueId()));
        return new ResponseEntity<>(league, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "myEventSubs", key = "#request.playerId")
    @PostMapping("/my-subs")
    public ResponseEntity<GenericEntityListDTO<EventSubsResponseDTO>> findMyEventSubs(@RequestBody PlayerIdRequestDTO request){
        GenericEntityListDTO<EventSubsResponseDTO> events= new GenericEntityListDTO(eventsService.findPlayerSubbedEvents(request.getPlayerId()));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leagueSubs", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "myLeagues", key = "#request.playerId"),
            @CacheEvict(cacheNames = "league", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "profile", key = "#request.playerId"),
            @CacheEvict(cacheNames = "leagues", allEntries = true)
    })
    @PostMapping("/league/accept")
    @Transactional  //using player in vuex
    public ResponseEntity<Player> acceptLeagueSubRequest(@RequestBody SubReqRequestDTO request){
        Player playerToSubscribe= playersService.findById(request.getPlayerId());
        League league = leagueService.registerPlayer(request.getLeagueId(), playerToSubscribe);
        subReqService.deleteSubReq(league, playerToSubscribe);
        return new ResponseEntity<>(playerToSubscribe, HttpStatus.CREATED);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "myEvents", key = "#request.playerId"),
            @CacheEvict(cacheNames = "myEventSubs", key = "#request.playerId"),
            @CacheEvict(cacheNames = "event", key = "#request.eventId"),
            @CacheEvict(cacheNames = "leagueProgram", key = "#request.leagueId")
    })
    @PostMapping("/event/accept")
    @Transactional  //using player in vuex
    public ResponseEntity<Player> acceptEventSubRequest(@RequestBody SubReqRequestDTO request){
        Player playerToSubscribe= playersService.findById(request.getPlayerId());
        Event event = eventsService.registerPlayer(request.getEventId(), playerToSubscribe);
        subReqService.deleteSubReq(event, playerToSubscribe);
        return new ResponseEntity<>(playerToSubscribe, HttpStatus.CREATED);
    }

    @CacheEvict(cacheNames = "leagueSubs", key = "#request.leagueId")
    @PostMapping("/league/deny")
    public ResponseEntity<?> denyLeagueSubRequest(@RequestBody SubReqRequestDTO request){
        Player player= playersService.findById(request.getPlayerId());
        League league = leagueService.findById(request.getLeagueId());
        subReqService.deleteSubReq(league, player);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = "myEventSubs", key = "#request.playerId"),
            @CacheEvict(cacheNames = "event", key = "#request.eventId"),
            @CacheEvict(cacheNames = "leagueProgram", key = "#request.leagueId")
    })
    @PostMapping("/event/deny")
    public ResponseEntity<?> denyEventSubRequest(@RequestBody SubReqRequestDTO request){
        Player player= playersService.findById(request.getPlayerId());
        Event event = eventsService.findById(request.getEventId());
        subReqService.deleteSubReq(event, player);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
