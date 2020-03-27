--为了方便演示  我们手动生成往hive 中添加 20181104的数据
insert into table dw_webflow_basic_info partition(datestr="20181104") values("201811","04",10137,1129,1129,103);

insert into table dw_webflow_basic_info partition(datestr="20190710") values("201907","10",10137,1129,1129,103);

----------------------------------------------------
--编写linux shell脚本执行sqoop导出
--配合crontab或者定时调度软件进行周期性调度执行