export default {
  setLoadGlobalRankingsFetchTimestamp(state) {
    state.lastGlobalRankingsFetch = new Date().getTime();
  },
  setGlobalRankings(state, payload) {
    state.globalRankings = payload
  },
  setLoadLeagueRankingsFetchTimestamp(state) {
    state.lastLeagueRankingsFetch = new Date().getTime();
  },
  setLeagueRankings(state, payload) {
    state.leagueRankings = payload
  },
  setLoadEventRankingsFetchTimestamp(state) {
    state.lastEventRankingsFetch = new Date().getTime();
  },
  setEventRankings(state, payload) {
    state.eventRankings = payload
  },
  setLastEventIdRankingsFetch(state, payload) {
    state.lastEventIdRankings = payload
  },
  setLastLeagueIdRankingsFetch(state, payload) {
    state.lastLeagueIdRankings = payload
  },

  reset(state) {
    state.lastGlobalRankingsFetch = null,
    state.globalRankings = [],
    state.lastLeagueRankingsFetch = null,
    state.leagueRankings = [],
    state.lastEventRankingsFetch = null,
    state.eventRankings = [],
    state.lastEventIdRankings = null,
    state.lastLeagueIdRankings = null
  }
};
