<template>
  <div class="container" v-if="!isLoading">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <h2>Seleziona per modificare</h2>
    <div class="tournament-wrapper" v-if="events.length > 0">
      <div class="tournament" >
        <EventItem
          v-for="event in events"
          :key="event"
          :class="'edit-event'"
          :event="event"
          @click="onGoToEdit(event)"
        />
      </div>
    </div>
    <div class="tournament" v-else>
      <h2>Nessun torneo in programma!</h2>
    </div>
  </div>
  <div v-else></div>
</template>

<script>
import EventItem from '@/components/events/EventItem.vue';
import { mapGetters } from 'vuex';

export default {
  props: ['leagueId'], //i get this from the url -> defined in the router
  computed: {
    ...mapGetters('events', ['getLeagueProgram']),
    ...mapGetters('leagues', ['getSelectedLeague']),
  },
  data() {
    return {
      events: [],
      isLoading: false,
      error: null,
    }
  },
  components: {
    EventItem,
  },
  methods: {
    handleError() {
      this.error = null;
    },
    onGoToEdit(event) {
      this.$store.commit('events/setSelectedEventToEdit', event)
      this.$router.push(this.$route.path + '/edit');
    },
    setEvents() {
      this.events = this.getLeagueProgram;
    },
    async loadEvents() {
      this.isLoading = true
      try {
        await this.$store.dispatch('events/loadLeagueProgram', {
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.setEvents();
      this.isLoading = false
    },
  },
  created() {
    if(!this.getSelectedLeague) {
      this.$router.back()
    }
    this.loadEvents()
  },
};
</script>

<style scoped>
.container {
  max-width: 600px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  position: relative; /* Required for absolute positioning of children */
  display: flex;
  height: 100%; /* Ensure it takes the full height */
  overflow-y: auto; /* Enable vertical scrolling */
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
}

.tournament-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.5s ease;
  height: 100%; /* Ensure it takes the full height */
  padding-bottom: 20px;
  overflow-y: auto;
  scrollbar-width: none; /* Hide scrollbar for Firefox */
  width: 100%;
  padding: 0 1.5rem;
}

.tournament-wrapper::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

.tournament {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  max-width: 400px;
  scrollbar-width: none; /* Hide scrollbar for Firefox */
}

.tournament::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

h2 {
  margin: 2rem;
  font-size: 26px; /* Larger heading */
  text-align: center;
  color: #f0f0f0; /* Brightened color for headings */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5); /* Shadow for depth */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>
