<template>
    <MyHeader></MyHeader>
    <div class="block">
        <table class="homeTable">
            <tr class="tableNameTr">
                <th class="homeThName">書名</th>
                <th class="homeThAuthor">作者</th>
                <th class="homeThISBN">ISBN</th>
            </tr>
            <tr v-for="book in currentBooks" :key="book.isbn" class="tableRowTr" @click="$router.push('/book?ISBN=' + book.isbn);">
                <td>{{ book.name }}</td>
                <td>{{ book.author }}</td>
                <td>{{ book.isbn }}</td>
            </tr>
        </table>
        <div class="changePage">
            <p v-if="index != 1" @click="previousPage()" class="pageController">&lt;</p>
            <p v-else>&nbsp;&nbsp;</p>
            <p>{{ index + "/" + Math.ceil(books.length / 10) }}</p>
            <p v-if="index != Math.ceil(books.length / 10)" @click="nextPage()" class="pageController">&gt;</p>
            <p v-else>&nbsp;&nbsp;</p>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import config from "../config";
import MyHeader from "./MyHeader.vue";

export default {
    name: 'Home',
    data() {
        return {
            books: [],
            currentBooks: [],
            index: 1
        }
    },
    components: {
        MyHeader
    },
    async mounted() {
        let books = (await axios.get(config.url + "/book/all")).data;
        this.books = books;
        this.currentBooks = books.slice(0, 10)
    },
    methods: {
        nextPage(){
            this.currentBooks = this.books.slice(this.index * 10, (this.index + 1) * 10);
            this.index++;
        },
        previousPage(){
            this.index--;
            this.currentBooks = this.books.slice((this.index - 1) * 10, this.index * 10);
        }
    }

}
</script>

<style>
.homeTable {
  border-collapse: collapse;
  display: block;
  border: 0px;
  width: 100%;
  padding-top: 70px;
  border-bottom: #080808 solid 1px;
}

.tableNameTr {
  background-color: rgba(214, 178, 98, 0.65);
}

.tableRowTr {
  background-color: rgba(163, 127, 61, 0.35);
}

.tableRowTr:hover {
  background-color: rgba(163, 127, 61, 0.8);
}

.homeThName{
    width: 50%;
}

.homeThAuthor{
    width: 20%;
}

.homeThISBN{
    width: 30%;
}

.changePage{
    justify-content: center;
    display: flex;
}

.changePage p{
    font-size: 25px;
    margin: 15px 15px;
}

.changePage .pageController:hover {
    color: rgb(3, 3, 163);
}
</style>
