const user = {
    namespaced: true,
    state: {
        email: '',
        nickname: '',
        memberName: '',
        link: '',
        interests: [],
        participatedPrograms: [],
    },
    mutations: {
        loginUser(state, payload) {
            state.email = payload.email;
            state.nickname = payload.nickname;
            state.memberName = payload.memberName;
            state.link = payload.link;
            state.interests = payload.interests;
            state.participatedPrograms = payload.participatedPrograms;
        },
        addProgram(state, payload) {
            state.participatedPrograms.push(payload);
        },
        logout(state) {
            state.email = '';
            state.nickname = '';
            state.memberName = '';
            state.link = '';
            state.interests = [];
            state.participatedPrograms = [];
        },
        update(state, payload) {
            state.nickname = payload.nickname;
            state.link = payload.link;
            state.interests = payload.interests;
            state.memberName = payload.memberName;
        }
    },
    actions: {
        loginUser: ({commit}, params) => {
            commit('loginUser', params);
         },
         addProgram: ({commit}, params) => {
            commit('addProgram', params);
         },
         logout: ({commit}) => {
            commit('logout');
         },
         update: ({commit}, params) => {
            commit('update', params);
         }
    },
    getters: {
        GET_LOGIN_USER: function(state) {
            return state.email;
        },
        GET_PARTICIPATE_PROGRAM: function(state) {
            return state.participatedPrograms;
        }
    }
}

export default user;