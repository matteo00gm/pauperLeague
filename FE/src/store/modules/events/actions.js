import { endpoint } from '../../index.js';

export default {
  async loadMyEvents(context) {
    
    const token = context.rootGetters.getToken || localStorage.getItem('token');
    const playerId = {
      playerId: context.rootGetters.userId || localStorage.getItem('userId')
    };
    if (!context.getters.shouldUpdateMyEvents) {
      return;
    }
    
    const response = await fetch(
      endpoint + `/events/my-events`,
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

    const events = responseData.results;

    context.commit('setMyEvents', events);
    context.commit('setLoadMyEventsFetchTimestamp');
  },
  async addEvent(context, data) {
    const token = context.rootGetters.getToken;
    const newEvent = {
      name: data.name,
      date: data.date,
      cap: data.cap,
      location: data.location,
      leagueId: data.leagueId,
    };
    const response = await fetch(
      endpoint + `/events/new`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(newEvent),
      }
    );

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('addEvent', responseData);
  },

  async editEvent(context, data) {
    const token = context.rootGetters.getToken;
    const newEvent = {
      eventId: data.eventId,
      name: data.name,
      date: data.date,
      cap: data.cap,
      location: data.location,
      leagueId: data.leagueId
    };
    const response = await fetch(
      endpoint + `/events/edit`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(newEvent),
      }
    );

    if (!response.ok) {
      const responseData = await response.json();
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    context.commit('updateEvent', newEvent)
  },

  async deleteEvent(context, data) {
    const token = context.rootGetters.getToken;
    const eventId = data.eventId
    const leagueId = data.leagueId
    const eventToDelete = {
      eventId: eventId,
      leagueId: leagueId
    };
    const response = await fetch(
      endpoint + `/events/delete`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(eventToDelete),
      }
    );

    if (!response.ok) {
      const responseData = await response.json();
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    const events = context.getters.getFutureEvents.filter(
      event => event.eventId !== +eventId
    );

    context.commit('setFutureEvents', events);

    const myEventsUpdated = context.getters.getMyEvents.filter(
      event => event.eventId !== +eventId
    );

    context.commit('setMyEvents', myEventsUpdated);

    const leagueProgramUpdated = context.getters.getLeagueProgram.filter(
      event => event.eventId !== +eventId
    );

    context.commit('setLeagueProgram', leagueProgramUpdated);
  },

  async loadLeagueProgram(context, payload) {
    const token = context.rootGetters.getToken;

    if (+payload.leagueId === context.getters.getLastLeagueProgramFetchForLeague) {
      if (!context.getters.shouldUpdateLeagueProgram) {
        return;
      }
    }

    const response = await fetch(
      endpoint + `/leagues/${payload.leagueId}/program`,
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

    const events = responseData.results;

    context.commit('setLeagueProgram', events);
    context.commit('setLastLeagueProgramFetchForLeague', +payload.leagueId);
    context.commit('setLoadLeagueProgramFetchTimestamp');
  },

  async loadEvents(context, payload) {

    if (+payload.leagueId === context.getters.getLastEventFetchForLeague) {
      if (!context.getters.shouldUpdateEvents) {
        return;
      }
    }

    const response = await fetch(
      endpoint + `/leagues/${payload.leagueId}/events`
    );
    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    const events = responseData.results;

    context.dispatch('filterEvents', events);

    context.commit('setLoadEventsFetchTimestamp');
    context.commit('setLastEventFetchForLeague', +payload.leagueId);
  },

  async filterEvents(context, events) {
    const now = new Date();
    // Get today's date without time
    const todayString = now.toISOString().split('T')[0];

    const { futureEvents, pastEvents } = events.reduce((acc, event) => {
        // Get the event date without time
        const eventDateString = new Date(event.date).toISOString().split('T')[0];

        // Check if the event is in the past or ended
        if (eventDateString < todayString || event.ended) {
            acc.pastEvents.push({ ...event, date: event.date });
        } else {
            acc.futureEvents.push({ ...event, date: event.date });
        }

        return acc;
    }, { futureEvents: [], pastEvents: [] });

    context.commit('setFutureEvents', futureEvents);
    context.commit('setLoadEventsFetchTimestamp');
    context.commit('setPastEvents', pastEvents);
  },

  async sendEventSubReq(context, payload) {
    const userId = context.rootGetters.userId;
    const token = context.rootGetters.getToken;
    const eventId = payload.eventId;
    const leagueId = payload.leagueId;

    const playerToSubscribe = {
      playerId: userId,
      eventId: eventId,
      leagueId: leagueId
    };

    const response = await fetch(
      endpoint + `/events/register`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerToSubscribe),
      }
    );

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(
        responseData.message || 'Il server risulta irraggiungibile..'
      );
      throw error;
    }

    const sub = {
      event: responseData,
      eventId: eventId
    }

    context.commit('addEventSubRequest', sub);
    context.commit('subs/addMyEventSubReq', sub, { root: true });
  },

  async kickPlayerFromEvent(context, payload) {
    const eventId= payload.eventId
    const playerId= payload.playerId
    const leagueId= payload.leagueId
    const token = context.rootGetters.getToken;

    const playerToUnSubscribe = {
      playerId: playerId,
      leagueId: leagueId
    };
    

    const response = await fetch(
      endpoint + `/events/${eventId}/kick`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(playerToUnSubscribe),
      }
    );

    if (!response.ok) {
      const responseData = await response.json();
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    const unSub = {
      playerId: playerId,
      eventId: eventId
    }

    context.commit('kickPlayerFromEvent', unSub);
  },

  async findEventById(context, payload) {
    const eventId= payload.eventId
    if (eventId === context.getters.getLastEventIdFetched) {
      if (!context.getters.shouldUpdateEventDetails) {
        return;
      }
    }
    const response = await fetch(
      endpoint + `/events/${eventId}`
    );
    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('setSelectedEvent', responseData);
    context.commit('setLastEventIdFetched', eventId);
    context.commit('setLastEventDetailsFetchTimestamp', responseData);
  },

  async findCurrentRound(context) {
    const userId = {
      playerId: context.rootGetters.userId || localStorage.getItem('userId')
    };
    const token = context.rootGetters.getToken || localStorage.getItem('token');

    const response = await fetch(
      endpoint + `/rounds/current`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(userId),
      }
    );
    const responseData = await response.json();
    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
    context.commit('setCurrentRound', responseData);
  },

  async confirmScore(context, payload){
    const round = {
      playerId: context.rootGetters.userId,
      roundId: payload.round.roundId,
      p1Wins: payload.round.p1Wins,
      p2Wins: payload.round.p2Wins
    };
    const token = context.rootGetters.getToken;
    const response = await fetch(
      endpoint + `/rounds/save`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(round),
      }
    );
    
    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('resetCurrentRound');
  },

  async startTimer(context, payload){
    const token = context.rootGetters.getToken;

    const leagueId = {
      leagueId: payload.leagueId
    }

    const response = await fetch(
      endpoint + `/events/${payload.eventId}/startTimer`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(leagueId),
      }
    );
    
    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('startTimer');
  },

  async startEvent(context, payload){
    const token = context.rootGetters.getToken;

    const leagueId = {
      leagueId: payload.leagueId
    }

    const response = await fetch(
      endpoint + `/events/${payload.eventId}/start`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(leagueId),
      }
    );
    
    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }

    context.commit('startEvent');
  },

  async dropEvent(context, payload){
    const playerId = context.rootGetters.userId
    const body = {
      playerId: playerId,
      eventId: payload.eventId,
      leagueId: payload.leagueId,
    }

    const token = context.rootGetters.getToken;
    const response = await fetch(
      endpoint + `/events/drop`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(body),
      }
    );
    
    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
    context.commit('dropEvent', playerId);
  },

  resetSelectedEventToEdit(context) {
    context.commit('resetSelectedEventToEdit')
  },

  reset(context) {
    context.commit('reset')
  }
};
