for n in node01 node02 node03
do
ssh $n "source /etc/profile;jps | grep Elasticsearch | grep -v grep | awk '{print $1}'|xargs kill -9 &"
echo $n" stoped--"
done
