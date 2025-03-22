<!-- src/components/LeagueItem.vue -->
<template>
  <div class="content-item" @click="showDetails">
    {{ league.name }}
  </div>
</template>

<script>
export default {
  props: {
    league: {
      type: Object,
      required: true
    },
  },
  methods: {
    showDetails() {
      this.$emit('toggle-zoom', this.league.id); // Emit event to toggle zoom
    }
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

@media (hover: none) {
  .content-item {
    -webkit-tap-highlight-color: transparent; /* Remove tap highlight color on mobile */
  }
}
</style>
