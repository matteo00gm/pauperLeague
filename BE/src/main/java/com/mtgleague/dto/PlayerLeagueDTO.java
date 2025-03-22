package com.mtgleague.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PlayerLeagueDTO {
    private Long id;
    private String name;
    private String description;
}
