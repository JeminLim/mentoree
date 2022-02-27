import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import store from '../store/index.js'

// 보드 관련
import Board from '../views/Board.vue'
import BoardCreate from '../views/BoardCreate.vue'
import BoardInfo from '../views/BoardInfo.vue'

//미션 관련
import MissionCreate from '../views/MissionCreate.vue'

//프로그램 관련
import ParticipantList from '../views/ParticipantList.vue'
import Mission from '../views/Mission.vue'
import ProgramCreate from '../views/ProgramCreate.vue'
import ProgramInfo from '../views/ProgramInfo.vue'

// 유저관련
import ProfileBrowse from '../views/ProfileBrowse.vue'
import ProfileEdit from '../views/ProfileEdit.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/mission/:missionId/board',
    name: 'Board',
    component: Board,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/mission/:missionId/board/create',
    name: 'BoardCrate',
    component: BoardCreate,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/board/:boardId',
    name: 'BoardInfo',
    component: BoardInfo,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/program/:programId/mission/create',
    name: 'MissionCreate',
    component: MissionCreate,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/program/:programId/participant',
    name: 'ParticipantList',
    component: ParticipantList,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/profile/:email',
    name: 'ProfileBrowse',
    component: ProfileBrowse,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/edit/profile',
    name: 'ProfileEdit',
    component: ProfileEdit,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/program/:programId/mission',
    name: 'Mission',
    component: Mission,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/program/create',
    name: 'ProgramCreate',
    component: ProgramCreate,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/program/:programId/info',
    name: 'ProgramInfo',
    component: ProgramInfo,
    meta: {
      authRequired: true
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      authRequired: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      authRequired: false
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {

    if(to.matched.some(function(routeInfo) {
      return routeInfo.meta.authRequired;
    })) {
      var loginEmail = store.getters['user/GET_LOGIN_USER'];
      if(loginEmail == '') {
        next('/login');
      } else {
        next();
        // if(from.path != to.path)
        //   next();
        // else
        //   VueRouter.go(to);
      }
    } 
    else {
      next();
    }

    
});

const makeTitle = (title) => title ? '${title} | Mentoree' : "Mentoree";

router.afterEach( (to) => {
  Vue.nextTick(() => {
    document.title = makeTitle(to.meta.title);
  });
});

export default router

