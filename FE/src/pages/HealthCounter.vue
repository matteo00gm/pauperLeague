<template>
  <div class="container">
    <div class="counter">
      <div class="overlay p2" @click="loseHealth('p1')">
        <div>-</div>
        <div :class="{ change: true, visible: showLossP1 }">{{ lossSignP1 }}{{ lossCountP1 }}</div>
      </div>
      <div class="overlay-down p2" @click="gainHealth('p1')">
        <div>+</div>
        <div :class="{ change: true, visible: showGainP1 }">{{ gainSignP1 }}{{ gainCountP1 }}</div>
      </div>
      <div class="circle player1">{{ p1 }}</div>
    </div>
    <div class="reset" @click="reset">
      <img src="../assets/reset.svg" alt="reset">
    </div>
    <div class="counter">
      <div class="overlay p2" @click="loseHealth('p2')">
        <div>-</div>
        <div :class="{ change: true, visible: showLossP2 }">{{ lossSignP2 }}{{ lossCountP2 }}</div>
      </div>
      <div class="overlay-down p2" @click="gainHealth('p2')">
        <div>+</div>
        <div :class="{ change: true, visible: showGainP2 }">{{ gainSignP2 }}{{ gainCountP2 }}</div>
      </div>
      <div class="circle player2">{{ p2 }}</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      p1: 20,
      p2: 20,
      gainCountP1: 0,
      lossCountP1: 0,
      gainCountP2: 0,
      lossCountP2: 0,
      gainSignP1: "+",
      lossSignP1: "-",
      gainSignP2: "+",
      lossSignP2: "-",
      showGainP1: false,
      showLossP1: false,
      showGainP2: false,
      showLossP2: false,
      gainTimerP1: null,
      lossTimerP1: null,
      gainTimerP2: null,
      lossTimerP2: null,
    };
  },
  methods: {
    gainHealth(player) {
      if (player === 'p1') {
        this.resetVisibility('loss', 'p1'); // Reset visibility if switching actions
        this.p1++;
        this.gainCountP1++;
        this.showGainP1 = true;
        this.gainSignP1 = "+"; // Ensure "+" sign
        clearTimeout(this.gainTimerP1);
        this.gainTimerP1 = setTimeout(() => {
          this.fadeOutGain('p1');
        }, 1500);
      } else {
        this.resetVisibility('loss', 'p2');
        this.p2++;
        this.gainCountP2++;
        this.showGainP2 = true;
        this.gainSignP2 = "+";
        clearTimeout(this.gainTimerP2);
        this.gainTimerP2 = setTimeout(() => {
          this.fadeOutGain('p2');
        }, 1500);
      }
    },
    loseHealth(player) {
      if (player === 'p1') {
        this.resetVisibility('gain', 'p1');
        this.p1--;
        this.lossCountP1++;
        this.showLossP1 = true;
        this.lossSignP1 = "-";
        clearTimeout(this.lossTimerP1);
        this.lossTimerP1 = setTimeout(() => {
          this.fadeOutLoss('p1');
        }, 1500);
      } else {
        this.resetVisibility('gain', 'p2');
        this.p2--;
        this.lossCountP2++;
        this.showLossP2 = true;
        this.lossSignP2 = "-";
        clearTimeout(this.lossTimerP2);
        this.lossTimerP2 = setTimeout(() => {
          this.fadeOutLoss('p2');
        }, 1500);
      }
    },
    fadeOutGain(player) {
      if (player === 'p1') {
        this.showGainP1 = false; // Start fade out
        setTimeout(() => {
          this.gainCountP1 = 0; // Reset count after fading out
        }, 500); // Wait for fade out duration before resetting count
      } else {
        this.showGainP2 = false;
        setTimeout(() => {
          this.gainCountP2 = 0;
        }, 500);
      }
    },
    fadeOutLoss(player) {
      if (player === 'p1') {
        this.showLossP1 = false; // Start fade out
        setTimeout(() => {
          this.lossCountP1 = 0; // Reset count after fading out
        }, 500);
      } else {
        this.showLossP2 = false;
        setTimeout(() => {
          this.lossCountP2 = 0;
        }, 500);
      }
    },
    resetVisibility(type, player) {
      if (type === 'gain') {
        if (player === 'p1') {
          this.showGainP1 = false;
          this.gainCountP1 = 0;
          clearTimeout(this.gainTimerP1);
        } else {
          this.showGainP2 = false;
          this.gainCountP2 = 0;
          clearTimeout(this.gainTimerP2);
        }
      } else {
        if (player === 'p1') {
          this.showLossP1 = false;
          this.lossCountP1 = 0;
          clearTimeout(this.lossTimerP1);
        } else {
          this.showLossP2 = false;
          this.lossCountP2 = 0;
          clearTimeout(this.lossTimerP2);
        }
      }
    },
    reset() {
      this.p1 = 20;
      this.p2 = 20;
      this.gainCountP1 = this.lossCountP1 = this.gainCountP2 = this.lossCountP2 = 0;
      this.showGainP1 = this.showLossP1 = this.showGainP2 = this.showLossP2 = false;
      clearTimeout(this.gainTimerP1);
      clearTimeout(this.lossTimerP1);
      clearTimeout(this.gainTimerP2);
      clearTimeout(this.lossTimerP2);
    },
  },
};
</script>

<style scoped>
.container {
  min-width: 300px;
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  justify-content: space-between;
}

.counter {
  position: relative;
  height: 45%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  font-size: 2rem;
}

.reset {
  position: relative;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  align-self: center;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid black;
  background: linear-gradient(45deg, var(--yellow), var(--gold));
}

img {
  height: 50%;
  width: 100%;
}

.overlay {
  background: rgba(30, 30, 30, 0.8);
  position: absolute;
  width: 50%;
  height: 100%;
  left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  border-right: 1px solid #ffbc6b12;
}

.overlay-down {
  background: rgba(30, 30, 30, 0.8);
  position: absolute;
  width: 50%;
  height: 100%;
  left: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-left: 1px solid #ffbc6b12;
}

.circle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2.5rem;  
  transform: translate(-50%, -50%);
}

.change {
  position: absolute;
  bottom: 20%;
  font-size: 1.2rem;
  color: #fff;
  font-weight: bold;
  opacity: 0;
  transition: opacity 0.5s ease-in-out;
}

.visible {
  opacity: 1;
  transition: opacity 1s ease-out;
}

.circle {
  position: absolute;
  border: 1px solid var(--yellow);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
}

.player1 {
  background: linear-gradient(45deg, var(--pink), var(--purple));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

.player2 {
  background: linear-gradient(45deg, var(--blue), var(--purple));
  background-size: 300% 300%; /* Ensure enough space for animation */
  animation: gradientMove 6s ease infinite; /* Slower animation for mobile */
  transition: background-color 0.3s, color 0.3s, box-shadow 0.3s;
}

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
</style>
