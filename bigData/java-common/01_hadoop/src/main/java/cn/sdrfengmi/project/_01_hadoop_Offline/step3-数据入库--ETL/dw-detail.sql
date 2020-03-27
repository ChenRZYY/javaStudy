-- 建表——明细宽表 dw_weblog_detail

drop table dw_weblog_detail;
create table dw_weblog_detail
(
    valid string,           -- 有效标识
    remote_addr string,     -- 来源IP
    remote_user string,     -- 用户标识
    time_local string,      -- 访问完整时间
    daystr string,          -- 访问日期
    timestr string,         -- 访问时间
    month string,           -- 访问月
    day string,             -- 访问日
    hour string,            -- 访问时
    request string,         -- 请求的url
    status string,          -- 响应码
    body_bytes_sent string, -- 传输字节数
    http_referer string,    -- 来源url
    ref_host string,        -- 来源的host
    ref_path string,        -- 来源的路径
    ref_query string,       -- 来源参数query
    ref_query_id string,    -- 来源参数query的值
    http_user_agent string --客户终端标识
) partitioned by(datestr string);
-- -----------------------------------------------------------------------------
-- 通过查询插入数据到明细宽表  dw_weblog_detail中


-- 分步：
-- 抽取refer_url到中间表  t_ods_tmp_referurl
-- 也就是将来访url分离出host  path  query  query id

drop table if exists t_ods_tmp_referurl;
create table t_ods_tmp_referurl as
SELECT a.*, b.*
FROM ods_weblog_origin a LATERAL VIEW parse_url_tuple(regexp_replace(http_referer, "\"", ""), 'HOST', 'PATH','QUERY', 'QUERY:id') b as host, path, query, query_id;

-- 抽取转换time_local字段到中间表明细表 t_ods_tmp_detail

drop table if exists t_ods_tmp_detail;
create table t_ods_tmp_detail as
select b.*,
       substring(time_local, 0, 10) as daystr,
       substring(time_local, 12)    as tmstr,
       substring(time_local, 6, 2)  as month,
       substring(time_local, 9, 2)  as day,
       substring(time_local, 12, 2) as hour
From t_ods_tmp_referurl b;

-- 以上语句可以改写成：
insert into table dw_weblog_detail partition (datestr='20181101')
select c.valid,
       c.remote_addr,
       c.remote_user,
       c.time_local,
       substring(c.time_local, 0, 10) as daystr,
       substring(c.time_local, 12)    as tmstr,
       substring(c.time_local, 6, 2)  as month,
       substring(c.time_local, 9, 2)  as day,
       substring(c.time_local, 12, 2) as hour,
       c.request,
       c.status,
       c.body_bytes_sent,
       c.http_referer,
       c.ref_host,
       c.ref_path,
       c.ref_query,
       c.ref_query_id,
       c.http_user_agent
from (SELECT a.valid,
             a.remote_addr,
             a.remote_user,
             a.time_local,
             a.request,
             a.status,
             a.body_bytes_sent,
             a.http_referer,
             a.http_user_agent,
             b.ref_host,
             b.ref_path,
             b.ref_query,
             b.ref_query_id
      FROM ods_weblog_origin a LATERAL VIEW parse_url_tuple(regexp_replace(http_referer,
           "\"",
           ""),
           'HOST',
           'PATH',
           'QUERY',
           'QUERY:id') b as ref_host, ref_path, ref_query, ref_query_id) c;


show partitions dw_weblog_detail;




