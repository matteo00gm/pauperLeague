export default {
  getPastEvents(state) {
    return state.pastEvents;
  },
  getFutureEvents(state) {
    return state.futureEvents;
  },
  getMyEvents(state) {
    return state.myEvents;
  },
  getLeagueProgram(state) {
    return state.leagueProgram;
  },
  shouldUpdateEvents(state){
    const lastEventsFetch= state.lastEventsFetch
    if(!lastEventsFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastEventsFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  shouldUpdateMyEvents(state) {
    const lastMyEventsFetch= state.lastMyEventsFetch
    if(!lastMyEventsFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastMyEventsFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  shouldUpdateEventDetails(state) {
    const lastEventDetailsFetch= state.lastEventDetailsFetch
    if(!lastEventDetailsFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastEventDetailsFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  shouldUpdateLeagueProgram(state) {
    const lastLeagueProgramFetch= state.lastLeagueProgramFetch
    if(!lastLeagueProgramFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastLeagueProgramFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  getLastEventFetchForLeague(state){
    return state.lastEventFetchForLeague
  },
  getLastLeagueProgramFetchForLeague(state){
    return state.lastLeagueProgramFetchForLeague
  },
  getLastEventIdFetched(state){
    return state.lastEventIdFetched
  },
  getSelectedEvent(state){
    return state.selectedEvent
  },
  getSelectedEventToEdit(state){
    return state.selectedEventToEdit
  },
  getCurrentRound(state){
    return state.currentRound
  },
  isGone(state){
    return state.selectedEvent.date <= new Date()
  },
};
