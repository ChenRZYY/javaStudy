#!/bin/bash
# author:lyc
# date:2019-9-10

# 将 zookeeper 解压至 /export/servers/zookeeper-3.4.9
# 在 node01 服务器上运行此脚本即可

# 修改 zoo.conf 文件
zoo_path="/export/servers/zookeeper-3.4.9/conf/zoo.cfg"


if [ -e $zoo_path ]
then
	rm -rf $zoo_path
	echo "删除 zoo 重新复制 zoo "
fi

cp /export/servers/zookeeper-3.4.9/conf/zoo_sample.cfg $zoo_path

# 修改文件,追加配置

cat>>$zoo_path<<EOF

dataDir=/export/servers/zookeeper-3.4.9/zkdatas
# 保留多少个快照
autopurge.snapRetainCount=3
# 日志多少小时清理一次
autopurge.purgeInterval=1
# 集群中服务器地址
server.1=node01:2888:3888
server.2=node02:2888:3888
server.3=node03:2888:3888

EOF

echo "zoo.cfg修改完成"

# 重新创建 zkdata文件
dataDir="/export/servers/zookeeper-3.4.9/zkdatas"
if [ -d $dataDir ] 
then
	rm -rf $dataDir
fi

mkdir -p $dataDir 

echo "创建zkdatas 文件夹"

# 复制文件至另外二台主机
scp -r /export/servers/zookeeper-3.4.9 node02:/export/servers/
scp -r /export/servers/zookeeper-3.4.9 node03:/export/servers/

echo "复制完成"
sleep 2s

echo "设置不同的myid"
echo 1 > $dataDir/myid
ssh node02  "mkdir -p $dataDir; echo 2 > $dataDir/myid"
ssh node03  "mkdir -p $dataDir; echo 3 > $dataDir/myid"


