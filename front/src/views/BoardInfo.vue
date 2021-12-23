<template>
    <div class="container-fluid px-4">
        <h1 class="mt-4">{{boardInfo.missionTitle}}</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">작성자: {{boardInfo.writerNickname}}</li>
        </ol>
        <div class="row">
            <div class="col-xl-12 col-md-12">
                <div class="card bg-white text-dark mb-4">
                    <div class="card-header">
                        <span><font-awesome-icon :icon="['fas','clone']" class="mx-3" />수행 내용</span>
                    </div>
                    <div class="card-body board-content">
                        {{boardInfo.content}}
                    </div>
                </div>
            </div>
        </div>
        <BoardReply />
    </div>
</template>

<script>
import BoardReply from '@/components/main/BoardReply';
import axios from '@/service/axios';

export default {
    name: 'BoardInfo',
    components: {
        BoardReply: BoardReply,
    },
    data() {
        return {
            boardInfo: {},
        }
    },
    beforeMount() {
        let boardId = this.$route.params.boardId;

        axios.get('/board/' + boardId + '/info')
        .then(res => {
            this.boardInfo = res.data.boardInfo;
        }).catch(err => {
            console.log(err);
        });
    }

}
</script>

<style scoped>
textarea {
    width: 95%; 
    overflow: scroll;
}

.reply-date {
    font-size: 0.8em; 
    color: #c1c1c1;
    text-align: right;
}

.reply-writer {
    border-right: 3px solid #A4A4A4;
}

.board-content {
    height: 500px;
    text-align: left;
}

</style>