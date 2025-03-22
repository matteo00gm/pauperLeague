<template>
  <base-swipe>
    <div class="info left">
      <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
      <table class="ranked-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Player</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(player, index) in players"
            :key="player.id"
            @click="goToProfile(player)"
            class="ranked-player"
          >
             <td>
              <img v-if="index === 0" src="@/assets/gold.png" alt="Gold Medal" class="gold-icon" />
              <img v-else-if="index === 1" src="@/assets/silver.png" alt="Silver Medal" class="silver-icon" />
              <img v-else-if="index === 2" src="@/assets/bronze.png" alt="Bronze Medal" class="bronze-icon" />
              <span v-else>{{ index + 1 }}</span>
            </td>
            <td class="player-name">
              {{ player.surname }} {{ player.name }}
            </td>
            <td>{{ player.score }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="info right">
      <base-swipe-text :side="'right'" :swipeText="rightPanelText"></base-swipe-text>
      <table class="ranked-table">
        <thead>
          <tr>
            <th>Player</th>
            <th>Score</th>
            <th>OMW</th>
            <th>GW</th>
            <th>OGW</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(player) in players"
            :key="player.id"
            @click="goToProfile(player)"
            class="ranked-player"
          >
            <td class="player-name">
              {{ player.surname }} {{ player.name }}
            </td>
            <td>{{ player.score }}</td>
            <td>{{ player.omw }}</td>
            <td>{{ player.gw }}</td>
            <td>{{ player.ogw }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </base-swipe>
</template>

<script>
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'

export default {
  components: { 
    BaseSwipe,
    BaseSwipeText 
  },
  props: ['players'],
  data() {
    return {
      leftPanelText: 'swipe → per i dettagli',
      rightPanelText: 'swipe ← per la classifica',
    };
  },
  methods: {
    goToProfile(player) {
      this.$router.push(`/profile/${player.id}`);
    },
  },
};
</script>

<style scoped>

.ranked-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed; /* Ensure the table layout is consistent */
}

.ranked-table thead {
  display: table;
  width: 100%; /* Make sure the thead is as wide as the table */
  table-layout: fixed;
  position: sticky;
  top: 0; /* Fix header to top */
  z-index: 2; /* Keep header above scrollable body */
  background-color: #ffbc6b72;
}

.ranked-table tbody {
  display: block; /* Make tbody scrollable */
  max-height: 65vh; /* Adjust height to make it scrollable */
  overflow-y: auto; /* Enable vertical scrolling */
  scrollbar-width: none; /* Firefox scrollbar removal */
}

.ranked-table tbody::-webkit-scrollbar {
  width: 0; /* Chrome/Safari hides scrollbar */
}

.ranked-table tbody tr {
  display: table;
  width: 100%; /* Ensure rows take full width of the table */
  table-layout: fixed;
}

.ranked-table th, 
.ranked-table td {
  padding: 0.5rem;
  color: #d3d3d3;
  text-align: center;
  white-space: nowrap; /* Prevent text wrapping */
  overflow: hidden; /* Hide overflowing text */
  text-overflow: ellipsis; /* Show ellipsis (...) for overflowing text */
}

th {
  background-color: rgba(30, 30, 30, 0.8);
  color: #d3d3d3;
}

/* Styles specific to the left panel */
.info {
  min-width: 100%;
  overflow: hidden;
}

.info.left,
.info.right {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
}

/* Medal Icons */
.gold-icon {
  width: 40px;
  height: 38px;
  vertical-align: middle;
}
.silver-icon {
  width: 40px;
  height: 40px;
  vertical-align: middle;
}
.bronze-icon {
  width: 34px;
  height: 34px;
  vertical-align: middle;
}


/* Hover effects for players on small screens */
@media (max-width: 768px) {
  .ranked-table th,
  .ranked-table td {
    font-size: 0.9rem;
  }

  .ranked-player {
    backdrop-filter: blur(1rem);
  }
}
</style>
