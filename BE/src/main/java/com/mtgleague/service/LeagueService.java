package com.mtgleague.service;

import com.mtgleague.dto.request.league.CreateLeagueRequestDTO;
import com.mtgleague.dto.response.league.CreateLeagueResponseDTO;
import com.mtgleague.dto.response.league.LeagueResponseDTO;
import com.mtgleague.dto.response.league.SelectedLeagueResponseDTO;
import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import com.mtgleague.exception.GenericException;
import com.mtgleague.model.*;
import com.mtgleague.repo.LeagueRepository;
import com.mtgleague.repo.PlayersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final PlayersRepository playersRepository;

    public List<LeagueResponseDTO> findAll(){
        try {
            return toLeagueDTO(leagueRepository.findAllByOrderByNameAsc());
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare le leghe esistenti, riprova più tardi..");
        }
    }

    public League findById(Long leagueId) {
        try {
            return leagueRepository.getReferenceById(leagueId);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare la lega cercata, riprova più tardi..");
        }
    }

    public SelectedLeagueResponseDTO getLeagueResponseById(Long leagueId) {
        try {
            return toSelectedLeagueDTO(findById(leagueId));
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare la lega cercata, riprova più tardi..");
        }
    }

    public List<League> findPlayerLeagues(Long id) {
        try {
            return leagueRepository.findPlayerLeagues(id);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a trovare le leghe del giocatore selezionato, riprova più tardi..");
        }
    }

    public CreateLeagueResponseDTO addLeague(CreateLeagueRequestDTO createLeagueRequestDTO){
        try {
            return toCreateLeagueDTO(leagueRepository.save(toEntity(createLeagueRequestDTO)));
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a creare la tua lega, riprova più tardi..");
        }
    }

    public League registerPlayer(Long leagueId, Player playerToSubscribe) {
        League league= findById(leagueId);
        try {
            Set<Player> playersSubscribed= league.getPlayers();
            playersSubscribed.add(playerToSubscribe);
            league.setPlayers(playersSubscribed);
            return leagueRepository.save(league);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti ad ammettere il giocatore alla tua lega, riprova più tardi..");
        }
    }

    @Transactional
    public void removeMemberFromLeague(League league, Player player) {
        try {
            if(league.getAdmins().contains(player)){
                throw new RuntimeException("Non è possibile rimuovere un amministratore dalla lega.");
            }
            league.getPlayers().remove(player);
            leagueRepository.save(league);
        } catch (RuntimeException ex) {
            throw new GenericException(ex.getMessage());
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti ad escludere il giocatore dalla lega, riprova più tardi..");
        }
    }

    @Transactional
    public void removePlayerFromLeague(League league, Player player) {
        try {
            league.getPlayers().remove(player);
            league.getAdmins().remove(player);
            leagueRepository.save(league);
        } catch (Exception ex) {
            throw new GenericException("Non siamo riusciti a rimuoverti dalla lega, riprova più tardi..");
        }
    }

    //used when creating league only
    private League toEntity(CreateLeagueRequestDTO createLeagueRequestDTO) {
        //saving the player who created the league both as player and admin
        Set<Player> owner = new HashSet<>();
        owner.add(playersRepository.getReferenceById(createLeagueRequestDTO.getOwnerId()));
        return League.builder()
                .name(createLeagueRequestDTO.getLeagueName())
                .description(createLeagueRequestDTO.getDescription())
                .players(owner)
                .admins(owner)
                .build();
    }

    private List<LeagueResponseDTO> toLeagueDTO(List<League> leagues) {
        List<LeagueResponseDTO> dtos = new ArrayList<>();


        leagues.forEach(
                league -> {

                    dtos.add(
                            LeagueResponseDTO.builder()
                                    .id(league.getId())
                                    .name(league.getName())
                                    .description(league.getDescription())
                                    .admins(toBasicPlayerDTO(league.getAdmins()))
                                    .players(toBasicPlayerDTO(league.getPlayers()))
                                    .leagueSubRequests(subToBasicPlayerDTO(league.getLeagueSubRequests()))
                                    .build()
                    );
                }
        );
        return dtos;
    }

    private Set<BasicPlayerResponseDTO> subToBasicPlayerDTO(Set<LeagueSubRequest> subs) {
        return subs.stream()
                .map(this::toBasicPlayerDTO) // Use the helper method
                .collect(Collectors.toSet());
    }

    private BasicPlayerResponseDTO toBasicPlayerDTO(LeagueSubRequest sub) {
        Player player = sub.getPlayer();
        return new BasicPlayerResponseDTO(player.getId());
    }

    private Set<BasicPlayerResponseDTO> toBasicPlayerDTO(Set<Player> players) {
        return players.stream()
                .map(this::toBasicPlayerDTO) // Use the helper method
                .collect(Collectors.toSet());
    }

    private BasicPlayerResponseDTO toBasicPlayerDTO(Player player) {
        return new BasicPlayerResponseDTO(player.getId(), player.getName(), player.getSurname(), player.getEmail());
    }

    private SelectedLeagueResponseDTO toSelectedLeagueDTO(League league) {
        // Convert admins to Set of DTOs
        Set<BasicPlayerResponseDTO> admins = league.getAdmins().stream()
                .map(admin -> BasicPlayerResponseDTO.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .surname(admin.getSurname())
                        .email(admin.getEmail())
                        .build())
                .collect(Collectors.toSet());

        // Convert players to List of DTOs, excluding those in admins, and sorted by surname
        List<BasicPlayerResponseDTO> players = league.getPlayers().stream()
                .filter(player -> league.getAdmins().stream().noneMatch(admin -> admin.getId().equals(player.getId())))
                .map(player -> BasicPlayerResponseDTO.builder()
                        .id(player.getId())
                        .name(player.getName())
                        .surname(player.getSurname())
                        .email(player.getEmail())
                        .build())
                .sorted(Comparator.comparing(BasicPlayerResponseDTO::getSurname))
                .collect(Collectors.toList());

        return SelectedLeagueResponseDTO.builder()
                .id(league.getId())
                .name(league.getName())
                .description(league.getDescription())
                .players(players)
                .admins(admins)
                .build();
    }



    private CreateLeagueResponseDTO toCreateLeagueDTO(League league) {
        Set<BasicPlayerResponseDTO> players = new HashSet<>();
        league.getPlayers().forEach(
                player -> players.add(BasicPlayerResponseDTO.builder()
                        .id(player.getId())
                        .name(player.getName())
                        .surname(player.getSurname())
                        .email(player.getEmail())
                        .build())
        );
        return CreateLeagueResponseDTO.builder()
                .id(league.getId())
                .name(league.getName())
                .description(league.getDescription())
                .players(players)
                .admins(players) //using players for both because when i create a league i'm both a player and admin
                .build();
    }

}
