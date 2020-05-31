--流量分析
-------------------------------------------------------------------------------------------
--基础指标统计分析：

--PageView 浏览次数（PV）:
select count(*) as pvs from dw_weblog_detail where datestr ="20181101";
select count(*) as pvs from dw_weblog_detail where datestr ="20181101" and valid = "true"; --过滤非法请求

--Unique Visitor 独立访客（UV）: 
select count(distinct remote_user) as uvs from dw_weblog_detail where datestr ="20181101"; --以cookie统计 精确度高
select count(distinct remote_addr) as uvs from dw_weblog_detail where datestr ="20181101"; --以ip统计，精确度低

--访问次数（VV）：
select count(distinct session) as vvs from ods_click_stream_visit where datestr ="20181101";

--IP：
select count(distinct remote_addr) as ips from dw_weblog_detail where datestr ="20181101";


--基础指标结果保存入库：

drop table dw_webflow_basic_info;
create table dw_webflow_basic_info(month string,day string,pv bigint,uv bigint,ip bigint,vv bigint) partitioned by(datestr string);

insert into table dw_webflow_basic_info partition(datestr="20181101")
select '201811', '01', a.*, b.*
from (select count(*) as pv, count(distinct remote_addr) as uv, count(distinct remote_addr) as ips
      from dw_weblog_detail
      where datestr = '20181101') a
         join
     (select count(distinct session) as vvs from ods_click_stream_visit where datestr = "20181101") b;

--------------------------------------------------------------------------------------------
--多维度统计分析：注意gruop by语句的语法

--1.1． 多维度统计PV总量
--第一种方式：直接在dw_weblog_detail单表上进行查询
--1.1.1 计算该处理批次（一天）中的各小时pvs
drop table dw_pvs_everyhour_oneday;
create table dw_pvs_everyhour_oneday(month string,day string,hour string,pvs bigint) partitioned by(datestr string);

insert into table dw_pvs_everyhour_oneday partition(datestr='20181101')
select a.month as month,a.day as day,a.hour as hour,count(*) as pvs from dw_weblog_detail a
where  a.datestr='20181101' group by a.month,a.day,a.hour;


--计算每天的pvs
drop table dw_pvs_everyday;
create table dw_pvs_everyday(pvs bigint,month string,day string);

insert into table dw_pvs_everyday
select count(*) as pvs,a.month as month,a.day as day from dw_weblog_detail a
group by a.month,a.day;

--1.1.2 第二种方式：与时间维表关联查询

--维度：日
drop table dw_pvs_everyday;
create table dw_pvs_everyday(pvs bigint,month string,day string);

insert into table dw_pvs_everyday
select count(*) as pvs,a.month as month,a.day as day from (select distinct month, day from t_dim_time) a
join dw_weblog_detail b
on a.month=b.month and a.day=b.day
group by a.month,a.day;

--维度：月
drop table dw_pvs_everymonth;
create table dw_pvs_everymonth (pvs bigint,month string);

insert into table dw_pvs_everymonth
select count(*) as pvs,a.month from (select distinct month from t_dim_time) a
join dw_weblog_detail b on a.month=b.month group by a.month;


--另外，也可以直接利用之前的计算结果。比如从之前算好的小时结果中统计每一天的
Insert into table dw_pvs_everyday
Select sum(pvs) as pvs,month,day from dw_pvs_everyhour_oneday group by month,day having day='18';


--------------------------------------------------------------------------------------------
--1.2	按照来访维度统计pv

--统计每小时各来访url产生的pv量，查询结果存入：( "dw_pvs_referer_everyhour" )

drop table dw_pvs_referer_everyhour;
create table dw_pvs_referer_everyhour(referer_url string,referer_host string,month string,day string,hour string,pv_referer_cnt bigint) partitioned by(datestr string);

insert into table dw_pvs_referer_everyhour partition(datestr='20181101')
select http_referer,ref_host,month,day,hour,count(1) as pv_referer_cnt
from dw_weblog_detail
group by http_referer,ref_host,month,day,hour
having ref_host is not null
order by hour asc,day asc,month asc,pv_referer_cnt desc;



--统计每小时各来访host的产生的pv数并排序
drop table dw_pvs_refererhost_everyhour;
create table dw_pvs_refererhost_everyhour(ref_host string,month string,day string,hour string,ref_host_cnts bigint) partitioned by(datestr string);

insert into table dw_pvs_refererhost_everyhour partition(datestr='20181101')
select ref_host,month,day,hour,count(1) as ref_host_cnts
from dw_weblog_detail
group by ref_host,month,day,hour
having ref_host is not null
order by hour asc,day asc,month asc,ref_host_cnts desc;
---------------------------------------------------------------------------------------------

































