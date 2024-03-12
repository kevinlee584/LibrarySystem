<template>
    <MyHeader></MyHeader>
    <div class="block">
        <table border="1">
            <tr>
                <th>Name</th>
                <th>Autour</th>
                <th>ISBN</th>
            </tr>
            <tr v-for="book in books" :key="book.isbn">
                <td><router-link :to="'/book?ISBN=' + book.isbn">{{ book.name }}</router-link></td>
                <td>{{ book.author }}</td>
                <td>{{ book.isbn }}</td>
            </tr>
        </table>
    </div>
</template>

<script>
import axios from "axios";
import MyHeader from './MyHeader.vue'
import config from "../config";
export default {
    name: 'Home',
    components: {
        MyHeader
    },
    data() {
        return {
            name: '',
            books: [],
        }
    },
    async mounted() {
        let books = (await axios.get(config.url + "/book/all")).data;
        this.books = books;
    }

}
</script>
