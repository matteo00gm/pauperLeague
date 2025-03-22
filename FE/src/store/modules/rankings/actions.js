import { endpoint } from '../../index.js';
export default {
  async loadEventRankings(context, payload) {
    const eventId = payload.eventId

    if (!context.getters.shouldUpdateEventRankings(eventId)) {
      return;
    }

    const response = await fetch(
      endpoint + `/events/${eventId}/ranking`
    );
    const responseData = await response.json();
  
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  
    context.commit('setEventRankings', responseData.results);
  
    context.commit('setLoadEventRankingsFetchTimestamp');
    context.commit('setLastEventIdRankingsFetch', eventId);
  },

  async loadLeagueRankings(context, payload) {
    const leagueId= payload.leagueId;
    
    if (!context.getters.shouldUpdateLeagueRankings(leagueId)) {
      return;
    }
  
    const response = await fetch(
      endpoint + `/leagues/${leagueId}/ranking`
    );
    const responseData = await response.json();
  
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  
    context.commit('setLeagueRankings', responseData.results);
  
    context.commit('setLoadLeagueRankingsFetchTimestamp');
    context.commit('setLastLeagueIdRankingsFetch', leagueId);
  },

  async loadGlobalRankings(context) {
    
    if (!context.getters.shouldUpdateGlobalRankings) {
      return;
    }
    
    const response = await fetch(
      endpoint + `/ranking`,
      {
        method: 'GET',
      }
    );
    const responseData = await response.json();
  
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  
    context.commit('setGlobalRankings', responseData.results);
  
    context.commit('setLoadGlobalRankingsFetchTimestamp');

  },

  reset(context) {
    context.commit('reset')
  }
};
