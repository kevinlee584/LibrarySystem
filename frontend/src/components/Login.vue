<template>
    <MyHeader></MyHeader>
    <div class="container">
        <h3>Login</h3>
        <label for="phoneNumber">PhoneNumber</label>
        <input type="text" v-model="phoneNumber" placeholder="請輸入10位數字">
        <label for="password">Password</label>
        <input type="password" v-model="password" placeholder="請輸入4~20位中英文組合">

        <button class="abutton" v-on:click="login">Login</button>
        <button class="bbutton" v-on:click="redirectToSignUp">Sign Up</button>
    </div>
</template>

<script>
import axios from 'axios'
import MyHeader from './MyHeader.vue'

export default {
    name: 'LogIn',
    components: {
        MyHeader
    },
    data() {
        return {
            phoneNumber: '',
            password: ''
        }
    },
    methods: {
        redirectToSignUp() {
            this.$router.push({ name: "SignUp" });
        },
        async login() {
            var bodyFormData = new FormData()
            bodyFormData.append('password', this.password)
            bodyFormData.append('phoneNumber', this.phoneNumber)

            await axios({
                method: "post",
                url: "http://localhost:8090/signin",
                data: bodyFormData,
                headers: { "Content-Type": "multipart/form-data" },
            }).then(res => {
                localStorage.setItem("token", res.headers.getAuthorization())
                this.$router.push({ name: "Home" });
            }).catch(() => {window.alert("帳號或密碼錯誤");})
            
        }
    }
}
</script>