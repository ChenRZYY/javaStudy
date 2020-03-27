--  回头/单次访客统计
drop table dw_user_returning;
create table dw_user_returning
(
    day         string,
    remote_addr string,
    acc_cnt     string
)
    partitioned by (datestr string);

insert overwrite table dw_user_returning partition (datestr = '20181101')
select tmp.day, tmp.remote_addr, tmp.acc_cnt
from (select '20181101' as day, remote_addr, count(session) as acc_cnt
      from ods_click_stream_visit
      group by remote_addr) tmp
where tmp.acc_cnt > 1;

------------------------------------------------------------------------------------
