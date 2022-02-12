<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-7">
                <div class="card shadow-lg border-0 rounded-lg mt-5">
                    <div class="card-header"><h3 class="text-center font-weight-light my-4">회원가입</h3></div>
                    <div class="card-body">
                        <form action="#" method="post" v-on:submit.prevent="submitRegistForm" >
                            <div class="form-floating mb-3">
                                <input class="form-control" id="inputEmail" type="email" ref="email" v-model="email" placeholder="name@example.com" />
                                <label for="inputEmail">이메일</label>
                            </div>
                            <div class="form-floating mb-3">
                                <input class="form-control" id="inputName" type="text" ref="memberName" placeholder="이름을 입력하세요" />
                                <label for="inputName">이름</label>
                            </div>
                            <div class="form-floating mb-3">
                                <input class="form-control" id="inputNickname" type="text" ref="nickname" v-model="nickname" placeholder="name@example.com" />
                                <label for="inputEmail">닉네임</label>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="form-floating mb-3 mb-md-0">
                                        <input class="form-control" id="inputPassword" type="password" ref="password" placeholder="Create a password" />
                                        <label for="inputPassword">비밀번호</label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-floating mb-3 mb-md-0">
                                        <input class="form-control" id="inputPasswordConfirm" type="password" v-model="doubleCheck" placeholder="Confirm password" />
                                        <label for="inputPasswordConfirm">비밀번호 확인</label>
                                    </div>
                                </div>
                            </div>
                            <div v-if="duplicateEmail || duplicateNickname || passwordNotMatch" class="row">
                                <span class="red bold">{{ message }}</span>
                            </div>
                            <div class="mt-4 mb-0">
                                <button class="btn btn-primary btn-block" type="submit">회원가입하기</button>
                            </div>
                        </form>
                    </div>
                    <div class="card-footer text-center py-3">
                        <div class="small"><router-link to="/login">회원이신가요? 로그인하러 가기</router-link></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'Register',
    data() {
        return {
            email: '',
            duplicateEmail: false,
            nickname: '',
            duplicateNickname: false,
            doubleCheck: '',
            passwordNotMatch: false,
            message: '',
        }
    },
    watch: {
        email(newEmail) {
            if(newEmail != null) {
                this.duplicateCheckEmail('이메일');
            }
        },
        nickname(newNickname) {
            if(newNickname != null) {
                this.duplicateCheckNickname('닉네임');
            }
        },
        doubleCheck(newDoubleCheck) {
            if(newDoubleCheck != this.$refs.password.value){
                this.passwordNotMatch = true;
                this.message = "비밀번호가 일치하지 않습니다.";
            } else {
                this.passwordNotMatch = false;
                this.message = '';
            }
        }
    },
    methods: {
        duplicateCheckEmail() {
            data = { email : this.$refs.email.value};
            axios.post('/members/join/email-check', data)
            .then(res => { 
                this.duplicateEmail = res.data;
                if(this.duplicateEmail) {
                    this.message = '이미 존재하는 이메일 입니다.';
                }
            }).catch(err => {
                console.log(err);
            })
        },
        duplicateCheckNickname() {
            data = { email : this.$refs.nickname.value};
            axios.post('/members/join/nickname-check', data)
            .then(res =>{
                this.duplicateNickname = res.data;
                if(this.duplicateNickname) {
                    this.message = '이미 존재하는 닉네임 입니다.';
                }
            }).catch(err => {
                console.log(err);
            })
        },
        submitRegistForm() {
            if(this.duplicateEmail || this.duplicateNickname || this.passwordNotMatch) {
                let msg = '';
                if(this.duplicateEmail) msg = "이미 존재하는 이메일입니다.";
                if(this.duplicateNickname) msg = "이미 존재하는 닉네임입니다.";
                if(this.passwordNotMatch) msg = "비밀번호가 일치하지 않습니다.";

                alert(msg);
            } else {
                var data = {
                    email: this.$refs.email.value,
                    memberName: this.$refs.memberName.value,
                    nickname: this.$refs.nickname.value,
                    password: this.$refs.password.value,
                }

                axios.post('/members/join', data)
                .then(() => {
                    alert("회원가입이 완료되었습니다.");
                    this.$router.push('/login');
                }).catch(err => {
                    console.log(err);
                });
            }
        }
    }
    
}
</script>

<style scoped>
.red {
    color: red;
}

.bold {
    font-weight: bold;
}


</style>