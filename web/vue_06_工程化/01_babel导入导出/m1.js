let a = 1
let b = 2
let c = 3

function show() {
    console.log("1111")
}

export default {
    a,
    c,
    show,
}

//按需导出
export let s1 = "aaa"
export let s2 = "bbb"
export function say() {
    console.log("0023442")
}