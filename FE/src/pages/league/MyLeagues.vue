<template>
  <div class="container" v-if="!isLoading">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <div class="header">
      <h2>Iscrizioni</h2>
      <div class="buttons">
        <base-button link :to="leagueList">Unisciti</base-button>
        <base-button link :class="'border'" :to="newLeagueLink">Crea</base-button>
      </div>
    </div>
    <div class="my-leagues" v-if="hasSubscriptions">
      <league-list
      :leagues="myLeagues"
      @leave="leaveLeague"></league-list>
    </div>
    <div v-else-if="!hasSubscriptions">
      <p>Sembra che tu non faccia ancora parte di una lega..</p>
    </div>
  </div>
</template>

<script>
import LeagueList from '@/components/leagues/LeagueList.vue';
import { mapGetters } from 'vuex';


export default {
  components: {
    LeagueList,
  },
  computed: {
    ...mapGetters('leagues', ['getMyLeagues']),
    hasSubscriptions(){
      return this.myLeagues && this.myLeagues.length > 0
    },
    leagueList() {
      return '/leagues';
    },
    newLeagueLink() {
      return '/leagues/new';
    },
  },
  data() {
    return {
      myLeagues: [],
      isLoading: false,
      error: null,
    };
  },
  methods: {
    handleError() {
      this.error = null;
    },
    setLeagues() {
      this.myLeagues = this.getMyLeagues;
    },
    leaveLeague() {
      this.setLeagues()
    },
    async loadLeagues() {
      try {
        this.isLoading = true
        await this.$store.dispatch('leagues/loadMyLeagues');
        this.setLeagues()
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.isLoading = false
    },
  },
  created() {
    this.loadLeagues()
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100%; /* Ensure it takes the full height */
  min-width: 100%;
  padding: 2rem 2.5rem;
  overflow-y: auto; /* Enable vertical scrolling */
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
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

p {
  margin-bottom: 2rem;
  font-size: 1.5rem; /* Larger heading */
  text-align: center;
}

.header {
  width: 100%;
  height: 10rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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

.buttons {
  padding: 2rem;
  width: 100%;
  height: 100%;
  display: flex;
  gap: 1rem;
}
</style>
