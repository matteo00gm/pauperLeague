import { endpoint } from '../../index.js';
export default {

  async loadPlayer(context, payload) {
    const playerId = payload.playerId
    if (+playerId === +context.getters.getLastPlayerIdFetch) {
      if (!context.getters.shouldUpdatePlayer) {
        return;
      }
    }

    const response = await fetch(
      endpoint + `/players/profile`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(playerId),
      }
    );
    const responseData = await response.json();
  
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  
    context.commit('setSelectedPlayer', responseData);
  
    context.commit('setLoadPlayerFetchTimestamp');
    context.commit('setLastPlayerIdFetch', playerId);
  },

  reset(context) {
    context.commit('reset')
  }
};
