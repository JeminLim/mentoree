<template>
    <div class="container-fluid">
        <div class="row">
            <h2 class="header-pos">{{this.$store.state.user.nickname}} 님의 프로필</h2>
            <form action="#" role="form" id="userInfoForm" method="post" v-on:submit.prevent="submitProfile" >
                <div class="row d-flex">
                    <aside class="leftSection col-md-2">
                        <label>이메일</label>
                    </aside>
                    <div class="rightSection col-md-7">
                        <input class="infoInput" type="text" placeHolder="이메일" name="email" id="inputUserEmail" ref="email" v-model="email" readonly/>
                    </div>
                </div>

                <div class="row d-flex">
                    <aside class="leftSection col-md-2">
                        <label>이름</label>
                    </aside>
                    <div class="rightSection col-md-7">
                        <input class="infoInput" type="text" placeHolder="이름" name="memberName" id="inputUserName" ref="memberName" v-model="memberName" />
                    </div>
                </div>

                <div class="row d-flex">
                    <aside class="leftSection col-md-2">
                        <label>닉네임</label>
                    </aside>
                    <div class="rightSection col-md-7">
                        <input class="infoInput" type="text" placeHolder="사용자 이름" name="nickname" id="inputUserId" ref="nickname" v-model="nickname" />
                    </div>
                </div>

                <div class="row d-flex">
                    <aside class="leftSection col-md-2">
                        <label>관심분야</label>
                    </aside>
                    <div class="rightSection col-md-7" >
                        <div v-for="n in selectLength" :key="n">
                            <label class="mx-3" for="selectInterest"> {{n}}. </label>
                            <select ref="interests" name="selectInterest" v-model="interests[n-1]">
                                <option disabled value="undefined">흥미분야선택</option>
                                <option v-for="(item, index) in selectList" :key="index" :value="item" :disabled="excludeSelectedItem(item)">
                                    {{item}}
                                </option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="row d-flex">
                    <aside class="leftSection col-md-2">
                        <label>경력소개</label>
                    </aside>
                    <div class="rightSection col-md-7">
                        <textarea class="infoInput" name="link" style="height: 200px" id="inputUserDesc" ref="link" v-model="link"></textarea>
                        <div class="additionalInfo">
                            <p class="desc descInfo">자신의 관심분야와 관련된 경력을 알 수 있도록, 간략한 소개 및 링크를 부탁드려요</p>
                        </div>
                    </div>
                </div>
                <div class="row btnRow col-md-1 mt-5" style="margin-left: 25%">
                    <button type="submit" class="btn btn-primary" id="updateUserInfo">제출</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios';
export default {
    name: 'ProfileEdit',
    data() {
        return {
            email: this.$store.state.user.email,
            memberName: this.$store.state.user.memberName,
            nickname: this.$store.state.user.nickname,
            interests: this.$store.state.user.interests,
            link: this.$store.state.user.link,

            selectLength: 3,
            selectList: [
                'IT/Programming',
                '음악',
                '인생상담',
                '취업',
                '미술',
            ]
        }
    },
    methods: {
        excludeSelectedItem(itemName) {
            var flag = false;
            for(var i = 0; i < this.interests.length; i++) {
                if(this.interests[i] == itemName)
                    flag = true;
            }
            return flag;
        },
        submitProfile() {
            var data = {
                email: this.email,
                memberName: this.memberName,
                nickname: this.nickname,
                interests: this.interests,
                link: this.link,
            }

            axios.post('/member/profile', data)
            .then(res => {
                alert("프로필 정보가 변경되었습니다.");
                this.$store.dispatch('user/update', res.data, {root: true});
                this.$router.push('/profile/' + this.email);
            }).catch(err => {
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