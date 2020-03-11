#!/bin/bash

for((i=1;i<=500;i++));  
do
netstat -tun | grep ":7391" >>test.log;
echo ' ';
echo -n socket连接总数量: >> test.log ; netstat -tun | grep ":7391" |wc -l >> test.log;
netstat -tun | grep ":7391" |wc -l ;
date >> test.log ;
echo -----------------------------------------
sleep 1;
done  
