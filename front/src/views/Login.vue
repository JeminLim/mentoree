<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-5">
                <div class="card shadow-lg border-0 rounded-lg mt-5">
                    <div class="card-header"><h3 class="text-center font-weight-light my-4">Login</h3></div>
                    <div class="card-body">
                        <form v-on:submit.prevent="loginSubmit">
                            <div class="row">
                                <div class="mb-3">
                                    <input class="form-control" id="inputEmail" type="email" ref="username" placeholder="name@example.com" />
                                    <label for="inputEmail">이메일 주소</label>
                                </div>
                                <div class="mb-3">
                                    <input class="form-control" id="inputPassword" type="password" ref="password" placeholder="Password" />
                                    <label for="inputPassword">비밀번호</label>
                                </div>
                                <div class="mb-3 login-row">
                                    <input id="inputRememberPassword" type="checkbox" value="" />
                                    <label id="inputRememberPassword-label" for="inputRememberPassword">자동 로그인</label>
                                    <button id="login-button" class="btn btn-primary btn-block" type="submit">로그인</button>
                                </div>
                                <div v-if="loginFail" class="mb-3">
                                    <p class="warning"> {{ errorMessage }}</p>
                                </div>
                            </div>
                        </form>
                        <div class="row">
                                <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a>
                        </div>
                    </div>
                    <div class="card-footer text-center py-3">
                        <div class="small"><router-link to="/register">회원가입</router-link></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '@/service/axios.js';

export default {
    name: 'Login',
    data() {
        return {
            loginFail: false,
            errorMessage: '',
        }
    },
    methods: {
        loginSubmit: async function() {
            let form = new FormData();
            form.append("username", this.$refs.username.value);
            form.append("password", this.$refs.password.value);

            await axios.post('/login/process', form)
            .then(res => {
                this.$store.dispatch('user/loginUser', res.data.user, {root: true});
                this.$router.push('/');
            }).catch(error => {
                console.log(error);
                if(error.response.data.status == 401){
                    this.loginFail = true;
                    this.errorMessage = '아이디 또는 비밀번호가 틀립니다.'
                }
            });
        }
    }
}
</script>

<style scoped>
.warning {
    color: red;
}

#inputRememberPassword{
    margin-top: 1%;
}
#inputRememberPassword, #inputRememberPassword-label{
    float: left;
}

#login-button {
    float: right;
}

</style>