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
SELECT * FROM Teacher;
SELECT * FROM Course ;
SELECT * FROM Score;-- 中间表
SELECT * FROM Student;


-- 1 
SELECT * FROM Score s ,Student st WHERE st.s_id= s.s_id ;

-- where条件 只能和外部 和同一行比较
-- 1、查询"01"课程比"02"课程成绩高的学生的信息及课程分数  
SELECT * FROM Score s1,Score s2,Student st WHERE s1.s_id=st.s_id AND s1.c_id=01 AND s2.s_id=st.s_id AND s2.c_id=02 AND s1.s_score > s2.s_score;

-- 3、查询平均成绩大于等于60分的同学的学生编号和学生姓名和平均成绩
SELECT s.s_id,st.s_name,ROUND(AVG(s.s_score),2) avg1 FROM Score s ,Student st WHERE st.s_id= s.s_id GROUP BY s.s_id HAVING avg1 >60;

-- 4 查询平均成绩小于60分的同学的学生编号和学生姓名和平均成绩 (包括有成绩的和无成绩的)
SELECT s.s_id,st.s_name,ROUND(AVG(s.s_score),2) avg1 FROM Score s ,Student st WHERE st.s_id= s.s_id GROUP BY s.s_id HAVING avg1 <60
UNION SELECT st.s_id,st.s_name,0 FROM Student st  WHERE st.s_id NOT IN(SELECT s.s_id FROM Score s);

SELECT * FROM Student st , Score s WHERE st.s_id=s.s_id ;

-- 5 group by 如果是主键分组,真个表都能显示,因为每行数据都有,后面是三行复制的数据,三行数据相同就能写到select 后面
SELECT st.s_id,st.s_name,st.s_birth,st.s_sex,s.s_id,COUNT(s.s_id) , COUNT(s.c_id),SUM(s.s_score) FROM Student st , Score s WHERE st.s_id=s.s_id GROUP BY st.s_id;

-- 6、查询"李"姓老师的数量 
SELECT COUNT(1) FROM Teacher t WHERE t.t_name LIKE "李%";

-- 7、查询学过"张三"老师授课的同学的信息
SELECT * FROM Teacher t,  Course c,Score s,Student st WHERE t.t_id=c.t_id AND c.c_id=s.c_id AND s.s_id=st.s_id  AND t.t_name="张三";

-- 8、查询没学过"张三"老师授课的同学的信息
SELECT * FROM Student st
WHERE st.`s_id` NOT IN (
	SELECT s.`s_id` 
	FROM Teacher t ,Course c, Score s 
	WHERE t.t_id=c.`t_id` AND s.`c_id`=c.`c_id` AND t.`t_name`='张三'
)


SELECT DISTINCT(a.s_id) ,a.*
FROM Student a 
LEFT JOIN Score b ON a.s_id = b.s_id 
WHERE a.s_id NOT IN 
	(SELECT s_id FROM Score WHERE c_id = 
		(SELECT c_id FROM Course WHERE t_id =
			 (SELECT t_id FROM Teacher WHERE t_name = '张三'))) ;


-- 9、查询学过编号为"01"并且也学过编号为"02"的课程的同学的信息
SELECT st.* FROM Teacher t,  Course c,Score s,Student st WHERE t.t_id=c.t_id AND c.c_id=s.c_id AND s.s_id=st.s_id  AND c.`c_id` IN (01,02);

SELECT * 
FROM student 
WHERE s_id IN (SELECT a.`s_id`
		FROM Score a 
		JOIN Score b ON a.s_id = b.s_id
		WHERE a.c_id = '01' AND b.c_id = '02');

-- 10、查询学过编号为"01"但是没有学过编号为"02"的课程的同学的信息  in 都是= 放到from后和where后一样
SELECT s.`s_id` FROM Score s WHERE s.`c_id`='01';

SELECT * 
FROM Student st ,(SELECT s.`s_id` FROM Score s WHERE s.`c_id`='01') s1
WHERE st.`s_id` = s1.s_id AND st.`s_id` NOT IN (SELECT s.`s_id` FROM Score s WHERE s.`c_id`='02');

-- 11、查询没有学全所有课程的同学的信息 

SELECT * 
FROM Student st 
WHERE st.`s_id` NOT IN (SELECT s.`s_id` FROM Score s GROUP BY s.`s_id` HAVING COUNT(s.`c_id`) = 3);

-- 12、查询至少有一门课与学号为"01"的同学所学相同的同学的信息 
SELECT * FROM Student st1,Score s2 WHERE s2.`s_id`=st1.`s_id` AND s2.`c_id` IN (SELECT s.`c_id` FROM Score s WHERE s.`s_id`='01') AND st1.`s_id`!='01';

-- 13、查询和"01"号的同学学习的课程完全相同的其他同学的信息 

SELECT s1.`s_id`,s1.`c_id` FROM Score s1 GROUP BY s1.`s_id`,s1.`c_id` HAVING COUNT(s1.`s_id`,s1.`c_id`)==3
SELECT   COUNT(c_id) FROM  Score WHERE s_id = '01' ;



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



