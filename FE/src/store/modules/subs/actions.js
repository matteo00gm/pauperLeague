import { endpoint } from '../../index.js';

export default {
  async findSubsByLeagueId(context, payload) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const leagueId= payload.leagueId

    if (+leagueId === context.getters.getLastSubsFetchForLeague) {
      if (!context.getters.shouldUpdateLeagueSubs) {
        return;
      }
    }

    const request = {
      leagueId: leagueId,
    };
    
    const response = await fetch(
      endpoint + `/subs/league`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(request),
      }
    );
    const responseData = await response.json();
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
    const subs = responseData.results;

    context.commit('setLeagueSubRequests', subs);
    context.commit('setLoadLeagueSubsFetchTimestamp');
    context.commit('setLastSubsFetchForLeague', +leagueId);
  },

  async acceptRequest(context, payload) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const eventId = payload.eventId

    const request = {
      leagueId: payload.leagueId,
      eventId: eventId,
      playerId: payload.playerId,
    };

    let newEndpoint = endpoint;
    
    if (eventId) {
      newEndpoint += `/subs/event/accept`
    } else {
      newEndpoint += `/subs/league/accept`
    }

    const response = await fetch(
      newEndpoint,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(request),
      }
    );

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    const p = {
      player: responseData,
      playerId: request.playerId,
      eventId: eventId
    }
    
    if(eventId) {
      context.commit('events/subscribePlayerToEvent', p, { root: true });
      context.commit('events/removeEventSubRequest', p, { root: true });
    } else {
      const p = {
        player: responseData,
        leagueId: payload.leagueId
      }
      context.commit('leagues/addPlayerToSelectedLeague', p, { root: true });
      context.commit('removeLeagueSubRequest', { playerId: request.playerId });
    }
  },

  async denyRequest(context, payload) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const eventId = payload.eventId

    const request = {
      leagueId: payload.leagueId,
      eventId: eventId,
      playerId: payload.playerId,
    };

    let newEndpoint = endpoint;
    
    if (eventId) {
      newEndpoint += `/subs/event/deny`
    } else {
      newEndpoint += `/subs/league/deny`
    }

    const response = await fetch(
      newEndpoint,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(request),
      }
    );

    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    const p = {
      playerId: request.playerId,
      eventId: eventId
    }
    
    if(eventId) {
      context.commit('events/removeEventSubRequest', p, { root: true });
    } else {
      context.commit('removeLeagueSubRequest', { playerId: request.playerId });
    }
  },

  async loadMyEventsSubReq(context) {
    
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const playerId = {
      playerId: context.rootGetters.userId || localStorage.getItem('userId')
    };
    if (!context.getters.shouldUpdateMyEventsSubReq) {
      return;
    }
    
    const response = await fetch(
      endpoint + `/subs/my-subs`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerId),
      }
    );

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    context.commit('setMyEventsSubReq', responseData.results);
    context.commit('setLoadMyEventsSubReqFetchTimestamp');
  },

  reset(context) {
    context.commit('reset')
  }
};
