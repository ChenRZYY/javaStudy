-- select * FROM tab;

SHOW TABLES;

CREATE TABLE mytest 


CREATE TABLE studytest.mytest (
	id INT AUTO_INCREMENT NOT NULL,
	NAME VARCHAR(20) NULL COMMENT '名字',
	age VARCHAR(2) NULL,
	PRIMARY KEY (id)
)
ENGINE=INNODB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_general_ci;
CREATE INDEX mytest_id_IDX USING BTREE ON studytest.mytest (id);


-- 建表
-- 学生表
CREATE TABLE `Student`(
    `s_id` VARCHAR(20),
    `s_name` VARCHAR(20) NOT NULL DEFAULT '',
    `s_birth` VARCHAR(20) NOT NULL DEFAULT '',
    `s_sex` VARCHAR(10) NOT NULL DEFAULT '',
    PRIMARY KEY(`s_id`)
);
-- 课程表
CREATE TABLE `Course`(
    `c_id`  VARCHAR(20),
    `c_name` VARCHAR(20) NOT NULL DEFAULT '',
    `t_id` VARCHAR(20) NOT NULL,
    PRIMARY KEY(`c_id`)
);
-- 教师表
CREATE TABLE `Teacher`(
    `t_id` VARCHAR(20),
    `t_name` VARCHAR(20) NOT NULL DEFAULT '',
    PRIMARY KEY(`t_id`)
);
-- 成绩表
CREATE TABLE `Score`(
    `s_id` VARCHAR(20),
    `c_id`  VARCHAR(20),
    `s_score` INT(3),
    PRIMARY KEY(`s_id`,`c_id`)
);
-- 插入学生表测试数据
INSERT INTO Student VALUES('01' , '赵雷' , '1990-01-01' , '男');
INSERT INTO Student VALUES('02' , '钱电' , '1990-12-21' , '男');
INSERT INTO Student VALUES('03' , '孙风' , '1990-05-20' , '男');
INSERT INTO Student VALUES('04' , '李云' , '1990-08-06' , '男');
INSERT INTO Student VALUES('05' , '周梅' , '1991-12-01' , '女');
INSERT INTO Student VALUES('06' , '吴兰' , '1992-03-01' , '女');
INSERT INTO Student VALUES('07' , '郑竹' , '1989-07-01' , '女');
INSERT INTO Student VALUES('08' , '王菊' , '1990-01-20' , '女');
-- 课程表测试数据
INSERT INTO Course VALUES('01' , '语文' , '02');
INSERT INTO Course VALUES('02' , '数学' , '01');
INSERT INTO Course VALUES('03' , '英语' , '03');

-- 教师表测试数据
INSERT INTO Teacher VALUES('01' , '张三');
INSERT INTO Teacher VALUES('02' , '李四');
INSERT INTO Teacher VALUES('03' , '王五');

-- 成绩表测试数据
INSERT INTO Score VALUES('01' , '01' , 80);
INSERT INTO Score VALUES('01' , '02' , 90);
INSERT INTO Score VALUES('01' , '03' , 99);
INSERT INTO Score VALUES('02' , '01' , 70);
INSERT INTO Score VALUES('02' , '02' , 60);
INSERT INTO Score VALUES('02' , '03' , 80);
INSERT INTO Score VALUES('03' , '01' , 80);
INSERT INTO Score VALUES('03' , '02' , 80);
INSERT INTO Score VALUES('03' , '03' , 80);
INSERT INTO Score VALUES('04' , '01' , 50);
INSERT INTO Score VALUES('04' , '02' , 30);
INSERT INTO Score VALUES('04' , '03' , 20);
INSERT INTO Score VALUES('05' , '01' , 76);
INSERT INTO Score VALUES('05' , '02' , 87);
INSERT INTO Score VALUES('06' , '01' , 31);
INSERT INTO Score VALUES('06' , '03' , 34);
INSERT INTO Score VALUES('07' , '02' , 89);
INSERT INTO Score VALUES('07' , '03' , 98);

SHOW TABLES;
SELECT * FROM Teacher ;
SELECT * FROM Course ; -- 课程
SELECT * FROM Score ; -- 中间关系表 分数
SELECT * FROM Student;

-- 等量关系是什么:  维度是什么

-- 1 
SELECT * FROM Score s ,Student st WHERE st.s_id= s.s_id ;

-- 如何使用笛卡尔积
-- 1、查询"01"课程比"02"课程成绩高的学生的信息及课程分数   
SELECT * FROM Score s1,Score s2,Student st WHERE s1.s_id=st.s_id AND s2.s_id=st.s_id AND s1.c_id=01 AND s2.c_id=02 AND s1.s_score > s2.s_score;

-- 2、查询"01"课程比"02"课程成绩低的学生的信息及课程分数
SELECT s.s_id,st.s_name,ROUND(AVG(s.s_score),2) avg1 FROM Score s ,Student st WHERE st.s_id= s.s_id GROUP BY s.s_id HAVING avg1 >60;

-- 3、查询平均成绩大于等于60分的同学的学生编号和学生姓名和平均成绩
select s2.s_id ,round(avg(s_score )),s3.* from Score s2 ,Student s3 where s2.s_id =s3.s_id group by s2.s_id HAVING avg(s_score )>60;

-- 4、查询平均成绩小于60分的同学的学生编号和学生姓名和平均成绩        -- (包括有成绩的和无成绩的)
SELECT s.s_id,st.s_name,ROUND(AVG(s.s_score),2) avg1 FROM Score s ,Student st WHERE st.s_id= s.s_id GROUP BY s.s_id HAVING avg1 <60
UNION SELECT st.s_id,st.s_name,0 FROM Student st  WHERE st.s_id NOT IN(SELECT s.s_id FROM Score s);
-- 不用in 做判断
select * from Student st where not exists(select 1 from Score s where st.s_id = s.s_id) ;

-- 5、查询所有同学的学生编号、学生姓名、选课总数、所有课程的总成绩
SELECT st.s_id,st.s_name,st.s_birth,st.s_sex,s.s_id,COUNT(s.s_id) , COUNT(s.c_id),SUM(s.s_score) FROM Student st , Score s WHERE st.s_id=s.s_id GROUP BY st.s_id;

-- 6、查询"李"姓老师的数量 
SELECT  COUNT(1) FROM Teacher t WHERE t.t_name LIKE "李%";

-- 7、查询学过"张三"老师授课的同学的信息 
SELECT * FROM Teacher t,  Course c,Score s,Student st WHERE t.t_id=c.t_id AND c.c_id=s.c_id AND s.s_id=st.s_id  AND t.t_name="张三";

-- 8、查询没学过"张三"老师授课的同学的信息
SELECT * FROM Student st
WHERE  NOT exists (
	SELECT s.`s_id` 
	FROM Teacher t ,Course c, Score s 
	WHERE t.t_id=c.`t_id` AND s.`c_id`=c.`c_id` AND t.`t_name`='张三' and st.`s_id` = s.`s_id` );


SELECT DISTINCT(a.s_id) ,a.*
FROM Student a 
LEFT JOIN Score b ON a.s_id = b.s_id 
WHERE a.s_id NOT IN 
	(SELECT s_id FROM Score WHERE c_id = 
		(SELECT c_id FROM Course WHERE t_id =
			 (SELECT t_id FROM Teacher WHERE t_name = '张三'))) ;


-- 9、查询学过编号为"01"并且也学过编号为"02"的课程的同学的信息
SELECT distinct(st.s_name ),st.* FROM Teacher t,  Course c,Score s,Student st WHERE t.t_id=c.t_id AND c.c_id=s.c_id AND s.s_id=st.s_id  AND c.`c_id` IN (01,02);

SELECT * 
FROM student 
WHERE s_id IN (SELECT a.`s_id`
		FROM Score a 
		JOIN Score b ON a.s_id = b.s_id
		WHERE a.c_id = '01' AND b.c_id = '02');

-- 10、查询学过编号为"01"但是没有学过编号为"02"的课程的同学的信息
SELECT * FROM Student st ,(SELECT s.`s_id` FROM Score s WHERE s.`c_id`='01') s1
WHERE st.`s_id` = s1.s_id AND st.`s_id` NOT IN (SELECT s.`s_id` FROM Score s WHERE s.`c_id`='02');

-- 11、查询没有学全所有课程的同学的信息 
SELECT * 
FROM Student st 
WHERE st.`s_id` NOT IN (SELECT s.`s_id` FROM Score s GROUP BY s.`s_id` HAVING COUNT(s.`c_id`) = 3);

-- 12、查询至少有一门课与学号为"01"的同学所学相同的同学的信息 
SELECT * FROM Student st1,Score s2 WHERE s2.`s_id`=st1.`s_id` AND s2.`c_id` IN (SELECT s.`c_id` FROM Score s WHERE s.`s_id`='01') AND st1.`s_id`!='01';
-- exists方法
SELECT * FROM Student st1,Score s2 WHERE EXISTS (SELECT 1 FROM Score s WHERE s.`s_id`='01' AND s2.`c_id`=s.`c_id`) AND s2.`s_id`=st1.`s_id` AND st1.`s_id`!='01';


-- 13、查询和"01"号的同学学习的课程完全相同的其他同学的信息 
SELECT s1.`s_id`,s1.`c_id` FROM Score s1 GROUP BY s1.`s_id`,s1.`c_id` HAVING COUNT(s1.`s_id`,s1.`c_id`)==3
SELECT   COUNT(c_id) FROM  Score WHERE s_id = '01' ;

SELECT * 
FROM Student 
WHERE s_id IN (SELECT s_id 
		FROM Score 
		GROUP BY s_id 
		HAVING COUNT(c_id) = (SELECT COUNT(c_id) 
					FROM Score 
					WHERE s_id = '01') 
		AND s_id NOT IN (SELECT s_id 
				FROM Score 
				WHERE c_id NOT IN(SELECT c_id 
						  FROM Score 
						  WHERE s_id = '01')) 
		AND s_id != '01');


select s.s_id from Score s 
where  s.s_id !=01 
and s.s_id  not IN (SELECT s_id FROM Score WHERE c_id NOT IN(SELECT c_id FROM Score WHERE s_id = '01'))
group by s.s_id HAVING COUNT(s.s_id ) = (select COUNT(1) from Score s where s.s_id =01 group by s.s_id);

-- 14、查询没学过"张三"老师讲授的任一门课程的学生姓名
SELECT * FROM Student s2 where s2.s_id not in (select s_id FROM Score s3 where s3.c_id =02);

-- 15、查询两门及其以上不及格课程的同学的学号，姓名及其平均成绩 
select s.s_id,AVG(s.s_score ),s2.s_name from Score s,Student s2 where s2.s_id =s.s_id  and s.s_score <60 group by s.s_id HAVING count(s.s_id)>=2;

-- 16、检索"01"课程分数小于60，按分数降序排列的学生信息
-- 等式 <60 01课程 
SELECT * FROM Score s,Student s2 where s.s_id=s2.s_id and s.c_id =01 and s.s_score <60 order by s.s_score DESC ;

-- 17、按平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩
-- 等式条件  平均成绩
-- 展示字段
select s2.s_id,s2.s_name ,SUM(CASE when s.c_id ='01' then s.s_score else null END) 语文,SUM(CASE when s.c_id ='02' then s.s_score else null END)  数学,SUM(CASE when s.c_id ='03' then s.s_score else null END)  英语 ,avg(s.s_score) 平均成绩 
from Score s  right join Student s2 on s.s_id=s2.s_id   group by s2.s_id,s2.s_name order by avg(s.s_score );

-- 18.查询各科成绩最高分、最低分和平均分：以如下形式显示：课程ID，课程name，最高分，最低分，平均分，及格率，中等率，优良率，优秀率
-- 维度 分组  以主键分组,后面所有的都能带
select s.c_id ,c.c_name ,MAX(s.s_score),min(s.s_score),avg(s.s_score),
ROUND(sum(CASE WHEN s.s_score >=60 THEN 1 else 0 end )/count(1),2)  及格率,
ROUND(SUM(CASE WHEN s.s_score>= 70 AND s.s_score <80 THEN 1 ELSE 0 END)/COUNT(c.c_id),2) AS 中等率,
ROUND(SUM(CASE WHEN s.s_score>= 80 AND s.s_score <90 THEN 1 ELSE 0 END)/COUNT(c.c_id),2) AS 优良率,
ROUND(SUM(CASE WHEN s.s_score>= 90 THEN 1 ELSE 0 END)/COUNT(c.c_id),2) AS 优秀率,
count(1) 
from Score s ,Course c where s.c_id =c.c_id group by c.c_id;

-- 19、按各科成绩进行排序，并显示排名
select * ,row_number() over (partition by c_id order by s_score desc ) as row_num from Score;
select * ,rank () over (partition by c_id order by s_score ) as row_num from Score s;
select * ,dense_rank () over (partition by c_id order by s_score) as row_num from score s;

-- 20、查询学生的总成绩并进行排名
select s.s_id ,sum(s.s_score ) su from score s group by s.s_id order by su;
select *,rank () over ( order by c.su ) as row_num from (select s.s_id ,sum(s.s_score ) su from score s group by s.s_id order by su) c;

-- 21、查询不同老师所教不同课程平均分从高到低显示 
select s.s_id ,sum(s.s_score ) su from score s group by s.s_id order by su;
select *,rank () over ( order by sc.su ) as row_num from (select s.c_id ,avg(s.s_score ) su from score s group by s.c_id order by su) sc ,Course c where sc.c_id=c.c_id ;
select * from teacher;
select * from Teacher t2 ;
SELECT * FROM Course c2 ;
SELECT * FROM Score s2 ;
select * from Student s1  ;

-- 22、查询所有课程的成绩第2名到第3名的学生信息及该课程成绩
select * from (select *, row_number() over (partition by s.c_id order by s.s_score ) as row_num from score s) f  where f.row_num=2 or f.row_num=3;

-- sum, avg, count, max, min
select *,rank() OVER() ,PERCENT_RANK() OVER(),from score s ;
SELECT *,sum(s_score ) over(partition by s_id order by s_score ) FROM Score s2 ;
SELECT *,avg(s_score ) over(partition by s_id order by s_score ) FROM Score s2 ;
SELECT *,count(s_score ) over(partition by s_id order by s_score ) FROM Score s2 ;
SELECT *,max(s_score ) over(partition by s_id order by s_score ) FROM Score s2 ;
SELECT *,min(s_score ) over(partition by s_id order by s_score ) FROM Score s2 ;

-- 23、统计各科成绩各分数段人数：课程编号,课程名称,[100-85],(85-70],(70-60],(0-60]及所占百分比

select s.s_id from score s ;




-- 46、查询各学生的年龄
SELECT s_name ,
	(DATE_FORMAT(NOW(),'%Y')-DATE_FORMAT(s_birth,'%Y') + 
		(CASE WHEN DATE_FORMAT(NOW(),'%m%d')>=DATE_FORMAT(s_birth,'%m%d') THEN 0 ELSE 1 END)) AS age
FROM student;

SELECT *  from student s2 ;

SELECT s_name ,DATE_FORMAT(s_birth,'%Y%m%d'),	((DATE_FORMAT(NOW(),'%Y')-DATE_FORMAT(s_birth,'%Y')+1))  AS age from student  ;
-- 47、查询本周过生日的学生
SELECT NOW() FROM DUAL;
SELECT LOCALTIME();-- 2017-05-15 10:20:00
SELECT SYSDATE();-- 当前日期时间：2017-05-12 11:42:03
SELECT WEEK('2017-01-01 10:37:14.123456'); -- 20 (获取周)
SELECT WEEK('2017-05-15 10:37:14.123456', 7);-- ****** 测试此函数在MySQL5.6下无效
SELECT DATE_FORMAT('2017-05-12 17:03:51', '%Y年%m月%d日 %H时%i分%s秒');-- 2017年05月12日 17时03分51秒(具体需要什么格式的数据根据实际情况来;小写h为12小时制;)
SELECT TIME_FORMAT('2017-05-12 17:03:51', '%Y年%m月%d日 %H时%i分%s秒');-- 0000年00月00日 17时03分51秒(time_format()只能用于时间的格式化)
SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'); -- 时间转字符串
SELECT STR_TO_DATE('2019-01-20 16:01:45', '%Y-%m-%d %H:%i:%s');
select DATE_FORMAT(NOW(),'%Y');













SET @pre_c_id:= '01';
SET @rank:=0;
SELECT tb2.s_id,tb2.c_id,tb2.s_score,tb2.排名 
FROM(SELECT *,
	(CASE WHEN tb1.c_id = @pre_c_id THEN @rank:=@rank+1 ELSE @rank:=1 END) AS 排名,
	(CASE WHEN @pre_c_id = tb1.c_id THEN @pre_c_id ELSE @pre_c_id:=tb1.c_id END ) AS pre_c_id 
     FROM(SELECT * 
	  FROM Score 
	  ORDER BY c_id,s_score DESC) tb1 )tb2;



select COUNT(1) from Score s where s.s_id =01 group by s.s_id ;	




SHOW VARIABLES LIKE '%slow_query_log%';
SET GLOBAL slow_query_log=1;

SELECT * FROM score GROUP BY 


CREATE TABLE `tem` (
`id` INT(11) NOT NULL AUTO_INCREMENT,
`str` CHAR(1) DEFAULT NULL,
PRIMARY KEY (`id`)
) ;

INSERT INTO `tem`(`id`, `str`) VALUES (1, 'A');
INSERT INTO `tem`(`id`, `str`) VALUES (2, 'B');
INSERT INTO `tem`(`id`, `str`) VALUES (3, 'A');
INSERT INTO `tem`(`id`, `str`) VALUES (4, 'C');
INSERT INTO `tem`(`id`, `str`) VALUES (5, 'A');
INSERT INTO `tem`(`id`, `str`) VALUES (6, 'C');
INSERT INTO `tem`(`id`, `str`) VALUES (7, 'B');

SELECT * FROM tem;

SELECT
    @num := @num+1 num,
    id,
    str
FROM
    tem, (SELECT @str := '', @num := 0) t1
ORDER BY
    str, id;
 
 SELECT @num := @num+1 num;
    
    
    
SELECT VERSION();

SELECT
         val,
         ROW_NUMBER()   OVER w AS 'row_number',
         CUME_DIST()    OVER w AS 'cume_dist',
         PERCENT_RANK() OVER w AS 'percent_rank'
       FROM numbers
       WINDOW w AS (ORDER BY val);



