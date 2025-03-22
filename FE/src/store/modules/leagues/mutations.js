export default {
  addLeague(state, payload) {
    state.leagues.push(payload);
    state.myLeagues.push(payload);
  },
  setLeagues(state, payload) {
    state.leagues = payload;
  },
  setMyLeagues(state, payload) {
    state.myLeagues = payload;
  },
  setSelectedLeague(state, payload) {
    state.selectedLeague = payload;
  },
  setLoadLeaguesFetchTimestamp(state) {
    state.lastLeaguesFetch = new Date().getTime();
  },
  setLoadMyLeaguesFetchTimestamp(state) {
    state.lastMyLeaguesFetch = new Date().getTime();
  },
  setLoadSelectedLeagueFetchTimestamp(state) {
    state.lastSelectedLeagueFetch = new Date().getTime();
  },
  addPlayerToSelectedLeague(state, payload) {
    const myLeague = state.myLeagues.find(league => league.id === +payload.leagueId);
    const league = state.leagues.find(league => league.id === +payload.leagueId);
    const selectedLeague = state.selectedLeague;
    if (myLeague) {
      //adding player to his league
      myLeague.players.push(payload.player);
    }
    if (league) {
      //adding player to his league
      league.players.push(payload.player);
    }
    if (selectedLeague) {
      //adding player to selected league
      selectedLeague.players.push(payload.player);
    }
  },
  removePlayerFromSelectedLeague(state, payload) {
    const myLeague = state.myLeagues.find(league => league.id === +payload.leagueId);
    const league = state.leagues.find(league => league.id === +payload.leagueId);
    const selectedLeague = state.selectedLeague;
    if (myLeague) {
      //removing player from his league
      myLeague.players = myLeague.players.filter(player => player.id !== +payload.playerId);
    }
    if (league) {
      //removing player from his league
      league.players = league.players.filter(player => player.id !== +payload.playerId);
    }
    if (selectedLeague) {
      //removing player from his league
      selectedLeague.players = selectedLeague.players.filter(player => player.id !== +payload.playerId);
    }
  },
  removePlayerFromLeague(state, payload) {
    //remove league from my leagues
    state.myLeagues = state.myLeagues.filter(league => league.id !== +payload.leagueId);
    
    //remove player from league
    const league = state.leagues.find(league => league.id === +payload.leagueId);
    if (league) {
      league.players = league.players.filter(player => player.id !== +payload.playerId);
      league.admins = league.admins.filter(player => player.id !== +payload.playerId);
    }
  },
  addLeagueSubReq(state, payload) {
    // Ensure that leagues.leagueSubRequests exists and is reactive
    if (state.leagues.leagueSubRequests) {
      state.leagues.leagueSubRequests = [...state.leagues.leagueSubRequests, payload];
    }
  
    // Ensure that selectedLeague.leagueSubRequests exists and is reactive
    if (state.selectedLeague.leagueSubRequests) {
      state.selectedLeague.leagueSubRequests = [...state.selectedLeague.leagueSubRequests, payload];
    }
  },

  reset(state) {
    state.lastLeaguesFetch = null,
    state.lastMyLeaguesFetch = null,
    state.lastPlayerLeaguesFetch = null,
    state.lastSelectedLeagueFetch = null,
    state.leagues = [],
    state.myLeagues = [],
    state.selectedLeague = null
  }
};
