// "vue": {
//     "devServer": {
//         "port": "9990",
//         "open": true
//     }
// }
// 可以放到package.json  一般单独放到vue.config.js文件中
module.exports = {
  chainWebpack: config => {
    // 发布模式
    config.when(process.env.NODE_ENV === 'production', config => {
      config.entry('app').clear().add('./src/main-prod.js')

      config.set('externals', {
        vue: 'Vue',
        'vue-router': 'VueRouter',
        axios: 'axios',
        lodash: '_',
        echarts: 'echarts',
        nprogress: 'NProgress',
        'vue-quill-editor': 'VueQuillEditor'
      })
      //isProd 定义是dev还是prod
      config.plugin('html').tap(args => {
        args[0].isProd = true
        return args
      })
    })

    // 开发模式
    config.when(process.env.NODE_ENV === 'development', config => {
      config.entry('app').clear().add('./src/main-dev.js')
      //isProd 定义是dev还是prod,为了在index.html中获取这个值,来判断dev,prod显示不同的标题内容
      config.plugin('html').tap(args => {
        args[0].isProd = false
        return args
      })
    })
  }
}
