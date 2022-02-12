<template>
    <div class="container-fluid">
        <div class="row">
            <h2 class="header-pos">
                <span>{{this.nickname}} 님의 프로필</span>
                <router-link v-if="this.isLoginUser" class="btn btn-primary mx-3" to="/edit/profile">프로필 편집</router-link>
            </h2>
            <div class="row d-flex">
                <aside class="leftSection col-md-2">
                    <label>이메일</label>
                </aside>
                <div class="rightSection col-md-7">
                    <p class="infoInput" type="text" name="email" id="inputEmail"> {{this.email}} </p>
                </div>
            </div>

            <div class="row d-flex">
                <aside class="leftSection col-md-2">
                    <label>이름</label>
                </aside>
                <div class="rightSection col-md-7">
                    <p class="infoInput" type="text" name="memberName" id="inputName"> {{this.memberName}} </p>
                </div>
            </div>

            <div class="row d-flex">
                <aside class="leftSection col-md-2">
                    <label>닉네임</label>
                </aside>
                <div class="rightSection col-md-7">
                    <p class="infoInput" type="text" name="nickname" id="inputNickname"> {{this.nickname}} </p>
                </div>
            </div>

            <div class="row d-flex">
                <aside class="leftSection col-md-2">
                    <label>관심분야</label>
                </aside>
                <div class="rightSection col-md-7">
                    <p class="infoInput" type="text" name="nickname" id="inputInterest1"> 1.  {{this.interests[0]}} </p>
                    <p class="infoInput" type="text" name="nickname" id="inputInterest2"> 2.  {{this.interests[1]}} </p>
                    <p class="infoInput" type="text" name="nickname" id="inputInterest3"> 3.  {{this.interests[2]}} </p>
                </div>
            </div>

            <div class="row d-flex">
                <aside class="leftSection col-md-2">
                    <label>경력소개</label>
                </aside>
                <div class="rightSection col-md-7">
                    <p class="infoInput" type="text" name="link" id="inputCareer"> {{this.link}} </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    data() {
        return { 
            isLoginUser: false,
            email: '',
            memberName: '',
            nickname: '',
            interests: [],
            link: '',
        }
    },
    created() {
        this.isLoginUser = this.$store.state.user.email == this.$route.params.email ? true : false;
        if(this.isLoginUser) {
            this.email = this.$store.state.user.email;
            this.memberName = this.$store.state.user.memberName;
            this.nickname = this.$store.state.user.nickname;
            this.interests = this.$store.state.user.interests;
            this.link = this.$store.state.user.link;
        } else {
            let param = { email : this.$route.params.email}
            axios.get('/members/profile', param)
            .then(res => {
                this.email = res.data.email;
                this.memberName = res.data.memberName;
                this.nickname = res.data.nickname;
                this.interests = res.data.interests;
                this.link = res.data.link;
                console.log(this.memberName);
            }).catch( err => {
                console.log(err);
            });
        }
    }
}
</script>

<style scoped>
.header-pos {
    margin-top: 3%;
}

.leftSection {
    padding-right: 20px;
    margin-top: 3%;
    text-align: right;
    font-size: 20px;
    border-right: 3px solid #BBBBBB;
}

.rightSection {
    margin-top: 3%;
    text-align: left;
    padding-right: 20%;
    font-size: 15px;
}

.loginUserImg{
	border-radius: 50%;
}

#userId {
    margin-top: 5px;
    margin-left: 10px;
    font-size: 25px;
    font-weight: bold;
}

.infoInput {
    width: 100%;
    height: 40px;
}

.infoInput::-webkit-input-placeholder{
    padding-left: 10px;
    text-align: left;
}

.desc{
    margin-bottom: 0;
}

.descInfo {
    font-size: 12px;
}

.btnRow {
    text-align: center;
    margin: 15px 10px;
}

.disabled {
    color: #BBBBBB;
    pointer-events: none;
}

</style>