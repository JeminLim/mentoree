<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">수행 보드</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">현재 미션</li>
        </ol>
        <div class="row">
            <div class="col-xl-4 col-md-6">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header d-flex">
                        <div class="col-md-9">
                            <font-awesome-icon :icon="['fas', 'clone']" />
                            <span>{{mission.title}}</span>
                        </div>
                        <div class="col-md-3">
                            <router-link class="btn btn-primary btn-sm" :to="'/mission/' + mission.id + '/board/create'">미션 수행하기</router-link>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="w-100">
                            <tbody>
                                <tr>
                                    <td class="bold" width="20%">제목</td>
                                    <td width="80%">{{mission.title}}</td>
                                </tr>
                                <tr>
                                    <td class="bold" width="20%">목표</td>
                                    <td width="80%">{{mission.goal}}</td>
                                </tr>
                                <tr>
                                    <td class="bold" width="20%">미션 내용</td>
                                    <td width="80%">{{mission.content}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <ol class="breadcrumb mb-4">
                <li class="breadcrumb-item active">수행 보드 리스트</li>
            </ol>
            <div class="row" v-if="boardList.length > 0">
                <a class="col-xl-12" href="#" v-for="board in boardList" v-bind:key="board.boardId" @click="goToBoardInfo(board.boardId)">
                    <div class="card mb-4">
                        <div class="card-header d-flex">
                            <div class="col-md-10"><font-awesome-icon :icon="['fas', 'clone']" class="mx-3"/>{{mission.title}}</div>
                            <div class="col-md-2">작성자 : {{board.writerNickname}}</div>
                        </div>
                        <div class="card-body content-brief">
                            <span>{{board.content}}</span>
                        </div>
                    </div>
                </a>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios';

export default {
    name: 'Board',
    data() {
        return {
            mission: {},
            boardList: [],
        }
    },
    methods: {
        goToBoardInfo(boardId) {
            this.$router.push('/board/' + boardId);
        }
    },
    beforeMount() {
        let missionId = this.$route.params.missionId;

        axios.get('/missions/' + missionId)
        .then(res => {
            this.mission = res.data.mission;
            res.data.boardList.forEach(board => this.boardList.push(board));
        }).catch(err => {
            console.log(err);
        })
    }
}
</script>

<style scoped>
a {
    text-decoration: none;
    color: black;
}
a:hover {
    cursor: pointer;
    color: black;
}

.bold {
    font-weight: bold;
}

.content-brief {
    height: 100px; 
    overflow: hidden; 
    text-overflow: ellipsis; 
    white-space: nowrap;
}
</style>