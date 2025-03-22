<template>
  <div v-if="!this.getSelectedLeague && !this.getSelectedEventToEdit" class="container">
  </div>
  <div v-else class="container">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <div v-if="eventToEdit" class="sub-container">
      <div class="players-container" v-if="eventToEdit.started">
        <h2>Giocatori iscritti</h2>
        <player-item
          @removePlayer="removePlayer(player.id)"
          v-for="player in players"
          :key="player"
          :player="player"
          :editMode="!player.drop"
          :dropped="player.drop"
        ></player-item>
      </div>
      <base-swipe v-else>
        <div class="panel">
          <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
          <event-form @save-data="saveData" @delete-event="deleteEvent"></event-form>
        </div>
        <div class="panel">
          <players-management :pendingEventSubs="pendingEventSubs" :players="players" :eventId="eventToEdit.eventId" :leagueId="leagueId">
          </players-management>
        </div>
      </base-swipe>
    </div>
    <div v-else>
      <event-form @save-data="saveData"></event-form>
    </div>
  </div>
</template>

<script>
import EventForm from '@/components/events/EventForm.vue';
import PlayersManagement from '@/components/players/PlayersManagement.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import PlayerItem from '@/components/players/PlayerItem.vue';
import { mapGetters } from 'vuex';

export default {
  props: ['leagueId'], // i get this from the url -> defined in the router
  components: {
    EventForm,
    PlayersManagement,
    BaseSwipe,
    BaseSwipeText,
    PlayerItem
  },
  computed: {
    ...mapGetters('events', ['getSelectedEventToEdit']),
    ...mapGetters('leagues', ['getSelectedLeague']),

    players() {
      return this.eventToEdit ? this.eventToEdit.players : [];
    },
    pendingEventSubs() {
      return this.eventToEdit ? this.eventToEdit.pendingEventSubs : [];
    },
  },
  data() {
    return {
      error: null,
      eventToEdit: null,
      leftPanelText: 'swipe → per gestire le iscrizioni',
    };
  },
  created() {
    if(!this.getSelectedLeague && !this.getSelectedEventToEdit) {
      this.$router.back()
    }
    if (this.getSelectedEventToEdit) {
      this.eventToEdit = this.getSelectedEventToEdit;
    }
  },
  methods: {
    handleError() {
      this.error = null;
    },
    saveData(formData) {
      if (this.eventToEdit) {
        formData.leagueId = this.leagueId;
        formData.eventId = this.eventToEdit.eventId
        this.editEvent(formData);
      } else {
        formData.leagueId = this.leagueId;
        this.addEvent(formData);
      }
    },

    async deleteEvent(data) {
      try {
        await this.$store.dispatch('events/deleteEvent', {
          eventId: data.eventId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.$router.push('/leagues/'+this.leagueId+'/manage'); //todo this.$router.push('/counterspell');
    },

    async addEvent(data) {
      try {
        await this.$store.dispatch('events/addEvent', data);
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.$router.push('/leagues/'+this.leagueId+'/manage');
    },

    async editEvent(data) {
      try {
        await this.$store.dispatch('events/editEvent', data);
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.$router.push('/leagues/'+this.leagueId+'/manage/events');
    },
    async removePlayer(playerId) {
      try {
        await this.$store.dispatch('events/kickPlayerFromEvent', {
          eventId: this.eventToEdit.eventId,
          playerId: playerId,
          leagueId: this.leagueId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
};
</script>

<style scoped>
.container {
  height: 100%;
}

h2 {
  text-align: center;
  margin: 1.5rem;
}

.sub-container {
  height: 100%;
  overflow-y: auto; /* Enable vertical scrolling */
  scrollbar-width: none;
  display: flex; /* Ensure it behaves properly */
  flex-direction: column; /* Stack children vertically */
  align-items: center; /* Center children horizontally */
}

.sub-container::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

.players-container {
  height: auto; /* Adjust based on content */
  width: 80%; /* Keep your width */
  overflow-y: auto; /* Enable vertical scrolling if needed */
  scrollbar-width: none; /* Hide scrollbar */
}

.players-container::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

.panel {
  min-width: 100%;
  height: 100%;
}

p {
  margin-top: 0.5rem;
}
</style>
