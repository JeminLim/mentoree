<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">미션 작성</h1>
        <div class="row">
            <div class="col-xl-12 col-md-12">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas', 'clone']" class="mx-3"/>미션작성</span>
                    </div>
                    <div class="card-body">
                        <form action="#" method="post" v-on:submit.prevent="submitForm">
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="form-floating mb-3 mb-md-0">
                                        <input class="form-control" name="title" id="inputProgramTitle" type="text" ref="title" placeholder="미션 타이틀"/>
                                        <label for="inputProgramTitle">미션 타이틀</label>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-10">
                                    <div class="form-floating mb-3">
                                        <input class="form-control" name="goal" id="inputGoal" type="text" ref="goal" placeholder="미션 목표" />
                                        <label for="inputGoal">미션 목표</label>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <div class="form-floating mb-3 mb-md-0">
                                        <b-form-datepicker id="inputDueDate" class="form-control" ref="dueDate" v-model="datePick" />
                                        <label for="inputDueDate"> 마감기한 </label>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label class="mb-3" for="inputDesc">미션 설명</label>
                                    <div class="form-floating mb-3">
                                        <textarea name="content" rows="5" class="form-control text-area" id="inputDesc" ref="content" type="textarea" placeholder="미션 설명"></textarea>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-4 mb-0 float-right">
                                <button type="submit" class="btn btn-primary btn-block pull-right">작성하기</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios';

export default {
    name: 'MissionCreate',
    data() {
        return {
            datePick: '',
        }
    },
    methods: {
        submitForm() {
            const programId = this.$route.params.programId;
            var data = {
                programId : programId,
                title: this.$refs.title.value,
                goal: this.$refs.goal.value,
                content: this.$refs.content.value,
                dueDate: this.datePick
            }
            axios.post('/mission', data)
            .then(() => {
                alert("미션이 등록되었습니다.")
                this.$router.push('/program/' + programId + '/mission')
            }).catch(err => {
                console.log(err);
            })
        }
    }


}
</script>

<style scoped>
.text-area {
    min-height: 500px;
    resize: none;
}
</style>