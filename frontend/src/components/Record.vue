<template>
    <MyHeader></MyHeader>
    <div class="block">
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Autour</th>
                <th>BorrowingTime</th>
                <th>return</th>
            </tr>
            <tr v-for="inv in inventory" :key="inv.inventoryId">
                <td>{{ inv.inventoryId }}</td>
                <td>{{ inv.bookName }}</td>
                <td>{{ inv.author }}</td>
                <td>{{ inv.borrowingTime }}</td>
                <td v-if="inv.returnTime==null"><a @click="returnBook(inv.inventoryId)">return</a></td>
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
    name: 'Record',
    components: {
        MyHeader
    },
    data() {
        return {
            inventory:[]
        }
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
                this.inventory = r.data;
            })
        }
    },
    async mounted() {
        if (!localStorage.getItem("token")) {
            window.alert("請先登入");
            this.$router.push({ name: "Login" });
        }else {
            this.inventory = (await axios.get(config.url + "/book/show/record", {
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("token")
                }
            })).data;
        }
    },
}
</script>
