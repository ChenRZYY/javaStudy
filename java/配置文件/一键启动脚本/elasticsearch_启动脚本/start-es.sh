for n in node01 node02 node03
do
ssh $n "sysctl -w vm.max_map_count=262144
su - es <<EOF
source /etc/profile
nohup /export/servers/es/elasticsearch-6.7.0/bin/elasticsearch >es-start.log 2>&1 &
echo $n "es start!!"
EOF"
done

