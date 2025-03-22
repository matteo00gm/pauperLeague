<template>
  <div class="container" v-if="!isLoading">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <h2>Leghe</h2>
    <div class="content">
      <league-list
      :leagues="getLeagues"></league-list>
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
    ...mapGetters('leagues', ['getLeagues']),
  },
  data() {
    return {
      error: null,
      isLoading: false,
    }
  },
  methods: {
    async loadLeagues() {
      this.isLoading = true
      try {
        await this.$store.dispatch('leagues/loadAllLeagues');
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.isLoading = false
    },

    handleError() {
      this.error = null;
    },
  },
  created() {
    this.loadLeagues();
  },
}
</script>


<style scoped>
.container{
  width: 100%;
  display: flex;
  height: 100%; /* Ensure it takes the full height */
  overflow-y: auto; /* Enable vertical scrolling */
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
}

.content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  gap: 20px;
  width: 80%;
  max-width: 400px;
  margin: 0 auto;
  overflow-x: hidden;
}

.container::-webkit-scrollbar {
  width: 0; /* Hide scrollbar */
}

.content::-webkit-scrollbar {
  width: 0; /* Hide scrollbar */
}

h2 {
  height: 4rem;
  margin: 2rem;
  margin-bottom: calc(2rem - 20px);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
