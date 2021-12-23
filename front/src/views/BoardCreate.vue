<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">게시글 작성</h1>
        <div class="row">
            <div class="col-xl-12 col-md-12">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas','clone']" class="mx-3" />
                        미션 수행 게시글</span>
                    </div>
                    <div class="card-body">
                        <form action="#" method="post" v-on:submit.prevent="submitForm" >
                            <div class="row mb-3">
                                <div class="col-md-10">
                                    <div class="form-floating mb-3 mb-md-0">
                                        <input class="form-control" id="inputProgramTitle" type="text" :value="mission.title" name="missionTitle" placeholder="미션 타이틀" disabled/>
                                        <label for="inputProgramTitle">해당 미션</label>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <div class="form-floating mb-3">
                                        <input class="form-control" id="inputWriter" type="text" placeholder="작성자" name="writerNickname" :value="this.$store.state.user.nickname" disabled/>
                                        <label for="inputWriter">작성자</label>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label class="mb-3" for="inputDesc">수행 결과 내용</label>
                                    <div class="form-floating mb-3 height-enough">
                                        <textarea class="form-control text-area" rows="5" ref="content" name="content" id="inputDesc" />
                                    </div>
                                </div>
                            </div>
                            <div class="mt-2 mb-0 float-right">
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
    name: 'BoardCreate',
    data() {
        return {
            mission: {},
        }
    },
    methods: {
        submitForm() {
            var data = {
                missionId : this.mission.id,
                missionTitle : this.mission.title,
                writerNickname : this.$store.state.user.nickname,
                content : this.$refs.content.value,
            }
            const missionId = this.mission.id;
            axios.post('/board', data)
            .then(() => {
                alert("작성 완료 되었습니다");
                this.$router.push('/mission/' + missionId + '/board');              
            }).catch(err => {
                console.log(err);
            })
        }
    },
    created() {
        const missionId = this.$route.params.missionId;
        axios.get('/mission/' + missionId)
        .then(res => {
            this.mission = res.data.mission;
        })
        .catch(err => {
            console.log(err);
        });
    }

}
</script>

<style scoped>
.height-enough {
    height: 500px;
}
.text-area {
    min-height: 500px;
    resize: none;
}
</style>