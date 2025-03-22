package com.mtgleague.controller;

import com.mtgleague.dto.request.event.EventIdRequestDTO;
import com.mtgleague.dto.request.event.EventRequestDTO;
import com.mtgleague.dto.request.league.LeagueIdRequestDTO;
import com.mtgleague.dto.request.player.PlayerEventRequestDTO;
import com.mtgleague.dto.request.player.PlayerLeagueRequestDTO;
import com.mtgleague.dto.response.event.EventDetailsResponseDTO;
import com.mtgleague.dto.response.GenericEntityListDTO;
import com.mtgleague.dto.response.event.MyEventResponseDTO;
import com.mtgleague.dto.response.event.NewEventResponseDTO;
import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.dto.response.subs.EventSubsResponseDTO;
import com.mtgleague.service.EventsService;
import com.mtgleague.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;
    private final RankingService rankingService;

    @Cacheable(cacheNames = "eventRank", key = "#eventId")
    @GetMapping("/{eventId}/ranking")
    public ResponseEntity<GenericEntityListDTO<PlayerResponseDTO>> getEventRanks(@PathVariable("eventId") Long eventId) {
        GenericEntityListDTO<PlayerResponseDTO> players= new GenericEntityListDTO(rankingService.getEventRank(eventId));
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @Cacheable(cacheNames = "event", key = "#eventId")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsResponseDTO> findEventById(@PathVariable("eventId") Long eventId){
        EventDetailsResponseDTO event= eventsService.findByIdDTO(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }


    @Cacheable(cacheNames = "myEvents", key = "#request.playerId")
    @PostMapping("/my-events")
    public ResponseEntity<GenericEntityListDTO<MyEventResponseDTO>> findMyEvents(@RequestBody PlayerLeagueRequestDTO request){
        GenericEntityListDTO<MyEventResponseDTO> events= new GenericEntityListDTO(eventsService.findPlayerEvents(request.getPlayerId()));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leagueProgram", key = "#eventRequestDTO.leagueId"),
            @CacheEvict(cacheNames = "leagueEvents", key = "#eventRequestDTO.leagueId")
    })
    @PostMapping("/new")
    public ResponseEntity<NewEventResponseDTO> createEvent(@RequestBody EventRequestDTO eventRequestDTO){
        NewEventResponseDTO event = eventsService.addEvent(eventRequestDTO);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leagueProgram", key = "#eventIdRequestDTO.leagueId"),
            @CacheEvict(cacheNames = "leagueEvents", key = "#eventIdRequestDTO.leagueId"),
            @CacheEvict(cacheNames = "myEvents", allEntries = true)
    })
    @PostMapping("/delete")
    public ResponseEntity<?> deleteEvent(@RequestBody EventIdRequestDTO eventIdRequestDTO){
        eventsService.deleteEvent(eventIdRequestDTO.getEventId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "event", key = "#request.eventId"),
            @CacheEvict(cacheNames = "myEventSubs", key = "#request.playerId"),
            @CacheEvict(cacheNames = "leagueProgram", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "leagueEvents", key = "#request.leagueId")
    })
    @PostMapping("/register") //ritorno l'intero evento perchè lo aggiungo a "my events" in vuex così da averlo a disposizione anche se ho la request in pending
    public ResponseEntity<EventSubsResponseDTO> saveLeagueSubReq(@RequestBody PlayerEventRequestDTO request){
        EventSubsResponseDTO newEvent = eventsService.saveEventSubReq(request);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "event", key = "#eventId"),
            @CacheEvict(cacheNames = "myEvents", key = "#request.playerId"),
            @CacheEvict(cacheNames = "leagueProgram", key = "#request.leagueId"),
            @CacheEvict(cacheNames = "leagueEvents", key = "#request.leagueId"),
    })
    @PostMapping("/{eventId}/kick")
    public ResponseEntity<?> kickPlayer(@PathVariable("eventId") Long eventId, @RequestBody PlayerLeagueRequestDTO request){
        eventsService.kickPlayer(eventId, request.getPlayerId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "round", allEntries = true),
            @CacheEvict(cacheNames = "event", key = "#request.eventId"),
    })
    @PostMapping("/drop")
    public ResponseEntity<?> dropEvent(@RequestBody PlayerEventRequestDTO request){
        eventsService.dropEvent(request.getEventId(), request.getPlayerId(), null);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "event", key = "#eventId"),
            @CacheEvict(cacheNames = "round", allEntries = true),
    })
    @PostMapping("/{eventId}/start")    //leagueId is used for the admin check in the filter
    public ResponseEntity<?> startEvent(@PathVariable("eventId") Long eventId, @RequestBody LeagueIdRequestDTO leagueId){
        eventsService.calculatePairings(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "event", key = "#eventId"),
            @CacheEvict(cacheNames = "round", allEntries = true),
    })
    @PostMapping("/{eventId}/startTimer")
    public ResponseEntity<?> startTimer(@PathVariable("eventId") Long eventId, @RequestBody LeagueIdRequestDTO leagueId){
        eventsService.startTimer(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "event", key = "#event.eventId"),
            @CacheEvict(cacheNames = "myEvents", allEntries = true),
            @CacheEvict(cacheNames = "leagueProgram", allEntries = true),
            @CacheEvict(cacheNames = "leagueEvents", allEntries = true)
    })
    @PostMapping("/edit")
    public ResponseEntity<?> editEvent(@RequestBody EventRequestDTO event) {
        eventsService.editEvent(event);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
