<template>
  <div class="content-item player-item" v-if="editMode">
    <div class="player-name">{{ player.surname }} {{ player.name }}</div>
    <div class="action-buttons">
      <base-button @click.stop="onRemovePlayer" @touchstart.stop class="remove-button" :class="'danger'">✖</base-button>
    </div>
  </div>
  <div class="content-item player-item" v-else @click="goToProfile">
    <div class="player-name" :class="{'dropped': dropped }">{{ player.surname }} {{ player.name }}</div>
  </div>
</template>

<script>
export default {
  props: {
    player: {
      type: Object,
      required: true
    },
    editMode: {
      type: Boolean,
      required: false
    },
    dropped: {
      type: Boolean,
      required: false
    },
  },
  methods: {
    goToProfile() {
      this.$router.replace(`/profile/${this.player.id}`);
    },
    onRemovePlayer(){
      this.$emit('removePlayer', {
        playerId: this.player.id,
      });
    },
  }
};
</script>

<style scoped>
.content-item {
  backdrop-filter: blur(30px);
  border: 1px solid #282828;
  padding: 15px;
  border-radius: 10px;
  font-size: 1.2rem;
  text-align: center;
  cursor: pointer;
}

.player-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  transition: background-color 0.3s ease;
}

.remove-button {
  height: 1.5rem;
  width: 1.5rem;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dropped {
  text-decoration: line-through; /* Strikethrough effect */
  color: gray; /* Optional: change text color */
}

/* Remove tap highlight color on mobile for better UX */
@media (hover: none) {
  .content-item {
    -webkit-tap-highlight-color: transparent;
  }
}
</style>
