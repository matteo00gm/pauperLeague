<template>
  <div class="main-container">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <base-swipe v-if="!isLoadingPage">
      <div class="panel">
        <div class="container">
          <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
          <div class="header">
            <h2>Iscrizioni</h2>
            <div class="buttons">
              <base-button link :to="myLeagues">Iscrizioni lega</base-button>
            </div>
          </div>
          <div class="my-events" v-if="hasSubscriptions">
            <event-item
                v-for="event in myEvents"
                :key="event"
                :class="'future-event'"
                :event="event"
                @click="onGoToDetails(event)"
              />
          </div>
          <div v-else class="placeholder">
            <p>Qui verranno visualizzate le tue iscrizioni ai tornei.</p>
          </div>
        </div>
      </div>
      <div class="panel">
        <div class="container">
          <base-swipe-text :side="'right'" :swipeText="rightPanelText"></base-swipe-text>
          <div class="header">
            <h2>Richieste</h2>
            <div class="buttons">
              <base-button link :to="myLeagues">Iscrizioni lega</base-button>
            </div>
          </div>
          <div class="my-events" v-if="hasSubRequests">
            <event-item
                v-for="event in myEventSubReq"
                :key="event"
                :class="'pending-event'"
                :event="event"
                @click="onGoToDetails(event)"
              />
          </div>
          <div v-else class="placeholder">
            <p>Qui verranno visualizzate le tue richieste di iscrizione ai tornei.</p>
          </div>
        </div>
      </div>
    </base-swipe>
  </div>
</template>

<script>
import EventItem from '@/components/events/EventItem.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import { mapGetters } from 'vuex';


export default {
  components: {
    EventItem,
    BaseSwipe,
    BaseSwipeText
  },
  computed: {
    ...mapGetters('events', ['getMyEvents']),
    ...mapGetters('subs', ['getMyEventsSubReq']),
    
    isLoadingPage() {
      return this.isLoadingEvents || this.isLoadingSubsReq
    },
    hasSubscriptions(){
      return this.myEvents && this.myEvents.length > 0
    },
    hasSubRequests(){
      return this.myEventSubReq && this.myEventSubReq.length > 0
    },
    myLeagues() {
      return '/leagues/my-subs';
    },
  },
  data() {
    return {
      myEvents: [],
      myEventSubReq: [],
      isLoadingEvents: false,
      isLoadingSubsReq: false,
      error: null,
      leftPanelText: 'swipe → per le richieste',
      rightPanelText: 'swipe ← per le iscrizioni',
    };
  },
  methods: {
    handleError() {
      this.error = null;
    },
    onGoToDetails(event) {
      this.$router.push('/leagues/' + event.leagueId + '/events/' + event.eventId);
    },
    setEvents() {
      this.myEvents = this.getMyEvents;
    },
    setEventSubReq() {
      this.myEventSubReq = this.getMyEventsSubReq
    },
    async loadEvents() {
      this.isLoadingEvents = true
      try {
        await this.$store.dispatch('events/loadMyEvents');
        this.setEvents()
        this.isLoadingEvents = false
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async loadEventsSubReq() {
      this.isLoadingSubsReq = true
      try {
        await this.$store.dispatch('subs/loadMyEventsSubReq');
        this.setEventSubReq()
        this.isLoadingSubsReq = false
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
  created() {
    this.loadEvents();
    this.loadEventsSubReq();
  },
};
</script>

<style scoped>

.main-container {
  height: 100%;
}

.panel {
  min-width: 100%;
  height: 100%;
  padding-top: 0.5rem;
}

.container {
  display: flex;
  height: 100%; /* Ensure it takes the full height */
  min-width: 100%;
  overflow-y: auto; /* Enable vertical scrolling */
  flex-direction: column;
  align-items: center;
}

ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

h2{
  font-size: 2rem; /* Larger heading */
  text-align: center;
  color: #f0f0f0; /* Brightened color for headings */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5); /* Shadow for depth */
}

.header {
  width: 100%;
  height: 10rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.my-events {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  max-width: 400px;
  overflow-x: hidden;
  padding: 1.5rem;
}

.my-events::-webkit-scrollbar {
  width: 0; /* Hide scrollbar */
}

.buttons {
  padding: 1rem;
  width: 50%;
  height: 100%;
  display: flex;
  justify-content: space-evenly;
}

.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 1rem;
}

p {
  text-align: center;
  font-size: 1.2rem;
}
</style>
