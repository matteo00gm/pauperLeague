package com.mtgleague.model;

import com.mtgleague.dto.response.player.PlayerResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private Long leagueId;

    @ElementCollection
    private List<PlayerResponseDTO> players;

    private Boolean isValid;

}
