package com.mtgleague.dto.response.event;

import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventDetailsResponseDTO {
    private Long eventId;
    private String name;
    private String date;
    private int cap;
    private String location;
    private boolean started;
    private boolean ended;
    private Date roundEndTime;
    private Set<EventRegistrationResponseDTO> players;
    private Set<BasicPlayerResponseDTO> leagueAdmins;
    private Set<BasicPlayerResponseDTO> pendingEventSubs;
}

