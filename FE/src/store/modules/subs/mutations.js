export default {
  setLeagueSubRequests(state, payload) {
    state.leagueSubRequests = payload;
  },
  removeLeagueSubRequest(state, payload) {
    state.leagueSubRequests = state.leagueSubRequests.filter(sub => sub.playerId !== payload.playerId);
  },
  setLoadLeagueSubsFetchTimestamp(state) {
    state.lastLeagueSubsFetch = new Date().getTime();
  },
  setLastSubsFetchForLeague(state, payload) {
    state.lastSubsFetchForLeague = payload
  },
  setMyEventsSubReq(state, payload) {
    state.myEventsSubReq = payload;
  },
  setLoadMyEventsSubReqFetchTimestamp(state) {
    state.lastMyEventsSubReqFetch = new Date().getTime();
  },
  addMyEventSubReq(state, payload) {
    const myEventsSubReq = state.myEventsSubReq
    if(myEventsSubReq) {
      myEventsSubReq.push(payload.event)
    }
  },
  reset(state) {
    state.leagueSubRequests = [],
    state.lastLeagueSubsFetch = null,
    state.lastSubsFetchForLeague = null,
    state.myEventsSubReq = [],
    state.lastMyEventsSubReqFetch = null
  }
};
