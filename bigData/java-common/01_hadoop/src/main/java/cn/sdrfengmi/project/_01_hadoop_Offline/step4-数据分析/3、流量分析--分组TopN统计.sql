--流量分析

---------------------------------------------------------------------------
--统计pv总量最大的来源TOPN
--需求：按照时间维度，统计一天内各小时产生最多pvs的来源topN


--row_number函数
select ref_host,
       ref_host_cnts,
       concat(month, day, hour),
       row_number() over (partition by concat(month, day, hour) order by ref_host_cnts desc) as od
from dw_pvs_refererhost_everyhour;


--综上可以得出
drop table dw_pvs_refhost_topn_everyhour;
create table dw_pvs_refhost_topn_everyhour
(
    hour          string,
    toporder      string,
    ref_host      string,
    ref_host_cnts string
) partitioned by (datestr string);

insert into table dw_pvs_refhost_topn_everyhour partition (datestr = '20181101')
select t.hour, t.od, t.ref_host, t.ref_host_cnts
from (select ref_host,
             ref_host_cnts,
             concat(month, day, hour)                                                              as hour,
             row_number() over (partition by concat(month, day, hour) order by ref_host_cnts desc) as od
      from dw_pvs_refererhost_everyhour) t
where od <= 3;
































