package com.mtgleague.dto.response.league;

import com.mtgleague.model.Player;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MyLeagueResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Player> admins;
}
