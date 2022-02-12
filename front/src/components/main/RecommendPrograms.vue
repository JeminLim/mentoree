<template>
    <div>
        <div class="section-title d-flex">
            <span class="title">추천 프로그램 <a class="refresh-button mx-3" href="#" @click="reloadRecommendProgram()"><font-awesome-icon :icon="['fas', 'undo']" /></a></span>
        </div>
        <!-- 추천 프로그램이 없을 경우 -->
        <div v-if="this.programs.length <= 0" class="col-md-12 text-center">
            <span>개인정보 설정에서 관심분야를 설정해주세요</span>
        </div>
        <div id="recommendArea" class="row">
            <div class="col-xl-4 col-md-4" v-for="(program, index) in recommendProgram" v-bind:key="program.id">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <div class="d-flex">
                            <div class="col-md-12">
                                <font-awesome-icon :icon="['fas', 'clone']" />
                                <span> 프로그램 {{index + 1}} </span>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="w-100">
                            <colgroup>
                                <col width="20%"/>
                                <col width="60%"/>
                                <col width="20%"/>
                            </colgroup>
                            <tbody>
                                <tr>
                                    <td>제목</td>
                                    <td>{{program.title}}</td>
                                    <td>{{program.category}}</td>
                                </tr>
                                <tr>
                                    <td class="bold" width="20%" colspan="1">멘토</td>
                                    <td colspan="2">
                                        <div>
                                            <span v-if="program.mentor.length > 0">{{program.mentor[0].nickname}} 외 {{program.mentor.length - 1}} 명</span>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td>모집기간</td>
                                    <td>{{program.dueDate}}까지</td>
                                </tr>
                                <tr>
                                    <td class="bold" width="20%" colspan="1">목표</td>
                                    <td colspan="2">{{program.goal}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer d-flex align-items-center justify-content-between">
                        <a class="small text-dark stretched-link" href="#" @click="toProgramInfo(program.id)">자세히 보기</a>
                        <div class="small text-dark"><font-awesome-icon :icon="['fas', 'angle-right']"/></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios.js';
export default {
    name: 'RecommendPrograms',
    data() {
        return {
            page: 0,
            hasNext: true,
            programs: [],
        }
    },
    methods: {
        reloadRecommendProgram() {
            const data = {
                page: this.page,
                participatedPrograms: this.$store.state.user.participatedPrograms,
                interests: this.$store.state.user.interests
            };

            axios.post('/programs/list/recommend', JSON.stringify(data))
            .then(res => {
                if (res.data.programRecommendList != null) {
                    this.page += 1;
                    this.programs = [];
                    for(var i = 0; i < res.data.programRecommendList.length; i++) {
                        this.programs.push(res.data.programRecommendList[i])
                    }
                    this.hasNext = res.data.hasNext;
                }
            }).catch(err => {
                console.log(err);
            })
        },
        toProgramInfo(programId) {
            this.$router.push('/program/' + programId + '/info');
        }
    },
    computed: {
        recommendProgram() {
            return this.programs;
        }
    },
    beforeMount() {
        this.reloadRecommendProgram();
    }
}
</script>

<style scoped>
.section-title{
    margin: 1%;
}

.title {
    margin-right: 5%;
}

.refresh-button {
    text-decoration: none;
    cursor: pointer;
}

.bold {
    font-weight: bold;
}
</style>