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
exit 0



# 无提示，复制配置文件
alias cp='cp'
hadoopDir="/export/servers/hadoop-2.7.5"

## 删除 hadoop 重新解压
rm -rf $hadoopDir
tar -vxf /export/softwares/hadoop-2.7.5.tar.gz -C /export/servers/

# 复制配置文件
confDir='../conf-hadoop-HA'

if [ -d $confDir ] ; then
	cp $confDir/core-site.xml /export/servers/hadoop-2.7.5/etc/hadoop/core-site.xml
	cp $confDir/hadoop-env.sh /export/servers/hadoop-2.7.5/etc/hadoop/hadoop-env.sh
	cp $confDir/hdfs-site.xml /export/servers/hadoop-2.7.5/etc/hadoop/hdfs-site.xml
	cp $confDir/mapred-env.sh /export/servers/hadoop-2.7.5/etc/hadoop/mapred-env.sh
	cp $confDir/mapred-site.xml /export/servers/hadoop-2.7.5/etc/hadoop/mapred-site.xml
	cp $confDir/slaves /export/servers/hadoop-2.7.5/etc/hadoop/slaves
	cp $confDir/yarn-site.xml /export/servers/hadoop-2.7.5/etc/hadoop/yarn-site.xml
	
	echo "从父目录 copy 配置文件"
else
	echo "没有找到配置文件，请将 conf-hadoop 放在 $(dirname $(pwd)) 目录"
	exit 1
fi


echo "配置文件 copy 完成 "

# 创建所需要的文件夹

mkdir -p /export/servers/hadoop-2.7.5/data/dfs/nn/name
mkdir -p /export/servers/hadoop-2.7.5/data/dfs/nn/edits
mkdir -p /export/servers/hadoop-2.7.5/data/system/jobtracker
mkdir -p /export/servers/hadoop-2.7.5/data/system/local
mkdir -p /export/servers/hadoop-2.7.5/data/dfs/dn
mkdir -p /export/servers/hadoop-2.7.5/data/tmp
mkdir -p /export/servers/hadoop-2.7.5/data/dfs/jn
mkdir -p /export/servers/hadoop-2.7.5/data/system/jobtracker

echo "数据文件夹 创建完成"

ssh node02 "rm -rf $hadoopDir"
ssh node03 "rm -rf $hadoopDir"

scp -r $hadoopDir node02:/export/servers/
scp -r $hadoopDir node03:/export/servers/

##  给 node02 node03  单独设置
ssh node02 "rm -rf $hadoopDir/etc/hadoop/yarn-site.xml"
ssh node03 "rm -rf $hadoopDir/etc/hadoop/yarn-site.xml"
scp -r $confDir/yarn-site-rm2.xml node02:$hadoopDir/etc/hadoop/yarn-site.xml
scp -r $confDir/yarn-site-rm1.xml node02:$hadoopDir/etc/hadoop/yarn-site.xml


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
echo "node 01 启动"
echo "输入 (y/n)"
read isF

if [ $isF == y ];then
	hdfs zkfc -formatZK
	hdfs namenode -format
	hdfs namenode -initializeSharedEdits -force
	
	hadoop-daemons.sh start journalnode
	start-dfs.sh
	
else
	echo "bye"
	exit 0
fi

echo "node 02 启动 standby 节点"
echo "输入 (y/n)"
read isF

if [ $isF == y ];then
	
	ssh node02 "source /etc/profile; hdfs namenode -bootstrapStandby; hadoop-daemon.sh start namenode"
else
	echo "bye"
	exit 0
fi


ssh node02 "source /etc/profile; start-yarn.sh"
ssh node03 "source /etc/profile; start-yarn.sh"

jps


