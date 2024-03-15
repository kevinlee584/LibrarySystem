<template>
    <div style="margin-left: 55px; margin-right: 35px; margin-top: 40px;">
        <div style="display: flex;">
            <div class="reviewHeader" v-on:click="showReviews">
                <p>評論</p>
                <div v-if="index == 1" style="background-color: #426fe5; height: 3px; margin: 0px 41%;"></div>
            </div>
            <div class="reviewHeader" v-on:click="writeReview">
                <p>撰寫評論</p>
                <div v-if="index == 2" style="background-color: #426fe5; height: 3px; margin: 0px 41%;"></div>
            </div>
        </div>
        <hr style="border: 2px solid rgb(24 23 23 / 50%); margin-top: 0px;" />
    </div>
    <div v-if="index == 1">
        <div v-for="review in reviews" :key="review.userName" class="review">
            <div style="display: flex;">
                <p class="name">{{ review.userName }}</p>
                <span v-for="n in 5" :class="n <= review.rate ? 'fa fa-star checked' : 'fa fa-star '"
                    :key="review.userName + n"></span>
            </div>
            <p style="font-size: 20px;">{{ review.comment }}</p>
            <hr style="border: 2px solid rgb(24 23 23 / 50%);">
        </div>
    </div>
    <div v-else style="display: grid;">
        <textarea  class="comment" placeholder="請詳細說明對本書的看法" v-model="comment"></textarea>
        <span class="star-rating">
            <p style="font-size: 30px;">評價 :
                <label for="rate-1" style="--i:1"><i class="fa fa-solid fa-star"></i></label>
                <input type="radio" name="rating" id="rate-1" value="1" v-model="rate" checked>
                <label for="rate-2" style="--i:2"><i class="fa fa-solid fa-star"></i></label>
                <input type="radio" name="rating" id="rate-2" value="2" v-model="rate">
                <label for="rate-3" style="--i:3"><i class="fa fa-solid fa-star"></i></label>
                <input type="radio" name="rating" id="rate-3" value="3" v-model="rate">
                <label for="rate-4" style="--i:4"><i class="fa fa-solid fa-star"></i></label>
                <input type="radio" name="rating" id="rate-4" value="4" v-model="rate">
                <label for="rate-5" style="--i:5"><i class="fa fa-solid fa-star"></i></label>
                <input type="radio" name="rating" id="rate-5" value="5" v-model="rate">
                <i class="fa fa-solid fa-paper-plane send" @click="sendReview"> 送出</i>
            </p>

        </span>
    </div>

</template>

<script>
import axios from "axios";
import config from '../config'

export default {
    name: "UserReviews",
    data() {
        return {
            reviews: [],
            index: 1,
            rate: 1,
            comment: ""
        }
    },
    methods: {
        writeReview() {
            this.index = 2;
        },
        showReviews() {
            this.index = 1;
        },
        async sendReview(){
            if (!localStorage.getItem("token")) {
                window.alert("請先登入");
                this.$router.push({ name: "Login" });
                return;
            }
            let isbn = this.$route.query.ISBN

            var bodyFormData = new FormData()
            bodyFormData.append('ISBN', isbn);
            bodyFormData.append('comment', this.comment);
            bodyFormData.append('rate', this.rate);

            await axios({
                method: "post",
                url: config.url + "/review/add",
                data: bodyFormData,
                headers: { "Content-Type": "multipart/form-data", Authorization: "Bearer " + localStorage.getItem("token") },
            }).then(async (res) => {
                this.index = 1;

                if (res.data == "success")
                    await axios({
                        method: "get",
                        url: config.url + "/review/" + isbn
                    }).then(res => {
                        this.reviews = res.data;
                    })
                else if (res.data == "repeated")
                    window.alert("已撰寫過評論");

            }).catch(e => console.log(e))
        }
    }, 
    async mounted(){
        let isbn = this.$route.query.ISBN

        await axios({
            method: "get",
            url: config.url + "/review/" + isbn
        }).then(res => {
            this.reviews = res.data;
        }).catch(e => console.log(e))
    }
}

</script>

<style>
.fa {
    font-size: 28px;
    font-family: 'FontAwesome';
    margin-left: 5px;
}

.checked {
    color: #faec1b;

}

.review {
    display: block;
    margin: 40px 15px;
    padding-left: 40px;
    padding-right: 20px;
}

.review .name {
    width: 30%;
    font-size: 20px;
    font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
    margin-left: 20px;
}

.review p {
    margin-top: 0px;
    margin-left: 20px;
}

.reviewHeader {
    width: 50%;
}

.reviewHeader:hover {
    background-color: rgba(0, 0, 0, 0.2);
}

.reviewHeader p {
    text-align: center;
}

.star-rating {
    white-space: nowrap;
    margin-left: 70px;
    margin-top: 5px;
}

.star-rating [type="radio"] {
    appearance: none;
}

.star-rating label:has(~ :checked) i {
	color: #faec1b;
	text-shadow: 0 0 2px #ffffff, 0 0 10px #ffee58;
}

.comment {
    display: block;
    margin-left: 70px;
    width: 80%;
    height: 78px;
    border-radius: 5px;
    margin-top: 20px;
    resize: none;
    box-sizing: border-box;
    border: 2px solid #837c7c;
    background-color: #cfcbc1cb;
    font-size: 16px;
    resize: none;
    padding: 5px 15px;
}

.send {
    margin-left: 25%;
    font-size: 30px;
    padding: 5px;
    border-radius: 5px;
}

.send:hover {
    color: #c44242;
}
</style>