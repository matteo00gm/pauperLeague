export default {
  //league subs
  getLeagueSubRequests(state) {
    return state.leagueSubRequests;
  },
  shouldUpdateLeagueSubs(state) {
    const lastLeagueSubsFetch= state.lastLeagueSubsFetch
    if(!lastLeagueSubsFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastLeagueSubsFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  getLastSubsFetchForLeague(state) {
    return state.lastSubsFetchForLeague
  },
  getMyEventsSubReq(state) {
    return state.myEventsSubReq;
  },
  shouldUpdateMyEventsSubReq(state) {
    const lastMyEventsSubReqFetch= state.lastMyEventsSubReqFetch
    if(!lastMyEventsSubReqFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastMyEventsSubReqFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
};
