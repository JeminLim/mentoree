<template>
<div>
    <div v-if="getMission.length > 0">
        <div class="row">
            <div class="col-xl-3 col-md-3 mission-card" v-for="mission in getMission" v-bind:key="mission.id" @click="goToBoard(mission.id)">
                <div class="card mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas','clone']" class="mx-3" />과거 미션</span>
                    </div>
                    <div class="card-body">
                        <table class="w-100">
                            <tbody>
                            <tr>
                                <td class="bold" width="20%">제목</td>
                                <td width="80%"> {{mission.title}} </td>
                            </tr>
                            <tr>
                                <td class="bold" colspan="2" width="100%">미션 내용</td>
                            </tr>
                            <tr>
                                <td colspan="2" width="100%">
                                    {{mission.content}}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div v-else>
        <div class="col-xl-12 col-md-12">
                <span> 진행된 미션이 없습니다. </span>
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
        axios.get('/missions/list', { params:{
            programId : this.$route.params.programId,
            isOpen: false
        }})
        .then(res => {
            res.data.missions.forEach(mission => this.missions.push(mission));
        }).catch(err => {
            console.log(err);
        });


    }
}
</script>

<style scoped>
.card-header {
    text-align: left;
}

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