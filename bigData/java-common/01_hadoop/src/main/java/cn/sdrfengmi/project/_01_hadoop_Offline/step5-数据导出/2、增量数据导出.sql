
create table dw_webflow_basic_info(
monthstr varchar(20),
daystr varchar(10),
pv bigint,
uv bigint,
ip bigint,
vv bigint
)


bin/sqoop export \
--connect jdbc:mysql://node-1:3306/weblog \
--username root --password hadoop \
--table dw_webflow_basic_info \
--fields-terminated-by '\001' \
--export-dir /user/hive/warehouse/db02.db/dw_webflow_basic_info/datestr=20181101/


-----------------------------------------------------------------------------------
--为了方便演示  我们手动生成往hive 中添加 20181103的数据
insert into table dw_webflow_basic_info partition(datestr="20181103") values("201811","03",14250,1341,1341,96);


--sqoop 增量导入：

bin/sqoop export \
--connect jdbc:mysql://node-1:3306/weblog \
--username root \
--password hadoop \
--table dw_webflow_basic_info \
--fields-terminated-by '\001' \
--update-key monthstr,daystr \
--update-mode allowinsert \
--export-dir /user/hive/warehouse/itheima.db/dw_webflow_basic_info/datestr=20181103/







