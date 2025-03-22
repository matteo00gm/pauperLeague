<template>
  <div class="container"
       @touchstart="touchStart"
       @touchend="touchEnd">
    <div class="wrapper" :style="{ transform: `translateX(${translateX}%)` }">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      translateX: 0,
      startX: 0,
    };
  },
  methods: {
    touchStart(event) {
      this.startX = event.touches[0].clientX; // Record the starting touch position
    },
    touchEnd(event) {
      const endX = event.changedTouches[0].clientX; // Record the ending touch position
      const swipeThreshold = window.innerWidth * 0.1; // Set swipe threshold (10% of the viewport width)
      const difference = this.startX - endX; // Calculate the difference in swipe

      // Check if swipe exceeds the threshold
      if (Math.abs(difference) > swipeThreshold) {
        if (difference > 0) {
          // Swipe left - move to the left
          this.translateX = -100; // Fully translate to the left (next item)
        } else {
          // Swipe right - move back to the right
          this.translateX = 0; // Fully translate back to the initial position
        }
      }
    },
  },
};
</script>

<style scoped>
.container {
  width: 100%;
  max-width: 600px;
  height: calc(100vh - 10px); /* Increased height for visibility */
  overflow: hidden;
  position: relative; /* Required for absolute positioning of children */
}

.wrapper {
  display: flex;
  transition: transform 0.5s ease;
  height: 100%; /* Ensure it takes the full height */
}
</style>
