export default {
  setLoadPlayerFetchTimestamp(state) {
    state.lastPlayerFetch = new Date().getTime();
  },
  setSelectedPlayer(state, payload) {
    state.selectedPlayer = payload
  },
  setLastPlayerIdFetch(state, payload) {
    state.lastPlayerIdFetch = payload
  },

  reset(state) {
    state.lastPlayerFetch = null,
    state.selectedPlayer = null,
    state.lastPlayerIdFetch = null
  }
};
