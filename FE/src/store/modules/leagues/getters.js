export default {
  getLeagues(state) {
    return state.leagues;
  },
  getMyLeagues(state) {
    return state.myLeagues;
  },
  shouldUpdateLeagues(state){
    const lastLeaguesFetch= state.lastLeaguesFetch
    if(!lastLeaguesFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastLeaguesFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  shouldUpdateMyLeagues(state){
    const lastMyLeaguesFetch= state.lastMyLeaguesFetch
    if(!lastMyLeaguesFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastMyLeaguesFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  shouldUpdateSelectedLeague(state){
    const lastSelectedLeagueFetch= state.lastSelectedLeagueFetch
    if(!lastSelectedLeagueFetch){
      return true
    }else{
      const currentTimeStamp= new Date().getTime()
      return (currentTimeStamp - lastSelectedLeagueFetch) /1000 >60 //ritorna true se è più di un minuto fa
    }
  },
  getSelectedLeague(state){
    return state.selectedLeague
  },
};
