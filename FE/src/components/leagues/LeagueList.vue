<template>
  <div v-if="isZoomed" class="dark-background" @click="zoomOut"></div>

  <league-item 
    v-for="league in leagues" 
    :key="league.id" 
    :league="league"
    ref="leagueItems"
    :zoomed="zoomedLeague?.id === league.id" 
    @click="onSetSelectedLeague(league)"
    @toggle-zoom="handleZoom({ league, zoomedIn: true })"
  ></league-item>
  
  <div v-if="zoomedLeague" class="overlay" @click="closeZoom">
    <div class="zoomed-item" @click.stop>
      <league-details
      :id="zoomedLeague.id"
      :name="zoomedLeague.name"
      :desc="zoomedLeague.description"
      @leave="leave(zoomedLeague.id)"
      ></league-details>
    </div>
  </div>
</template>


<script>
import LeagueItem from '../../components/leagues/LeagueItem.vue';
import LeagueDetails from '../../components/leagues/LeagueDetails.vue';

export default {
  emits: ['leave'],
  components: {
    LeagueItem,
    LeagueDetails
  },
  props: ['leagues'],
  data() {
    return {
      zoomedLeague: null,
      isZoomed: false,
      visibilityStates: [],
      leagueItems: [],
      lastScrollTop: 0
    };
  },
  mounted() {
    this.visibilityStates = Array.from({ length: this.leagues.length }, () => false);

    const observerOptions = {
      root: null,
      threshold: [0]
    };

    this.observer = new IntersectionObserver(this.handleIntersect, observerOptions);

    this.$nextTick(() => {
      this.leagueItems.forEach(item => {
        this.observer.observe(item.$el);
      });

      // Manually trigger animations for items already in view
      this.updateVisibilityClasses(false);
    });

    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    async leave(leagueId) {
      this.closeZoom()
      try {
        await this.$store.dispatch('leagues/leaveLeague', {
          leagueId: leagueId,
        });
      } catch (error) {
        this.error = error.message || 'Something went wrong!';
      }
      this.$emit('leave', leagueId);
    },
    onSetSelectedLeague(league) {
      this.$store.commit('leagues/setSelectedLeague', league);
    },
    handleIntersect(entries) {
      entries.forEach(entry => {
        const index = this.leagueItems.findIndex(item => item.$el === entry.target);
        const isVisible = entry.isIntersecting;

        if (isVisible && !this.visibilityStates[index]) {
          entry.target.classList.remove('slide-out');
          entry.target.classList.add('slide-in');
          this.visibilityStates[index] = true;
        } else if (!isVisible && this.visibilityStates[index]) {
          entry.target.classList.remove('slide-in');
          entry.target.classList.add('slide-out');
          this.visibilityStates[index] = false;
        }
      });
    },
    handleZoom({ league, zoomedIn }) {
      if (zoomedIn) {
        this.zoomedLeague = league;
        this.isZoomed = true;
      } else {
        this.zoomOut();
      }
    },
    zoomOut() {
      this.isZoomed = false;
      this.zoomedLeague = null;
    },
    closeZoom() {
      this.zoomOut();
    },
    updateVisibilityClasses() {
      this.leagueItems.forEach(item => {
        const itemRect = item.$el.getBoundingClientRect();
        const itemInView = itemRect.top < window.innerHeight && itemRect.bottom > 0;

        if (itemInView) {
          item.$el.classList.add('slide-in');
          item.$el.classList.remove('slide-out');
        } else {
          item.$el.classList.add('slide-out');
          item.$el.classList.remove('slide-in');
        }
      });
    },
    handleScroll() {
      const scrollTop = window.scrollY || document.documentElement.scrollTop;
      const isScrollingDown = scrollTop > this.lastScrollTop;

      this.updateVisibilityClasses(isScrollingDown);
      this.lastScrollTop = Math.max(scrollTop, 0);
    }
  }
};
</script>


<style scoped>
.dark-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  z-index: 998;
}

.slide-in {
  transform: translateX(0);
  opacity: 1;
  transition: transform 0.6s ease, opacity 0.6s ease;
}

.slide-out {
  transform: translateX(100px);
  opacity: 0;
  transition: transform 0.6s ease, opacity 0.6s ease;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.zoomed-item {
  background-color: #282828;
  padding: 20px;
  border-radius: 10px;
  transform: scale(1);
  height: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  text-align: center;
}

@media (max-width: 768px) {
  .zoomed-item {
    width: 80%;
    height: auto;
    max-height: 80%;
  }
}
</style>
