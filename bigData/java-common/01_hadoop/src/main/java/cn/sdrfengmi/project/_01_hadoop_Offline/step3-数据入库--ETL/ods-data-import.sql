--先把数据上传到hdfs
-- hdfs dfs -mkdir -p /weblog/preprocessed
-- hdfs dfs -mkdir -p /weblog/clickstream/pageviews
-- hdfs dfs -mkdir -p /weblog/clickstream/visits
-- hdfs dfs -mkdir -p /weblog/dim_time

-- hdfs dfs -put part-m-00000 /weblog/preprocessed
-- hdfs dfs -put part-r-00000 /weblog/clickstream/pageviews
-- hdfs dfs -put part-r-00000 /weblog/clickstream/visits
-- hdfs dfs -put dim_time_dat.txt /weblog/dim_time

set hive.exec.mode.local.auto=true;

---------------------

--导入清洗结果数据到贴源数据表ods_weblog_origin

load data inpath '/weblog/preprocessed/' overwrite into table ods_weblog_origin partition(datestr='20181101');
show partitions ods_weblog_origin;
select count(*) from ods_weblog_origin;
---------------------------------------------------------------------------
--导入点击流模型pageviews数据到ods_click_pageviews表

load data local inpath '/weblog/clickstream/pageviews' overwrite into table ods_click_pageviews partition(datestr='20181101');
select count(*) from ods_click_pageviews;
-----------------------------------------------------------------------------
--导入点击流模型visit数据到ods_click_stream_visit表

load data inpath '/weblog/clickstream/visits' overwrite into table ods_click_stream_visit partition(datestr='20181101');

----------------------------------------------------------------------------------------------------------------------
-- 时间维度表数据导入
-- 参考数据《dim_time_dat.txt》
load data inpath '/weblog/dim_time' overwrite into table t_dim_time;

load data local inpath '/root/hivedata/part-r-00000' into table ods_click_pageviews partition(datestr='20181101');
load data local inpath '/root/hivedata/part-r-00000' into table ods_click_stream_visit partition(datestr='20181101');
load data local inpath '/root/hivedata/dim_time.dat' into table t_dim_time;