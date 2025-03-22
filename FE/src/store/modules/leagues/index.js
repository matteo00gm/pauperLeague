import mutations from './mutations.js';
import actions from './actions.js';
import getters from './getters.js';

export default {
  namespaced: true,
  state() {
    return {
      lastLeaguesFetch: null,
      lastMyLeaguesFetch: null,
      lastPlayerLeaguesFetch: null,
      lastSelectedLeagueFetch: null,
      leagues: [],
      myLeagues: [],
      selectedLeague: null,
    };
  },
  mutations,
  actions,
  getters,
};
