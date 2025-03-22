package com.mtgleague.dto.response.league;

import com.mtgleague.dto.response.player.BasicPlayerResponseDTO;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateLeagueResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<BasicPlayerResponseDTO> admins;
    private Set<BasicPlayerResponseDTO> players;
}
