# node01 node02 node03 是三台 zkServer 节点，注意配置 hosts 文件
for host in node01 node02 node03
do
	echo "===========start zk cluster :$host==============="
	ssh  $host 'source /etc/profile; /export/servers/zookeeper-3.4.9/bin/zkServer.sh stop'
done
