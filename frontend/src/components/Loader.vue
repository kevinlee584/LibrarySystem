<template>
    <span class="loader"></span>
</template>

<script>
export default {
    name: "Loader"
}
</script>

<style>
.loader {
    width: 96px;
    height: 96px;
    border: 10px solid #fff;
    border-bottom-color: transparent;
    border-radius: 50%;
    display: inline-block;
    box-sizing: border-box;
    animation: rotation 1s linear infinite;
    position: relative;
    left: 50%;
    margin-top: 10%;
}

@keyframes rotation {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>