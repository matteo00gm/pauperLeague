export default {
  shouldUpdatePlayer(state) {
    const lastPlayerFetch = state.lastPlayerFetch;
    if (!lastPlayerFetch) {
      return true;
    } else {
      const currentTimeStamp = new Date().getTime();
      return (currentTimeStamp - lastPlayerFetch) / 1000 > 60; //ritorna true se è più di un minuto fa
    }
  },
  getSelectedPlayer(state) {
    return state.selectedPlayer;
  },
  getLastPlayerIdFetch(state) {
    return state.lastPlayerIdFetch;
  },
};
