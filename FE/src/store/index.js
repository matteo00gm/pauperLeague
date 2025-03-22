import {createStore} from 'vuex'

import authModule from './modules/auth';
import eventsModule from './modules/events';
import playersModule from './modules/players';
import leaguesModule from './modules/leagues';
import subsModule from './modules/subs';
import rankingsModule from './modules/rankings';

const store= createStore({
    modules: {
        auth: authModule,
        events: eventsModule,
        players: playersModule,
        leagues: leaguesModule,
        subs: subsModule,
        rankings: rankingsModule,
    },
})

export const endpoint = 'https://pauperleague.eu-south-1.elasticbeanstalk.com';

export default store;