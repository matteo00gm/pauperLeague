package com.mtgleague.dto.response.player;

import jakarta.persistence.Embeddable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class BasicPlayerResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;

    public BasicPlayerResponseDTO(Long id){
        this.id = id;
    }
}
