import { endpoint } from '../../index.js';

export default {

  async addLeague(context, data) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const userId = context.rootGetters.userId || localStorage.getItem('userId');

    const newLeague = {
      leagueName: data.name,
      description: data.desc,
      ownerId: userId,
    };

    const response = await fetch(
      endpoint + `/leagues/new`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(newLeague),
      }
    );

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }
    
    context.commit('addLeague', responseData);
  },

  async loadAllLeagues(context) {

    if (!context.getters.shouldUpdateLeagues) {
      return;
    }

    const response = await fetch(
      endpoint + `/leagues`
    );
  
    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    const leagues = responseData.results;

    context.commit('setLeagues', leagues);

    context.commit('setLoadLeaguesFetchTimestamp');
  },

  async loadMyLeagues(context) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const playerId = {
      playerId: context.rootGetters.userId || localStorage.getItem('userId')
    };

    if (!context.getters.shouldUpdateMyLeagues) {
      return;
    }

    const response = await fetch(
      endpoint + `/leagues/my-subs`,
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
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
    
    const myLeagues = responseData.results;

    context.commit('setMyLeagues', myLeagues);

    context.commit('setLoadMyLeaguesFetchTimestamp');
  },

  async sendLeagueSubReq(context, payload) {
    const userId = context.rootGetters.userId;
    const token = context.rootGetters.getToken;
    const leagueId = payload.leagueId;

    const playerToSubscribe = {
      playerId: userId,
      leagueId: leagueId
    };

    const response = await fetch(
      endpoint + `/leagues/register`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerToSubscribe),
      }
    );

    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    const player = {
      id: +userId
    }

    //add request to vuex both in selected league and in leagues
    context.commit('addLeagueSubReq', player)
  },

  async removeMember(context, payload) {
    const leagueId = payload.leagueId;
    const playerId = payload.playerId;
    const token = context.rootGetters.getToken;
    const playerToRemove = {
      playerId: playerId,
      leagueId: leagueId
    };

    const response = await fetch(
      endpoint + `/leagues/removeMember`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerToRemove),
      }
    );
    
    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    const p = {
      playerId: playerId,
      leagueId: leagueId
    }

    context.commit('removePlayerFromSelectedLeague', p)
  },

  async leaveLeague(context, payload) {
    const leagueId = payload.leagueId;
    const playerId = context.rootGetters.userId || localStorage.getItem('userId');
    const token = context.rootGetters.getToken;
    const playerToRemove = {
      playerId: playerId,
      leagueId: leagueId
    };

    const response = await fetch(
      endpoint + `/leagues/leave`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerToRemove),
      }
    );

    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
    
    const p = {
      playerId: playerId,
      leagueId: leagueId
    }

    context.commit('removePlayerFromLeague', p)
  },

  async findLeagueById(context, payload) {
    const token = context.rootGetters.getToken || localStorage.getItem('token');

    if (context.getters.getSelectedLeague && +payload.leagueId === context.getters.getSelectedLeague.leagueId) {
      if (!context.getters.shouldUpdateSelectedLeague) {
        return;
      }
    }

    const response = await fetch(
      endpoint + `/leagues/${payload.leagueId}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      }
    );
  
    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('setSelectedLeague', responseData);

    context.commit('setLoadSelectedLeagueFetchTimestamp');
  },

  reset(context) {
    context.commit('reset')
  }
};
