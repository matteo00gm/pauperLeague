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
public class LeagueProgramResponseDTO {
    private Long eventId;
    private String name;
    private String date;
    private boolean isStarted;
    private int cap;
    private String location;
    private Set<EventRegistrationResponseDTO> players;
    private Set<BasicPlayerResponseDTO> pendingEventSubs;
}

