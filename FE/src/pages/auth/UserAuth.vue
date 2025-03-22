<template>
  <div class="user-auth">
    <base-dialog :show="!!error" title="Errore di autenticazione" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>

    <div class="form-container">
      <h2>{{ submitButtonCaption }}</h2>
      <form @submit.prevent="submitForm">
        <div class="form-control" :class="{ invalid: !name.isValid && name.touched }" v-if="getMode === 'signup'">
          <label>Nome</label>
          <input type="text" id="name" v-model.trim="name.val" @blur="markTouched('name')" />
        </div>

        <div class="form-control" :class="{ invalid: !lastName.isValid && lastName.touched }" v-if="getMode === 'signup'">
          <label>Cognome</label>
          <input type="text" id="lastName" v-model.trim="lastName.val" @blur="markTouched('lastName')" />
        </div>

        <div class="form-control" :class="{ invalid: !email.isValid && email.touched }">
          <label>Email</label>
          <input type="email" id="email" v-model.trim="email.val" @blur="markTouched('email')" />
        </div>

        <div class="form-control" :class="{ invalid: !password.isValid && password.touched }">
          <label>Password</label>
          <input type="password" id="password" v-model.trim="password.val" @blur="markTouched('password')" />
          <p v-if="!password.isValid && password.touched">
            La password deve contenere almeno 6 caratteri!
          </p>
        </div>

        <div class="form-control" :class="{ invalid: !repeatPassword.isValid && repeatPassword.touched }" v-if="getMode === 'signup'">
          <label>Ripeti Password</label>
          <input type="password" id="repeatPassword" v-model.trim="repeatPassword.val" @blur="markTouched('repeatPassword')" />
          <p v-if="!repeatPassword.isValid && repeatPassword.touched">
            Le password non corrispondono!
          </p>
        </div>

        <p v-if="!formIsValid" class="error-message">Correggi gli errori e riprova.</p>

        <div class="button-container">
          <base-button :class="'border'" type="button" @click="switchAuthMode">
            {{ switchModeButtonCaption }}
          </base-button>
          <base-button>{{ submitButtonCaption }}</base-button>
        </div>

        <div class="forgot-password" v-if="getMode !== 'signup'">
          <a @click.prevent="forgotPassword" href="#">Password dimenticata?</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      error: null,
      mode: 'login',
      name: {
        val: '',
        isValid: true,
        touched: false,
      },
      lastName: {
        val: '',
        isValid: true,
        touched: false,
      },
      email: {
        val: '',
        isValid: true,
        touched: false,
      },
      password: {
        val: '',
        isValid: true,
        touched: false,
      },
      repeatPassword: {
        val: '',
        isValid: true,
        touched: false,
      },
      formIsValid: true,
      loading: false,
    };
  },
  computed: {
    submitButtonCaption() {
      return this.mode === 'login' ? 'Accedi' : 'Registrati';
    },
    switchModeButtonCaption() {
      return this.mode === 'login' ? 'Registrati' : 'Accedi';
    },
    getMode() {
      return this.mode;
    },
  },
  methods: {
    forgotPassword() {
      this.$router.push('/password-reset'); // O qualsiasi route definita
    },
    markTouched(field) {
      this[field].touched = true;
    },

    validateField(input) {
      if (input === 'email') {
        this[input].isValid = this.validateEmail(this[input].val);
      } else if (input === 'repeatPassword') {
        this[input].isValid = this.password.val === this.repeatPassword.val;
      } else {
        this[input].isValid = this[input].val !== '';
      }
    },

    validateEmail(email) {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return regex.test(email);
    },

    validateForm() {
      this.formIsValid = true;

      if (this.mode === 'signup') {
        if (this.name.val === '') {
          this.name.isValid = false;
          this.formIsValid = false;
        } else {
          this.name.isValid = true;
        }

        if (this.lastName.val === '') {
          this.lastName.isValid = false;
          this.formIsValid = false;
        } else {
          this.lastName.isValid = true;
        }
      }

      if (this.email.val === '' || !this.validateEmail(this.email.val)) {
        this.email.isValid = false;
        this.formIsValid = false;
      } else {
        this.email.isValid = true;
      }

      if (this.password.val === '' || this.password.val.length < 6) {
        this.password.isValid = false;
        this.formIsValid = false;
      } else {
        this.password.isValid = true;
      }

      if (this.mode === 'signup') {
        if (this.repeatPassword.val !== this.password.val) {
          this.repeatPassword.isValid = false;
          this.formIsValid = false;
        } else {
          this.repeatPassword.isValid = true;
        }
      }
    },

    async submitForm() {
      if (this.loading) return;
      this.loading = true;
      this.validateForm();
      this.markAllTouched();

      if (!this.formIsValid) {
        this.loading = false;
        return;
      }

      const actionPayload = {
        name: this.name.val,
        lastName: this.lastName.val,
        email: this.email.val,
        password: this.password.val,
      };
      const redirectUrl = '/' + (this.$route.query.redirect || 'leagues/my-subs');

      try {
        if (this.mode === 'login') {
          await this.$store.dispatch('login', actionPayload);
        } else {
          await this.$store.dispatch('signup', actionPayload);
        }
        this.$router.replace(redirectUrl);
      } catch (err) {
        this.error = err.message || 'Il server risulta irraggiungibile..';
      } finally {
        this.loading = false;
      }
    },

    markAllTouched() {
      this.name.touched = true;
      this.lastName.touched = true;
      this.email.touched = true;
      this.password.touched = true;
      this.repeatPassword.touched = true;
    },

    handleError() {
      this.error = null;
    },

    switchAuthMode() {
      this.name.touched = false;
      this.lastName.touched = false;
      this.email.touched = false;
      this.password.touched = false;
      this.repeatPassword.touched = false;

      this.name.isValid = true;
      this.lastName.isValid = true;
      this.email.isValid = true;
      this.password.isValid = true;
      this.repeatPassword.isValid = true;

      this.mode = this.mode === 'login' ? 'signup' : 'login';
      this.formIsValid = true;
    },
  },
};
</script>

<style scoped>
.user-auth {
  display: flex;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.form-container {
  width: 100%;
  max-width: 600px;
  padding: 2rem;
  transition: transform 0.3s;
}

h2 {
  color: #ffffff;
  text-align: center;
}

.form-control {
  margin: 0.5rem 0;
}

label {
  font-weight: bold;
  margin-bottom: 0.5rem;
  display: block;
  color: #ffffff;
}

input {
  display: block;
  width: 100%;
  font: inherit;
  border: none;
  padding: 0.75rem;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transition: background 0.3s, box-shadow 0.3s;
}

input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

input:focus {
  background: rgba(255, 255, 255, 0.2);
  outline: none;
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.7);
}

.invalid label {
  color: red;
}

.invalid input {
  background: rgba(255, 0, 0, 0.1);
}

.error-message {
  color: red;
  text-align: center;
  margin: 1rem 0;
}

.button-container {
  display: flex;
  justify-content: space-between;
  gap: 5px;
  margin-top: 1.5rem;
}

@media (min-width: 768px) {
  .form-container {
    padding: 3rem;
  }
}

.forgot-password {
  text-align: center;
  margin-top: 1rem;
}

.forgot-password a {
  color: var(--blue);
  text-decoration: underline;
}
</style>
