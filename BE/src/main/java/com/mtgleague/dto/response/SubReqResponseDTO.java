package com.mtgleague.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubReqResponseDTO {
    private Long playerId;
    private String playerName;
    private String playerSurname;
}
