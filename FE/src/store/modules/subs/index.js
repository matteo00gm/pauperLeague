import mutations from './mutations.js';
import actions from './actions.js';
import getters from './getters.js';

export default {
  namespaced: true,
  state() {
    return {
      leagueSubRequests: [],
      lastLeagueSubsFetch: null,
      lastSubsFetchForLeague: null,
      myEventsSubReq: [],
      lastMyEventsSubReqFetch: null,
    };
  },
  mutations,
  actions,
  getters,
};
