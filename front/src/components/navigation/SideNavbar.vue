<template>
  <div>
    <b-button v-b-toggle.sidebar-1><font-awesome-icon :icon="['fas', 'bars']" /></b-button>
    <b-sidebar id="sidebar-1" title="MENTOREE" shadow>
        <div class="container-fluid">
            <div class="row">
                <h6 class="sidebar-head">Match</h6>
            </div>
            <div class="row">
                <span class="sidebar-item">
                    <font-awesome-icon :icon="['fas', 'search']" />
                    <router-link class="search-menu" :to="'/'">프로그램 찾기</router-link >
                </span>
            </div>
            <div class="row">
                <h6 class="sidebar-head">MY MENU</h6>
            </div>
            <div class="row">
                <a class="sidebar-item sidebar-collapse" v-b-toggle.collapseProgram href="#">
                    <font-awesome-icon :icon="['fas', 'columns']" class="collapse-icon" />내 프로그램<font-awesome-icon :icon="['fas', 'angle-down']" class="collapse-arrow" />
                </a>
                <b-collapse class="sidebar-item sidebar-collapse collapse-item" id="collapseProgram">
                    <div v-for="program in participatedPrograms" v-bind:key="program.id">
                        <hr>
                            <p class="collapse-item collapse-item-header">{{program.title}}</p>
                            <router-link class="collapse-item nav-link" :to="'/program/' + program.id + '/info'">프로그램 정보</router-link >
                            <router-link class="collapse-item nav-link" :to="'/program/' + program.id + '/mission'">미션</router-link>
                        <hr>
                    </div>
                </b-collapse>
            </div>
            
        </div>
    </b-sidebar>
  </div>
</template>

<script>
export default {
    computed: {
        participatedPrograms() {
            return this.$store.state.user.participatedPrograms;
        }
    }
}
</script>

<style scoped>
.sidebar-head {
    margin-top: 10%;
    text-align: left;
}

.sidebar-item {
    padding-left: 10%;
    margin-top: 5%;
    text-align: left;
}

.sidebar-collapse{
    text-align: left;
    text-decoration: none;
    color: black;
}

.collapse-icon, .collapse-arrow {
    margin-right: 5%;
}

.collapse-arrow {
    float: right;
}

.collapse-item {
    padding-left: 15%;
}

.collapse-item-header {
    margin-top: 3%;
    padding: 0;
}

.nav-link {
    margin-left: 5%;
    text-decoration: none;
}

.search-menu{
    margin-left: 3%;
    text-decoration: none;
    color: black;
}



</style>