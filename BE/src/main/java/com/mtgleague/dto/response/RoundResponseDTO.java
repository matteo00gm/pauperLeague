package com.mtgleague.dto.response;

import com.mtgleague.dto.response.player.PlayerResponseDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoundResponseDTO {

    private Long id;

    private String nameP1;
    private String surnameP1;

    private String nameP2;
    private String surnameP2;

    private boolean ended;

    private int p1Wins;
    private int p2Wins;

    private Date roundEndTime;

    private List<PlayerResponseDTO> previousRoundRankings;

}
