<template>
  <div class="create-league">

    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>

    <section class="form-container">
      <h2>Crea una lega!</h2>
      <form @submit.prevent="submitForm">
        <div class="form-control" :class="{ invalid: !name.isValid && name.touched }">
          <label>Nome</label>
          <input
            type="text"
            id="name"
            v-model.trim="name.val"
            @blur="markTouched('name')"
            @input="validateField('name')"
          />
        </div>

        <div class="form-control">
          <label>Descrizione</label>
          <textarea
            id="description"
            rows="5"
            v-model.trim="description.val"
            placeholder="Descrizione della lega (optional)"
          />
        </div>

        <p v-if="!formIsValid" class="error-message">Please correct the errors to proceed.</p>

        <div class="button-container">
          <base-button type="submit">Crea</base-button>
        </div>
      </form>
      <section class="explain">
        <h3>A cosa serve?</h3>
        <p>Creando una lega potrai gestire i tuoi iscritti, organizzare tornei, consultare di volta in volta la classifica della tua lega e i risultati dei tornei che hai organizzato.</p>
      </section>
    </section>
  </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  data() {
    return {
      error: null,
      name: {
        val: '',
        isValid: true,
        touched: false,
      },
      description: {
        val: '',
      },
      formIsValid: true,
    };
  },
  methods: {
    ...mapActions('leagues', ['addLeague']),

    handleError() {
      this.error = null;
    },

    markTouched(field) {
      if (this[field].val !== '') {
        // Mark the field as touched only if there is input
        this[field].touched = true;
        this.validateField(field);
      }
    },

    validateField(input) {
      // Validate only the name, as description is optional
      if (input === 'name') {
        this[input].isValid = this[input].val !== '';
      }
      this.validateForm();
    },

    validateForm() {
      this.formIsValid = true;

      if (this.name.val === '') {
        this.name.isValid = false;
        this.formIsValid = false;
      }
    },

    async submitForm() {
      this.validateForm();
      this.markAllTouched();

      if (!this.formIsValid) {
        return;
      }

      const formData = {
        name: this.name.val,
        description: this.description.val,
      };

      try {
        await this.addLeague(formData);
      } catch (err) {
        this.error = err
      }

      if (!this.error) {
        this.$router.replace('/leagues/my-subs');
      }
    },

    markAllTouched() {
      this.name.touched = true;
    },
  },
};

</script>

<style scoped>
.create-league {
  display: flex;
  justify-content: center;
  height: 100vh; /* Full viewport height */
}

.form-container {
  width: 100%;
  background: rgba(30, 30, 30, 0.8); /* Dark semi-transparent background */
  padding: 2rem; /* Add some padding around the form */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 8px 40px rgba(0, 0, 0, 0.3); /* 3D shadow effect */
  transition: transform 0.3s, box-shadow 0.3s; /* Smooth transition for hover effects */
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #ffffff; /* Light text color */
}

.form-control {
  margin: 1.5rem 0;
}

label {
  font-weight: bold;
  margin-bottom: 0.5rem;
  display: block;
  color: #ffffff; /* Light label color */
}

input,
textarea {
  display: block;
  width: 100%;
  font: inherit;
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
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.5); /* Light box shadow on focus */
}

.invalid input {
  background: rgba(255, 0, 0, 0.1); /* Light red background for invalid inputs */
}

.invalid label {
  color: red; /* Color for invalid labels */
}

.error-message {
  color: red; /* Red text for error messages */
  text-align: center; /* Centered text */
  margin-top: 1rem; /* Margin above error message */
}

.button-container {
  display: flex;
  flex-direction: column; /* Stack buttons vertically */
  align-items: center;
  margin-top: 1rem;
}

.explain {
  margin: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

h3 {
  margin-bottom: 0.5rem;
}

/* Media Queries for larger screens */
@media (min-width: 768px) {
  .form-container {
    padding: 3rem; /* Increase padding for larger screens */
  }
}
</style>
