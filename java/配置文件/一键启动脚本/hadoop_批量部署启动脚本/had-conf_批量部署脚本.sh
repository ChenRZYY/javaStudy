#!/bash/bin
# author: lyc
# date: 2019-9-10

# 注意：使用说明
# 1- 将 hadoop 解压至 /export/servers 目录
# 2- 将 conf-hadoop 文件夹放至 /export/softwares 目录
# 3- 在 node01 节点上运行该脚本


# 无提示，复制配置文件
alias cp='cp'
cp /export/softwares/conf-hadoop/* /export/servers/hadoop-2.7.5/etc/hadoop/

echo "配置文件 copy 完成 "

# 创建所需要的文件夹
hadoopDir="/export/servers/hadoop-2.7.5"

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
export PATH=:\$HADOOP_HOME\bin:\$HADOOP_HOME/sbin:\$PATH

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

