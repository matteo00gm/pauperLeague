package com.mtgleague.service;

import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import com.mtgleague.dto.response.player.PlayerProfileResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.Player;
import com.mtgleague.repo.PlayersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PlayersService {

    private final PlayersRepository playersRepository;

    public List<Player> findAll() {
        try {
            return playersRepository.findAll();
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare i giocatori esistenti, riprova più tardi..");
        }
    }

    public Player findById(Long id){
        try {
            return playersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Player with id "+id+" not found"));
        } catch (NoSuchElementException ex) {
            throw new GenericException("Non siamo riusciti a trovare il giocatore selezionato, riprova più tardi..");
        }
    }

    public boolean isAdminOfLeague(String playerId, String leagueId) {
        try {
            List<Player> admins = playersRepository.getLeagueAdmins(Long.parseLong(leagueId));

            return admins.stream()
                    .anyMatch(admin -> admin.getId().equals(Long.parseLong(playerId)));
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare gli amministratori della lega");
        }
    }

    public PlayerProfileResponseDTO findPlayer(Long id) {
        Player entity = findById(id);
        return toPlayerProfileDTO(entity);
    }

    private PlayerProfileResponseDTO toPlayerProfileDTO(Player entity) {
        return PlayerProfileResponseDTO.builder()
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .leagues(entity.getSubscriptions()).build();
    }
}
