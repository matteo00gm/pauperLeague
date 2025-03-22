package com.mtgleague.dto.request;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoundRequestDTO {

    private Long roundId;
    private Long playerId;
    private int p1Wins;
    private int p2Wins;
}
