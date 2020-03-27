load data local inpath '/root/hivedata/click-part-r-00000' overwrite into table ods_click_pageviews partition (datestr = '20181103');

----------------------------------------------------------
---1、查询每一个步骤的总访问人数
-- UNION All将多个SELECT语句的结果集合并为一个独立的结果集

create table dw_oute_numbs as
select 'step1' as step, count(distinct remote_addr) as numbs
from ods_click_pageviews
where datestr = '20181103'
  and request like '/item%'
union all
select 'step2' as step, count(distinct remote_addr) as numbs
from ods_click_pageviews
where datestr = '20181103'
  and request like '/category%'
union all
select 'step3' as step, count(distinct remote_addr) as numbs
from ods_click_pageviews
where datestr = '20181103'
  and request like '/order%'
union all
select 'step4' as step, count(distinct remote_addr) as numbs
from ods_click_pageviews
where datestr = '20181103'
  and request like '/index%';


-- +---------------------+----------------------+--+
-- | dw_oute_numbs.step  | dw_oute_numbs.numbs  |
-- +---------------------+----------------------+--+
-- | step1               | 1029                 |
-- | step2               | 1029                 |
-- | step3               | 1028                 |
-- | step4               | 1018                 |
-- +---------------------+----------------------+--+
----------------------------------------------------------------------------
--2、查询每一步骤相对于路径起点人数的比例
--级联查询，自己跟自己join

select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
from dw_oute_numbs rn
         inner join
     dw_oute_numbs rr;

--自join后结果如下图所示：
-- +---------+----------+---------+----------+--+
-- | rnstep  | rnnumbs  | rrstep  | rrnumbs  |
-- +---------+----------+---------+----------+--+
-- | step1   | 1029     | step1   | 1029     |
-- | step2   | 1029     | step1   | 1029     |
-- | step3   | 1028     | step1   | 1029     |
-- | step4   | 1018     | step1   | 1029     |
-- | step1   | 1029     | step2   | 1029     |
-- | step2   | 1029     | step2   | 1029     |
-- | step3   | 1028     | step2   | 1029     |
-- | step4   | 1018     | step2   | 1029     |
-- | step1   | 1029     | step3   | 1028     |
-- | step2   | 1029     | step3   | 1028     |
-- | step3   | 1028     | step3   | 1028     |
-- | step4   | 1018     | step3   | 1028     |
-- | step1   | 1029     | step4   | 1018     |
-- | step2   | 1029     | step4   | 1018     |
-- | step3   | 1028     | step4   | 1018     |
-- | step4   | 1018     | step4   | 1018     |
-- +---------+----------+---------+----------+--+


--每一步的人数/第一步的人数==每一步相对起点人数比例
select tmp.rnstep, tmp.rnnumbs / tmp.rrnumbs as ratio
from (
         select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
         from dw_oute_numbs rn
                  inner join
              dw_oute_numbs rr) tmp
where tmp.rrstep = 'step1';


-- tmp
-- +---------+----------+---------+----------+--+
-- | rnstep  | rnnumbs  | rrstep  | rrnumbs  |
-- +---------+----------+---------+----------+--+
-- | step1   | 1029     | step1   | 1029     |
-- | step2   | 1029     | step1   | 1029     |
-- | step3   | 1028     | step1   | 1029     |
-- | step4   | 1018     | step1   | 1029     |

--------------------------------------------------------------------------------
--3、查询每一步骤相对于上一步骤的漏出率

--首先通过自join表过滤出每一步跟上一步的记录

select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
from dw_oute_numbs rn
         inner join
     dw_oute_numbs rr
where cast(substr(rn.step, 5, 1) as int) = cast(substr(rr.step, 5, 1) as int) - 1;


--注意：cast为Hive内置函数 类型转换
select cast(1 as float); --1.0  
select cast('2016-05-22' as date);
--2016-05-22

-- | step1   | 1029     | step2   | 1029     |
-- | step2   | 1029     | step3   | 1028     |
-- | step3   | 1028     | step4   | 1018     |


-- +---------+----------+---------+----------+--+
-- | rnstep  | rnnumbs  | rrstep  | rrnumbs  |
-- +---------+----------+---------+----------+--+
-- | step1   | 1029     | step2   | 1029     |
-- | step2   | 1029     | step3   | 1028     |
-- | step3   | 1028     | step4   | 1018     |
-- +---------+----------+---------+----------+--+

--然后就可以非常简单的计算出每一步相对上一步的漏出率
select tmp.rrstep as step, tmp.rrnumbs / tmp.rnnumbs as leakage_rate
from (
         select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
         from dw_oute_numbs rn
                  inner join
              dw_oute_numbs rr) tmp
where cast(substr(tmp.rnstep, 5, 1) as int) = cast(substr(tmp.rrstep, 5, 1) as int) - 1;

-----------------------------------------------------------------------------------
--4、汇总以上两种指标
select abs.step, abs.numbs, abs.rate as abs_ratio, rel.rate as leakage_rate
from (
         select tmp.rnstep as step, tmp.rnnumbs as numbs, tmp.rnnumbs / tmp.rrnumbs as rate
         from (
                  select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
                  from dw_oute_numbs rn
                           inner join
                       dw_oute_numbs rr) tmp
         where tmp.rrstep = 'step1'
     ) abs
         left outer join
     (
         select tmp.rrstep as step, tmp.rrnumbs / tmp.rnnumbs as rate
         from (
                  select rn.step as rnstep, rn.numbs as rnnumbs, rr.step as rrstep, rr.numbs as rrnumbs
                  from dw_oute_numbs rn
                           inner join
                       dw_oute_numbs rr) tmp
         where cast(substr(tmp.rnstep, 5, 1) as int) = cast(substr(tmp.rrstep, 5, 1) as int) - 1
     ) rel
     on abs.step = rel.step;