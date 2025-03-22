<template>
  <div class="home-container">
    <header class="header">
      <h1>PauperLeague</h1>
    </header>

    <div class="main-content">
      <section class="feature-box intro-box" :style="{ background: gradients[1] }">
        <h2>Benvenuto</h2>
        <p>
          PauperLeague è una piattaforma dedicata ai giocatori del formato Pauper di Magic the Gathering.
          <br>
          <br>
          Qui potrai organizzare e gestire i tuoi tornei con facilità.
          Crea leghe, competi a livello globale e segui i tuoi progressi contro altri giocatori!
        </p>
      </section>

      <section class="feature-box create-league" :style="{ background: gradients[0] }">
        <h3>Crea la tua Lega</h3>
        <p>
          Crea la tua lega, competi con i tuoi amici, organizza tornei e fai a gara per il primo posto in classifica!
        </p>
        <p v-if="!isAuthenticated">Registrati, poi torna qui o usa la barra di navigazione per creare la tua lega!</p>
        <div class="buttons">
          <base-button v-if="isAuthenticated" link to="/leagues/new">Inizia</base-button>
          <base-button v-else link to="/auth">Accedi</base-button>
        </div>
      </section>

      <section class="feature-box novice-league" :style="{ background: gradients[4] }">
        <h3>Sei un novizio?</h3>
        <p>Unisciti a una lega e conosci persone con la tua stessa passione!</p>
        <div class="buttons">
          <base-button link to="/leagues">Scopri</base-button>
        </div>
      </section>

      <section class="feature-box global-competition" :style="{ background: gradients[3] }">
        <h3>Classifica Generale</h3>
        <p>Competi con l'intera community Pauper e scala la classifica!</p>
        <div class="buttons">
          <base-button link to="/rankings">Classifica</base-button>
        </div>
      </section>

      <section class="feature-box tournament-management" :style="{ background: gradients[4] }">
        <h3>Organizza Tornei</h3>
        <p>Fissa gli appuntamenti per i tornei della tua lega, gestisci gli iscritti e consulta le classifiche aggiornate di ogni torneo.</p>
        <p v-if="!isAuthenticated">Prima però, unisciti a una lega o accedi per creare la tua!</p>
        <p v-else>Prima però, unisciti a una lega o crea la tua!</p>
        <div class="buttons">
          <base-button link to="/leagues">Unisciti</base-button>
          <base-button link to="/auth" class="border" v-if="!isAuthenticated">Accedi</base-button>
          <base-button link to="/leagues/new" class="border" v-else>Crea</base-button>
        </div>
      </section>

      <section class="feature-box recovery-tip" :style="{ background: gradients[0] }">
        <h3>A tutti capita la giornata no. Ha senso droppare?</h3>
        <p>Le classifiche di ogni torneo vengono aggiornate a fine turno. Fai un check, magari sei ancora in tempo per recuperare e strappare un posto in classifica!</p>
      </section>

      <section class="feature-box history" :style="{ background: gradients[3] }">
        <h3>No, avevo più punti io!</h3>
        <p>Manteniamo uno storico dei tornei della tua lega, così potrai ricordare ai tuoi amici l'incredibile performance di quella volta!</p>
      </section>

      <section class="feature-box about-box">
        <h3>Chi Siamo</h3>
        <p>
          PauperLeague è un progetto nato dalla passione per il formato Pauper di Magic: the Gathering. 
          <br><br> 
          Ho creato questa piattaforma per permettere ai fan del formato di organizzare, competere e connettersi all'interno di una community dedicata.
          <br>
          Credo nel valore della condivisione e nell'importanza del gioco competitivo accessibile a tutti: con PauperLeague, spero di offrire uno strumento che renda più facile e divertente organizzare tornei, tenere traccia dei risultati e competere con tutti i giocatori del formato.
          <br><br>
          Grazie per essere parte di questa avventura!
        </p>
      </section>

      <section class="feature-box footer-box">
        <h3>Contatti</h3>
        <p>Matteo Grosso</p>
        <p>Email: <a href="mailto:grossomatteo.lavoro@gmail.com">grossomatteo.lavoro@gmail.com</a></p>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  data() {
    return {
      gradients: [
        'linear-gradient(to bottom, rgba(43, 134, 197, 0.8), rgba(255, 255, 255, 0.8))',
        'linear-gradient(to bottom, rgba(120, 75, 160, 0.8), rgba(255, 60, 172, 0.8))',
        'linear-gradient(to bottom, rgba(255, 255, 255, 0.8), rgba(0, 0, 0, 0.8))',
        'linear-gradient(to bottom, rgba(43, 134, 197, 0.8), rgba(120, 75, 160, 0.8))',
        'linear-gradient(to bottom, rgba(255, 60, 172, 0.8), rgba(255, 255, 255, 0.8))',
      ],
    };
  },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
  },
};
</script>

<style scoped>
/* Stile generale per il container principale */
.home-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow-y: auto;
  scrollbar-width: none;
}

.home-container::-webkit-scrollbar {
  display: none; /* Nasconde la scrollbar su Chrome/Safari */
}

.header {
  text-align: center;
  margin: 20px 0;
  color: #ffffff; /* Colore bianco per il titolo principale */
  font-size: 2rem;
  font-weight: bold;
}

/* Stile principale del contenuto */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px;
  overflow-y: auto;
  scrollbar-width: none;
}

.main-content::-webkit-scrollbar {
  display: none;
}

.feature-box {
  width: 100%;
  padding: 5rem 2rem;
  text-align: center;
  color: #ffffff;
  flex-direction: column;
}

.about-box {
  background: rgba(38, 38, 38, 0.85);
  padding: 40px;
  color: #ffffff;
}

.footer-box {
  background: #111;
  color: #ffffff;
  padding: 20px;
}

/* Sezione "Benvenuto" */
.intro-box h2 {
  color: #ffe0e0;
}

.intro-box p {
  color: #f1f1f1;
}

/* Sezione "Crea la tua Lega" */
.create-league h3 {
  color: #ffcccc; /* Azzurro chiaro per abbinarsi con il background */
}

.create-league p {
  color: #3f4174;
}

/* Sezione "Sei un novizio?" */
.novice-league h3 {
  color: #ffe0e0;
}

.novice-league p {
  color: #ffffff;
}

/* Sezione "Classifica Generale" */
.global-competition h3 {
  color: #ccf2ff;
}

.global-competition p {
  color: #f8f8f8;
}

/* Sezione "Organizza Tornei" */
.tournament-management h3 {
  color: #ffe0e0;
}

.tournament-management p {
  color: #ffffff;
}

/* Sezione "A tutti capita la giornata no" */
.recovery-tip {
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.2), rgba(43, 134, 197, 0.9)); /* Updated gradient */
  color: #333333; /* Darker text color for contrast */
}

.recovery-tip h3 {
  color: #ffcccc; /* Light coral color to make the header stand out */
}

.recovery-tip p {
  color: #3f4174; /* Slightly off-white to improve contrast on the background */
}


/* Sezione "No, avevo più punti io!" */
.history h3 {
  color: #ccf2ff;
}

.history p {
  color: #ffffff;
}

/* Sezione "Chi Siamo" */
.about-box h3 {
  color: #ffe6d5; /* Colore crema per abbinarsi con il background scuro */
}

.about-box p {
  color: #dddddd;
}

/* Sezione Contatti */
.footer-box h3 {
  color: #ffebcd;
}

.footer-box p, .footer-box a {
  color: #e0e0e0;
}

/* Stile dei pulsanti */
.buttons {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
  gap: 5px;
}

p {
  margin-top: 1rem;
  line-height: 1.5;
  font-size: 1.2rem;
}

h1 {
  font-size: 1.8rem;
}

h2 {
  font-size: 1.8rem;
}

h3 {
  font-size: 1.5rem;
}

</style>
