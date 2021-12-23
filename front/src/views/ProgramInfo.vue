<template>
    <div>
        <div class="container-fluid px-4">
            <h1 class="mt-4">{{programInfo.title}}</h1>
            <ol class="breadcrumb mb-4">
                <li class="breadcrumb-item active">프로그램 상세 페이지</li>
            </ol>
            <div class="row">
                <div class="col-xl-12 col-md-12">
                    <div class="card bg-white text-dark mb-4">
                        <div class="card-header d-flex">
                            <div class="col-md-11 pt-1">
                                <span><font-awesome-icon :icon="['fas','clone']" class="mx-3" />
                                상세정보</span>
                            </div>
                            <div class="col-md-1">
                                <router-link v-if="isHost" id="manageBtn" class="btn btn-warning btn-sm" :to="'/program/' + program.id + '/participant'" >신청자관리</router-link>
                                <button v-if="!this.isParticipated" id="applyBtn" class="btn btn-primary btn-sm" @click="$bvModal.show('participate-modal')">참가신청</button>
                            </div>
                        </div>
                        <div sclass="card-body program-info-body">
                            <table class="w-100">
                                <tbody>
                                    <tr>
                                        <td class="bold" width="20%">제목</td>
                                        <td width="80%">{{programInfo.title}}</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">카테고리</td>
                                        <td width="80%">{{programInfo.category}}</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">모집기간</td>
                                        <td width="80%">{{programInfo.dueDate}}까지</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">모집인원</td>
                                        <td width="80%">{{programInfo.maxMember}}명</td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">현재멘토</td>
                                        <td width="80%">
                                            <div v-if="programInfo.mentor != null">
                                                <a v-for="mentorMember in programInfo.mentor" v-bind:key="mentorMember.id" href="#" @click="moveToProfile(mentorMember.email)">{{mentorMember.nickname}}</a>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="bold" width="20%">프로그램 목표</td>
                                        <td width="80%">
                                            <span>{{programInfo.goal}}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="bold" colspan="2" width="100%">프로그램 설명</td>
                                    </tr>
                                    <tr>
                                        <td colspan="2" width="100%">
                                            {{programInfo.description}}
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 참가 modal -->
        <b-modal id="participate-modal" ref="participate-modal" hide-footer title="참가 신청">
            <div class="modal-content">
                <div class="modal-body">
                    <div class="col-md-12 d-flex">
                        <div class="col-md-10">
                            <label>가입 신청 메시지</label>
                        </div>
                        <div class="col-md-2 mx-4">
                            <label>역할</label>
                        </div>
                    </div>
                    <form id="applyForm" method="post" v-on:submit.prevent="applyProgram">
                        <div class="col-md-12 d-flex">
                            <div class="col-md-10">
                                <input class="my-4" style="width: 100%;" type="text" name="message" ref="message" placeholder="가입신청 메시지를 입력해주세요"/>
                            </div>
                            <div class="col-md-2">
                                <select class="mt-4 mx-4" ref="role" name="role">
                                    <option value="MENTOR">멘토</option>
                                    <option value="MENTEE">멘티</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-12" style="text-align: right;">
                            <button id="formSendBtn" type="submit" class="btn btn-primary">신청하기</button>
                        </div>
                    </form>
                </div>
            </div>
        </b-modal>
    </div>
    
</template>

<script>
import axios from '@/service/axios.js';
export default {
    name: 'ProgramInfo',
    data() {
        return {
            program: {},
            isHost: false,
            isParticipated: false,
        }
    },
    methods: {
        moveToProfile(email) {
            this.$router.push('/profile/' + email);
        },
        applyProgram() {
            var data = {
                nickname: this.$store.state.user.nickname,
                programId: this.program.id,
                message: this.$refs.message.value,
                role: this.$refs.role.value,
            }

            axios.post('/program/' + this.program.id + '/join', data)
            .then(res => {
                if(res.data.result == 'success') {
                    alert("신청 완료되었습니다.");
                    this.$refs['message'].value = '';
                    this.$refs['participate-modal'].hide();
                }

                if(res.data.result == 'duplicate') {
                    alert("이미 신청된 프로그램 입니다.");
                    this.$refs['message'].value = '';
                }
            }).catch(err => {
                console.log(err);
            })
        }
    },
    computed: {
        programInfo() {
            return this.program;
        }
    },
    beforeMount() {
        var programId = this.$route.params.programId;

        axios.get('/program/' + programId + '/info')
        .then(res => {
            this.program = res.data.programInfo;
            this.isHost = res.data.isHost;
        }).catch(err => {
            console.log(err);
        });
        var participatedPrograms = this.$store.state.user.participatedPrograms;
        for(var i = 0; i < participatedPrograms.length; i++) {
            if(programId == participatedPrograms[i].id) {
                this.isParticipated = true;
            }
        }
    }
}
</script>

<style scoped>
.program-info-body {
    margin-left: 15%; 
    margin-right: 15%
}

.bold {
    font-weight: bold;
}

table {
    border-collapse: separate;
    border-spacing: 0 10px;
}
</style>