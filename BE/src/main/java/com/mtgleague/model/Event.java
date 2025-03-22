package com.mtgleague.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private boolean started;

    private boolean ended;
    private String date;
    private int cap;
    private String description;

    private Date roundEndTime;

    @JsonIgnore
    private int currentTurn;

    @JsonIgnore
    private int maxTurn;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventRegistration> eventRegistrations = new HashSet<>();

    @OneToMany(mappedBy="event", fetch = FetchType.LAZY)
    private Set<Round> matches = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;

    @JsonIgnore
    @OneToMany(mappedBy="event", fetch = FetchType.LAZY)
    private Set<EventSubRequest> eventSubRequests = new HashSet<>();

    public Event(String name, String date, int cap, String description) {
        this.name = name;
        this.date = date;
        this.cap = cap;
        this.description = description;
    }

}
