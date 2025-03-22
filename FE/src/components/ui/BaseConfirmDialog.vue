<template>
  <div v-if="show" class="dialog-overlay" @click="closeDialog">
    <div class="dialog-box" @click.stop>
      <header class="dialog-header">
        <h3>{{ title }}</h3>
        <button class="close-btn" @click="closeDialog">&times;</button>
      </header>

      <main class="dialog-content">
        <slot></slot>
      </main>

      <footer class="dialog-footer">
        <slot name="footer">
          <base-button @click="closeDialog">Annulla</base-button>
          <base-button :class="'danger'" @click="confirm">Confermo</base-button>
        </slot>
      </footer>
    </div>
  </div>
</template>


<script>
export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    title: {
      type: String,
      default: "Dialog",
    },
  },
  methods: {
    closeDialog() {
      this.$emit("close"); // Emit close event to parent
    },
    confirm() {
      this.$emit("confirm");
    }
  },
};
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-box {
  background-color: white;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  padding: 2rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  animation: slide-down 0.3s ease-out;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 1rem;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #333;
  transition: color 0.2s ease-in-out;
}

.close-btn:hover {
  color: red;
}

.dialog-content {
  padding: 1rem 0;
  color: #555;
}

.dialog-footer {
  display: flex;
  gap: 5px;
  justify-content: flex-end;
  border-top: 1px solid #e0e0e0;
  padding-top: 1rem;
}

@keyframes slide-down {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
