<template>
  <button v-if="!link" :class="mode">
    <slot></slot>
  </button>
  <router-link v-else :to="to" :class="mode">
    <slot></slot>
  </router-link>
</template>

<script>
export default {
  props: {
    mode: {
      type: String,
      required: false,
      default: null,
    },
    link: {
      type: Boolean,
      required: false,
      default: false,
    },
    to: {
      type: String,
      required: false,
      default: '/',
    },
  },
};
</script>

<style scoped>
@keyframes gradientMove {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

button,
a {
  padding: 12px 20px; /* Padding for better tap-target size */
  border: none;
  border-radius: 8px; /* Rounded corners for touch-friendly UI */
  color: #fff;
  font-size: 1rem; /* Base font size for mobile */
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  background: linear-gradient(45deg, var(--pink), var(--purple), var(--blue));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  box-shadow: 0 2px 10px rgba(255, 255, 255, 0.2); /* Soft glow */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 100%;
  width: 100%;
  height: 2.5rem;
}

.admin {
  padding: 12px 20px; /* Padding for better tap-target size */
  border: none;
  border-radius: 8px; /* Rounded corners for touch-friendly UI */
  color: #fff;
  font-size: 1rem; /* Base font size for mobile */
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  background: linear-gradient(45deg, var(--yellow), var(--gold));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  box-shadow: 0 2px 10px rgba(255, 255, 255, 0.2); /* Soft glow */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

.danger {
  padding: 12px 20px; /* Padding for better tap-target size */
  border: none;
  border-radius: 8px; /* Rounded corners for touch-friendly UI */
  color: #fff;
  font-size: 1rem; /* Base font size for mobile */
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  background: linear-gradient(45deg, var(--redish), var(--red));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  box-shadow: 0 2px 10px rgba(255, 255, 255, 0.2); /* Soft glow */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

.safe {
  padding: 12px 20px; /* Padding for better tap-target size */
  border: none;
  border-radius: 8px; /* Rounded corners for touch-friendly UI */
  color: #fff;
  font-size: 1rem; /* Base font size for mobile */
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  background: linear-gradient(45deg, var(--greenish), var(--green));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  box-shadow: 0 2px 10px rgba(255, 255, 255, 0.2); /* Soft glow */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

.border {
  color: white;
  text-align: center;
  --borderWidth: 1px;
  background: rgba(38, 38, 38, 0.95);
  position: relative;
  border-radius: 8px;
}

/* Animated gradient border */
.border:after {
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(45deg, var(--pink), var(--purple), var(--blue));
  border-radius: 8px;
  z-index: -1;
  animation: animatedgradient 3s ease infinite;
  background-size: 400% 400%;
}

@keyframes animatedgradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* Larger screen styles using media query */
@media (min-width: 768px) {
  .border {
    padding: 15px 25px; /* Larger padding on bigger screens */
    font-size: 1.1rem; /* Slightly larger font size for readability */
  }
}
</style>
