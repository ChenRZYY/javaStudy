-- 独立访客
--需求：按照时间维度来统计独立访客及其产生的pv量

--时间维度：时
drop table dw_user_dstc_ip_h;
create table dw_user_dstc_ip_h
(
    remote_addr string,
    pvs         bigint,
    hour        string
);

insert into table dw_user_dstc_ip_h
select remote_addr, count(1) as pvs, concat(month, day, hour) as hour
from dw_weblog_detail
Where datestr = '20181101'
group by concat(month, day, hour), remote_addr
order by hour asc, pvs desc;

--在上述基础之上，可以继续分析，比如每小时独立访客总数
select count(1) as dstc_ip_cnts, hour
from dw_user_dstc_ip_h
group by hour;


--时间维度：日
select remote_addr, count(1) as counts, concat(month, day) as day
from dw_weblog_detail
Where datestr = '20181101'
group by concat(month, day), remote_addr;


--时间维度： 月
select remote_addr, count(1) as counts, month
from dw_weblog_detail
group by month, remote_addr;


----------------------------------------------------------------------------------------
-- 每日新访客
-- 需求：将每天的新访客统计出来。

--历日去重访客累积表
drop table dw_user_dsct_history;
create table dw_user_dsct_history
(
    day string,
    ip  string
)
    partitioned by (datestr string);

--每日新访客表
drop table dw_user_new_d;
create table dw_user_new_d
(
    day string,
    ip  string
)
    partitioned by (datestr string);


--每日新用户插入新访客表
insert into table dw_user_new_d partition (datestr = '20181101')
select tmp.day as day, tmp.today_addr as new_ip
from (
         select today.day as day, today.remote_addr as today_addr, old.ip as old_addr
         from (select distinct remote_addr as remote_addr, "20181101" as day
               from dw_weblog_detail
               where datestr = "20181101") today
                  left outer join
              dw_user_dsct_history old
              on today.remote_addr = old.ip
     ) tmp
where tmp.old_addr is null;

--每日新用户追加到累计表
insert into table dw_user_dsct_history partition (datestr = '20181101')
select day, ip
from dw_user_new_d
where datestr = '20181101';


--验证：
select count(distinct remote_addr)
from dw_weblog_detail;

select count(1)
from dw_user_dsct_history
where datestr = '20181101';

select count(1)
from dw_user_new_d
where datestr = '20181101';










