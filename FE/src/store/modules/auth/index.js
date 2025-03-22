import mutations from './mutations.js'
import getters from './getters.js'
import actions from './actions.js'

export default {
    //not namespacing this to make it global and easily accessable from the others vuex modules
    state(){
        return {
            userId: null,
            token: null,
            role: null,
            didAutoLogout: false,
        }
    },
    mutations,
    getters,
    actions
}