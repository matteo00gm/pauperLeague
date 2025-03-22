export default {
  addEvent(state, payload) {
    state.futureEvents.push(payload);
    state.leagueProgram.push(payload)
  },
  setSelectedEvent(state, payload) {
    state.selectedEvent = payload;
  },
  setSelectedEventToEdit(state, payload){
    state.selectedEventToEdit=payload
  },
  resetSelectedEventToEdit(state) {
    state.selectedEventToEdit = null
  },
  setFutureEvents(state, payload) {
    state.futureEvents = payload;
  },
  setPastEvents(state, payload) {
    state.pastEvents = payload;
  },
  setMyEvents(state, payload) {
    state.myEvents = payload;
  },
  setLeagueProgram(state, payload) {
    state.leagueProgram = payload;
  },
  setLoadEventsFetchTimestamp(state) {
    state.lastEventsFetch = new Date().getTime();
  },
  setLoadLeagueProgramFetchTimestamp(state) {
    state.lastLeagueProgramFetch = new Date().getTime();
  },
  setLastEventFetchForLeague(state, payload) {
    state.lastEventFetchForLeague = payload
  },
  setLastLeagueProgramFetchForLeague(state, payload) {
    state.lastLeagueProgramFetchForLeague = payload
  },
  setLastEventIdFetched(state, payload) {
    state.lastEventIdFetched = payload
  },
  setLoadMyEventsFetchTimestamp(state) {
    state.lastMyEventsFetch = new Date().getTime();
  },
  setLastEventDetailsFetchTimestamp(state) {
    state.lastEventDetailsFetch = new Date().getTime();
  },
  setCurrentRound(state, payload){
    state.currentRound= payload;
  },
  kickPlayerFromEvent(state, payload) {
    const event = state.selectedEventToEdit
    console.log(event)
    if(event && event.started) {
      if (event) {
        event.players = event.players.map(player => {
          if (player.id === +payload.playerId) {
            return { ...player, drop: true }; // Update the player and set drop to true
          }
          return player; // Return other players unchanged
        });
        console.log(event)
      }
    } else {
      const futureEvent = state.futureEvents.find(event => event.eventId === +payload.eventId);
      if (futureEvent) {
        //removing player from the event
        futureEvent.players = futureEvent.players.filter(player => player.id !== +payload.playerId);
      }

      const leagueProgramEvent = state.leagueProgram.find(event => event.eventId === +payload.eventId);
      if (leagueProgramEvent) {
        //removing player from the event
        leagueProgramEvent.players = leagueProgramEvent.players.filter(player => player.id !== +payload.playerId);
      }
    }
  },
  dropEvent(state, payload) {
    const event = state.selectedEvent;
    if (event) {
      event.players = event.players.map(player => {
        if (player.id === +payload) {
          return { ...player, drop: true }; // Update the player and set drop to true
        }
        return player; // Return other players unchanged
      });
      console.log(event)
    }
  },
  resetCurrentRound(state) {
    state.currentRound = null;
  },
  startTimer(state) {
    const event = state.selectedEvent;
    if(event) {
      state.selectedEvent = { 
        ...state.selectedEvent, // Spread existing properties
        roundEndTime: new Date(),
      };
    }
  },
  startEvent(state) {
    const event = state.selectedEvent;
    if(event) {
      state.selectedEvent = { 
        ...state.selectedEvent, // Spread existing properties
        started: true, // Set started to true
      };
    }
  },
  updateEvent(state, payload) {

    const leagueEvent = state.leagueProgram.find(event => event.eventId === payload.eventId);
    if(leagueEvent) {
      leagueEvent.name = payload.name
      leagueEvent.date = payload.date
      leagueEvent.cap = payload.cap
      leagueEvent.location = payload.location
    }

    const event = state.futureEvents.find(event => event.eventId === payload.eventId);
    if(event) {
      event.name = payload.name
      event.date = payload.date
      event.cap = payload.cap
      event.location = payload.location
    }

    const myEvent = state.myEvents.find(event => event.eventId === payload.eventId);
    if(myEvent) {
      myEvent.name = payload.name
      myEvent.date = payload.date
      myEvent.cap = payload.cap
      myEvent.location = payload.location
    }
  },
  addEventSubRequest(state, payload) {
    const event = state.futureEvents.find(event => event.eventId === payload.eventId);
    const selectedEvent = state.selectedEvent;
    if(event) {
      event.pendingEventSubs = payload.event.pendingEventSubs
    }
    if(selectedEvent) {
      selectedEvent.pendingEventSubs = payload.event.pendingEventSubs
    }
  },
  subscribePlayerToEvent(state, payload) {
    const myEvent = state.myEvents.find(event => event.id === payload.eventId);
    const futureEvent = state.futureEvents.find(event => event.id === payload.eventId);
    const selectedEventToEdit = state.selectedEventToEdit
    if (myEvent) {
      myEvent.players.push(payload.player);
    }
    if (futureEvent) {
      futureEvent.players.push(payload.player);
    }
    if(selectedEventToEdit) {
      selectedEventToEdit.players.push(payload.player);
    }
  },
  removeEventSubRequest(state, payload) {
    const myEvent = state.myEvents.find(event => event.id === +payload.eventId);
    const futureEvent = state.futureEvents.find(event => event.id === +payload.eventId);
    const selectedEventToEdit = state.selectedEventToEdit
    if (myEvent) {
      myEvent.pendingEventSubs = myEvent.pendingEventSubs.filter(player => player.id !== +payload.playerId);
    }
    if (futureEvent) {
      futureEvent.pendingEventSubs = futureEvent.pendingEventSubs.filter(player => player.id !== +payload.playerId);
    }
    if(selectedEventToEdit) {
      selectedEventToEdit.pendingEventSubs = selectedEventToEdit.pendingEventSubs.filter(player => player.id !== +payload.playerId);
    }
  },

  reset(state) {
    state.lastEventsFetch = null,
    state.lastEventFetchForLeague = null,
    state.lastLeagueProgramFetchForLeague = null,
    state.lastMyEventsFetch = null,
    state.lastLeagueProgramFetch = null,
    state.futureEvents = [],
    state.pastEvents = [],
    state.myEvents = [],
    state.leagueProgram = [],
    state.selectedEvent = null,
    state.selectedEventToEdit = null,
    state.currentRound = null,
    state.lastEventIdFetched = null,
    state.lastEventDetailsFetch = null
  }
};
