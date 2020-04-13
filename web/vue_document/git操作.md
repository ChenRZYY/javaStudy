## 1.git常用命令

### 1 提交代码

git status
git add .
git commit -m "完成登录功能"   提交到本地

### 2 本地合并分支,提交代码

git branch  查看分支
git checkout master 切换到主分支
git merge login   现在在master主干从login上合并到master
git push  把master代码提交上去

### 3 本地分支提交到云端

git checkout login 切换到login
git branch  查看本地分支
git push -u origin login  本地login提交到云端origin云端login(名字自己可以改动)

### 4 将代码提交到码云

新建一个项目终端,输入命令‘git status’查看修改过的与新增的文件内容
将所有文件添加到暂存区：git add .
将所有代码提交到本地仓库：git commit -m "添加登录功能以及/home的基本结构"
查看分支： git branch  发现所有代码都被提交到了login分支
将login分支代码合并到master主分支，先切换到master：git checkout master
在master分支进行代码合并：git merge login
将本地的master推送到远端的码云：git push
推送本地的子分支到码云，先切换到子分支：git checkout 分支名
然后推送到码云：git push -u origin 远端分支名

### 5 推送代码

创建user子分支，并将代码推送到码云
A.创建user子分支  git checkout -b user
B.将代码添加到暂存区 git add .
C.将代码提交并注释 git commit -m '添加完成用户列表功能'
D.将本地的user分支推送到码云 git push -u origin user
E.将user分支代码合并到master:
切换到master   git checkout master
合并user       git merge user
F.将本地master分支的代码推送到码云  git push
创建rights子分支
A.创建rights子分支 git checkout -b rights
B.将本地的rights分支推送到码云 git push -u origin rights


## 2.webpack常用命令

### 1.常用命令
npm init -y 创建项目，并打开项目所在目录的终端，输入命令：
npm install module_name -S    即    npm install module_name –save    写入dependencies
npm install module_name -D    即    npm install module_name –save-dev 写入devDependencies
npm install module_name -g 全局安装(命令行使用)
npm install module_name 本地安装(将安装包放在 ./node_modules 下)
npm install webpack webpack-cli -D  安装包
npm uninstall webpack  卸载包

HADOOP_HOME	C:\devTools\hadoop-2.7.5
npm install jQuery -S   安装的版本是1.7.4
npm install jQuery --save 安装的是	3.4.1

#### npm安装模块

【npm install xxx】利用 npm 安装xxx模块到当前命令行所在目录；
【npm install -g xxx】利用npm安装全局模块xxx；
本地安装时将模块写入package.json中：

【npm install xxx】安装但不写入package.json；
【npm install xxx –save】 安装并写入package.json的”dependencies”中；
 npm install --save echarts nprogress 安装多个包到运行依赖
【npm install xxx –save-dev】安装并写入package.json的”devDependencies”中。
 npm install html-webpack-plugin -D

####  npm 删除模块

【npm uninstall xxx】删除xxx模块； 
【npm uninstall -g xxx】删除全局模块xxx；

#### npm 查看模块

npm list vue 查看vue版本
vue -V 查看vue脚手架版本

// "dev": "webpack"
npm install jQuery -S
npm i jsdom --save
jsdom



### 2.webpack基本使用
1).打开项目目录终端，输入命令:
npm install webpack webpack-cli -D
2).然后在项目根目录中，创建一个 webpack.config.js 的配置文件用来配置webpack
在 webpack.config.js 文件中编写代码进行webpack配置，如下：
module.exports = {
mode:"development"//可以设置为development(开发模式)，production(发布模式)
}
补充：mode设置的是项目的编译模式。
如果设置为development则表示项目处于开发阶段，不会进行压缩和混淆，打包速度会快一些
如果设置为production则表示项目处于上线发布阶段，会进行压缩和混淆，打包速度会慢一些
3).修改项目中的package.json文件添加运行脚本dev，如下：
"scripts":{
    "dev":"webpack"
}
注意：scripts节点下的脚本，可以通过 npm run 运行，如：
运行终端命令：npm run dev
将会启动webpack进行项目打包
4).运行dev命令进行项目打包，并在页面中引入项目打包生成的js文件
打开项目目录终端，输入命令:
npm run dev
等待webpack打包完毕之后，找到默认的dist路径中生成的main.js文件，将其引入到html页面中。
浏览页面查看效果。

### 3.Vue脚手架

Vue脚手架可以快速生成Vue项目基础的架构。
A.安装3.x版本的Vue脚手架：
npm install -g @vue/cli
命令：vue ui




