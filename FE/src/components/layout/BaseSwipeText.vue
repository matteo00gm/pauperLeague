<template>
  <p class="swipe-info">
    <span
      class="illuminate"
      :class="side"
    >
      {{ swipeText }}
    </span>
  </p>
</template>

<script>
export default {
  props: {
    swipeText: {
      type: String,
      required: true,
    },
    side: {
      type: String,
      required: true,
    },
  },
};
</script>

<style scoped>

.swipe-info {
  text-align: center;
  color: #555;
  font-size: 12px;
  margin: 0.5rem;
}

.illuminate {
  font-size: 1rem; /* Adjusted to a smaller size */
  color: transparent; /* Start with transparent text */
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 1) 50%,
    rgba(255, 255, 255, 0) 100%
  ); /* White gradient effect */
  background-clip: text; /* Clip the background to the text */
  -webkit-background-clip: text; /* For Safari */
  background-size: 200% 100%; /* Double the size to create movement */
  animation: illuminate 3s linear infinite; /* Add animation for illumination */
}

/* Dynamic animations based on side prop */
@keyframes illuminate-left {
  0% {
    background-position: 200% 0; /* Start with the gradient off-screen to the left */
  }
  100% {
    background-position: 0 0; /* Move the gradient across to the right */
  }
}

@keyframes illuminate-right {
  0% {
    background-position: 0 0; /* Start with the gradient at the beginning */
  }
  100% {
    background-position: 200% 0; /* Move the gradient across to the left */
  }
}

/* Use class-based animation selection */
.illuminate.left {
  animation: illuminate-left 3s linear infinite; /* Left animation */
}

.illuminate.right {
  animation: illuminate-right 3s linear infinite; /* Right animation */
}
</style>
