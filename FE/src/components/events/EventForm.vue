<template>
  <div class="form-container">
    <h2>{{ title }}</h2>
    <form @submit.prevent="submitForm">
      <div class="form-control" :class="{ invalid: !name.isValid && name.touched }">
        <label>Nome</label>
        <input type="text" id="name" v-model.trim="name.val" @blur="markTouched('name')" />
      </div>

      <div class="form-control" :class="{ invalid: !date.isValid && date.touched }">
        <label>Data</label>
        <input type="date" id="date" :min="minDate" v-model="date.val" @blur="markTouched('date')" />
      </div>

      <div class="form-control">
        <label>Cap</label>
        <input type="number" id="cap" v-model.number="cap.val" placeholder="Numero massimo di iscritti? (opzionale)"/>
      </div>

      <div class="form-control">
        <label>Luogo</label>
        <textarea id="location" rows="5" v-model.trim="location.val" placeholder="Dove si svolgerà il torneo? (opzionale)" />
      </div>

      <div class="button-container">
        <base-button>{{ buttonText }}</base-button>
        <base-button v-if="getSelectedEventToEdit" :class="'danger'" type="button" @click="deleteEvent">Cancella</base-button>
      </div>
    </form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  emits: ['save-data', 'delete-event'],
  computed: {
    ...mapGetters('events', ['getSelectedEventToEdit']),
    title() {
      return this.eventToEdit ? 'Modifica Torneo' : 'Crea Torneo';
    },
    buttonText() {
      return this.eventToEdit ? 'Salva' : 'Crea';
    },
    minDate() {
      const today = new Date();
      const formattedToday = today.toISOString().split('T')[0];
      return formattedToday;
    },
    // Computed properties for form data
    nameValue() {
      return this.getSelectedEventToEdit ? this.getSelectedEventToEdit.name : '';
    },
    dateValue() {
      return this.getSelectedEventToEdit ? this.getSelectedEventToEdit.date : '';
    },
    capValue() {
      return this.getSelectedEventToEdit ? this.getSelectedEventToEdit.cap : null;
    },
    locationValue() {
      return this.getSelectedEventToEdit ? this.getSelectedEventToEdit.location : '';
    },
    minCap() {
      return this.eventToEdit ? this.eventToEdit.players.length : 0; // Number of players
    },
  },
  data() {
    return {
      eventToEdit: null,
      name: {
        val: '',
        isValid: true,
        touched: false,
      },
      date: {
        val: '',
        isValid: true,
        touched: false,
      },
      cap: {
        val: null,
      },
      location: {
        val: '',
      },
      formIsValid: true,
    };
  },
  mounted() {
    // Fill form with values from getSelectedEventToEdit
    this.name.val = this.nameValue;
    this.date.val = this.dateValue;
    this.cap.val = this.capValue;
    this.location.val = this.locationValue;

    this.eventToEdit = this.getSelectedEventToEdit;
  },
  methods: {
    deleteEvent() {
      const formData = {
        eventId: this.getSelectedEventToEdit.eventId
      };
      this.$emit('delete-event', formData);
    },

    markTouched(field) {
      this[field].touched = true;
    },

    clearValidity(input) {
      // Check validity of input
      if (
        (this[input].val !== '' && this[input].val !== null) ||
        (this[input].type === Number && this[input].val > 0)
      ) {
        this[input].isValid = true;
      } else {
        this[input].isValid = false;
      }
      this.validateForm();
    },

    validateForm() {
      this.formIsValid = true;

      // Validate the name field
      if (this.name.val === '') {
        this.name.isValid = false;
        this.formIsValid = false;
      } else {
        this.name.isValid = true; // Clear invalid state
      }

      // Validate the date field
      if (this.date.val === '') {
        this.date.isValid = false;
        this.formIsValid = false;
      } else {
        this.date.isValid = true; // Clear invalid state
      }
    },

    submitForm() {
      this.validateForm(); // Validate the form on submit

      // Emit the data only if the form is valid
      if (!this.formIsValid) {
        // If the form is invalid, you might want to mark all fields as touched
        this.markTouched('name');
        this.markTouched('date');
        this.validateForm(); // This will also update the validity states
        return; // Stop submission if form is invalid
      }

      const formData = {
        name: this.name.val,
        date: this.date.val,
        cap: this.cap.val,
        location: this.location.val
      };

      this.$emit('save-data', formData);
    },
  },
};
</script>

<style scoped>
.form-container {
  height: 100%;
  width: 100%;
  max-width: 600px;
  padding: 1rem 2rem; /* Add some padding around the form */
  transition: transform 0.3s; /* Smooth transition on hover */
}

h2 {
  color: #ffffff; /* Light text color */
  text-align: center; /* Center align heading */
  margin-bottom: 2rem;
}

.form-control {
  margin: 0.5rem 0; /* Space between form controls */
}

label {
  font-weight: bold; /* Bold labels */
  margin-bottom: 0.5rem; /* Space below label */
  display: block; /* Labels on their own line */
  color: #ffffff; /* Light label color */
}

input,
textarea {
  display: block; /* Block layout for inputs */
  width: 100%; /* Full width inputs */
  font: inherit; /* Inherit font settings */
  border: none; /* Remove default border */
  padding: 0.75rem; /* Increased padding */
  border-radius: 8px; /* Rounded corners */
  background: rgba(255, 255, 255, 0.1); /* Semi-transparent white background */
  color: #ffffff; /* White text color */
  transition: background 0.3s, box-shadow 0.3s; /* Smooth transition */
}

textarea {
  resize: none;
}

input::placeholder,
textarea::placeholder {
  color: rgba(255, 255, 255, 0.6); /* Light placeholder color */
}

input:focus,
textarea:focus {
  background: rgba(255, 255, 255, 0.2); /* Darker background on focus */
  outline: none; /* Remove default outline */
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.7); /* Glow effect on focus */
}

.invalid label {
  color: red; /* Color for invalid labels */
}

.invalid input,
.invalid textarea {
  background: rgba(255, 0, 0, 0.1); /* Light red background for invalid inputs */
}

.error-message {
  color: red; /* Error message color */
  text-align: center; /* Center align error messages */
  margin: 1rem 0; /* Space around error messages */
}

.button-container {
  display: flex; /* Flex layout for buttons */
  flex-direction: row-reverse;
  justify-content: space-between;
  justify-content: center;
  margin-top: 1.5rem; /* Space above buttons */
  gap: 5px;
}

/* Media Queries for larger screens */
@media (min-width: 768px) {
  .form-container {
    padding: 3rem; /* Increase padding for larger screens */
  }
}
</style>
