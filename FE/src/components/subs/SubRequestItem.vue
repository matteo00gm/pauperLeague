<template>
  <div class="content-item sub-item">
    <div class="player-name" v-if="mode === 'event'">{{index}} - {{ name }} {{ surname }}</div>
    <div class="player-name" v-if="mode === 'league'">{{ name }} {{ surname }}</div>
    <div class="action-buttons">
      <base-button @click="onDenyRequest" class="button" :class="'danger'">✖</base-button>
      <base-button @click="onAcceptRequest" class="button" :class="'safe'">✔</base-button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['index', 'id', 'name', 'surname', 'mode'],
  methods: {
    onAcceptRequest(){
      this.$emit('acceptRequest', {
        playerId: this.id,
      });
    },
    onDenyRequest(){
      this.$emit('denyRequest', {
        playerId: this.id,
      });
    },
  },
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

.sub-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  transition: background-color 0.3s ease;
}

.action-buttons {
  margin-left: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.button {
  height: 1.5rem;
  width: 1.5rem;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
