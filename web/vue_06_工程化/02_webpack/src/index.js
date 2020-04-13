import $ from "jQuery"
import "./css/1.css"
import "./css/1.less"
import "./css/1.scss"

$(function() {
    $("li:odd").css("backgroundColor", "blue")
    $("li:even").css("backgroundColor", "red")
})

//js高级语法必须安装 babel插件才能编译
class Person {
    static info = "aaa"
}

// -------------------vue使用----------------------------
import Vue from "vue"
// 导入单文件组件
import App from "./components/App.vue"

const vm = new Vue({
    el: "#app",
    render: (h) => h(App),
})