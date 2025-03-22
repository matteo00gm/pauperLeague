package com.mtgleague.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewEventResponseDTO {
    private Long eventId;
    private String name;
    private String date;
    private int cap;
    private String location;
}

