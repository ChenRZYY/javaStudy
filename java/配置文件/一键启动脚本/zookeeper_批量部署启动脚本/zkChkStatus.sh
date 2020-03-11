#!/bin/bash

#check status
for host in node01 node02 node03
do
	echo "===========checking zk node status : $host==============="
	ssh  $host 'source /etc/profile;/export/servers/zookeeper-3.4.9/bin/zkServer.sh status'
done
















