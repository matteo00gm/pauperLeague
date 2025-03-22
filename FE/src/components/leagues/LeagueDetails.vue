<template>
  <base-dialog :show="!!error" title="Errore" @close="handleError">
    <p>{{ error }}</p>
  </base-dialog>
  <h2>{{ name }}</h2>
  <p v-if="desc != ''">{{ desc }}</p>
  <div class="button-container">
    <base-button
      v-if="isAuthenticated && !isSubscribed && !hasSubmittedReq"
      @click="subscribe">Richiedi iscrizione</base-button>
    <base-button
      @click="onGoToMemberList">Iscritti</base-button>
    <base-button
      @click="onGoToLeagueRankList">Classifica</base-button>
    <base-button
      @click="onGoToLeagueEvents">Tornei</base-button>
    <base-button :class="'admin'"
      v-if="isAuthenticated && isAdmin"
      @click="onGoToAdminDashboard">Gestisci</base-button>
    <base-button link :class="'border'" to="/auth" v-if="!isAuthenticated">Accedi per iscriverti</base-button>
    <base-button :class="'danger'" v-if="isAuthenticated && isSubscribed" @click="leaveLeague">Abbandona</base-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  emits: ['leave'],
  props: ['id', 'name', 'desc'],
  data() {
    return {
      selectedLeague: null,
      error: null,
    };
  },
  computed: {
    ...mapGetters('leagues', ['getSelectedLeague']),
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    isSubscribed() {
      return this.selectedLeague && this.selectedLeague.players
        ? this.selectedLeague.players.some(player => player.id === +this.$store.getters.userId) 
        : false;
    },
    hasSubmittedReq() {
      return this.selectedLeague && (this.selectedLeague.players || this.selectedLeague.leagueSubRequests)
        ? this.selectedLeague.players.some(player => player.id === +this.$store.getters.userId) || this.selectedLeague.leagueSubRequests.some(player => player.id === +this.$store.getters.userId) 
        : false;
    },
    isAdmin() {
      return this.selectedLeague ? this.selectedLeague.admins ? this.selectedLeague.admins.some(admin => admin.id == this.$store.getters.userId) : false : false;
    },
  },
  created() {
    this.selectedLeague = this.getSelectedLeague;
  },
  methods: {
    handleError() {
      this.error = null;
    },
    onGoToMemberList() {
      return this.$router.push('/leagues/members');
    },
    onGoToLeagueRankList() {
      return this.$router.push('/leagues/'+ this.id + '/rankings');
    },
    onGoToLeagueEvents() {
      this.$router.push('/leagues/'+ this.id + '/events');
    },
    onGoToAdminDashboard() {
      this.$router.push('/leagues/'+ this.id + '/manage');
    },
    async subscribe() {
      try {
        await this.$store.dispatch('leagues/sendLeagueSubReq', {
          leagueId: this.id,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
    leaveLeague() {
      this.$emit('leave');
    }
  }
};
</script>

<style scoped>
h2 {
  font-size: 1.5rem;
}

p {
  font-size: 1rem;
  line-height: 1.2;
}

.button-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  margin-top: 10px; /* Adjust as needed */
  width: 85%;
}

@media (max-width: 768px) {
  h2 {
      font-size: 1.3rem;
  }

  p {
      font-size: 0.9rem;
  }
}
</style>
