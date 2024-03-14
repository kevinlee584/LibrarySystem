<template>
    <MyHeader></MyHeader>
    <div class="container">
        <h3>Sign Up</h3>
        <label>Name</label>
        <input type="text" v-model="username" placeholder="請輸入4~20位中英文組合，可包含空白和底線">
        <label>PhoneNumber</label>
        <input type="text" v-model="phoneNumber" placeholder="請輸入10位數字">
        <label>Password</label>
        <input type="password" v-model="password" placeholder="請輸入4~20位中英文組合">
        <button class="abutton" v-on:click="signUp">Sign Up</button>
        <button class="bbutton" v-on:click="redirectToLogin">Login</button>
    </div>
</template>

<script>
import axios from 'axios'
import config from '../config'
import MyHeader from './MyHeader.vue'

export default {
    name: 'SignUp',
    data() {
        return {
            username: '',
            password: '',
            phoneNumber: ''
        }
    },
    components: {
        MyHeader
    },
    methods: {
        redirectToLogin() {
            this.$router.push({ name: "Login" });
        },
        async signUp() {
            var bodyFormData = new FormData()
            bodyFormData.append('username', this.username)
            bodyFormData.append('password', this.password)
            bodyFormData.append('phoneNumber', this.phoneNumber)

            await axios({
                method: "post",
                url: config.url + "/signup",
                data: bodyFormData,
                headers: { "Content-Type": "multipart/form-data" },
            })
            .then(() => {
                this.$router.push({ name: "Login" });
            })
            .catch(e => {window.alert(e.response.data.split('|')[1]);})

        }
    }

}
</script>

