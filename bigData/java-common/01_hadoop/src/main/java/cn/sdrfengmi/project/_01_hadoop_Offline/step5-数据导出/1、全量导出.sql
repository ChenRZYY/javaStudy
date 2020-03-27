-- HQL示例：
insert overwrite  directory ‘/user/root/export/test’ row format
delimited fields terminated by ‘,’ STORED AS textfile select F1,F2,F3 from <sourceHiveTable>;

-- SQOOP脚本：
sqoop export --connect jdbc:mysql://localhost:3306/wht --username root
--password cloudera --table <targetTable> --fields-terminated-by ','  --columns F1,F2,F3 --export-dir /user/root/export/test


-----------------------------
insert overwrite directory '/weblog/export/dw_pvs_referer_everyhour' row format
delimited fields terminated by '\001' STORED AS textfile select referer_url,hour,pv_referer_cnt from dw_pvs_referer_everyhour where datestr = "20181101";

-- Mysql:
create database weblog;
use weblog;
create table dw_pvs_referer_everyhour(
   hour varchar(10), 
   referer_url text, 
   pv_referer_cnt bigint);



bin/sqoop export \
--connect jdbc:mysql://node-1:3306/weblog \
--username root --password hadoop \
--table dw_pvs_referer_everyhour \
--fields-terminated-by '\001' \
--columns referer_url,hour,pv_referer_cnt \
--export-dir /weblog/export/dw_pvs_referer_everyhour

--注意：--columns选项当且仅当数据文件中的数据与表结构一致时（包括字段顺序）可以不指定
	  --否则应按照数据文件中各个字段与目标的字段的映射关系来指定该参数
	  
	  