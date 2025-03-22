export default {
  shouldUpdateGlobalRankings(state) {
    const lastGlobalRankingsFetch = state.lastGlobalRankingsFetch;
    if (!lastGlobalRankingsFetch) {
      return true;
    } else {
      const currentTimeStamp = new Date().getTime();
      return (currentTimeStamp - lastGlobalRankingsFetch) / 1000 > 60; //ritorna true se è più di un minuto fa
    }
  },
  getGlobalRankings(state) {
    return state.globalRankings;
  },
  shouldUpdateLeagueRankings: (state) => (leagueId) => {
    const lastLeagueRankingsFetch = state.lastLeagueRankingsFetch;
    const lastLeagueIdRankings = state.lastLeagueIdRankings;

    if (!lastLeagueRankingsFetch) {
      return true; // If the rankings were never fetched, we should fetch them
    } else {
      const currentTimeStamp = new Date().getTime();
      // Return true if more than a minute has passed OR if the leagueId is different
      return ((currentTimeStamp - lastLeagueRankingsFetch) / 1000 > 60) || (lastLeagueIdRankings !== leagueId);
    }
  },
  getLeagueRankings(state) {
    return state.leagueRankings;
  },
  shouldUpdateEventRankings: (state) => (eventId) => {
    const lastEventRankingsFetch = state.lastEventRankingsFetch;
    const lastEventIdRankings = state.lastEventIdRankings;
    if (!lastEventRankingsFetch) {
      return true;
    } else {
      const currentTimeStamp = new Date().getTime();
      return ((currentTimeStamp - lastEventRankingsFetch) / 1000 > 60) || (lastEventIdRankings !== eventId); //ritorna true se è più di un minuto fa
    }
  },
  getEventRankings(state) {
    return state.eventRankings;
  },
};
