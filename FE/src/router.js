import store from './store/index.js'
import { createRouter, createWebHistory } from 'vue-router';

import EventsPage from './pages/events/EventsPage.vue'
import EventCreation from './pages/events/EventCreation.vue'
import EventManagement from './pages/events/EventManagement.vue'
import EventDetails from './pages/events/EventDetails.vue'
import HealthCounter from './pages/HealthCounter.vue'
import NotFound from './pages/NotFound.vue'
import PermissionDenied from './pages/PermissionDenied.vue'
import UserAuth from './pages/auth/UserAuth.vue'
import CurrentGame from './pages/CurrentGame.vue'
import RankGlobal from './pages/players/RankGlobal.vue'
import RankLeague from './pages/players/RankLeague.vue'
import PlayersInLeague from './pages/players/PlayersInLeague.vue'
import MyLeagues from './pages/league/MyLeagues.vue'
import LeagueCreation from './pages/league/LeagueCreation.vue'
import LeaguesPage from './pages/league/LeaguesPage.vue'
import LeagueDashboard from './pages/league/LeagueDashboard.vue'
import PlayerProfile from './pages/players/PlayerProfile.vue'
import HomePage from './pages/HomePage.vue'
import MyEvents from './pages/events/MyEvents.vue'
import ForgotPassword from './pages/auth/ForgotPassword.vue'


const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', redirect: '/home' },
        { path: '/home', component: HomePage },
        { path: '/leagues', component: LeaguesPage },
        { path: '/leagues/:leagueId/rankings', component: RankLeague, props: true },
        { path: '/leagues/:leagueId/manage', component: LeagueDashboard, props: true, meta: {requiresAuth: true}},
        { path: '/leagues/:leagueId/manage/events', component: EventManagement, props: true, meta: {requiresAuth: true}},
        { path: '/leagues/:leagueId/manage/events/edit', component: EventCreation, props: true, meta: {requiresAuth: true}},
        { path: '/leagues/:leagueId/manage/new', component: EventCreation, props: true, meta: {requiresAuth: true}},
        { path: '/leagues/:leagueId/events', component: EventsPage, props: true },
        { path: '/leagues/:leagueId/events/:eventId', component: EventDetails, props: true },
        { path: '/leagues/new', component: LeagueCreation, meta: {requiresAuth: true}},
        { path: '/leagues/my-subs', component: MyLeagues, meta: {requiresAuth: true}},
        { path: '/leagues/members', component: PlayersInLeague },
        { path: '/my-events', component: MyEvents, meta: {requiresAuth: true}},
        { path: '/rankings', component: RankGlobal },
        { path: '/profile/:playerId', component: PlayerProfile, props: true },
        { path: '/counter', component: HealthCounter },
        { path: '/current', component: CurrentGame, meta: {requiresAuth: true} },
        { path: '/auth', component: UserAuth, meta: {requiresUnauth: true} },
        { path: '/password-reset', component: ForgotPassword },
        { path: '/counterspell', component: PermissionDenied },
        { path: '/:notFound(.*)', component: NotFound },
    ],
});


//global route guard
router.beforeEach((to, _, next)=> {
    if(to.meta.requiresAuth && !store.getters.isAuthenticated){
        next("/home")
    }else if(to.meta.requiresUnauth && store.getters.isAuthenticated){
        next('/leagues/my-subs')
    }else{
        next()
    }
})

export default router;