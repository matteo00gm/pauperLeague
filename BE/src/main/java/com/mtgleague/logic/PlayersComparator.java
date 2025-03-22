package com.mtgleague.logic;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayersComparator implements Comparator<PlayerScore> {
    private final Map<Long, PlayerScore> playerScoreMap;
    private final Map<Long, Double> omwMap;
    private final Map<Long, Double> gwMap;
    private final Map<Long, Double> ogwMap;

    public PlayersComparator(List<PlayerScore> players) {
        // Create a HashMap for quick lookups
        this.playerScoreMap = players.stream()
                .collect(Collectors.toMap(PlayerScore::getId, playerScore -> playerScore));

        // Pre-compute OMW, GW, and OGW for every player
        this.omwMap = players.stream()
                .collect(Collectors.toMap(
                        PlayerScore::getId,
                        player -> omwCalc(player, playerScoreMap)
                ));

        this.gwMap = players.stream()
                .collect(Collectors.toMap(
                        PlayerScore::getId,
                        player -> gwCalc(player)  // Precompute GW using the existing gwCalc method
                ));

        this.ogwMap = players.stream()
                .collect(Collectors.toMap(
                        PlayerScore::getId,
                        player -> ogwCalc(player, playerScoreMap) // Precompute OGW using the existing ogwCalc method
                ));
    }

    /*
    here I'm following the rules magic the gathering to do the pairings and the 3-step rules to break a tie:
        1- players points;
        2- omw (value that refers to the winRate (of the matches) of the opponents / the number of opponents)
        3- gw (value that refers to the winRate (of the single games) of the player)
        4- ogw (value that refers to the winRate (of the single games) of the opponents / the number of opponents)

    !!!Be careful: magic the gathering uses this rules "when calculating omw and ogw, the single winRates are set to 33 if they are below"!!!
    */
    @Override
    public int compare(PlayerScore player1, PlayerScore player2) {
        // Step 1: Compare scores
        int scoreComparison = Integer.compare(player2.getScore(), player1.getScore());
        if (scoreComparison != 0) return scoreComparison;

        // Step 2: Compare precomputed OMW values
        double omw1 = omwMap.get(player1.getId());
        double omw2 = omwMap.get(player2.getId());
        int omwComparison = Double.compare(omw2, omw1);
        if (omwComparison != 0) return omwComparison;

        // Step 3: Compare precomputed GW values
        double gw1 = gwMap.get(player1.getId());
        double gw2 = gwMap.get(player2.getId());
        int gwComparison = Double.compare(gw2, gw1);
        if (gwComparison != 0) return gwComparison;

        // Step 4: Compare precomputed OGW values
        double ogw1 = ogwMap.get(player1.getId());
        double ogw2 = ogwMap.get(player2.getId());
        return Double.compare(ogw2, ogw1);
    }

    //omw is the value that refers to the winRate (of the matches) of the opponents / the number of opponents
    //!!!Be careful: magic the gathering uses this rules "when calculating omw and ogw, the single winRates are set to 33 if they are below"!!!
    private double omwCalc(PlayerScore player, Map<Long, PlayerScore> playerScoreMap) {
        AtomicDouble omwPlayer = new AtomicDouble();
        player.getOpponentsIds().forEach(opponentId -> {
            PlayerScore opponentScore = playerScoreMap.get(opponentId); // Quick lookup
            if (opponentScore != null) {
                omwPlayer.addAndGet(opponentScore.getFixedMatchWinRateWithoutBye());
            }
        });

        int numberOfOpponents = player.getOpponentsIds().size();
        double omw = numberOfOpponents > 0 ? omwPlayer.get() / numberOfOpponents : 0;
        player.setOmw(omw);
        return omw;
    }

    //gw is the value that refers to the winRate (of the single games) of the player
    private double gwCalc(PlayerScore player){
        double gw= player.getGameWinRateWithoutBye();
        player.setGw(gw);
        return gw;
    }

    //omw is the value that refers to the winRate (of the single games) of the opponents / the number of opponents
    //!!!Be careful: magic the gathering uses this rules "when calculating omw and ogw, the single winRates are set to 33 if they are below"!!!
    private double ogwCalc(PlayerScore player, Map<Long, PlayerScore> playerScoreMap) {
        AtomicDouble ogwPlayer = new AtomicDouble();
        player.getOpponentsIds().forEach(opponentId -> {
            PlayerScore opponentScore = playerScoreMap.get(opponentId); // Quick lookup
            if (opponentScore != null) {
                ogwPlayer.addAndGet(opponentScore.getFixedGameWinRateWithoutBye());
            }
        });

        int numberOfOpponents = player.getOpponentsIds().size();
        double ogw = numberOfOpponents > 0 ? ogwPlayer.get() / numberOfOpponents : 0;
        player.setOgw(ogw);
        return ogw;
    }
}

