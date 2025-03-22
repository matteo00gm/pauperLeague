package com.mtgleague.service;

import com.mtgleague.dto.request.RoundRequestDTO;
import com.mtgleague.dto.request.event.EventRequestDTO;
import com.mtgleague.dto.request.player.PlayerEventRequestDTO;
import com.mtgleague.dto.response.event.*;
import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.dto.response.subs.EventSubsResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.logic.Pairing;
import com.mtgleague.logic.PlayerScore;
import com.mtgleague.model.*;
import com.mtgleague.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventsService {

    private final EventsRepository eventsRepository;

    private final EventRegistrationRepository eventRegistrationRepository;

    private final LeagueRepository leagueRepository;

    private final EventSubReqRepository eventSubReqRepository;

    private final RoundRepository roundRepository;

    private final RoundService roundService;

    private final PlayersService playersService;

    private final RankingService rankingService;

    private final Pairing pairing;

    public List<LeagueEventResponseDTO> findAll(Long leagueId){
        try {
            List<Event> results = eventsRepository.findAllOrdered(leagueId);

            List<LeagueEventResponseDTO> dtos= new ArrayList<>();
            results.forEach(event -> dtos.add(toLeagueEventDTO(event)));
            return dtos;
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare i tornei della lega, riprova più tardi..");
        }
    }

    public List<LeagueProgramResponseDTO> findUpcomingEvents(Long leagueId){
        try {
            List<Event> results;
            results = eventsRepository.findUpcomingEvents(leagueId);

            List<LeagueProgramResponseDTO> dtos= new ArrayList<>();
            results.forEach(event -> dtos.add(toLeagueProgramDto(event)));
            return dtos;
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare i tornei in programma della lega, riprova più tardi..");
        }
    }

    public List<MyEventResponseDTO> findPlayerEvents(Long playerId) {
        try {
            List<Event> results;
            results = eventsRepository.findPlayerEvents(playerId);

            List<MyEventResponseDTO> dtos= new ArrayList<>();
            results.forEach(event -> dtos.add(toMyEventDto(event)));
            return dtos;
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare i tuoi tornei, riprova più tardi..");
        }
    }

    public List<EventSubsResponseDTO> findPlayerSubbedEvents(Long playerId) {
        try {
            List<Event> results;
            results = eventSubReqRepository.findPlayerSubbedEvents(playerId);

            List<EventSubsResponseDTO> dtos= new ArrayList<>();
            results.forEach(event -> dtos.add(toEventSubsDTO(event)));
            return dtos;
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare le tue richieste di iscrizione, riprova più tardi..");
        }
    }

    public NewEventResponseDTO addEvent(EventRequestDTO event){
        try {
            Event newEvent = toEntity(event);
            newEvent.setLeague(leagueRepository.getReferenceById(event.getLeagueId()));
            return toNewEventDto(eventsRepository.save(newEvent));
        } catch (Exception ex) {
            throw new GenericException("Errore durante la creazione del torneo, riprova più tardi..");
        }
    }

    public void deleteEvent(Long eventId) {
        try {
            Event eventToDelete = eventsRepository.findById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Non abbiamo trovato il torneo selezionato.."));
            eventsRepository.delete(eventToDelete);
        } catch (NoSuchElementException ex) {
            throw new GenericException(ex.getMessage());
        } catch (Exception ex) {
            throw new GenericException("Errore durante la cancellazione del torneo, riprova più tardi..");
        }
    }

    public void editEvent(EventRequestDTO event) {
        try {
            Event eventToEdit = eventsRepository.findById(event.getEventId()).orElseThrow(() -> new NoSuchElementException("Non abbiamo trovato il torneo selezionato.."));
            Event editor = toEntity(event);
            eventToEdit.setName(editor.getName());
            eventToEdit.setCap(editor.getCap());
            eventToEdit.setDate(editor.getDate());
            eventToEdit.setDescription(editor.getDescription());
            eventsRepository.save(eventToEdit);
        } catch (NoSuchElementException ex) {
            throw new GenericException(ex.getMessage());
        } catch (Exception ex) {
            throw new GenericException("Errore durante la modifica del torneo, riprova più tardi..");
        }
    }

    public void dropEvent(Long eventId, Long playerId, Event event) {
        // Fetch event if null
        Event currentEvent = (event != null) ? event : findById(eventId);

        // Find EventRegistration
        EventRegistration registration = currentEvent.getEventRegistrations()
                .stream()
                .filter(er -> er.getPlayer().getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GenericException("Non abbiamo trovato la tua registrazione al torneo!"));

        // drop and save
        registration.setDrop(true);
        eventRegistrationRepository.save(registration);
    }

    public Event findById(Long id){
        try {
            return eventsRepository.getReferenceById(id);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare il torneo cercato, riprova più tardi..");
        }
    }

    public EventDetailsResponseDTO findByIdDTO(Long id){
        return toDetailsDto(findById(id));
    }

    private Event toEntity(EventRequestDTO eventRequestDTO) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(eventRequestDTO.getDate().toString(), inputFormatter);
            LocalDate date = zonedDateTime.toLocalDate();
            String formattedDate = outputFormatter.format(date);

            return new Event(eventRequestDTO.getName(), formattedDate, eventRequestDTO.getCap(), eventRequestDTO.getLocation());
        } catch (DateTimeParseException e) {
            throw new GenericException("Formato della data invalido. Formato atteso: Fri Oct 25 02:00:00 CEST 2024");
        }
    }

    public EventSubsResponseDTO saveEventSubReq(PlayerEventRequestDTO requestDTO) {
        boolean exists = eventSubReqRepository.findByPlayerAndEventId(requestDTO.getPlayerId(), requestDTO.getEventId()).isPresent();

        if (exists) {
            throw new GenericException("La tua richiesta è stata già inviata!");
        } else {
            Player player = playersService.findById(requestDTO.getPlayerId());
            Event event = findById(requestDTO.getEventId());
            return toEventSubsDTO(
                    eventSubReqRepository.save(
                            EventSubRequest.builder()
                                .player(player)
                                .event(event)
                                .build()
                    ).getEvent()
            );
        }
    }

    private LeagueEventResponseDTO toLeagueEventDTO(Event entity) {

        Set<BasicPlayerResponseDTO> players = getPlayers(entity.getEventRegistrations());

        Set<BasicPlayerResponseDTO> pendingSubs = getSubs(entity.getEventSubRequests());

        return new LeagueEventResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getCap(),
                entity.getDescription(),
                entity.isStarted(),
                entity.isEnded(),
                players,
                pendingSubs
        );
    }

    private NewEventResponseDTO toNewEventDto(Event entity) {

        return new NewEventResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getCap(),
                entity.getDescription()
        );
    }

    private EventSubsResponseDTO toEventSubsDTO(Event entity) {

        Set<BasicPlayerResponseDTO> players = getPlayers(entity.getEventRegistrations());

        Set<BasicPlayerResponseDTO> pendingSubs = getSubs(entity.getEventSubRequests());

        League league = entity.getLeague();

        return new EventSubsResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getCap(),
                entity.getDescription(),
                league.getId(),
                league.getName(),
                players,
                pendingSubs
        );
    }
    private MyEventResponseDTO toMyEventDto(Event entity) {

        Set<BasicPlayerResponseDTO> players = getPlayers(entity.getEventRegistrations());

        Set<BasicPlayerResponseDTO> pendingSubs = getSubs(entity.getEventSubRequests());

        League league = entity.getLeague();

        return new MyEventResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getCap(),
                entity.getDescription(),
                league.getId(),
                league.getName(),
                players,
                pendingSubs
        );
    }
    private LeagueProgramResponseDTO toLeagueProgramDto(Event entity) {

        Set<EventRegistrationResponseDTO> players = getRegisteredPlayers(entity.getEventRegistrations());

        Set<BasicPlayerResponseDTO> pendingSubs = getSortedSubs(entity.getEventSubRequests());

        //sort players
        players = sortRegistrationsDto(players);

        return new LeagueProgramResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.isStarted(),
                entity.getCap(),
                entity.getDescription(),
                players,
                pendingSubs
        );
    }

    private EventDetailsResponseDTO toDetailsDto(Event entity) {

        Set<EventRegistrationResponseDTO> registrations = getRegisteredPlayers(entity.getEventRegistrations());

        Set<BasicPlayerResponseDTO> pendingSubs = getSubs(entity.getEventSubRequests());

        //sort players
        registrations = sortRegistrationsDto(registrations);

        //sort subs
        pendingSubs = sortBasicPlayerDto(pendingSubs);

        // league Admins
        Set<BasicPlayerResponseDTO> leagueAdmins = entity.getLeague().getAdmins().stream()
                .map(this::toBasicPlayerDTO) // Use the helper method
                .collect(Collectors.toSet());

        return new EventDetailsResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getCap(),
                entity.getDescription(),
                entity.isStarted(),
                entity.isEnded(),
                entity.getRoundEndTime(),
                registrations,
                leagueAdmins,
                pendingSubs
        );
    }

    private Set<EventRegistrationResponseDTO> sortRegistrationsDto(Set<EventRegistrationResponseDTO> registrations) {
        return registrations.stream()
                .sorted(Comparator.comparing(EventRegistrationResponseDTO::getSurname, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toCollection(LinkedHashSet::new)); // Preserve order
    }

    private Set<BasicPlayerResponseDTO> sortBasicPlayerDto(Set<BasicPlayerResponseDTO> players) {
        return players.stream()
                .sorted(Comparator.comparing(BasicPlayerResponseDTO::getSurname, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toCollection(LinkedHashSet::new)); // Preserve order
    }

    private Set<BasicPlayerResponseDTO> getPlayers(Set<EventRegistration> registrations) {
        Set<Player> players = registrations.stream()
                .map(EventRegistration::getPlayer)
                .collect(Collectors.toSet());

        return players.stream()
                .filter(Objects::nonNull)
                .map(this::toBasicPlayerDTO) // Convert to BasicPlayerResponseDTO
                .collect(Collectors.toSet());
    }

    private Set<BasicPlayerResponseDTO> getSubs(Set<EventSubRequest> subs) {
        return subs.stream()
                .map(EventSubRequest::getPlayer)
                .filter(Objects::nonNull)
                .map(this::toBasicPlayerDTO) // Convert to BasicPlayerResponseDTO
                .collect(Collectors.toSet());
    }

    private Set<EventRegistrationResponseDTO> getRegisteredPlayers(Set<EventRegistration> er) {
        return er.stream()
                .filter(Objects::nonNull)
                .map(this::toEventRegistrationDTO) // Convert to BasicPlayerResponseDTO
                .collect(Collectors.toSet());
    }

    private Set<BasicPlayerResponseDTO> getSortedSubs(Set<EventSubRequest> subs) {
        return subs.stream()
                .sorted(Comparator.comparing(EventSubRequest::getId))
                .map(EventSubRequest::getPlayer)
                .filter(Objects::nonNull)
                .map(this::toBasicPlayerDTO) // Convert to BasicPlayerResponseDTO
                .collect(Collectors.toCollection(LinkedHashSet::new)); // Preserve order
    }

    private EventRegistrationResponseDTO toEventRegistrationDTO(EventRegistration eventRegistration) {
        Player player = eventRegistration.getPlayer();
        return new EventRegistrationResponseDTO(player.getId(), player.getName(), player.getSurname(), player.getEmail(), eventRegistration.isDrop());
    }

    private BasicPlayerResponseDTO toBasicPlayerDTO(Player player) {
        return new BasicPlayerResponseDTO(player.getId(), player.getName(), player.getSurname(), player.getEmail());
    }

    public Event registerPlayer(Long eventId, Player playerToSubscribe) {
        Event event= findById(eventId);
        try {
            EventRegistration registration = new EventRegistration(event, playerToSubscribe, false);
            EventRegistration er = eventRegistrationRepository.save(registration);
            return er.getEvent();
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti ad ammettere il giocatore al torneo, riprova più tardi..");
        }
    }

    public void kickPlayer(Long eventId, Long playerId) {
        Event event = findById(eventId);

        if(event.isStarted()) {
            //if the event has already started, then the admin will make the player drop
            dropEvent(eventId, playerId, event);
        } else {
            //if the event is not started yet, then the admin can kick him from the event
            EventRegistration registrationToDelete = eventRegistrationRepository.findByEventIdAndPlayerId(eventId, playerId)
                    .orElseThrow(() -> new GenericException("Il giocatore non risulta iscritto a questo torneo.."));

            eventRegistrationRepository.delete(registrationToDelete);
        }
    }

    public void startTimer(Long eventId) {
        Event event = findById(eventId);
        Date now = new Date(); // Current date and time

        // Add 50 minutes (50 * 60 * 1000 milliseconds)
        event.setRoundEndTime(new Date(now.getTime() + (50 * 60 * 1000)));
        eventsRepository.save(event);
    }

    public void calculatePairings(Long eventId){

        Event event = findById(eventId);
        try {
            LocalDate dateFromDb = LocalDate.parse(event.getDate(), DateTimeFormatter.ISO_DATE);
            LocalDate today = LocalDate.now();

            // Return if the event is not today
            if (!dateFromDb.isEqual(today)) {
                throw new GenericException("Il torneo non è oggi!");
            }

            // Check if max turn needs to be calculated
            if (event.getMaxTurn() == 0) {
                event.setMaxTurn(calcMaxTurn(event));
            }

            // Increment the current turn
            event.setCurrentTurn(event.getCurrentTurn() + 1);
            event.setRoundEndTime(null);

            calculatePairings(event);

            event.setStarted(true);
            eventsRepository.save(event);
        } catch (Exception ex) {
            throw new GenericException("C'è stato un errore durante il calcolo dei pairings, riprova più tardi..");
        }
    }

    private int calcMaxTurn(Event event) {
        int numberOfParticipants = event.getEventRegistrations().size();
        return (int) Math.ceil(Math.log(numberOfParticipants) / Math.log(2));
    }

    private void calculatePairings(Event event) {
        Long eventId = event.getId();
        Long leagueId = event.getLeague().getId();
        List<Player> players = event.getEventRegistrations().stream()
                .map(EventRegistration::getPlayer) // Extract Player objects
                .collect(Collectors.toList()); // Collect into a List

        List<Player> nonDropPlayers = event.getEventRegistrations().stream()
                .filter(registration -> !registration.isDrop()) // Exclude dropped players
                .map(EventRegistration::getPlayer) // Extract Player objects
                .collect(Collectors.toList()); // Collect into a List


        if (event.isStarted()) {
            //calculate event rankings of the previous round
            rankingService.invalidateEventRankings(eventId);
            rankingService.saveRankings(
                    rankingBuilder(eventId, leagueId, rankingCalc(players, eventId, null), true)
            );
        }

        if (event.getCurrentTurn() <= event.getMaxTurn()) {
            boolean eventStarted = event.isStarted();

            List<PlayerScore> activePlayers = calculatePlayersScores(eventStarted, nonDropPlayers, eventId, null);
            pairing.doPairings(activePlayers, event, event.getCurrentTurn());

        } else {
            rankingService.invalidateGeneralRankings();
            rankingService.invalidateLeagueRankings(leagueId);

            //calculate league rankings
            rankingService.saveRankings(
                    rankingBuilder(null, leagueId, rankingCalc(null, null, leagueId), true)
            );

            //calculate general rankings
            rankingService.saveRankings(
                    rankingBuilder(null, null, rankingCalc(null, null, null), true)
            );

            event.setEnded(true);
        }
    }

    private Ranking rankingBuilder(Long eventId, Long leagueId, List<PlayerResponseDTO> players, boolean isValid) {
        return Ranking.builder()
                .eventId(eventId)
                .leagueId(leagueId)
                .players(players)
                .isValid(isValid)
                .build();
    }

    public List<PlayerResponseDTO> rankingCalc(List<Player> playersInEvent, Long eventId, Long leagueId) {
        // Set eventId "null" to get the global ranking
        List<Player> results = null;
        League league = null;
        if(eventId != null) {
            results = playersInEvent;
        } else if (leagueId != null) {
            league = leagueRepository.findById(leagueId).orElseThrow(() -> new RuntimeException("League with id "+leagueId+" not found"));
            results = league.getPlayers().stream().toList();
        } else {
            results = playersService.findAll();
        }


        List<PlayerScore> playerScores = calculatePlayersScores(true, results, eventId, league);

        // using map for quick access to Player objects by ID
        Map<Long, Player> playerMap = results.stream()
                .collect(Collectors.toMap(Player::getId, player -> player));

        List<PlayerResponseDTO> result = playerScores.stream()
                .map(player -> {
                    Player matchedPlayer = playerMap.get(player.getId());
                    int eventsPlayed = (matchedPlayer != null) ? matchedPlayer.getEventRegistration().size() : 0;
                    player.setEventsPlayed(eventsPlayed);
                    return toPlayerDto(player);
                })
                .collect(Collectors.toList());

        return result;
    }


    //public because I'm using it in PlayersService for the rankList
    public List<PlayerScore> calculatePlayersScores(boolean started, List<Player> players, Long eventId, League league) {
        List<PlayerScore> playersScores;

        if (started) {
            List<Round> rounds;
            if (eventId != null) {
                rounds = roundService.findByEventId(eventId);
            } else if (league != null) {
                rounds = league.getEvents().stream()
                        .filter(event -> isInCurrentSeason(event.getDate())) //filtering to get only current season events
                        .flatMap(event -> roundService.findByEventId(event.getId()).stream())
                        .collect(Collectors.toList());
            } else {
                rounds = roundService.findRoundsInCurrentSeason(); //current season rounds
            }

            //grouping players past rounds by player id. if id P2 isnull, I won't consider it (those are bye, not players)
            Map<Long, List<Round>> playerRoundsMap = rounds.stream()
                    .flatMap(round -> Stream.of(
                            new AbstractMap.SimpleEntry<>(round.getIdP1(), round),
                            round.getIdP2() != null ? new AbstractMap.SimpleEntry<>(round.getIdP2(), round) : null
                    ))
                    .filter(Objects::nonNull)  // Remove null entries
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));


            //calculate pastRounds based on the grouped rounds
            playersScores = players.stream()
                    .map(player -> {
                        List<Round> pastRounds = playerRoundsMap.getOrDefault(player.getId(), Collections.emptyList());
                        return calculatePastRounds(buildPlayerScore(player.getId(), player.getName(), player.getSurname()), pastRounds);
                    })
                    .collect(Collectors.toList());

            // Sort the list of active players based on the actual score + the 3-step rules to break a tie
            pairing.sortPlayers(playersScores);
        } else {
            //if it's the first rounds we just shuffle the player to make pairs
            playersScores = players.stream()
                    .map(player -> buildPlayerScore(player.getId(), player.getName(), player.getSurname()))
                    .collect(Collectors.toList());

            Collections.shuffle(playersScores);
        }

        return playersScores;
    }

    private static boolean isInCurrentSeason(String dateStr) {
        LocalDate eventDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        // Define season boundaries
        LocalDate autumnStart = LocalDate.of(currentYear, 6, 2);   // 2 June
        LocalDate autumnEnd = LocalDate.of(currentYear, 11, 10);   // 10 November
        LocalDate winterStart = LocalDate.of(currentYear, 11, 11); // 11 November
        LocalDate winterEnd = LocalDate.of(currentYear + 1, 2, 23); // 23 February (next year)
        LocalDate springStart = LocalDate.of(currentYear, 2, 24);  // 24 February
        LocalDate springEnd = LocalDate.of(currentYear, 6, 1);     // 1 June

        // Check the current season based on today's date
        if (!today.isBefore(autumnStart) && today.isBefore(autumnEnd.plusDays(1))) {
            // Autumn Season: 2 June to 10 November
            return !eventDate.isBefore(autumnStart) && !eventDate.isAfter(autumnEnd);
        } else if (!today.isBefore(winterStart) && today.isBefore(winterEnd.plusDays(1))) {
            // Winter Season: 11 November to 23 February (crosses year boundary)
            return !eventDate.isBefore(winterStart) && !eventDate.isAfter(winterEnd);
        } else if (!today.isBefore(springStart) && today.isBefore(springEnd.plusDays(1))) {
            // Spring Season: 24 February to 1 June
            return !eventDate.isBefore(springStart) && !eventDate.isAfter(springEnd);
        }

        return false;
    }


    private PlayerScore buildPlayerScore(Long id, String name, String surname){
        return PlayerScore
                .builder()
                .id(id)
                .name(name)
                .surname(surname)
                .build();
    }

    private PlayerScore calculatePastRounds(PlayerScore playerScore, List<Round> rounds) {

        Set<Long> opponents = new HashSet<>();

        rounds.forEach(round -> {
            boolean isPlayerP1 = round.getIdP1().equals(playerScore.getId());
            int playerWins = isPlayerP1 ? round.getP1Wins() : round.getP2Wins();
            int opponentWins = isPlayerP1 ? round.getP2Wins() : round.getP1Wins();
            Long opponentId = isPlayerP1 ? round.getIdP2() : round.getIdP1();

            // General stats updates
            playerScore.setMatchPlayed(playerScore.getMatchPlayed() + 1);
            playerScore.setGameWin(playerScore.getGameWin() + playerWins);
            playerScore.setGamePlayed(playerScore.getGamePlayed() + playerWins + opponentWins);

            // Non-bye specific updates
            if (!round.isBye()) {
                playerScore.setMatchPlayedWithoutBye(playerScore.getMatchPlayedWithoutBye() + 1);
                playerScore.setGameWinWithoutBye(playerScore.getGameWinWithoutBye() + playerWins);
                playerScore.setGamePlayedWithoutBye(playerScore.getGamePlayedWithoutBye() + playerWins + opponentWins);
                opponents.add(opponentId);
            }

            // Match outcome updates
            if (playerWins > opponentWins) {
                playerScore.setMatchWin(playerScore.getMatchWin() + 1);
                if (!round.isBye()) {
                    playerScore.setMatchWinWithoutBye(playerScore.getMatchWinWithoutBye() + 1);
                }
            } else if (playerWins == opponentWins) {
                playerScore.setMatchDraw(playerScore.getMatchDraw() + 1);
            }
        });

        playerScore.setOpponentsIds(opponents);
        return playerScore;
    }


    private PlayerResponseDTO toPlayerDto(PlayerScore entity){
        PlayerResponseDTO dto= PlayerResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .matchWinRate(entity.getMatchWinRate())
                .score(entity.getScore())
                .eventsPlayed(entity.getEventsPlayed())
                .omw(entity.getOmw())
                .gw(entity.getGw())
                .ogw(entity.getOgw())
                .build();
        return dto;
    }
}
