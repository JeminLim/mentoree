import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';

import user from '@/store/modules/user.js'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user: user
  },

  plugins: [
    createPersistedState(),
  ]
});