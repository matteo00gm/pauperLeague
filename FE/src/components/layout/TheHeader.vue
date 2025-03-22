<template>
  <header>
    <nav class="navbar">
      <!-- Back Arrow / Logo -->
      <div class="back" v-if="currentLevel > 0 && isNavActive">
        <a @click="goBack">
          <span class="back-arrow">←</span>
        </a>
      </div>
      <div class="logo" v-else>
        <router-link :to="'/home'">
          <h1>PL</h1>
        </router-link>
      </div>

      <!-- Menu Container with Levels -->
      <div class="menu-container" :class="{ active: isNavActive }">
        <!-- Level 1 Menu -->
        <ul v-if="currentLevel === 0" class="nav-links">
          <li>
            <a href="#" @click.prevent="goToLevel(1, menuItems.LEAGUES)">{{ menuItems.LEAGUES.name }}</a>
          </li>
          <li v-if="isAuthenticated">
            <router-link @click="toggleHamburgerAndGoToLevel(0)" :to="menuItems.MY_EVENTS.link">{{ menuItems.MY_EVENTS.name }}</router-link>
          </li>
          <li>
            <router-link @click="toggleHamburgerAndGoToLevel(0)" :to="menuItems.RANKED.link">{{ menuItems.RANKED.name }}</router-link>
          </li>
          <li>
            <router-link @click="toggleHamburgerAndGoToLevel(0)" :to="menuItems.COUNTER.link">{{ menuItems.COUNTER.name }}</router-link>
          </li>
          <li v-if="isAuthenticated">
            <router-link @click="toggleHamburgerAndGoToLevel(0)" :to="menuItems.CURRENT.link">{{ menuItems.CURRENT.name }}</router-link>
          </li>
          <li v-if="!isAuthenticated">
            <router-link @click="toggleHamburgerAndGoToLevel(0)" :to="menuItems.AUTH.link">{{ menuItems.AUTH.name }}</router-link>
          </li>
          <li v-else>
            <router-link @click="logout" :to="menuItems.LOGOUT.link">{{ menuItems.LOGOUT.name }}</router-link>
          </li>
        </ul>

        <!-- Level 2 Menu -->
        <ul v-if="currentLevel === 1 && currentSubcategory" class="nav-links">
          <li v-for="(subcategory, key) in currentSubcategory.subcategories" :key="key">
            <a v-if="!subcategory.subcategories" @click.prevent="goToNextLevel(subcategory)" :href="subcategory.link">{{ subcategory.name }}</a>
            <a v-else @click.prevent="goToNextLevel(subcategory)">{{ subcategory.name }}</a>
          </li>
        </ul>
      </div>

      <!-- Hamburger Menu -->
      <div class="hamburger" @click="toggleHamburger" :class="{ active: isNavActive }">
        <span></span>
        <span></span>
      </div>
    </nav>
  </header>
</template>

<script>
import { menuItems } from "@/assets/js/menu-items.js";

export default {
  data() {
    return {
      currentLevel: 0,
      isNavActive: false,
      currentSubcategory: null, // To store the selected subcategory (MAGIC, ONE_PIECE, etc.)
      menuItems,
    };
  },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
  },
  methods: {
    toggleHamburger() {
      this.isNavActive = !this.isNavActive;
      setTimeout(() => {
        this.currentLevel = 0;  // Reset to level 0
        this.currentSubcategory = null;  // Clear the subcategory
      }, 600); // 600ms delay (0.6 seconds)
    },
    toggleHamburgerAndGoToLevel(level, subcategory = null) {
      this.toggleHamburger()
      this.goToLevel(level, subcategory);
    },
    logout() {
      this.$store.dispatch('logout');
      this.toggleHamburger();
    },
    goToLevel(level, subcategory = null) {
      if (!this.isAuthenticated && subcategory === this.menuItems.LEAGUES) {
      this.toggleHamburger(); // Close the nav menu
      this.$router.push(this.menuItems.LEAGUES.subcategories.ALL_LEAGUES.link); // Redirect to /leagues
      return;
    }

      this.currentLevel = level;
      if (subcategory) {
        this.currentSubcategory = subcategory; // Store the selected subcategory to render its items
      }
    },
    goBack() {
      if (this.currentLevel > 0) {
        this.currentLevel--;
        if (this.currentLevel === 1) {
          this.currentSubcategory = null; // Clear subcategory when going back to level 1
        }
      }
    },
    goToNextLevel(subcategory) {
      // If subcategory has no further subcategories (like "One Piece"), just close the menu
      if (!subcategory.subcategories) {
        this.toggleHamburger(); // Close the nav if there's no deeper level
        this.$router.push(subcategory.link); // Navigate directly to the link
      } else {
        this.goToLevel(2, subcategory); // Otherwise, go deeper into level 2
      }
    },
  },
};
</script>

<style scoped>
/* General Navbar Styles */
header {
  background-color: #1c1c1c;
  border-bottom: 2px solid #333;
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1000;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  position: relative;
  z-index: 1002;
}

.logo h1 {
  font-size: 1.5rem;
  color: #f5f5f5;
}

.back {
  display: flex;
  cursor: pointer;
  z-index: 1003;
  color: #ffffff;
}

/* Hamburger Menu */
.hamburger {
  display: flex;
  flex-direction: column;
  gap: 5px;
  cursor: pointer;
  z-index: 1003;
}

.hamburger span {
  width: 20px;
  height: 1px;
  background-color: #f5f5f5;
  transition: transform 0.3s ease;
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(4px);
}

.hamburger.active span:nth-child(2) {
  transform: rotate(-45deg) translate(4px);
}

/* Full-Screen Menu Container */
.menu-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: black;
  transform: translateY(-100%);
  transition: transform 0.6s ease-in-out;
  z-index: 1001;
}

.menu-container.active {
  transform: translateY(0);
}

.nav-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: 100%;
}

.nav-links li {
  opacity: 0;
  transform: translateX(20px);
  animation: fadeIn 0.5s ease forwards;
}

.nav-links li a {
  color: white;
  font-size: 1.5rem;
  text-decoration: none;
  transition: color 0.3s ease;
}

.nav-links li a:hover {
  color: #ff9800;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes backFadeIn {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
