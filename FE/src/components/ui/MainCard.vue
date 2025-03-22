<template>
  <main>
    <ul class="circles">
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
    </ul>
    <slot></slot>
  </main>
</template>

<script>
export default {
};
</script>

<style scoped>
main {
  flex-grow: 1;
  margin-top: 60px; /* Adjust as needed */
  height: 100dvh;
  overflow: hidden;
  background-size: cover;
  scrollbar-width: none; /* Hide scrollbar for Firefox */
  display: flex; /* Make sure the child component can control its own scrolling */
  flex-direction: column;
}

main::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome/Safari */
}

/* ANIMATED BACKGROUND */

.circles {
  position: absolute;
  z-index: -1;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circles li {
  position: absolute;
  display: block;
  list-style: none;
  width: 20px;
  height: 20px;
  background-color: var(--pink);
  background-image: linear-gradient(225deg, var(--pink) 0%, var(--purple) 50%, var(--blue) 100%);
  animation: animate 25s linear infinite;
  bottom: -150px;
}

.circles li:nth-child(1) {
  left: 0%;
  width: 120px;
  height: 120px;
  animation-delay: 6s;
  animation-duration: 15s;
}

.circles li:nth-child(2) {
  left: 10%;
  width: 20px;
  height: 20px;
  animation-delay: 0s;
  animation-duration: 12s;
}

.circles li:nth-child(13) {
  left: 15%;
  width: 70px;
  height: 70px;
  animation-delay: 3s;
  animation-duration: 25s;
}

.circles li:nth-child(3) {
  left: 20%;
  width: 60px;
  height: 60px;
  animation-delay: 1s;
  animation-duration: 12s;
}

.circles li:nth-child(4) {
  left: 30%;
  width: 120px;
  height: 120px;
  animation-delay: 0s;
  animation-duration: 18s;
}

.circles li:nth-child(15) {
  left: 35%;
  width: 80px;
  height: 80px;
  animation-delay: 2s;
  animation-duration: 22s;
}

.circles li:nth-child(5) {
  left: 40%;
  width: 20px;
  height: 20px;
  animation-delay: 0s;
  animation-duration: 10s;
}

.circles li:nth-child(14) {
  left: 45%;
  width: 130px;
  height: 130px;
  animation-delay: 3s;
  animation-duration: 18s;
}

.circles li:nth-child(6) {
  left: 50%;
  width: 80px;
  height: 80px;
  animation-delay: 0s;
  animation-duration: 17s;
}

.circles li:nth-child(7) {
  left: 55%;
  width: 15px;
  height: 15px;
  animation-delay: 2s;
  animation-duration: 10s;
}

.circles li:nth-child(8) {
  left: 60%;
  width: 25px;
  height: 25px;
  animation-delay: 4s;
  animation-duration: 15s;
}

.circles li:nth-child(9) {
  left: 70%;
  width: 90px;
  height: 90px;
  animation-delay: 0s;
  animation-duration: 35s;
}

.circles li:nth-child(10) {
  left: 80%;
  width: 150px;
  height: 150px;
  animation-delay: 0s;
  animation-duration: 11s;
}

/* New circles added below */
.circles li:nth-child(11) {
  left: 90%;
  width: 40px;
  height: 40px;
  animation-delay: 2s;
  animation-duration: 22s;
}

.circles li:nth-child(12) {
  left: 95%;
  width: 15px;
  height: 15px;
  animation-delay: 1s;
  animation-duration: 15s;
}

@keyframes animate {
  0% {
    transform: translateY(-1600px) rotate(0deg);
    opacity: 1;
    border-radius: 0;
  }
  100% {
    transform: translateY(0) rotate(720deg);
    opacity: 0;
    border-radius: 50%;
  }
}
</style>
