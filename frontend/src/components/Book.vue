<template>
    <MyHeader></MyHeader>
    <div v-if="isLoad" class="block">
        <div class="intro">
            <div class="introName">
                <h1 class="bookName">{{ book.name }}</h1>
            </div>
            <div class="introParg">
                <p class="bookIntro">{{ book.introduction }}</p>
            </div>
        </div>
        <table border="1" class="booktable">
            <tr class="inventoryName">
                <th class="homeThID">ID</th>
                <th class="homeThStstus">狀態</th>
                <th class="homeThBorrowingTime">借書日期</th>
                <th class="homeThBorrow"></th>
            </tr>
            <tr v-for="inv in inventory" :key="inv.inventoryId" class="inventoryDetail">
                <td class="homeThID">{{ inv.inventoryId }}</td>
                <td class="homeThStstus">{{ inv.status }}</td>
                <td class="homeThBorrowingTime">{{ inv.borrowingTime }}</td>
                <td v-if="inv.status == 'ALLOWED'"><a @click="borrowBook(inv.inventoryId)">借書</a></td>
                <td class="homeThBorrow" v-else></td>
            </tr>
        </table>
        <UserReviews></UserReviews>
    </div>
    <Loader v-else></Loader>
</template>
<script>
import axios from "axios";
import config from '../config'
import convertTime from "../utils/convertTime";
import MyHeader from "./MyHeader.vue";
import Loader from "./Loader.vue";
import UserReviews from "./UserReviews.vue"

export default {
    name: 'Book',
    components: {
        MyHeader,
        UserReviews,
        Loader
    },
    data() {
        return {
            name: '',
            book: {},
            inventory: {},
            isLoad: false
        }
    },
    methods: {
        async borrowBook(id) {
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
                window.alert(r.data);
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

        await axios.get(config.url + "/book/" + isbn)
            .then(async (res) => {
                this.book = res.data;
                if (this.book != "") {
                    await axios.get(config.url + "/book/inv/" + isbn)
                        .then(res => {
                            let inventory = res.data;
                            this.inventory = inventory.map(inv => {
                                inv.borrowingTime = convertTime(inv.borrowingTime);
                                return inv;
                            });
                            this.isLoad = true;
                        })
                } else {
                    this.$router.push({ name: "Home" });
                }
            })
    }

}
</script>

<style>
.booktable {
    margin: 5px 15px;
}

.intro {
    padding: 5px 15px;
    margin-bottom: 20px;
    margin-top: 30px;
}

.introName {
    border: #0a0808 solid 1px;
    margin-bottom: 10px;
}

.introParg {
    border: #0a0808 solid 1px;
    padding: 10px;
}

.inventoryName {
    background-color: rgb(214, 178, 98);
}

.inventoryDetail {
    background-color: rgb(163, 127, 61);
}

.intro p {
    color: #070606;
    font-size: 20px;
    margin: 0;
}

.bookName {
    text-align: center;
    text-transform: uppercase;
    color: #b34e5b;
}

.bookIntro {
    text-indent: 50px;
    text-align: justify;
    letter-spacing: 3px;
    display: inline-block;
}

.homeThID {
    width: 10%;
}

.homeThStstus {
    width: 15%;
}

.homeThBorrowingTime {
    width: 50%;
}

.homeThBorrow {
    width: 10%;
}
</style>
