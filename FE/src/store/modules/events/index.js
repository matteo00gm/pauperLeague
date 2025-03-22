import mutations from './mutations.js';
import actions from './actions.js';
import getters from './getters.js';

export default {
  namespaced: true,
  state() {
    return {
      lastEventsFetch: null,
      lastEventFetchForLeague: null,
      lastLeagueProgramFetchForLeague: null,
      lastMyEventsFetch: null,
      lastLeagueProgramFetch: null,
      futureEvents: [],
      pastEvents: [],
      myEvents: [],
      leagueProgram: [],
      selectedEvent: null,
      selectedEventToEdit: null,
      currentRound: null,
      lastEventIdFetched: null,
      lastEventDetailsFetch: null,
    };
  },
  mutations,
  actions,
  getters,
};
