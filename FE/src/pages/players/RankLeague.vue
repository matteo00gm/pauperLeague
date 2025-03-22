<template>
  <div class="ranked-list-page">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <h2>{{ seasonTitle }}</h2> <!-- Update here -->
    <div class="export">
      <base-button :class="'border'" @click="exportToExcel" v-if="getLeagueRankings && getLeagueRankings.length !== 0">Scarica Excel</base-button>
    </div>
    <div class="ranked">
      <ranked-list :players="getLeagueRankings"/>
    </div>
  </div>
</template>

<script>
import RankedList from '@/components/players/RankedList.vue';
import { mapGetters } from 'vuex';
import * as XLSX from 'xlsx';

export default {
  components: {
    RankedList,
  },
  props: ['leagueId'],
  computed: {
    ...mapGetters('rankings', ['getLeagueRankings']),
    seasonTitle() {
      const today = new Date();
      const currentYear = today.getFullYear();

      // Define season boundaries
      const autumnStart = new Date(currentYear, 5, 2); // June 2
      const autumnEnd = new Date(currentYear, 10, 10); // November 10
      const winterStart = new Date(currentYear, 10, 11); // November 11
      const winterEnd = new Date(currentYear + 1, 1, 23); // February 23 next year
      const springStart = new Date(currentYear, 1, 24); // February 24
      const springEnd = new Date(currentYear, 5, 1); // June 1

      // Determine current season
      if (today >= autumnStart && today <= autumnEnd) {
        return 'Classifica Autumn Season';
      } else if (today >= winterStart && today <= winterEnd) {
        return 'Classifica Winter Season';
      } else if (today >= springStart && today <= springEnd) {
        return 'Classifica Spring Season';
      } else {
        return 'Classifica';
      }
    },
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
    exportToExcel() {
      const wb = XLSX.utils.book_new();
      const combinedData = [];
      combinedData.push(['#', 'Player', 'Score', 'OMW', 'GW', 'OGW']); // Header

      // Populate the combined data array with player rankings
      this.getLeagueRankings.forEach((player, index) => {
        combinedData.push([
          index + 1,
          `${player.surname} ${player.name}`,
          player.score,
          player.omw,
          player.gw,
          player.ogw
        ]);
      });

      const ws = XLSX.utils.aoa_to_sheet(combinedData);
      XLSX.utils.book_append_sheet(wb, ws, 'League Rankings');

      // Set the filename based on the season
      let fileName;
      switch (this.seasonTitle) {
        case 'Classifica Autumn Season':
          fileName = 'autumn_season.xlsx';
          break;
        case 'Classifica Winter Season':
          fileName = 'winter_season.xlsx';
          break;
        case 'Classifica Spring Season':
          fileName = 'spring_season.xlsx';
          break;
        default:
          fileName = 'league_rankings.xlsx';
      }

      // Export the workbook with the determined filename
      XLSX.writeFile(wb, fileName);
    },

    async loadLeagueRankings() {
      try {
        await this.$store.dispatch('rankings/loadLeagueRankings', {
          leagueId: this.leagueId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
    },
  },
  created() {
    this.loadLeagueRankings();
  },
};
</script>

<style scoped>
.ranked-list-page {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
h2 {
  text-align: center;
  margin: 1rem;
  color: #d3d3d3;
}
.export {
  display: flex;
  justify-content: center;
  width: 40%;
}

</style>
