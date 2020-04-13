const path = require("path")
    //把index.html放到首目录下 导入包
const HtmlWebpackPlugin = require("html-webpack-plugin")
    //创建对象
const htmlPlugin = new HtmlWebpackPlugin({
    //设置生成预览页面的模板文件
    template: "./src/index.html",
    //设置生成的预览页面名称
    filename: "index.html",
})

//解析.vue文件
const VueLoaderPlugin = require("vue-loader/lib/plugin")
const vuePlugin = new VueLoaderPlugin()

module.exports = {
    //补充：mode设置的是项目的编译模式。
    // 如果设置为development则表示项目处于开发阶段， 不会进行压缩和混淆， 打包速度会快一些
    // 如果设置为production则表示项目处于上线发布阶段， 会进行压缩和混淆， 打包速度会慢一些
    mode: "development", //可以设置为development(开发模式)，production(发布模式)
    entry: path.join(__dirname, "./src/index.js"), //打包输入路径
    output: {
        path: path.join(__dirname, "./dist"), // 输出文件的存放路径
        filename: "bundle.js", // 输出文件的名称
    }, //打包输出路径
    plugins: [htmlPlugin, vuePlugin],
    //打包.css结尾文件
    module: {
        rules: [{
                //test设置需要匹配的文件类型，支持正则
                test: /\.css$/,
                //use表示该文件类型需要调用的loader
                use: ["style-loader", "css-loader", "postcss-loader"],
            },
            // { 下面已经有加载,不用再添加,否则报错
            //     test: /\.css$/,
            //     use: ["style-loader", "css-loader"],
            // },
            {
                test: /\.less$/,
                use: ["style-loader", "css-loader", "less-loader"],
            },
            {
                test: /\.scss$/,
                use: ["style-loader", "css-loader", "sass-loader"],
            },
            {
                test: /\.jpg|png|gif|bmp|ttf|eot|svg|woff|woff2$/,
                //limit用来设置字节数，只有小于limit值的图片，才会转换
                //为base64图片
                use: "url-loader?limit=16940",
            },
            { test: /\.js$/, use: "babel-loader", exclude: /node_modules/ },
            { test: /\.vue$/, use: "vue-loader" },
        ],
    },
}