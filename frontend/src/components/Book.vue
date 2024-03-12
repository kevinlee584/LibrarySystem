<template>
    <MyHeader></MyHeader>
        <div class="block">
            <div class="intro">
            <h1 class="bookName">{{ book.name }}</h1>
            <p class="bookIntro">{{ book.introduction }}</p>
        </div>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>STATUS</th>
                <th>BORROWING TIME</th>
                <th>borrow</th>
            </tr>
            <tr v-for="inv in inventory" :key="inv.inventoryId">
                <td>{{ inv.inventoryId }}</td>
                <td>{{ inv.status }}</td>
                <td>{{ inv.borrowingTime }}</td>
                <td v-if="inv.status=='ALLOWED'"><a @click="borrowBook(inv.inventoryId)">borrow</a></td>
                <td v-else></td>
            </tr>
        </table>
    </div>
</template>
<script>
import axios from "axios";
import MyHeader from './MyHeader.vue'
import config from '../config'

export default {
    name: 'Book',
    components: {
        MyHeader
    },
    data() {
        return {
            name: '',
            book: {},
            inventory: {}
        }
    },
    methods:{
        async borrowBook(id){
            if (!localStorage.getItem("token")) {
                window.alert("請先登入");
                this.$router.push({ name: "Login" });
                return;
            }
            var bodyFormData = new FormData()
            bodyFormData.append('inventoryId', id);

            await axios({
                method: "put",
                url: config.url + "/book/borrow",
                data: bodyFormData,
                headers: { "Content-Type": "multipart/form-data", Authorization: "Bearer " + localStorage.getItem("token") },
            }).then(r => {
                if (r.data == "success") window.alert("成功");
                else window.alert("失敗");
                this.$router.push({ name: "Home" });
            }).catch(() => {
                localStorage.removeItem("token");
                window.alert("請重新登入");
                this.$router.push({ name: "Login" });
            });

        }
    },
    async mounted() {
        let isbn = this.$route.query.ISBN
        let book = (await axios.get(config.url + "/book/" + isbn)).data;
        this.book = book;

        if (book != "") {
            let inventory = (await axios.get(config.url + "/book/inv/" + isbn)).data;
            this.inventory = inventory;
        } else {
            this.$router.push({ name: "Home" });
        }
    }

}
</script>

<style>
.intro {
    border: 1px solid gray;
    padding: 5px 5%;
    position: relative;
    margin-bottom: 20px;
}

.intro p {
    color: #fff;
    font-size: 20px;
}

.bookName {
    text-align: center;
    text-transform: uppercase;
    color: #4CAF50;
}

.bookIntro {
    text-indent: 50px;
    text-align: justify;
    letter-spacing: 3px;
    display: inline-block;
}
</style>
