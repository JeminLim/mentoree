<template>
    <div>
        <div class="section-title d-flex">
            <span class="title">전체 프로그램</span>
        </div>
        <div class="row">
                <div class="col-xl-3 programCard" v-for="(program, index) in allProgramList" v-bind:key="program.id">
                    <div class="card mb-4">
                        <div class="card-header">
                            <div class="d-flex">
                                <div class="col-md-8">
                                    <span><font-awesome-icon :icon="['fas', 'clone']" class="mx-2" />프로그램 {{ index + 1}}</span>
                                </div>
                                <div class="col-md-4">
                                    <a class="small text-dark stretched-link" href="#" @click="toProgramInfo(program.id)">자세히 보기</a>
                                </div>
                            </div>
                        </div>
                        <div class="card-body"> 
                            <table class="w-100">
                                <tbody>
                                    <tr>
                                        <td class="bold" width="20%">제목</td>
                                        <td width="80%">{{program.title}}</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">멘토</td>
                                        <td v-if="program.mentor.length > 0" width="80%"> {{program.mentor[0].nickname}} 외 {{program.mentor.length - 1}} 명</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">인원</td>
                                        <td width="80%"> {{program.maxMember}}명</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">분류</td>
                                        <td width="80%"> {{program.category}}</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">모집기간</td>
                                        <td width="80%"> {{program.dueDate}} 까지</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">목표</td>
                                        <td width="80%"> {{program.goal}} </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
        </div>
        <div class="row">
            <div class="col-md-12 text-center">
                <button v-if="hasNext" class="btn btn-primary" @click="updateProgram()">&plus; 더보기</button>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios.js';

export default {
    name: 'ProgramList',
    data() {
        return {
            page: 0,
            hasNext: true,
            programs: [],
        }
    },
    methods : {
        updateProgram() {
            const data = {
                page: this.page,
                participatedPrograms: this.$store.state.user.participatedPrograms,
            };
            axios.post('/programs/list', JSON.stringify(data))
            .then(res => {
                if(res.data.programList != null ) {
                    this.page += 1;
                    for(var i = 0; i < res.data.programList.length; i++) {
                        this.programs.push(res.data.programList[i])
                    }
                    this.hasNext = res.data.hasNext;
                }
            }).catch(err => {
                console.log(err);
            })
        },
        toProgramInfo(programId) {
            this.$router.push('/program/' + programId + '/info')
        }
    },
    computed: {
        allProgramList() {
            return this.programs;
        }
    },
    beforeMount() {
        this.updateProgram();
    },


}
</script>

<style scoped>
.section-title{
    margin: 1%;
}

.bold {
    font-weight: bold;
}


</style>