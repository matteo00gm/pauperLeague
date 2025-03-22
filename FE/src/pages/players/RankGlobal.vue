<template>
  <div class="ranked-list-page">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <h2>Classifica Generale</h2>
    <ranked-list :players="getGlobalRankings"/>
  </div>
</template>

<script>
import RankedList from '@/components/players/RankedList.vue';
import { mapGetters } from 'vuex';

export default {
  components: {
    RankedList,
  },
  computed: {
    ...mapGetters('rankings', ['getGlobalRankings']),
  },
  data() {
    return {
      error: null,
    };
  },
  methods: {
    handleError() {
      this.error = null;
    },
    async loadGlobalRankings() {
      try {
        await this.$store.dispatch('rankings/loadGlobalRankings');
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
  created() {
    this.loadGlobalRankings();
  },
};
</script>

<style scoped>
.ranked-list-page {
  overflow: hidden;
  color: #333;
  height: 100%;
  width: 100%;
}
h2 {
  text-align: center;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  color: #d3d3d3;
}
</style>
