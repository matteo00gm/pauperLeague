<template>
  <div v-if="selectedEvent" class="container">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <base-confirm-dialog :show="!!showConfirm" title="Drop" @close="closeConfirmModal" @confirm="confirmDrop">
      <p>{{ confirmDropMessage }}</p>
    </base-confirm-dialog>
    <div v-if="isGone">
      <section class="player-list-section">
        <h2>{{name}}</h2>
        <h3>{{date}}</h3>
        <div class="rankings-container">
          <ranked-list :players="getEventRankings"/>
        </div>
      </section>
    </div>
    <base-swipe v-else>
      <div class="info left">
        <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
        <section class="event-info">
          <h1 class="event-name">{{ name }}</h1>
          <p v-if="location" class="event-description">{{ location }}</p>
          <div class="event-meta">
            <div class="meta-item">
              <span class="meta-value">{{ date }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">Iscritti: </span>
              <span class="meta-value" v-if="cap">{{ playersLength }}/{{ cap }}</span>
              <span class="meta-value" v-else>{{ playersLength }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">Richieste: </span>
              <span class="meta-value">{{ pendingSubsLength }}</span>
            </div>
          </div>
        </section>
        <div class="buttons">
          <div class="closed" v-if="!isEventStarted && isAuthenticated && isSubscribed">Sei già iscritto a questo torneo.</div>
          <base-button
            v-if="isAuthenticated && !isSubscribed && !isEventStarted"
            @click="subscribePlayer">Richiedi iscrizione</base-button>
          <base-button
            :class="'admin'"
            v-if="isAuthenticated && isAdmin && !isEventStarted && isEventToday"
            @click="startEvent">
            Inizia Torneo
          </base-button>
          <base-button
            :class="'admin'"
            v-if="isAuthenticated && isAdmin && isEventStarted && isEventToday && isTimerStopped"
            @click="startTimer">
            Avvia Timer
          </base-button>
          <div class="closed" v-if="!isAdmin && isEventStarted">Torneo iniziato, iscrizioni chiuse!</div>
          <base-button :class="'danger'"
            v-if="isAuthenticated && !didDrop && isEventStarted && isSubscribed"
            @click="showConfirmModal">Droppa</base-button>
          <base-button link to="/auth" v-if="!isAuthenticated && !isEventStarted">Accedi per iscriverti</base-button>
        </div>
      </div>
      <div class="info right">
        <base-swipe>
          <div class="panel">
            <section class="player-list-section">
              <base-swipe-text :side="'right'" :swipeText="rightPanelText"></base-swipe-text>
              <h2>Giocatori iscritti</h2>
              <div class="players-container">
                <player-item
                  v-for="player in players"
                  :key="player.id"
                  :player="player"
                />
              </div>
            </section>
          </div>
          <div class="panel">
            <section class="player-list-section">
              <h2>Richieste di iscrizione</h2>
              <div class="players-container">
                <player-item
                  v-for="player in pendingEventSubs"
                  :key="player.id"
                  :player="player"
                />
              </div>
            </section>
          </div>
        </base-swipe>
      </div>
    </base-swipe>
  </div>
</template>

<script>
import PlayerItem from '@/components/players/PlayerItem.vue';
import RankedList from '@/components/players/RankedList.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import { mapGetters } from 'vuex';

export default {
  components: {
    PlayerItem,
    RankedList,
    BaseSwipe,
    BaseSwipeText
  },
  props: ['leagueId', 'eventId'],
  data() {
    return {
      error: null,
      leftPanelText: 'swipe → per gli iscritti',
      rightPanelText: 'swipe → per le richieste',
      selectedEvent: null,
      showConfirm: false,
    };
  },
  computed: {
    ...mapGetters('events', ['getSelectedEvent']),
    ...mapGetters('rankings', ['getEventRankings']),

    name() {
      return this.selectedEvent ? this.selectedEvent.name : '';
    },
    date() {
      return this.selectedEvent ? this.selectedEvent.date : '';
    },
    cap() {
      return this.selectedEvent ? this.selectedEvent.cap : '';
    },
    location() {
      return this.selectedEvent ? this.selectedEvent.location : '';
    },
    players() {
      return this.selectedEvent ? this.selectedEvent.players : []
    },
    pendingEventSubs() {
      return this.selectedEvent ? this.selectedEvent.pendingEventSubs : []
    },
    playersLength() {
      return this.selectedEvent ? this.selectedEvent.players.length : 0
    },
    pendingSubsLength() {
      return this.selectedEvent ? this.selectedEvent.pendingEventSubs.length : 0
    },
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    didDrop() {
      return this.selectedEvent 
        ? this.selectedEvent.players.some(player => player.id === +this.$store.getters.userId && player.drop) 
        : false;
    },
    isSubscribed() {
      return this.selectedEvent ? this.selectedEvent.pendingEventSubs.some(player => player.id === +this.$store.getters.userId) || this.selectedEvent.players.some(player => player.id === +this.$store.getters.userId) : false;
    },
    isAdmin() {
      return this.selectedEvent ? this.selectedEvent.leagueAdmins.some(player => player.id === +this.$store.getters.userId) : false;
    },
    isEventStarted() {
      return this.selectedEvent ? this.selectedEvent.started : true;
    },
    isGone() {
      if(!this.selectedEvent) return false;

      const eventDate = new Date(this.selectedEvent.date);
      const today = new Date();

      today.setHours(0, 0, 0, 0);

      return eventDate < today || this.selectedEvent.ended;
    },
    isTimerStopped() {
      if(!this.selectedEvent) return false;
      return !this.selectedEvent.roundEndTime;
    },
    isEventToday(){
      if(!this.selectedEvent) return false;

      const eventDate = new Date(this.selectedEvent.date);
      const now = new Date();

      return (
        eventDate.getFullYear() === now.getFullYear() &&
        eventDate.getMonth() === now.getMonth() &&
        eventDate.getDate() === now.getDate()
      )
    },
    confirmDropMessage() {
      return "Sicuro di voler droppare il torneo?"
    },
  },
  created() {
    this.loadDetails();
  },
  methods: {
    handleError() {
      this.error = null;
    },
    closeConfirmModal() {
      this.showConfirm = false;
    },
    async confirmDrop() {
      this.showConfirm = false;
      try {
        await this.$store.dispatch('events/dropEvent', {
          eventId: this.eventId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    showConfirmModal() {
      this.showConfirm = true;
    },
    async startEvent() {
      try {
        await this.$store.dispatch('events/startEvent', {
          eventId: this.eventId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.setSelectedEvent();
    },
    async startTimer() {
      try {
        await this.$store.dispatch('events/startTimer', {
          eventId: this.eventId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.setSelectedEvent();
    },
    async loadEventRankings() {
      try {
        await this.$store.dispatch('rankings/loadEventRankings',{
          eventId: this.selectedEvent.eventId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async subscribePlayer() {
      try {
        await this.$store.dispatch('events/sendEventSubReq', {
          eventId: this.selectedEvent.eventId,
          leagueId: this.leagueId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async loadDetails() {
      try {
        await this.$store.dispatch('events/findEventById', {
          eventId: this.eventId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.setSelectedEvent();
    },
    setSelectedEvent() {
      this.selectedEvent = this.getSelectedEvent;
      if(this.isGone) {
        this.loadEventRankings();
      }
    },
  }
};
</script>

<style scoped>

.container {
  height: 100%;
}

.panel {
  min-width: 100%;
  height: 100%;
}

.buttons {
  margin-top: 3rem;
  display: flex;
  flex-direction: column;
  gap: 1rem; /* Adjust the gap between items */
}

.player-list-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  padding: 0 2rem;
  padding-bottom: 2rem;
}

h2 {
  margin: 20px 0;
  font-size: 1.5rem;
  text-align: center;
}

h3 {
  margin-bottom: 0.5rem;
  font-size: 15px;
  text-align: center;
  color: #f0f0f0;
}

.rankings-container {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.players-container {
  width: 100%;
  max-width: 600px;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  scrollbar-width: none;
}

.players-container::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

.info {
  min-width: 100%;
  overflow-y: auto; /* Enable vertical scrolling */
}

/* Styles specific to the left panel */
.info.left {
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* Align items at the top */
  align-items: center; /* Center horizontally */
}

.event-info {
  text-align: center; /* Center the text within the event info */
}

/* event name style */
.event-name {
  font-size: 32px; /* Larger font size for the event name */
  font-weight: bold; /* Make the name bold */
  color: #e0e0e0; /* Light color for contrast */
  margin: 20px 0; /* Reduced space above and below the title */
}

/* event description style */
.event-description {
  font-size: 18px; /* Adjusted font size for description */
  color: #c0c0c0; /* Softer color for description */
  margin: 20px 0; /* Increased space around the description */
}

/* Placeholder for when description is null */
.placeholder {
  font-size: 16px; /* Size for the placeholder text */
  color: #a0a0a0; /* Gray color for the placeholder text */
  margin: 15px 0; /* Space around the placeholder */
}

/* event metadata style */
.event-meta {
  display: flex;
  flex-direction: column; /* Stack player cap and date vertically */
  gap: 20px; /* Space between the meta items */
  margin: 20px 0; /* Increased space around the description */
}

.meta-item {
  font-size: 20px; /* Increased font size for meta items */
  color: #a0a0a0; /* Lighter color for meta items */
}

.meta-label {
  font-weight: bold; /* Make label text bold */
}

/* Specific styles for the date */
.meta-value {
  font-size: 20px; /* Increased font size for date */
  color: #e0e0e0; /* Bright color for contrast */
}

.closed {
  text-align: center;
}

p {
  padding-top: 0.5rem;
}
</style>

