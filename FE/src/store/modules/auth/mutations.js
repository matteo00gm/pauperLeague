export default {
    setUser(state, payload){
        state.token= payload.token,
        state.userId= payload.userId,
        state.role= payload.role,
        state.didAutoLogout= false //i'm doing this to make the watcher in the App.vue main file recognise again the change
    },
    setAutoLogout(state){
        state.didAutoLogout= true
    }
}