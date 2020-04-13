//支持js高级语法babel插件
module.exports = {
    presets: ["@babel/preset-env"],
    plugins: [
        "@babel/plugin-transform-runtime",
        "@babel/plugin-proposal-class-properties",
    ],
}

//下面内容在 webpack.config.js中已经添加,这里不需要
// D.配置规则： 更改webpack.config.js的module中的rules数组
// module.exports = {
//     // ......
//     plugins: [htmlPlugin],
//     module: {
//         rules: [{
//                 //test设置需要匹配的文件类型，支持正则
//                 test: /\.css$/,
//                 //use表示该文件类型需要调用的loader
//                 use: ["style-loader", "css-loader"],
//             },
//             {
//                 test: /\.less$/,
//                 use: ["style-loader", "css-loader", "less-loader"],
//             },
//             {
//                 test: /\.scss$/,
//                 use: ["style-loader", "css-loader", "sass-loader"],
//             },
//             {
//                 test: /\.jpg|png|gif|bmp|ttf|eot|svg|woff|woff2$/,
//                 //limit用来设置字节数，只有小于limit值的图片，才会转换
//                 //为base64图片
//                 use: "url-loader?limit=16940",
//             },
//             {
//                 test: /\.js$/,
//                 use: "babel-loader",
//                 //exclude为排除项，意思是不要处理node_modules中的js文件,否则会报错
//                 exclude: /node_modules/,
//             },
//         ],
//     },
// }