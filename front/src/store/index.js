import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';

import token from '@/store/modules/token.js'
import user from '@/store/modules/user.js'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    token: token,
    user: user
  },

  plugins: [
    createPersistedState(),
  ]
});