package com.mtgleague.service;

import com.mtgleague.dto.request.player.PlayerLeagueRequestDTO;
import com.mtgleague.dto.response.SubReqResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.*;
import com.mtgleague.repo.EventSubReqRepository;
import com.mtgleague.repo.LeagueSubReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SubReqService {

    private final LeagueSubReqRepository leagueSubReqRepository;
    private final EventSubReqRepository eventSubReqRepository;
    private final LeagueService leagueService;
    private final PlayersService playersService;

    public void saveLeagueSubReq(PlayerLeagueRequestDTO requestDTO) {
        boolean exists = leagueSubReqRepository.findByPlayerAndLeagueId(requestDTO.getPlayerId(), requestDTO.getLeagueId()).isPresent();

        if (exists) {
            throw new GenericException("La tua richiesta è stata già inviata!");
        } else {
            Player player = playersService.findById(requestDTO.getPlayerId());
            League league = leagueService.findById(requestDTO.getLeagueId());
            leagueSubReqRepository.save(LeagueSubRequest.builder()
                    .player(player)
                    .league(league)
                    .build()
            );
        }
    }

    @Transactional
    public void deleteSubReq(League league, Player player) {
        LeagueSubRequest subReq = leagueSubReqRepository.findByPlayerAndLeagueId(player.getId(), league.getId())
                .orElseThrow(() -> new GenericException("Impossibile eliminare: non abbiamo trovato la richiesta di ammissione selezionata!"));

        // Remove the association between Player and SubRequest
        player.getLeagueSubRequests().remove(subReq);
        league.getLeagueSubRequests().remove(subReq);

        // Delete the SubRequest
        leagueSubReqRepository.delete(subReq);
    }

    @Transactional
    public void deleteSubReq(Event event, Player player) {
        EventSubRequest subReq = eventSubReqRepository.findByPlayerAndEventId(player.getId(), event.getId())
                .orElseThrow(() -> new GenericException("Impossibile eliminare: non abbiamo trovato la richiesta di ammissione selezionata!"));

        // Remove the association between Player and SubRequest
        player.getEventSubRequests().remove(subReq);
        event.getEventSubRequests().remove(subReq);

        // Delete the SubRequest
        eventSubReqRepository.delete(subReq);
    }



    public List<SubReqResponseDTO> findByLeagueId(Long leagueId) {
        try {
            List<SubReqResponseDTO> subRequests = leagueSubReqRepository.findByLeagueId(leagueId).stream()
                    .map(this::toDtoLeague)
                    .collect(Collectors.toList());
            return subRequests;
        } catch (NoSuchElementException ex) {
            throw new GenericException("Non siamo riusciti a trovare le richieste di ammissione alla tua lega, riprova più tardi..");
        }
    }


    private SubReqResponseDTO toDtoLeague(LeagueSubRequest leagueSubRequest) {
        return SubReqResponseDTO.builder()
                .playerId(leagueSubRequest.getPlayer().getId())
                .playerName(leagueSubRequest.getPlayer().getName())
                .playerSurname(leagueSubRequest.getPlayer().getSurname())
                .build();
    }

}
