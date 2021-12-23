import axios from "axios";
import store from "../store";
import VueCookies from 'vue-cookies';

const instance = axios.create({
    baseURL: '/api',
    timeout: 5000
})

// request interceptor
instance.interceptors.request.use(async function (config) {

        var acToken = store.state.token.accessToken;
        if(acToken != '') {
            var payload = Buffer.from(acToken.split('.')[1], 'base64');
            var parsingInfo = JSON.parse(payload.toString());
            var exp = parsingInfo.exp;

            const currentTime = new Date().getTime();
            if(exp < currentTime || (currentTime - exp / 1000) < 60) {
                const refreshToken = VueCookies.get('refreshToken');
                await store.dispatch('token/reissueToken', refreshToken);
            }
        }

        //setting for header
        config.headers['x-access-token'] = VueCookies.get('accessToken');
        config.headers['x-refresh-token'] = VueCookies.get('refreshToken');
        config.headers['Content-Type'] = 'application/json; charset=UTF-8';
        return config;


    }, function(error) {
        console.log('axios request error: ', error);
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    function(response) {
        try {
            return response;
        } catch(error) {
            console.error('[axios.interceptors.response] response : ', error.message);
        }
    }, function(error) {
        console.log(error);
    }
)

export default instance;