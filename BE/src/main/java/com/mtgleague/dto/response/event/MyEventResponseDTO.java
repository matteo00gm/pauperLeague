package com.mtgleague.dto.response.event;

import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyEventResponseDTO {
    private Long eventId;
    private String name;
    private String date;
    private int cap;
    private String location;
    private Long leagueId;
    private String leagueName;
    private Set<BasicPlayerResponseDTO> players;
    private Set<BasicPlayerResponseDTO> pendingEventSubs;
}

