import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from '@/store/index.js'
//vue cookies import
import VueCookies from 'vue-cookies'
//bootstrap import
import { BootstrapVue } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
//fontawesomeIcon
import "@/fontAwesomeIcon.js"

Vue.use(BootstrapVue);
Vue.use(VueCookies);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
