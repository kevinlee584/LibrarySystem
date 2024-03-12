<template>
    <header>
        <p>Library System</p>
        <ul class="nav_links">
            <li><router-link :to="'./'">Home</router-link> </li>
            <li><router-link :to="'./record'">MyRecord</router-link></li>
            <li v-if="isAuth()"><router-link @click="logout()" :to="'./'">Logout</router-link></li>
            <li v-else><router-link :to="'./login'">Login</router-link></li>
        </ul>
    </header>
</template>

<script>

export default {
    name: "MyHeader",
    data() {
        return {
        }
    },
    methods:{
        isAuth(){
            return localStorage.getItem("token") != undefined
        },
        logout(){
            localStorage.clear();
            this.$forceUpdate();
        }
    }
}
</script>

<style>

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 4%;
    background-color: #333;
}

header p {
    color: azure;
    font-size: 40px;
    font-family: fantasy;
    margin-top: 20px;
    margin-bottom: 20px;

}

.nav_links {
    list-style: none;
}

.nav_links li {
    display: inline-block;
    padding: 0px 20px;
}

.nav_links li a{
    transition: all 0.3s ease 0s;
    font-size: 20px;
    color: azure;
    text-decoration: none;
}

.nav_links li a:hover{
    color: #0088a9;
}


</style>