package com.mtgleague.dto.response.player;

import com.mtgleague.model.League;
import lombok.*;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlayerProfileResponseDTO {
    private String name;
    private String surname;
    private String email;
    private Set<League> leagues;
}
