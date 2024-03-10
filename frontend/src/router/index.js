import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import SignUp from '../components/SignUp.vue'
import Home from '../components/Home.vue'
import Book from '../components/Book.vue'
import Record from '../components/Record.vue'

const routes = [
  {
    name: "SignUp",
    component: SignUp,
    path: "/signup"
  },
  {
    name: "Login",
    component: Login,
    path: "/login"
  },
  {
    name: "Home",
    component: Home,
    path: "/"
  },
  {
    name: "Book",
    component: Book,
    path: "/book"
  },
  {
    name: "Record",
    component: Record,
    path: "/record"
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
