<template>
    <div>
        <div v-if="getMission.length > 0">
            <div class="col-xl-4 col-md-4 mission-card" v-for="mission in getMission" v-bind:key="mission.id" @click="goToBoard(mission.id)">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas','clone']" class="mx-3" />미션 카드</span>
                        <a class="move-board" href="#"> &#187;수행보드로 이동</a>
                    </div>
                    <div class="card-body" >
                        <table class="w-100">
                            <tbody>
                            <tr>
                                <td class="bold" width="20%">제목</td>
                                <td width="80%">{{mission.title}}</td>
                            </tr>
                            <tr>
                                <td class="bold" width="20%">기간</td>
                                <td width="80%">{{mission.dueDate}}까지</td>
                            </tr>
                            <tr>
                                <td class="bold" colspan="2" width="100%">미션 내용</td>
                            </tr>
                            <tr>
                                <td colspan="2" width="100%">
                                {{ mission.content}}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>      
        </div>
        <div v-else>
            <div class="col-xl-12 col-md-12">
                <span> 현재 진행중인 미션이 없습니다. </span>
            </div>
        </div>
    </div>
    
</template>

<script>
import axios from '@/service/axios';

export default {
    data() {
       return { 
           missions: [],
       }
    },
    methods: {
        goToBoard(missionId) {
            this.$router.push('/mission/' + missionId + '/board');
        }
    },
    computed: {
        getMission() {
            return this.missions;
        }
    },
    beforeMount() {
        var data = { programId : this.$route.params.programid}
        
        axios.get('/missions/list', data)
        .then(res => {
            res.data.missions.forEach(mission => this.missions.push(mission));
        }).catch(err => {
            console.log(err);
        });
    }
}
</script>

<style scoped>
.bold{
    font-weight: bold;
}

.move-board {
    margin-left:60%; 
    color: #808080; 
    font-size: 0.8em;
}

.mission-card:hover {
    cursor: pointer;
}
</style>