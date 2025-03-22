<template>
  <div>
    <base-dialog :show="!!error" title="Nessun match trovato" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <base-confirm-dialog :show="!!showConfirm" title="Drop" @close="closeConfirmModal" @confirm="sendScore">
      <p>{{ confirmScoreMessage }}</p>
    </base-confirm-dialog>
    <base-swipe v-if="currentRound">
      <div class="panel">
        <div class="container">
          <base-swipe-text :side="'left'" :swipeText="leftPanelText"></base-swipe-text>
          <div class="round-info">
            <h3 v-if="countdown">Tempo rimanente: {{ countdown }}</h3>
            <h3 v-else>Timer in attesa.</h3>
          </div>
          <div class="players">
            <div class="player">
              <div class="overlay" @click="addWinP1" v-if="!ended">+</div>
              <div class="points">{{ p1Wins }}</div>
              <div class="overlay-bottom" @click="removeWinP1" v-if="!ended">-</div>
              <h4>{{nameP1}} {{lastnameP1}}</h4>
            </div>
            <div class="player">
              <div class="overlay" @click="addWinP2" v-if="!ended">+</div>
              <div class="points">{{ p2Wins }}</div>
              <div class="overlay-bottom" @click="removeWinP2" v-if="!ended">-</div>
              <h4>{{nameP2}} {{lastnameP2}}</h4>
            </div>
          </div>
          <base-button class="btn-send" @click="showConfirmModal" v-if="!ended">Invia Punteggio</base-button>
          <div v-else><h4>Punteggio confermato</h4></div>
        </div>
      </div>

      <div class="panel">
        <div class="container" v-if="getCurrentRound && getCurrentRound.previousRoundRankings">
          <ranked-list :players="getCurrentRound.previousRoundRankings"/>
        </div>
        <div class="container" v-else>
          <h4>La classifica verrà mostrata una volta terminato il turno!</h4>
        </div>
      </div>
      
    </base-swipe>
  </div>
</template>

<script>
import RankedList from '@/components/players/RankedList.vue';
import BaseSwipe from '@/components/layout/BaseSwipe.vue'
import BaseSwipeText from '@/components/layout/BaseSwipeText.vue'
import { mapGetters } from 'vuex';

export default {
  components: {
    RankedList,
    BaseSwipe,
    BaseSwipeText
  },
  data() {
    return {
      currentRound: null,
      p1Wins: 0,
      p2Wins: 0,
      error: null,
      leftPanelText: 'swipe → per la classifica del turno precedente',
      countdown: '', // New data property for the countdown string
      timer: null,   // To store the interval ID
      showConfirm: false,
    };
  },
  computed: {
    ...mapGetters('events', ['getCurrentRound']),
    swipeText(){
      return "swipe → per la classifica del turno precedente"
    },
    confirmScoreMessage() {
      if(this.p1Wins > this.p2Wins) {
        return `${this.nameP1} ${this.lastnameP1} ha VINTO ${this.p1Wins} a ${this.p2Wins}. Corretto?`
      } else if (this.p2Wins > this.p1Wins){
        return `${this.nameP2} ${this.lastnameP2} ha VINTO ${this.p2Wins} a ${this.p1Wins}. Corretto?`
      } else {
        return `PAREGGIO ${this.p2Wins} a ${this.p1Wins}. Corretto?`
      }
    },
    nameP1(){
      return this.currentRound ? this.currentRound.nameP1 ? this.currentRound.nameP1 : "Bye" : ""
    },
    nameP2(){
      return this.currentRound ? this.currentRound.nameP2 ? this.currentRound.nameP2 : "Bye" : ""
    },
    lastnameP1(){
      return this.currentRound ? this.currentRound.surnameP1 : ""
    },
    lastnameP2(){
      return this.currentRound ? this.currentRound.surnameP2 : ""
    },
    ended(){
      return this.currentRound ? this.currentRound.ended : true
    },
  },
  methods: {
    handleError() {
      this.error = null;
      this.$router.replace('/my-events')
    },
    async sendScore() {
      this.showConfirm = false;
      const actionPayload = {
        roundId: this.currentRound.id,
        p1Wins: this.p1Wins,
        p2Wins: this.p2Wins,
      };
      try {
        await this.$store.dispatch('events/confirmScore', {
          round: actionPayload,
        });
        this.$router.replace('/leagues/my-subs')
      } catch (err) {
        this.error = err.message || 'Il server risulta irraggiungibile..';
      }
    },
    async loadCurrentRound(){
      try {
        await this.$store.dispatch('events/findCurrentRound');
      } catch (err) {
        this.error = err.message || 'Il server risulta irraggiungibile..';
      } finally {
        this.setCurrentRound()
      }
      if(this.currentRound && this.currentRound.roundEndTime) {
        this.startCountdown();
      }
    },
    showConfirmModal() {
      this.showConfirm = true;
    },
    closeConfirmModal() {
      this.showConfirm = false;
    },
    setCurrentRound(){
      this.currentRound= this.getCurrentRound
      this.p1Wins= this.currentRound ? this.currentRound.p1Wins : 0
      this.p2Wins= this.currentRound ? this.currentRound.p2Wins : 0
    },
    startCountdown() {
      if (this.timer) clearInterval(this.timer); // Clear any existing timer

      const updateCountdown = () => {
        const now = new Date();
        const endTime = new Date(this.currentRound?.roundEndTime);
        const timeLeft = endTime - now;

        if (timeLeft <= 0) {
          this.countdown = "00:00:00";
          clearInterval(this.timer); // Stop the timer when expired
          return;
        }

        const hours = String(Math.floor((timeLeft / (1000 * 60 * 60)) % 24)).padStart(2, '0');
        const minutes = String(Math.floor((timeLeft / (1000 * 60)) % 60)).padStart(2, '0');
        const seconds = String(Math.floor((timeLeft / 1000) % 60)).padStart(2, '0');

        this.countdown = `${hours}:${minutes}:${seconds}`;
      };

      updateCountdown(); // Initialize immediately
      this.timer = setInterval(updateCountdown, 1000); // Update every second
    },
    addWinP1(){
      if(this.p1Wins<2 && (this.p1Wins+this.p2Wins<3)){
        this.p1Wins++;
      }
    },
    addWinP2(){
      if(this.p2Wins<2 && (this.p1Wins+this.p2Wins<3)){
        this.p2Wins++;
      }
    },
    removeWinP1(){
      if(this.p1Wins>0){
        this.p1Wins--;
      }
    },
    removeWinP2(){
      if(this.p2Wins>0){
        this.p2Wins--;
      }
    }
  },
  created() {
    this.loadCurrentRound()
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer); // Cleanup the timer on component destroy
    this.countdown = ''
  },
};
</script>

<style scoped>
.panel {
  min-width: 100%;
}

.container {
  display: flex;
  flex-direction: column;
  width: 100%;
}
.players {
  display: flex;
  justify-content: space-around;
  position: relative;
  top: 6%;
  width: 100%;
  height: 100%;
}
.player {
  margin-top: 1rem;
  width: 40%;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  font-size: 1.4rem;
}
.points {
  width: 100%;
  height: 100%;
  font-size: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}
h4 {
  text-align: center;
  margin-top: 2rem;
  font-size: 1rem;
}
h3 {
  text-align: center;
  font-size: 1rem;
}
.round-info {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2.5rem;
}
.btn-send {
  margin-top: 2rem;
  align-self: center;
  width: 70%;
}

.overlay {
  width: 100%;
  height: 40%;
  display: flex;
  justify-content: center;
  position: absolute;
}

.overlay-bottom {
  width: 100%;
  height: 40%;
  display: flex;
  justify-content: center;
  position: absolute;
  margin-top: 2rem;
}
.overlay{
  top: 0;
  font-size: 1.5rem;
  align-items: flex-start;
}
.overlay-bottom{
  top: 40%;
  font-size: 2rem;
  align-items: flex-end;
}

@media(min-width: 41rem){
  .overlay,
  .overlay-bottom{
    margin: 1rem auto;
  }
  .overlay{
    font-size: 4rem;
  }
  .overlay-bottom{
    font-size: 6rem;
  }
}

</style>
