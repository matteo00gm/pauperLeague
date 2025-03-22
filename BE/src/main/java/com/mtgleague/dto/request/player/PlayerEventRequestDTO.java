package com.mtgleague.dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerEventRequestDTO {

    private Long playerId;
    private Long eventId;
    private Long leagueId;
}