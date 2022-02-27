<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">프로그램 신청자 목록</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">{{ this.programInfo.title}}</li>
        </ol>
        <div class="row">
            <div class="col-xl-12 col-md-12">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas', 'clone']" class="mx-3"/>신청자 목록</span>
                    </div>
                    <div class="card-body">
                        <table class="w-100">
                            <tbody>
                            <tr>
                                <td width="15%">신청자 목록</td>
                                <td width="60%">가입 신청 메시지</td>
                                <td width="10%">역할</td>
                                <td width="15%">현재인원( {{ this.currentNumMember}} / {{ this.programInfo.maxMember}} )</td>
                            </tr>
                            <tr v-for="(applicant, index) in getApplicants" v-bind:key="applicant.id">
                                <td width="15%">{{applicant.nickname}}</td>
                                <td width="60%">{{applicant.message}}</td>
                                <td width="10%">{{applicant.role}}</td>
                                <td width="15%">
                                    <button class="btn btn-primary" @click="accept(applicant.id, index)">승낙</button>
                                    <button class="btn btn-danger" @click="reject(applicant.id, index)">거절</button>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios';

export default {
    name: 'ParticipantList',
    data() {
        return {
            programInfo : {},
            applicants : [],
            currentNumMember : 0,
        }
    },
    methods: {
        accept(memberId, index) {
            if(this.currentNumMember >= this.programInfo.maxMember) {
                alert("정원을 초과하였습니다.");
            } else {
                var targetData = {memberId : memberId}
                axios.post('/programs/' + this.programInfo.id + '/applicants/accept' , targetData)
                    .then(() => {
                        this.applicants.splice(index, 1);
                        alert("승인 완료되었습니다.");
                        this.currentNumMember += 1;
                    }).catch(err => {
                        console.log(err);
                    });
            }
        },
        reject(memberId, index) {
            var targetData = {memberId : memberId}
            axios.post('/programs/' + this.programInfo.id + '/applicants/reject' , targetData)
                .then(() => {
                    this.applicants.splice(index, 1);
                    alert("거절 완료되었습니다.");
                }).catch(err => {
                    console.log(err);
                });
        }
    },
    computed: {
        getApplicants() {
            return this.applicants;
        }
    },
    created() {
        const programId = this.$route.params.programId;
        axios.get('/programs/' + programId + '/applicants')
        .then(res => {
            this.programInfo = res.data.programInfo;
            if(res.data.applicants != null) {
                res.data.applicants.forEach(a => this.applicants.push(a));
            }
            this.currentNumMember = res.data.currentNumMember;
        }).catch(err => {
            console.log(err);
        });
    }
}
</script>

<style>

</style>