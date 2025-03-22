<template>
  <base-swipe>
    <div class="panel">
      <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
      <h2>Richieste di partecipazione</h2>
      <div class="players-container">
        <sub-request-item
          @acceptRequest="acceptRequest"
          @denyRequest="denyRequest"
          v-for="(request, index) in pendingEventSubs"
          :key="request"
          :index="index+1"
          :id="request.id"
          :name="request.name"
          :surname="request.surname"
          :mode="'event'"
        ></sub-request-item>
      </div>
    </div>

    <div class="panel">
      <div>
        <base-swipe-text :side="'right'" :swipeText="rightPanelText"></base-swipe-text>
      </div>
      <h2>Giocatori iscritti</h2>
      <div class="players-container">
        <player-item
          @removePlayer="removePlayer"
          v-for="player in players"
          :key="player"
          :player="player"
          :editMode="true"
        ></player-item>
      </div>
    </div>
  </base-swipe>
</template>


<script>

import PlayerItem from '@/components/players/PlayerItem.vue';
import SubRequestItem from '@/components/subs/SubRequestItem.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'

export default {
  components: {
    PlayerItem,
    SubRequestItem,
    BaseSwipe,
    BaseSwipeText
  },
  props: ['players', 'pendingEventSubs', 'eventId', 'leagueId'],
  data() {
    return {
      leftPanelText: 'swipe → per gli iscritti',
      rightPanelText: 'swipe ← per tornare alla modifica',
    }
  },
  methods: {
    async acceptRequest(payload) {
      try {
        await this.$store.dispatch('subs/acceptRequest', {
          leagueId: this.leagueId,
          eventId: this.eventId,
          playerId: payload.playerId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async denyRequest(payload) {
      try {
        await this.$store.dispatch('subs/denyRequest', {
          leagueId: this.leagueId,
          eventId: this.eventId,
          playerId: payload.playerId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async removePlayer(requestDetails) {
      try {
        await this.$store.dispatch('events/kickPlayerFromEvent', {
          eventId: this.eventId,
          playerId: requestDetails.playerId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
}
</script>

<style scoped>

h2 {
  text-align: center;
  padding-top: 0.5rem;
  margin-bottom: 1.5rem;
}

.panel {
  min-width: 100%;
  height: 100%; /* Make each panel fill the viewport height */
  display: flex;
  flex-direction: column;
  align-items: center;
}

.players-container {
  display: flex;
  flex-direction: column;
  width: 80%;
  max-width: 600px;
  height: 100%;
  overflow-y: auto; /* Enable vertical scrolling */
  scrollbar-width: none;
}

.players-container::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

p {
  margin-top: 0.5rem;
}
</style>