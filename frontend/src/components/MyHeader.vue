<template>
    <header>
        <p>Library System</p>
        <ul class="nav_links">
            <li><a href="./">Home</a></li>
            <li><a href="./record">MyRecord</a></li>
            <li v-if="isAuth()"><a @click="logout()" href="./">Logout</a></li>
            <li v-else><a href="./login">Login</a></li>
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
            return localStorage.clear();
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