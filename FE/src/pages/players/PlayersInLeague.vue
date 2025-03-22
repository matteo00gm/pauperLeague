<template>
  <div class="player-list">
    <h1>Iscritti {{leagueName}}</h1>
    <div class="players-container">
      <player-item
        v-for="player in players"
        :key="player.id"
        :player="player"
      />
    </div>
  </div>
</template>

<script>
import PlayerItem from '@/components/players/PlayerItem.vue';
import { mapGetters } from 'vuex';

export default {
  computed: {
    ...mapGetters('leagues', ['getSelectedLeague']),
    leagueName() {
      return this.getSelectedLeague ? this.getSelectedLeague.name : ''
    }
  },
  data() {
    return {
      players: [],
    };
  },
  components: {
    PlayerItem,
  },
  created() {
    if(this.getSelectedLeague){
      this.players = this.getSelectedLeague.players
    } else {
      this.$router.replace('/leagues')
    }
  },
};
</script>

<style scoped>
.player-list {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
}

.players-container {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  scrollbar-width: none;
}

.players-container.scrollable-list::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

h1 {
  margin-bottom: 2rem;
  font-size: 1.5rem;
  text-align: center;
}

.list-enter-active, .list-leave-active {
  transition: opacity 0.5s;
}
.list-enter, .list-leave-to {
  opacity: 0;
}
</style>
