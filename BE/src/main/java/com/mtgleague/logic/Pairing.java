package com.mtgleague.logic;

import com.google.common.util.concurrent.AtomicDouble;
import com.mtgleague.model.Event;
import com.mtgleague.model.Round;
import com.mtgleague.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class Pairing {
    private final RoundService roundService;

    public void sortPlayers(List<PlayerScore> players) {
        players.sort(new PlayersComparator(players));
    }

    public void doPairings(List<PlayerScore> players, Event event, int currentTurn) {

        if (!event.isStarted()) {
            createInitialPairings(players, event, currentTurn);
        } else {
            calcPossibleOpponents(players);

            // Generate and create rounds from match pairings
            List<Round> pairings = createMatches(players);
            pairings.forEach(pair -> createRoundFromPair(pair, event, currentTurn));
        }
    }

    private void createInitialPairings(List<PlayerScore> players, Event event, int currentTurn) {
        for (int i = 0; i < players.size() - 1; i += 2) {
            PlayerScore player1 = players.get(i);
            PlayerScore player2 = players.get(i + 1);

            createRound(player1, player2, event, currentTurn);
        }
        if (players.size() % 2 != 0) {
            PlayerScore unpairedPlayer = players.get(players.size() - 1);
            createRound(unpairedPlayer, null, event, currentTurn);
        }
    }

    private void createRound(PlayerScore player1, PlayerScore player2, Event event, int currentTurn) {
        if (player2 != null) {
            roundService.createRound(
                    player1.getId(), player1.getName(), player1.getSurname(),
                    player2.getId(), player2.getName(), player2.getSurname(),
                    0, 0, event, currentTurn, false
            );
        } else {
            //assigning bye if there is player 2 (odd players)
            roundService.createRound(
                    player1.getId(), player1.getName(), player1.getSurname(),
                    null, null, null,
                    2, 0, event, currentTurn, true
            );
        }

    }

    private void createRoundFromPair(Round pair, Event event, int currentTurn) {
        if (pair.getIdP2() != null) {
            roundService.createRound(
                    pair.getIdP1(), pair.getNameP1(), pair.getSurnameP1(),
                    pair.getIdP2(), pair.getNameP2(), pair.getSurnameP2(),
                    0, 0, event, currentTurn, false
            );
        } else {
            //assigning bye if there is player 2 (odd players)
            roundService.createRound(
                    pair.getIdP1(), pair.getNameP1(), pair.getSurnameP1(),
                    null, null, null,
                    2, 0, event, currentTurn, true
            );
        }
    }

    private void calcPossibleOpponents(List<PlayerScore> players) {
        // Calculate possible opponents for each player based on score and past encounters
        players.forEach(player -> player.setPossibleOpponents(
                players.stream()
                        .filter(possibleOpponent -> isValidOpponent(player, possibleOpponent))
                        .collect(Collectors.toList())
        ));
    }

    private boolean isValidOpponent(PlayerScore player, PlayerScore possibleOpponent) {
        // Opponent is valid if it's not the player itself, has an equal or lower score,
        // and hasn't been played against before
        return player.getId() != possibleOpponent.getId() &&
                possibleOpponent.getScore() <= player.getScore() &&
                !player.getOpponentsIds().contains(possibleOpponent.getId());
    }




    private List<Round> createMatches(List<PlayerScore> players) {
        int i = 0;
        List<PlayerScore> pairedList = new ArrayList<>();
        Set<PlayerScore> pairedSet = new HashSet<>();  // HashSet for fast `contains` checks
        List<Round> pairings = new ArrayList<>();

        //handling bye case
        if (players.size() % 2 != 0) {
            PlayerScore p = players.get(players.size()-1);
            pairedSet.add(p);
            pairedList.add(p);
            pairings.add(
                    Round.builder()
                            .idP1(p.getId())
                            .nameP1(p.getName())
                            .surnameP1(p.getSurname())
                            .idP2(null)
                            .nameP2(null)
                            .surnameP2(null)
                            .build());
        }

        while (pairedList.size() < players.size()) {

            // Ensure 'i' is within bounds
            if (i < 0 || i >= players.size()) {
                throw new IllegalStateException("Index i is out of bounds: " + i);
            }

            PlayerScore p = players.get(i);

            // Check if player is already paired using HashSet
            if (pairedSet.contains(p)) {
                i++;
                continue;
            }

            List<PlayerScore> o = getOppNotPaired(p, pairedSet);

            if (p.getJ() >= o.size()) {
                p.setJ(0);
                removeLastPaired(pairedList, pairedSet);
                p = pairedList.get(pairedList.size() - 1);
                p.setJ(p.getJ() + 1);

                removeLastPaired(pairedList, pairedSet);
                pairings.remove(pairings.size() - 1);
                i = players.indexOf(p);
                continue;
            }

            if (o.size() > 0) {
                pairings.add(
                        Round.builder()
                                .idP1(p.getId())
                                .nameP1(p.getName())
                                .surnameP1(p.getSurname())
                                .idP2(o.get(p.getJ()).getId())
                                .nameP2(o.get(p.getJ()).getName())
                                .surnameP2(o.get(p.getJ()).getSurname())
                                .build());
                pairedList.add(p);
                pairedSet.add(p);
                pairedList.add(o.get(p.getJ()));
                pairedSet.add(o.get(p.getJ()));
                i++;
            } else {
                removeLastPaired(pairedList, pairedSet);
                p = pairedList.get(pairedList.size() - 1);
                p.setJ(p.getJ() + 1);

                removeLastPaired(pairedList, pairedSet);
                pairings.remove(pairings.size() - 1);

                i = players.indexOf(p);
            }
        }
        return pairings;
    }

    // Helper function to remove the last paired element from both the List and Set
    private void removeLastPaired(List<PlayerScore> pairedList, Set<PlayerScore> pairedSet) {
        if (!pairedList.isEmpty()) {
            PlayerScore last = pairedList.remove(pairedList.size() - 1);
            pairedSet.remove(last);
        }
    }

    private List<PlayerScore> getOppNotPaired(PlayerScore player, Set<PlayerScore> pairedSet) {
        return player.getPossibleOpponents().stream().filter(opp ->
                !pairedSet.contains(opp)
        ).collect(Collectors.toList());
    }


}