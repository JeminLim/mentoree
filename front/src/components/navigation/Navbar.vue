<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <a class="navbar-brand" href="#" @click="goToHome()">MENTOREE</a>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <SideNavbar />
                    </li>
                </ul>
                <div class="d-flex">
                    <b-navbar-nav>
                        <b-nav-item-dropdown right>
                            <template #button-content>
                                <em><font-awesome-icon :icon="['fas', 'user']" /></em>
                            </template>
                            <b-dropdown-item href="#" @click="goToProfile()">내 프로필</b-dropdown-item>
                            <hr>
                            <b-dropdown-item @click="logout()">로그아웃</b-dropdown-item>
                        </b-nav-item-dropdown>
                    </b-navbar-nav>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
import axios from '@/service/axios.js';
import SideNavbar from '@/components/navigation/SideNavbar';

export default {
    components: {
        SideNavbar
    },
    methods: {
        logout() {
            this.$store.dispatch('logout').user;
            axios.post('/logout');
        },
        goToProfile() {
            this.$router.push('/profile/' + this.$store.state.user.email);
        },
        goToHome() {
            this.$router.push('/');
        }
    }
}
</script>

<style scoped>
.nav-container {
    margin: 0;
}
</style>