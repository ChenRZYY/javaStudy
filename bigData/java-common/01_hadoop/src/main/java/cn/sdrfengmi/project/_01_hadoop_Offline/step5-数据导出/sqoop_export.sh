#!/bin/bash
export SQOOP_HOME=/export/servers/sqoop

if [ $# -eq 1 ]
then
    execute_date=`date --date="${1}" +%Y%m%d`
else
    execute_date=`date -d'-1 day' +%Y%m%d`
fi

echo "execute_date:"${execute_date}

table_name="dw_webflow_basic_info"
hdfs_dir=/user/hive/warehouse/db02.db/dw_webflow_basic_info/datestr=${execute_date}
mysql_db_pwd=123
mysql_db_name=root

echo 'sqoop start'
$SQOOP_HOME/bin/sqoop export \
--connect "jdbc:mysql://hadoop01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table $table_name \
--fields-terminated-by '\001' \
--update-key monthstr,daystr \
--update-mode allowinsert \
--export-dir $hdfs_dir

echo 'sqoop end'
