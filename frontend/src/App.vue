<template>
  <router-view />
</template>

<script>

export default {
  name: "App"
}

</script>

<style>
body {
  padding: 0;
  margin: 0;
  background-image: url("../public/images/pexels-caio-46274.jpg");
  background-size: 100% 1080px;
  background-repeat: no-repeat;
}

.container {
  height: 550px;
  width: 400px;
  background-color: rgba(255, 255, 255, 0.01);
  position: absolute;
  transform: translate(-50%, -50%);
  top: 50%;
  left: 50%;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 50px rgba(6, 2, 32, 0.6);
  padding: 50px 35px;
}

.container * {
  font-family: sans-serif;
  color: #000;
  letter-spacing: 0.5px;
  outline: none;
}

.container h3 {
  font-size: 32px;
  font-weight: 500;
  line-height: 42px;
  text-align: center;
}

label {
  display: block;
  margin-top: 30px;
  font-size: 16px;
  font-weight: 500;
}

input {
  display: block;
  height: 50px;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 3px;
  padding: 0 10px;
  margin-top: 8px;
  font-size: 14px;
  font-weight: 300;
  border: #000 solid 1px;
  width: -webkit-fill-available;
}

::placeholder {
  color: #0f0f0f;
}

.container button {
  width: 100%;
  background-color: #ffffff;
  color: #080710;
  padding: 15px 0;
  font-size: 18px;
  font-weight: 600;
  border-radius: 5px;
  border: none;
  cursor: pointer;
}

.container .abutton {
  margin-top: 50px;
}

.container .bbutton {
  margin-top: 10px;
}

.block {
  width: 750px;
  height: 750px;
  background-color: rgba(255, 255, 255, 0.1);
  position: absolute;
  left: 50%;
  top: 50%;
  margin-top: 10px;
  transform: translate(-50%, -50%);
  border-radius: 10px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 40px rgba(8, 7, 16, 0.6);
  overflow: scroll;
  scrollbar-width: none;
  padding: 5px 5px;
}

.block * {
  font-family: 'Poppins', sans-serif;
  letter-spacing: 0.5px;
  outline: none;
}

table {
  border: none;
  border-collapse: collapse;
  display: block;
  border: 0px;
}

table th {
  padding: 5px;
  color: brown;
  font-size: 25px;
  widows: 33%;
  height: 50px;
  border-bottom: #000 solid 1px;
}

table td {
  padding: 5px;
  color: #080710;
  font-size: 20px;
  padding-left: 10px;
  height: 40px;
}

td a:hover{
  text-decoration: none;
  color: rgb(201, 19, 19);
}

td a {
  color: rgb(3, 3, 163);
}

</style>
