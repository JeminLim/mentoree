import axios from 'axios';
import VueCookies from 'vue-cookies';

const token = {
    namespaced: true,
    state: {
        accessToken: '',
    },
    mutations: {
        login(state, payload) {
            VueCookies.set('accessToken', payload.accessToken, '1500s');
            state.accessToken = payload.accessToken;
        },
        reissueToken(state, payload) {
            VueCookies.set('accessToken', payload, '1500s');
            state.accessToken = payload;
        },
        removeToken(state) {
            VueCookies.remove('accessToken');
            state.accessToken = '';
        }
    },
    actions: {
        login: ({commit}, params) => {
           commit('login', params);
        },
        reissueToken: ({commit}, params) => {
            axios.post('/api/reissue', {refreshToken: params})
            .then(res => {
                commit('reissueToken', res.data);
            })
            .catch(err => {
                console.log('reissueToken error: ', err.config);
            });
        },
        logout: ({commit}) => {
            commit('removeToken');
            location.reload();
        }
    },
    getters: {
       GET_TOKEN(state) {
           return {
               accessToken: state.accessToken,
           }
       }
    }
}

export default token;