<template>
  <div class="profile-page" v-if="!isLoading">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    
    <!-- Ensure the profile header is always at the top -->
    <header class="profile-header">
      <h1>{{ player.name }} {{ player.surname }}</h1>
    </header>

    <h2 v-if="subscriptions.length > 0">Iscrizioni</h2>
    <div class="my-leagues" v-if="subscriptions.length > 0">
      <league-list :leagues="subscriptions"></league-list>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import LeagueList from '@/components/leagues/LeagueList.vue';

export default {
  props: ['playerId'],
  components: {
    LeagueList,
  },
  computed: {
    ...mapGetters('players', ['getSelectedPlayer']),

    subscriptions() {
      return this.player ? this.player.leagues : [];
    }
  },
  created() {
    this.loadProfile();
  },
  data() {
    return {
      player: null,
      error: null,
      isLoading: false,
    };
  },
  methods: {
    handleError() {
      this.error = null;
    },
    setSelectedPlayer() {
      this.player = this.getSelectedPlayer;
    },
    async loadProfile() {
      this.isLoading = true;
      try {
        await this.$store.dispatch('players/loadPlayer', {
          playerId: this.playerId
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.setSelectedPlayer();
      this.isLoading = false;
    },
  }
};
</script>

<style scoped>
.profile-page {
  display: flex;
  height: 100%; /* Ensure it takes the full height */
  top: 0;
  padding: 2rem;
  overflow-y: auto; /* Enable vertical scrolling */
  flex-direction: column;
  align-items: center;
  justify-content: flex-start; /* Align items to the top */
}

.profile-header {
  text-align: center; /* Center the header text */
}

h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

h2 {
  margin: 2rem;
}

.my-leagues {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 400px;
  margin: 0 auto;
  overflow-x: hidden;
}

.my-leagues::-webkit-scrollbar {
  width: 0; /* Hide scrollbar */
}
</style>
