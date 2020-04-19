import Vue from 'vue'
import Router from 'vue-router'
import Login from './components/file.vue'
// import Home from './components/Home.vue'
// import Welcome from './components/Welcome.vue'
// import Users from './components/user/Users.vue'
// import Rights from './components/power/Rights.vue'
// import Roles from './components/power/Roles.vue'
// import Cate from './components/goods/Cate.vue'
// import Params from './components/goods/Params.vue'
// import GoodsList from './components/goods/List.vue'
// import Add from './components/goods/Add.vue'
// import Order from './components/order/Order.vue'
// import Report from './components/report/Report.vue'

// 路由懒加载模式 定义

Vue.use(Router)

const router = new Router({
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login }
  ]
})
export default router
