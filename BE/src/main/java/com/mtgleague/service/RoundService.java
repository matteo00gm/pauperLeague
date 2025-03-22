package com.mtgleague.service;

import com.mtgleague.dto.request.RoundRequestDTO;
import com.mtgleague.dto.response.RoundResponseDTO;
import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.Event;
import com.mtgleague.model.Round;
import com.mtgleague.repo.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;

    private final RankingService rankingService;

    public Round findById(Long id){
        try {
            return roundRepository.getReferenceById(id);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare il tuo match, riprova più tardi..");
        }
    }

    public List<Round> findRoundsInCurrentSeason() {
        return roundRepository.findRoundsInCurrentSeason();
    }

    public List<Round> findByEventId(Long eventId){
        try {
            return roundRepository.findByEventId(eventId);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare i match del torneo selezionato, riprova più tardi..");
        }
    }

    public Round createRound(Long idPlayer1, String nameP1, String surnameP1, Long idPlayer2, String nameP2, String surnameP2, int p1wins, int p2Wins, Event event, int turn, boolean ended){
        try {
            return roundRepository.save(
                    Round.builder()
                            .idP1(idPlayer1)
                            .nameP1(nameP1)
                            .surnameP1(surnameP1)
                            .idP2(idPlayer2)
                            .nameP2(nameP2)
                            .surnameP2(surnameP2)
                            .p1Wins(p1wins)
                            .p2Wins(p2Wins)
                            .event(event)
                            .turn(turn)
                            .ended(ended)
                            .build()
            );
        } catch (Exception ex) {
            throw new GenericException("Errore durante la creazione del match");
        }
    }

    public Long confirmScore(RoundRequestDTO round){
        Round roundToSave = findById(round.getRoundId());
        if (roundToSave.isEnded()) {
            throw new GenericException("Il risultato è già stato inviato!");
        }
        //only the players playing this match can save the score
        if(!(round.getPlayerId().equals(roundToSave.getIdP1()) || round.getPlayerId().equals(roundToSave.getIdP2()))){
            throw new GenericException("Non fai parte di questo match.");
        }
        roundToSave.setP1Wins(round.getP1Wins());
        roundToSave.setP2Wins(round.getP2Wins());
        roundToSave.setEnded(true);
        return roundRepository.save(roundToSave).getEvent().getId();
    }

    public boolean isRoundEnded(Long eventId){
        List<Round> currentRounds= roundRepository.findCurrentByEventId(eventId);
        return currentRounds.size()==0;
    }

    public RoundResponseDTO getCurrentRound(Long playerId){
        try {
            Round currentRound= roundRepository.findCurrentByPlayerId(playerId).orElseThrow(() -> new NoSuchElementException("No current round exists for player with id "+playerId));
            return toDto(currentRound);
        } catch (NoSuchElementException ex) {
            throw new GenericException("Non hai un match da giocare per ora");
        }
    }

    private RoundResponseDTO toDto(Round round){
        Event event = round.getEvent();
        List<PlayerResponseDTO> previousRoundRankings = rankingService.getEventRank(event.getId());

        return RoundResponseDTO.builder()
                .id(round.getId())
                .nameP1(round.getNameP1())
                .nameP2(round.getNameP2())
                .surnameP1(round.getSurnameP1())
                .surnameP2(round.getSurnameP2())
                .p1Wins(round.getP1Wins())
                .p2Wins(round.getP2Wins())
                .ended(round.isEnded())
                .roundEndTime(event.getRoundEndTime())
                .previousRoundRankings(previousRoundRankings)
                .build();
    }
}
