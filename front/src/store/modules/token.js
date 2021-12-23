import axios from 'axios';
import VueCookies from 'vue-cookies';

const token = {
    namespaced: true,
    state: {
        accessToken: '',
        refreshToken: ''
    },
    mutations: {
        login(state, payload) {
            VueCookies.set('accessToken', payload.accessToken, '30s');
            VueCookies.set('refreshToken', payload.refreshToken, '168h');
            state.accessToken = payload.accessToken;
            state.refreshToken = payload.refreshToken;
        },
        reissueToken(state, payload) {
            VueCookies.set('accessToken', payload, '30s');
            state.accessToken = payload;
        },
        removeToken(state) {
            VueCookies.remove('accessToken');
            VueCookies.remove('refreshToken');
            state.accessToken = '';
            state.refreshToken = '';
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
               refreshToken: state.refreshToken
           }
       }
    }
}

export default token;