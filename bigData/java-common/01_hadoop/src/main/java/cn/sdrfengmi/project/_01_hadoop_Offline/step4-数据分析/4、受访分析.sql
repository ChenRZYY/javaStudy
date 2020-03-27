--各页面访问统计
--各页面PV

select request as request, count(request) as request_counts
from dw_weblog_detail
group by request
having request is not null
order by request_counts desc
limit 20;


-----------------------------------------------
--热门页面统计
--统计每日最热门的页面top10

drop table dw_hotpages_everyday;
create table dw_hotpages_everyday
(
    day string,
    url string,
    pvs string
);

insert into table dw_hotpages_everyday
select '20181101', a.request, a.request_counts
from (select request as request, count(request) as request_counts
      from dw_weblog_detail
      where datestr = '20181101'
      group by request
      having request is not null) a
order by a.request_counts desc
limit 10;

--改：
select day, request, count(request) as request_counts
from dw_weblog_detail
group by day, request
having request is not null
order by request_counts desc
limit 10