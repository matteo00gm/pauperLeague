package com.mtgleague.dto.response.player;

import jakarta.persistence.Embeddable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class PlayerResponseDTO {

    private Long id;
    private String name;
    private String surname;
    private Double matchWinRate;
    private Integer score;
    private Integer eventsPlayed;

    //for single event
    private Double omw;
    private Double gw;
    private Double ogw;
}
