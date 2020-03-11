echo -e "\033[31m$(ps -ef | grep ecfTrade) \033[0m";
pid=$(ps -ef | grep ecfTrade | grep -v grep | awk '{print $2}') 
if [ ! $pid ];then
echo "ecfTrade no ecfTrade server pid"
else
kill -9 $pid
fi
sleep 2;
nohup java -jar ecfTrade-1.0.0-SNAPSHOT.jar >>/dev/null 2>&1 &