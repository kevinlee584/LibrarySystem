<template>
    <MyHeader></MyHeader>
    <div class="block recordBlock">
        <table v-if="inventory.length != 0">
            <tr>
                <th class="recordThID">ID</th>
                <th class="recordThName">書名</th>
                <th class="recordThAuthor">作者</th>
                <th class="recordThTime">借書時間</th>
                <th></th>
            </tr>
            <tr v-for="inv in inventory" :key="inv.inventoryId">
                <td>{{ inv.inventoryId }}</td>
                <td>{{ inv.bookName }}</td>
                <td>{{ inv.author }}</td>
                <td>{{ inv.borrowingTime }}</td>
                <td v-if="inv.returnTime==null"><a @click="returnBook(inv.inventoryId)">還書</a></td>
                <td v-else></td>
            </tr>
        </table>
        <div v-else><h1>尚未有借書紀錄</h1></div>
    </div>
</template>

<script>
import axios from "axios";
import config from '../config'
import convertTime from '../utils/convertTime'
import MyHeader from "./MyHeader.vue";

export default {
    name: 'Record',
    data() {
        return {
            inventory:[]
        }
    },
    components: {
        MyHeader
    },
    methods:{
        async returnBook(id){
            var bodyFormData = new FormData()
            bodyFormData.append('inventoryId', id);

            await axios({
                method: "put",
                url: config.url + "/book/return",
                data: bodyFormData,
                headers: { "Content-Type": "multipart/form-data", Authorization: "Bearer " + localStorage.getItem("token") },
            }).then(async ()=> {
                const r = await axios.get(config.url + "/book/show/record", {
                    headers: {
                        Authorization: "Bearer " + localStorage.getItem("token")
                    }
                });
                this.inventory = r.data.map(inv => {
                    inv.borrowingTime = convertTime(inv.borrowingTime);
                    return inv;
                });
            }).catch(() => {
                localStorage.removeItem("token");
                window.alert("請重新登入");
                this.$router.push({ name: "Login" });
            })
        }
    },
    async mounted() {
        if (!localStorage.getItem("token")) {
            window.alert("請先登入");
            this.$router.push({ name: "Login" });
        }else {
            await axios.get(config.url + "/book/show/record", {
                headers: {
                        Authorization: "Bearer " + localStorage.getItem("token")
                    }
                }).then(res => {
                    this.inventory = res.data.map(inv => {
                        inv.borrowingTime = convertTime(inv.borrowingTime);
                        return inv;
                    });
                }).catch(() => {
                    localStorage.removeItem("token");
                    window.alert("請重新登入");
                    this.$router.push({ name: "Login" });
                });
        }
    },
}
</script>

<style>
    .recordBlock{
        width: 850px;
    }

    .recordBlock tr {
        border-bottom: #080808 solid 1px;
    }

    .recordBlock h1 {
        text-align: center;

    }

    .recordThID{
        width: 5%;
    }

    .recordThName{
        width: 30%;
    }

    .recordThAuthor{
        width: 20%;
    }

    .recordThTime{
        width: 35%;
    }
</style>
