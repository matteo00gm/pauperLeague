package com.mtgleague.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Player implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Column(unique=true, nullable = false)
    private String email;
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventRegistration> eventRegistration = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
    private Set<League> subscriptions = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY)
    private Set<League> owner = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="player", fetch = FetchType.LAZY)
    private Set<LeagueSubRequest> leagueSubRequests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="player", fetch = FetchType.LAZY)
    private Set<EventSubRequest> eventSubRequests = new HashSet<>();

    @OneToOne(mappedBy = "player")
    private ForgotPassword forgotPassword;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

}
