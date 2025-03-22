<template>
  <div class="event" :class="mode">
    <h2 v-if="event.leagueName">{{ event.leagueName }}</h2>
    <h3>{{ event.name }}</h3>
    <p>Data: {{ event.date }}</p>
    <p v-if="event.location">Luogo: {{ event.location }}</p>
    <div class="participants-info" v-if="event.cap && event.pendingEventSubs && event.players">{{ event.pendingEventSubs.length + event.players.length }}/{{ event.cap }}</div>
    <div class="participants-info" v-else-if="event.pendingEventSubs && event.players">{{ event.pendingEventSubs.length + event.players.length }}</div>
    <div class="participants-info" v-else>0</div>
  </div>
</template>

<script>
export default {
  props: {
    event: {
      type: Object,
      required: true
    },
    mode: {
      type: String,
      required: false,
      default: null,
    },
  },
  computed: {
    isFuture() {
      return new Date(this.event.date) >= new Date();
    }
  },
};
</script>

<style scoped>
h2 {
  font-size: 1.2rem;
  color: var(--gold);
  margin-bottom: 0.3rem;
}
h3 {
  font-size: 1rem;
  margin-bottom: 0.3rem;
}

.event {
  position: relative;
  margin: 15px 0;
  padding: 20px;
  background: #2d2d2d; /* Brightened base background */
  border-radius: 12px; /* Rounded corners for event cards */
  box-shadow: 0 6px 5px rgba(0, 0, 0, 0.5); /* Slightly reduced shadow for brightness */
  transition: transform 0.2s ease, box-shadow 0.2s ease; /* Transition for hover effects */
}

/* Hover effect only on screens larger than 600px */
@media (min-width: 600px) {
  .event:hover {
    transform: translateY(-5px); /* Lift effect on hover */
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4); /* Deeper shadow on hover */
  }
}

.past-event {
  background: rgba(39, 39, 39, 0.9); /* Lighter and brighter for past events */
}

.future-event {
  color: white;
  --borderWidth: 2px;
  background: rgba(69, 69, 69, 0.6); /* Brighter for future events */
  border-radius: 8px;
}

/* Animated gradient border */
.future-event:after {
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(45deg, #649dc5, #2b86c5);
  border-radius: 8px;
  z-index: -1;
  animation: animatedgradient 3s ease infinite;
  background-size: 400% 400%;
}

.pending-event {
  border-radius: 8px;
  background: rgba(69, 69, 69, 0.6); /* Brighter for future events */
}

/* Animated gradient border */
.pending-event:after {
  color: white;
  --borderWidth: 2px;
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(45deg, var(--yellow), var(--gold));
  border-radius: 8px;
  z-index: -1;
  animation: animatedgradient 3s ease infinite;
  background-size: 400% 400%;
}

.edit-event {
  color: white;
  --borderWidth: 1px;
  background: rgba(69, 69, 69, 0.6); /* Brighter for future events */
  position: relative;
  border-radius: 8px;
}

/* Animated gradient border */
.edit-event:after {
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(45deg, var(--redish), var(--red));
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

.participants-info {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.1); /* Semi-transparent background for participants info */
  color: #ffffff; /* White color for participants info */
  padding: 8px 12px; /* Slightly increased padding */
  border-radius: 8px; /* Rounded corners for participants info */
  font-size: 14px; /* Font size for the info */
}

.future-event > .participants-info {
  color: #fff; /* White color for participants info */
}
</style>
