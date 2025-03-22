<template>
  <base-swipe v-if="!isLoading">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <div class="tournament">
      <h2>Programmazione</h2>
      <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
      <div class="scrollable">
        <EventItem
          v-for="event in getFutureEvents"
          :key="event"
          :class="'future-event'"
          :event="event"
          @click="onGoToDetails(event)"
        />
      </div>
    </div>
    <!-- Past Events -->
    <div class="tournament">
      <h2>Storico</h2>
      <base-swipe-text :side="'right'" :swipeText="rightPanelText"></base-swipe-text>
      <div class="scrollable">
        <EventItem
          v-for="event in getPastEvents"
          :key="event.eventId"
          :event="event"
          :class="'past-event'"
          @click="onGoToDetails(event)"
        />
      </div>
    </div>
  </base-swipe>
</template>

<script>
import EventItem from '@/components/events/EventItem.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import { mapGetters } from 'vuex';

export default {
  props: ['leagueId'], //from the url
  computed: {
    ...mapGetters('events', ['getFutureEvents', 'getPastEvents']),
  },
  components: {
    EventItem,
    BaseSwipe,
    BaseSwipeText
  },
  data() {
    return {
      error: null,
      leftPanelText: 'swipe → per lo storico',
      rightPanelText: 'swipe ← per i tornei in programma',
      isLoading: false,
    };
  },
  created() {
    this.loadAllEvents()
  },
  methods: {
    handleError() {
      this.error = null;
    },
    onGoToDetails(event) {
      this.$router.push(this.$route.path + '/' + event.eventId);
    },
    async loadAllEvents() {
      this.isLoading = true
      try {
        await this.$store.dispatch('events/loadEvents', {
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.isLoading = false
    },
  },
};
</script>

<style scoped>

.tournament {
  min-width: 100%;
  padding: 20px;
  overflow: hidden; /* Enable vertical scrolling */
  display: flex;
  flex-direction: column;
}

.scrollable {
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none; /* Hide scrollbar for Firefox */
}

.scrollable::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

h2 {
  margin: 20px;
  font-size: 26px; /* Larger heading */
  text-align: center;
  color: #f0f0f0; /* Brightened color for headings */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5); /* Shadow for depth */
}

@media (max-width: 600px) {
  h2 {
    font-size: 24px; /* Adjusted font size */
  }
}
</style>
