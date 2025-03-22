<template>
  <div class="user-auth">
    <base-dialog :show="!!error" title="Errore" @close="handleError">
      <p>{{ error }}</p>
    </base-dialog>
    <base-dialog :show="sent" title="Mail Inviata" @close="handleMailDialog">
      <p>{{ mailDialogText }}</p>
    </base-dialog>

    <div class="form-container">
      <h2 v-if="showPasswordResetForm">Imposta Nuova Password</h2>
      <h2 v-else>Reset Password</h2>
      <template v-if="showPasswordResetForm">
        <form @submit.prevent="submitNewPassword">
          <div class="form-control" :class="{ invalid: !newPassword.isValid && newPassword.touched }">
            <label>Nuova Password</label>
            <input
              type="password"
              id="newPassword"
              v-model.trim="newPassword.val"
              @blur="markTouched('newPassword')"
            />
            <p v-if="!newPassword.isValid && newPassword.touched">
              La password deve essere di almeno 6 caratteri!
            </p>
          </div>
          <div class="form-control" :class="{ invalid: !confirmPassword.isValid && confirmPassword.touched }">
            <label>Conferma Password</label>
            <input
              type="password"
              id="confirmPassword"
              v-model.trim="confirmPassword.val"
              @blur="markTouched('confirmPassword')"
            />
            <p v-if="!confirmPassword.isValid && confirmPassword.touched">
              Le password non corrispondono!
            </p>
          </div>

          <p v-if="!formIsValid" class="error-message">Correggi gli errori e riprova.</p>

          <div class="button-container">
            <base-button :class="'border'" type="button" @click="goBack">
              Torna indietro
            </base-button>
            <base-button>Conferma</base-button>
          </div>
        </form>
      </template>
      <template v-else>
        <form @submit.prevent="submitReset">
          <div class="form-control" :class="{ invalid: !email.isValid && email.touched }">
            <label>Email</label>
            <input type="email" id="email" v-model.trim="email.val" @blur="markTouched('email')" />
            <p v-if="!email.isValid && email.touched">Inserisci un'email valida!</p>
          </div>

          <p v-if="!formIsValid" class="error-message">Correggi gli errori e riprova.</p>

          <div class="button-container">
            <base-button :class="'border'" type="button" @click="goBack">
              Torna indietro
            </base-button>
            <base-button>Invia richiesta</base-button>
          </div>
        </form>
      </template>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: {
        val: '',
        isValid: true,
        touched: false,
      },
      newPassword: {
        val: '',
        isValid: true,
        touched: false,
      },
      confirmPassword: {
        val: '',
        isValid: true,
        touched: false,
      },
      formIsValid: true,
      error: null,
      sent: false,
      mailDialogText: `Abbiamo inviato una mail con le istruzioni per il reset della password all'indirizzo indicato.`,
      loading: false,
      showPasswordResetForm: false,
    };
  },
  mounted() {
    const { email, otp } = this.$route.query;
    if (email && otp) {
      this.showPasswordResetForm = true;
    }
  },
  methods: {
    markTouched(field) {
      this[field].touched = true;
    },

    validateEmail(email) {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return regex.test(email);
    },

    validateForm() {
      this.formIsValid = true;

      if (this.showPasswordResetForm) {
        if (this.newPassword.val.length < 6) {
          this.newPassword.isValid = false;
          this.formIsValid = false;
        } else {
          this.newPassword.isValid = true;
        }

        if (this.confirmPassword.val !== this.newPassword.val) {
          this.confirmPassword.isValid = false;
          this.formIsValid = false;
        } else {
          this.confirmPassword.isValid = true;
        }
      } else {
        if (this.email.val === '' || !this.validateEmail(this.email.val)) {
          this.email.isValid = false;
          this.formIsValid = false;
        } else {
          this.email.isValid = true;
        }
      }
    },

    async submitReset() {
      if (this.loading) return;
      this.loading = true;
      this.validateForm();
      this.markTouched('email');

      if (!this.formIsValid) {
        this.loading = false;
        return;
      }

      try {
        await this.$store.dispatch('resetPassword', { email: this.email.val });
        this.sent = true;
      } catch (err) {
        this.error = err.message || 'Il server risulta irraggiungibile.';
      } finally {
        this.loading = false;
      }
    },

    async submitNewPassword() {
      if (this.loading) return;
      this.loading = true;
      this.validateForm();
      this.markTouched('newPassword');
      this.markTouched('confirmPassword');

      if (!this.formIsValid) {
        this.loading = false;
        return;
      }

      try {
        const { email, otp } = this.$route.query;
        await this.$store.dispatch('updatePassword', {
          email,
          otp,
          newPassword: this.newPassword.val,
          confirmPassword: this.confirmPassword.val,
        });
        this.$router.push('/auth');
      } catch (err) {
        this.error = err.message || 'Errore durante l\'aggiornamento della password.';
      } finally {
        this.loading = false;
      }
    },

    handleError() {
      this.error = null;
    },

    handleMailDialog() {
      this.$router.push('/auth');
    },

    goBack() {
      this.$router.push('/auth');
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
  margin-bottom: 2rem;
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
  gap: 5px;
  margin-top: 1.5rem;
}

@media (min-width: 768px) {
  .form-container {
    padding: 3rem;
  }
}
</style>