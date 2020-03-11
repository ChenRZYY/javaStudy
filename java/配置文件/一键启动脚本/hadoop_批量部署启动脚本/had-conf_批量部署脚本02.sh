#!/bash/bin
# author: lyc
# date: 2019-9-10

echo '
注意：使用说明
1- 确保在 hosts 文件中配置了 node01 node02 node03 三个节点
2- 确保已经正确安装 jdk1.8 并配置环境变量 PATH
3- 确保node01 02 03 相互之间已经配置了 ssh 免密登陆
4- 将hadoop-2.7.5.tar.gz 放置在 /export/softwares/ 目录
5- 在 node01 节点上运行该脚本
开始安装：(y / n)'

read isInstall

if [ ! $isInstall = y ]
then
	echo byebye
	exit 1
fi

echo 开始安装...

# 声明 hadoopDir
hadoopDir="/export/servers/hadoop-2.7.5"

## 删除 hadoop 重新解压
rm -rf $hadoopDir
tar -vxf /export/softwares/hadoop-2.7.5.tar.gz -C /export/servers/

# 无提示，复制配置文件
alias cp='cp'

if [ -d ../conf-hadoop ] ; then
	cp ../conf-hadoop/* $hadoopDir/etc/hadoop/
	echo "从父目录 copy 配置文件"
else
	echo "没有找到配置文件，请将 conf-hadoop 放在 $(dirname $(pwd)) 目录"
	exit 1
fi

echo "配置文件 copy 完成 "



rm -rf /export/servers/hadoop-2.7.5/hadoopDatas/

mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/tempDatas
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/namenodeDatas
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/namenodeDatas2
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/datanodeDatas
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/datanodeDatas2
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/nn/edits
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/snn/name
mkdir -p /export/servers/hadoop-2.7.5/hadoopDatas/dfs/snn/edits

echo "数据文件夹 创建完成"

ssh node02 "rm -rf $hadoopDir"
ssh node03 "rm -rf $hadoopDir"

scp -r $hadoopDir node02:/export/servers/
scp -r $hadoopDir node03:/export/servers/

# 配置环境变量
source /etc/profile

if [ ! $HADOOP_HOME ]
then
	echo "没有环境变量 HADOOP_HOME , 即将添加。"
# 写入变量 HADOOP_HOME
cat>>'/etc/profile'<<EOF

export HADOOP_HOME=/export/servers/hadoop-2.7.5
export PATH=:\$HADOOP_HOME/bin:\$HADOOP_HOME/sbin:\$PATH

EOF

else
    echo "已经有环境变量 HADOOP_HOME 了"
fi

source /etc/profile

# copy 环境变量
scp  /etc/profile node02:/etc/profile
scp  /etc/profile node03:/etc/profile

# 
echo "是否进行 namenode format"
echo "输入 (y/n)"
read isF

if [ $isF == y ];then
	/export/servers/hadoop-2.7.5/bin/hdfs namenode -format
else
	echo "bye"
	exit 0
fi

echo "是否全部启动？"
echo "输入 (y/n)"
read isB

if [ $isB == y ];then
	start-dfs.sh
	start-yarn.sh
	mr-jobhistory-daemon.sh start historyserver
fi

jps

