<template>
  <the-header></the-header>
  <main-card>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in" @before-enter="scrollTop" appear>
        <component :is="Component" />
      </transition>
    </router-view>
  </main-card>
</template>

<script>
import TheHeader from "./components/layout/TheHeader.vue";
import MainCard from "./components/ui/MainCard.vue";

export default {
  components: {
    TheHeader,
    MainCard,
  },
  computed: {
    didAutoLogout(){
      return this.$store.getters.didAutoLogout
    }
  },
  watch: {
    didAutoLogout(curValue, oldValue){
      if(curValue && curValue !== oldValue){
        this.$router.replace('/home')
      }
    }
  },
  created(){
    this.$store.dispatch('tryLogin')
  },
  methods: {
    scrollTop(){
    document.getElementById('app').scrollIntoView();
    },
  }
};
</script>

<style>
/* Basic global reset and styling */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  --pink: #ff0090;
  --purple: #72409d;
  --blue: #2583c6;
  --yellow: #ffcc00;
  --gold: #ff9100;
  --redish: #cf6969;
  --red: #cd0000;
  --greenish: #84cf69;
  --green: #44c515;

}

html, body {
  height: 100%; /* Allow full height to the body */
}

body {
  font-family: 'Arial', sans-serif; /* Font family */
  background-color: #121212; /* Dark background */
  color: #ffffff; /* White text color */
  display: flex; /* Flexbox for vertical stacking */
  flex-direction: column; /* Stack children vertically */
  overflow: auto;
}

#app {
  height: 100vh; /* Full height of the viewport */
  display: flex; /* Flexbox layout */
  flex-direction: column; /* Stack components vertically */
  overflow: hidden; /* Prevent scrolling */
}

/* Global transition effect */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease; /* Smooth transition for fading */
}

.fade-enter, .fade-leave-to {
  opacity: 0; /* Start and end with opacity 0 */
}
</style>
