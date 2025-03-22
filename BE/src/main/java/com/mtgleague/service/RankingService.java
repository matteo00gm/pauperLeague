package com.mtgleague.service;

import com.mtgleague.dto.response.player.PlayerResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.Ranking;
import com.mtgleague.repo.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public void saveRankings(Ranking ranking){
        try {
            rankingRepository.save(ranking);
        } catch (Exception ex) {
            throw new GenericException("Errore durante il salvataggio della classifica, riprova più tardi..");
        }
    }

    public List<PlayerResponseDTO> getEventRank(Long eventId) {
        try {
            List<Ranking> rankings = rankingRepository.findByEventId(eventId);
            return rankings.isEmpty() ? null : rankings.get(0).getPlayers();
        } catch (Exception ex) {
            throw new GenericException("Non abbiamo trovato la classifica del torneo aggiornata, riprova più tardi..");
        }
    }

    public List<PlayerResponseDTO> getLatestGeneralRanking() {
        try {
            List<Ranking> latestGeneralRanking = rankingRepository.getLatestGeneralRanking();
            return latestGeneralRanking.isEmpty() ? null : latestGeneralRanking.get(0).getPlayers();
        } catch (Exception ex) {
            throw new GenericException("Non abbiamo trovato la classifica generale aggiornata, riprova più tardi..");
        }
    }

    public List<PlayerResponseDTO> getLatestLeagueRanking(Long leagueId) {
        try {
            List<Ranking> latestLeagueRanking = rankingRepository.getLatestLeagueRanking(leagueId);
            return latestLeagueRanking.isEmpty() ? null : latestLeagueRanking.get(0).getPlayers();
        } catch (Exception ex) {
            throw new GenericException("Non abbiamo trovato la classifica della lega aggiornata, riprova più tardi..");
        }
    }


    @CacheEvict(cacheNames = "generalRank")
    public void invalidateGeneralRankings() {
        rankingRepository.invalidateGeneralRankings();
    }

    @CacheEvict(cacheNames = "leagueRank", key = "#leagueID")
    public void invalidateLeagueRankings(Long leagueID) {
        rankingRepository.invalidateLeagueRankings(leagueID);
    }

    @CacheEvict(cacheNames = "eventRank", key = "#eventID")
    public void invalidateEventRankings(Long eventID) {
        rankingRepository.invalidateEventRankings(eventID);
    }

}
