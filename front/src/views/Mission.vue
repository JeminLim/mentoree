<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">{{programTitle}}</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">
                <span>현재 진행중인 미션 <router-link v-if="isMentor" class="btn btn-primary btn-sm mx-3" :to="'/program/' + programId + '/mission/create'"> 미션 생성</router-link></span>
            </li>
        </ol>
        <div class="row">
            <CurrentMission />
        </div>
        <ol class="breadcrumb mb-4 mx-3">
            <li class="breadcrumb-item active">과거 미션 목록</li>
        </ol>
        <div class="row">
            <PastMission />
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios';

import CurrentMission from '@/components/main/CurrentMission';
import PastMission from '@/components/main/PastMission'

export default {
    name: 'Mission',
    components: {
        CurrentMission,
        PastMission
    },
    data() {
        return {
            programId : this.$route.params.programId,
            programTitle : '',
            isMentor : false,
        }
    },
    beforeMount() {
        let programId = this.$route.params.programId;
        let loginUserEmail = this.$store.state.user.email;

        axios.get('/program/' + programId + '/info')
        .then(res => {
            this.programTitle = res.data.programInfo.title;
            if(res.data.programInfo.mentor != null) {
                res.data.programInfo.mentor.forEach(m => {
                    if(m.email == loginUserEmail) {
                        this.isMentor = true;
                    } 
                });
            } else {
                console.log("mentor is null");
            }
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

</style>