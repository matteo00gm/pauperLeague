<template>
  <section class="container">
    <h2>Pagina non trovata!</h2>
    <p>Ti sei perso?</p>
    <p>Torna alla <router-link class="home-link" to="/home">Home</router-link></p>
  </section>
</template>

<script>
export default {};
</script>

<style scoped>
/* Base styles for smartphones */
.container {
  height: calc(100% - 18rem); /* Take full height of the viewport */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* Center content vertically */
  gap: 2rem; /* Adjust the gap between items */
  text-align: center;
  padding: 0 20px; /* Add padding to prevent text from touching screen edges */
}

h2 {
  font-size: 2rem; /* Smaller font size for heading on small screens */
  margin: 0;
}

p {
  font-size: 1.2rem; /* Smaller font size for paragraphs */
  margin: 0;
}

.home-link {
  font-size: 1.3rem; /* Font size for the link */
  color: #3498db;
  text-decoration: none;
  font-weight: bold;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease-in-out;
}

.home-link:hover {
  color: #2c3e50;
  border-bottom: 2px solid #2c3e50;
}

/* Larger screens (e.g., tablets and desktops) */
@media (min-width: 768px) {
  .container {
    gap: 20px; /* Increase gap for larger screens */
  }

  h2 {
    font-size: 3rem; /* Larger font size for heading */
  }

  p {
    font-size: 1.5rem; /* Larger font size for paragraphs */
  }

  .home-link {
    font-size: 1.5rem; /* Larger font size for the link */
  }
}
</style>
