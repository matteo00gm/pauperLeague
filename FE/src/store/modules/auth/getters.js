export default {
  userId(state) {
    return state.userId;
  },
  getToken(state){
    return state.token
  },
  isAuthenticated(state){
    return !!state.token //converto in boolean
  },
  didAutoLogout(state){
    return state.didAutoLogout
  },
  getLoggedUserRole(state){
    return state.role
  }
};
