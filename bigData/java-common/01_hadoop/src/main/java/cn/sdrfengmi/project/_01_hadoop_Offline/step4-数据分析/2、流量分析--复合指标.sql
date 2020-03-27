---------------------------------------------------------------------------------------------
--复合指标统计分析

--人均浏览页数（平均访问深度）
--需求描述：统计今日所有来访者平均请求的页面数。
--总页面请求数pv/去重总人数uv

drop table dw_avgpv_user_everyday;
create table dw_avgpv_user_everyday
(
    day   string,
    avgpv string
);


--方式一：
insert into table dw_avgpv_user_everyday
select '20181101', pv / uv
from dw_webflow_basic_info;

--方式二：
insert into table dw_avgpv_user_everyday
select '20181101', sum(b.pvs) / count(b.remote_addr)
from (select remote_addr, count(*) as pvs from dw_weblog_detail where datestr = '20181101' group by remote_addr) b;

---------------------------------------------------------------------------
--平均访问频度
--平均每个独立访客一天内访问网站的次数（产生的session个数）。
--计算方式：访问次数vv/独立访客数uv
select '20181101', vv / uv
from dw_webflow_basic_info; --注意vv的计算采用的是点击流模型表数据 已经去除无效数据

select count(session) / count(distinct remote_addr)
from ods_click_stream_visit
where datestr = "20181101";
--符合逻辑

------------------------------------------------------------------------------------------------------
--平均访问时长

--平均每次访问（会话）在网站上的停留时间。
--体现网站对访客的吸引程度。
--平均访问时长=访问总时长/访问次数。

--先计算每次会话的停留时长

select session, sum(page_staylong) as web_staylong
from ods_click_pageviews
where datestr = "20181101"
group by session;


--计算平均访问时长
select sum(a.web_staylong) / count(a.session)
from (select session, sum(page_staylong) as web_staylong
      from ods_click_pageviews
      where datestr = "20181101"
      group by session) a;


-------------------------------------------------------------------------------------------------------
--跳出率
--跳出率是指用户到网站上仅浏览了一个页面就离开的访问次数与所有访问次数的百分比。
--是评价网站性能的重要指标。

-- /hadoop-mahout-roadmap/ 页面跳出率

--总的访问次数vv
select count(*)
from ods_click_stream_visit
where datestr = "20181101";

--或者通过基础指标信息表直接获取
select vv
from dw_webflow_basic_info
where datestr = "20181101";

-- 浏览/hadoop-mahout-roadmap/一个页面 并且离开的人数
select count(*)
from ods_click_stream_visit
where datestr = "20181101"
  and pagevisits = 1
  and outpage = "/hadoop-mahout-roadmap/";


--合并计算结果：
select (b.nums / a.vv) * 100
from dw_webflow_basic_info a
         join (select count(*) as nums
               from ods_click_stream_visit
               where datestr = "20181101"
                 and pagevisits = 1
                 and outpage = "/hadoop-mahout-roadmap/") b;