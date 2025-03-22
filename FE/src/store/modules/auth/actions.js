import { endpoint } from '../../index.js';

let timer;

export default {
  async login(context, payload) {
    return context.dispatch('auth', {
      ...payload,
      mode: 'login',
    });
  },
  async signup(context, payload) {
    return context.dispatch('auth', {
      ...payload,
      mode: 'signup',
    });
  },
  async auth(context, payload) {
    const mode = payload.mode;
    let url = endpoint + `/auth/` + mode;
    let body= {}
    if (mode ==='signup'){
      body= {
        name: payload.name,
        surname: payload.lastName,
        email: payload.email,
        password: payload.password,
      }
    }else {
      body= {
        email: payload.email,
        password: payload.password,
      }
    }

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const responseData = await response.json();

    if (!response.ok) {
      const error = new Error(responseData.message || 'Il server risulta irraggiungibile..');
      throw error;
    }

    const expiresIn = +responseData.expiresIn;
    const expirationDate = new Date().getTime() + expiresIn;

    try {
      localStorage.setItem('token', responseData.token);
      localStorage.setItem('userId', responseData.userId);
      localStorage.setItem('tokenExpiration', expirationDate);
    } catch (error) {
      console.error("Error saving to localStorage:", error);
    }
    
    timer= setTimeout(()=> {
      context.dispatch('autoLogout')
    }, expiresIn)

    context.commit('setUser', {
      token: responseData.token,
      userId: responseData.userId,
    });
  },
  async tryLogin(context) {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    const tokenExpiration = localStorage.getItem('tokenExpiration');
    const expiresIn = +tokenExpiration - new Date().getTime();

    if (expiresIn < 0) {
      return;
    }

    timer = setTimeout(() => {
      context.dispatch('autoLogout');
    }, expiresIn);
    
    if (token && userId) {
      context.commit('setUser', {
        token: token,
        userId: userId,
      });
    }
  },

  async resetPassword(_, payload) {
    const body = {
      mail: payload.email
    }
    const response = await fetch(endpoint + `/forgotPassword/verifyMail`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  },

  async updatePassword(_, payload) {
    console.log(payload.email)
    const body = {
      otp: payload.otp,
      newPassword: payload.newPassword,
      confirmPassword: payload.confirmPassword,
    }

    const response = await fetch(endpoint + `/forgotPassword/changePassword/${payload.email}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      const error = new Error(errorResponse.message || 'Il server risulta irraggiungibile..!');
      throw error;
    }
  },

  logout(context) {
    context.commit('setUser', {
      token: null,
      userId: null,
    });
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('tokenExpiration');

    context.dispatch('resetAll')
    clearTimeout(timer);
  },
  autoLogout(context) {
    console.log('token expired, autologout...')
    context.dispatch('logout');
    context.commit('setAutoLogout');
  },

  resetAll({ dispatch }) {
    dispatch('events/reset', { root: true });
    dispatch('leagues/reset', { root: true });
    dispatch('players/reset', { root: true });
    dispatch('rankings/reset', { root: true });
    dispatch('subs/reset', { root: true });
  },
};
