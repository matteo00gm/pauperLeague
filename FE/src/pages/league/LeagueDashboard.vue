<template>
  <base-swipe v-if="isAuthenticated && isAdmin">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <div class="info">
      <base-swipe-text side="left" :swipeText="leftPanelText"></base-swipe-text>
      <div class="header">
        <h1>{{ name }}</h1>
        <div class="buttons">
          <base-button @click="onGoToEventManagement" class="border">Gestione Tornei</base-button>
          <base-button @click="onGoToEventCreation">Crea Torneo</base-button>
        </div>
      </div>
      <h2>Richieste di iscrizione</h2>
      <!-- Scrollable Requests Section -->
      <div class="requests">
        <div v-if="getLeagueSubRequests && getLeagueSubRequests.length > 0">
          <sub-request-item
            @acceptRequest="acceptRequest"
            @denyRequest="denyRequest"
            v-for="request in getLeagueSubRequests"
            :key="request"
            :id="request.playerId"
            :name="request.playerName"
            :surname="request.playerSurname"
            :mode="'league'"
          ></sub-request-item>
        </div>
        <div v-else>
          <h5>Qui verranno mostrate le richieste di iscrizione</h5>
        </div>
      </div>
      
    </div>

    <div class="info">
      <div>
        <base-swipe-text side="right" :swipeText="rightPanelText"></base-swipe-text>
        <h3>Espelli giocatori dalla lega</h3>
      </div>
      <!-- Scrollable Players Section -->
      <div class="players-section">
        <div v-if="members && members.length > 0">
          <div class="players-container">
            <player-item @removePlayer="removeMember"
              v-for="member in members"
              :key="member"
              :player="member"
              :leagueId="selectedLeague.id"
              :editMode="true"
            ></player-item>
          </div>
        </div>
        <div v-else>
          <h5>Nessun iscritto oltre agli amministratori..</h5>
        </div>
      </div>
    </div>
  </base-swipe>
</template>

<script>
import SubRequestItem from '@/components/subs/SubRequestItem.vue';
import PlayerItem from '@/components/players/PlayerItem.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import { mapGetters } from 'vuex';

export default {
  components: {
    PlayerItem,
    SubRequestItem,
    BaseSwipe,
    BaseSwipeText
  },
  props: ['leagueId'], // League ID prop
  data() {
    return {
      error: null,
      selectedLeague: null,
      members: [],
      admins: [],
      leftPanelText: 'swipe → per gli iscritti',
      rightPanelText: 'swipe ← per la gestione',
    };
  },
  computed: {
    ...mapGetters('leagues', ['getSelectedLeague']),
    ...mapGetters('subs', ['getLeagueSubRequests']),
    name() {
      return this.selectedLeague ? this.selectedLeague.name : '';
    },
    description() {
      return this.selectedLeague ? this.selectedLeague.description : '';
    },
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    isAdmin() {
      return this.selectedLeague ? this.selectedLeague.admins ? this.selectedLeague.admins.some(admin => admin.id == this.$store.getters.userId) : false : false;
    },
  },
  methods: {
    handleError() {
      this.error = null;
    },
    onGoToEventCreation() {
      this.$store.dispatch('events/resetSelectedEventToEdit');
      this.$router.push(this.$route.path + '/new');
    },
    onGoToEventManagement() {
      this.$router.push(this.$route.path + '/events');
    },
    loadMembers() {
      this.admins = this.selectedLeague.admins
      this.members = this.selectedLeague.players

      // Remove admins from members to prevent them from being expelled
      const adminsEmails = new Set(this.admins.map(b => b.email));
      this.members = this.members.filter(a => !adminsEmails.has(a.email));
    },
    setSelectedLeague() {
      this.selectedLeague = this.getSelectedLeague
      this.loadRequests()
      this.loadMembers()
      if (!this.isAdmin) {
        this.$router.push("/counterspell")
      }
    },
    async acceptRequest(payload) {
      try {
        await this.$store.dispatch('subs/acceptRequest', {
          leagueId: this.leagueId,
          playerId: payload.playerId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.loadMembers()
    },
    async denyRequest(payload) {
      try {
        await this.$store.dispatch('subs/denyRequest', {
          leagueId: this.leagueId,
          playerId: payload.playerId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async removeMember(requestDetails) {
      try {
        await this.$store.dispatch('leagues/removeMember', {
          leagueId: this.selectedLeague.id,
          playerId: requestDetails.playerId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.loadMembers()
    },
    async loadRequests() {
      try {
        await this.$store.dispatch('subs/findSubsByLeagueId', {
          leagueId: this.leagueId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    async loadSelectedLeague() {
      try {
        await this.$store.dispatch('leagues/findLeagueById', {
          leagueId: this.leagueId,
        });
        this.setSelectedLeague()
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
  created() {
    if (!this.getSelectedLeague) {
      this.loadSelectedLeague()
    } else {
      this.setSelectedLeague()
    }
  },
};
</script>

<style scoped>
.info {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100%;
  padding: 1rem;
}

/* Sticky header */
.header {
  position: sticky;
  top: 0;
  width: 100%;
  z-index: 10;
  text-align: center;
  margin-bottom: 2.5rem;
}

/* Make .players-section and .requests scrollable */
.players-section, .requests {
  width: 100%;
  max-width: 600px;
  overflow-y: auto;
  scrollbar-width: none;
}

.players-section::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

.requests::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}


/* Original styles */
h1 {
  font-size: 32px;
  font-weight: bold;
  color: #e0e0e0;
  margin: 1rem 0;
}

.buttons {
  display: flex;
  margin-top: 2rem;
  margin-bottom: 1.5rem;
  gap: 5px;
}

h2 {
  margin: 20px 0;
  font-size: 26px;
  text-align: center;
  color: #f0f0f0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

h3 {
  color: var(--red);
  font-size: 20px;
  margin: 2rem 0;
}

h5 {
  text-align: center;
  color: #f0f0f0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.players-container {
  width: 100%;
  display: flex;
  flex-direction: column;
}
</style>
