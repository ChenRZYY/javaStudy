#!/bin/sh
port = 'ps -ef | grep ecfTrade'
echo $port;
ps -ef | grep ecfTrade | grep -v grep | awk '{print $2}' | xargs kill -9
echo "关闭程序";
sleep 5;
nohup java -jar ecfTrade-1.0.0-SNAPSHOT.jar &


#for((i=1;i<=500;i++));
#do
#netstat -n | grep 7391 >>test.log;
#echo ' ';
#echo -n socket连接总数量: >> test.log ; netstat -n | grep 7391 |wc -l >> test.log;
#netstat -n | grep 7391 |wc -l ;
#date >> test.log ;
#echo -----------------------------------------
#sleep 1;
#done

#var=` find /apps/swapping -name '*swapping*.jar' `
#打印变量结果
#echo $var