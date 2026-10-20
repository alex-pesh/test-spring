
select ref(jp."name") from job_position jp ;

update department set name = 'Подадминистрация первая__' 
where name = 'Подадминистрация первая';


SELECT * 
FROM department d cross JOIN job_position jp
offset 3 limit 5;


explain (ANALYZE, buffers)
SELECT max(jp.salary) max_sal , min(jp.salary) min_sal, avg(jp.salary) avg_sal
FROM job_position jp 
GROUP BY department_id
ORDER BY max_sal, min_sal ; 


create table t(a integer, b text, c boolean);

insert into t(a,b,c)
  select s.id, chr((32+random()*94)::integer), random() < 0.01
  from generate_series(1,100000) as s(id)
  order by random();


--drop index t_a_idx;
--drop index t_a_idx_hash;
create index t_a_idx on t (a) ;
vcreate index t_a_idx_hash on t using hash(a) ;

analyze t;

explain analyze
select count (*) from t
where a <= 100;

commit

rollback




--drop table t2;
create table t2(
    id serial primary key,
    a integer,
    "name" text,
    is_active boolean
);


insert into t2(a, name)
(
  select
    s.id,
   ( SELECT encode(gen_random_bytes(20 + (s.id % 10)),'base64') )as data
  from
    generate_series(1, 1000000) as s(id)
  order by random()
);


--select uuid_generate_v7();
--select gen_random_uuid();



create index t2_a_idx on t2(a);

-- drop index idx_t2_name
-- drop index idx_t2_name__pattern
-- drop index idx_t2_name__text
-- drop index idx_t2_name__hash
CREATE INDEX idx_t2_name ON t2 (name);
CREATE INDEX idx_t2_name__pattern ON t2(name text_pattern_ops);
CREATE INDEX idx_t2_name__text ON t2(name text_ops);
CREATE INDEX idx_t2_name__hash ON t2 USING hash(name);

alter TABLE t2 drop column uid;
alter TABLE t2 add column uid UUID default uuid_generate_v7();


drop index idx_t2_uid;
drop index idx_t2_uid__hash;

create index idx_t2_uid on t2 using btree(uid);
create index idx_t2_uid__hash on t2 using hash(uid);

vacuum analyze;



explain analyze
select * from t2
--order by uid;
--where id = 900000
where uid = '0195c9c7-cb2a-7d41-9f86-c22e996e9fb6'
--where name like '3%H%'
;

explain (analyze, buffers)

EXPLAIN (analyze, buffers)
SELECT  id, a, name
FROM t2
WHERE a >= 800000
;


select count(*) from t2;

explain (analyze, buffers)
SELECT t2.ctid, t2.id, a, name
from t2
join (
	select id from t2
	order by name desc
	offset 900005 rows
	fetch first 5 rows only
) as j on j.id = t2.id;
--where name like 'FB%'



set statement_timeout to 60000

show transaction_timeout;

show shared_buffers;
show maintenance_work_mem;

set maintenance_work_mem = '100MB';
set work_mem = '64MB';




--DROP INDEX idx_t_a;
--DROP INDEX idx_t_b;
CREATE INDEX idx_t_a ON public.t USING btree (a);
CREATE INDEX idx_t_b ON public.t USING btree (b);


--DROP INDEX idx_t2_a;
--DROP INDEX idx_t2_a_name;
CREATE INDEX idx_t2_a ON public.t2 USING btree (a);
CREATE INDEX idx_t2_a_name ON public.t2 USING btree (a, name);


explain (ANALYZE, buffers)
SELECT * 
FROM  t
LEFT JOIN t2 ON t2.a = t.a
ORDER BY t2.a;


explain (analyze, buffers)
	SELECT *,
	rank() 		OVER (PARTITION BY a ORDER BY b) rnk,
	rank() 		OVER (PARTITION BY b ORDER BY a) rnum
--	rank() OVER (ORDER BY a, b) rnk_2
	FROM t
;



vacuum


SET work_mem = '16MB';
show work_mem;

SHOW shared_buffers;



select to_char(999, 'FM999');

explain (analyze)
select  ''::varchar(1024);




CREATE EXTENSION pg_stat_statements;

select * from pg_stat_statements;

SELECT 
query, calls, total_exec_time, rows, 
(100.0 * shared_blks_hit) / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent 
FROM pg_stat_statements 
ORDER BY total_exec_time DESC LIMIT 100;



SELECT 
  round((total_exec_time / 1000 / 60)::numeric, 3) as total_minutes, 
  round((total_exec_time/calls)::numeric, 3) as average_time, 
  query 
FROM pg_stat_statements 
ORDER BY 1 DESC 
LIMIT 100;



select * from pg_stat_statements

SELECT
  query,
  round(total_exec_time::numeric, 3) AS total_time,
  calls,
  round(mean_exec_time::numeric, 3) AS mean,
  round((100 * total_exec_time / sum(total_exec_time::numeric) OVER ())::numeric, 2) AS percentage_cpu
FROM  public.pg_stat_statements
where query ilike 'select * from t2%'
ORDER BY mean DESC;


vacuum analyze;
select pg_stat_statements_reset();



-------------------


drop table t3;
create table t3 as
select unnest(array[null, 2, null, 3,5]) as id; 


select 
count (id),
count (*),
sum(id),
avg(id),
min(id),
max(id),
sum(id)::numeric/count(id) as   avg_id,
sum(id)::numeric/count(*)as   "avg_*",
avg(coalesce(id, 0)) as   avg_coal
from t3;




drop table t3;
create table t3 (
	id integer,
	name varchar
);

insert into t3 values 
	(1, null),
	(null, 'WWWW'),
	(null, 'de'),
	(3, 'sss'),
	(null, null),
	(5, 'ccc');

select 
count (*),
count (name),
count (*)
from t3;


select * from t3
order by id desc;



select (5/nullif(0, 0));
select coalesce(NULL, 0);




---------------------------------

--drop table t4;
create table t4 as 
select generate_series(1, 37) % 10 as col1, random;


create table t4 (a, b, c) as 
  select 
  	s.id % 10, 
  	(SELECT encode(gen_random_bytes(20 + (s.id % 10)),'base64')), 
  	random() < 0.5
  from generate_series(1, 37) as s(id)
  order by random();





select * from t4
order by a
;

explain (analyze, buffers)
select a, count(*)
from t4
group by a
order by a;


explain (analyze, buffers)
select 
	 a, b,
	 first_value(b) over (PARTITION by a),
	 last_value(b) over (PARTITION by a),
	 row_number() over (PARTITION by a)
from t4
--where a = 8
order by a;





select * from t4
where t4.b = 'Tzh18tcjy+eUwy8UFJ79Y044bCrw'
for share; 


ROLLBACK ;




CREATE TABLE employees (
  id INTEGER NOT NULL PRIMARY KEY,
  managerId INTEGER, 
  name VARCHAR(30) NOT NULL,
  FOREIGN KEY (managerId) REFERENCES employees(id)
);

INSERT INTO employees(id, managerId, name) VALUES(1, NULL, 'John');
INSERT INTO employees(id, managerId, name) VALUES(2, 1, 'Mike');
INSERT INTO employees(id, managerId, name) VALUES(3, 1, 'Pussy');



explain (analyze, buffers) 
select name 
from employees e
where not exists (select id from employees where managerid = e.id);


set enable_indexscan=on;

--explain (analyze, buffers) 
select * from t
order by id desc;
;

select * from pg_am;

/*
copy t (a, b, c, id) from stdin;
4	XXXCCC	true	100203
\.
*/


explain (analyze, buffers)
select a from t2 d where a > 5000 and a < 8000;





CREATE TABLE parent (
   p_id bigint PRIMARY KEY,
   p_val integer NOT NULL
);

INSERT INTO parent VALUES (1, 42);

CREATE TABLE child (
   c_id bigint PRIMARY KEY,
   p_id bigint REFERENCES parent
);


START TRANSACTION;
INSERT INTO child VALUES (100, 1);

SELECT p_val FROM parent WHERE p_id = 1 FOR UPDATE;


ROLLBACK;



select *,
	row_number() over wnd, 
	avg(salary) over wnd 
from job_position jp 
window wnd as (partition by department_id order by department_id);



--explain (analyze, buffers on)

select *
from department d
left join job_position jp  on d.id = jp.department_id
	 and jp.salary = 80000
--where jp.salary = 80000
order by d.id, jp.id;




explain (analyze, buffers, FORMAT TEXT /*JSON*/)
WITH T(style_id, ID) AS 
(
	SELECT 1, 1 UNION ALL
    SELECT 1, 1 UNION ALL
    SELECT 1, 2 UNION ALL
    SELECT 1, 3 UNION ALL
    SELECT 2, 2 UNION ALL
    SELECT 2, 2 UNION ALL
    SELECT 2, 4 UNION ALL
    SELECT 3, 2 UNION ALL
    SELECT 3, 3 UNION ALL
    SELECT 3, 4 UNION ALL
    SELECT 3, 5
)
SELECT 
	*,
	ROW_NUMBER() OVER wnd_1,
	RANK() OVER wnd_2

FROM T
window 
wnd_1 as (PARTITION BY style_id ORDER BY id),
wnd_2 as (PARTITION BY style_id);




with 
t1(id, val) as (
	select 1, null union all
	select 2, 'b' union all
	select 3, 'c' union all
	select 4, 'd' union all
	select 5, 'e' union all
	select 6, 'f'
),

t2(id, val) as (
	select 1, 'q' union all
	select 2, 'w' union all
	select 3, 'e' union all
	select 4, 'r' union all
	select 5, 'a' union all
	select 6, 'b'
)
select
	t1.id as t1_id, t1.val as t1_val,
	t2.id as t2_id, t2.val as t2_val
from t1
full outer join t2 on 
	t1.val is not null and 
	 t2.val = t1.val;



select ctid, xmin, xmax, pg_backend_pid(), *  
from t where id = 1 FOR KEY SHARE;


update t2 set is_active = true where id = 100029;




begin isolation level serializable;

explain (analyze, buffers, WAL) 
select * from t where a = 2 FOR NO KEY UPDATE;

update t set b = 'XXX' where id = 79482;

update t  set c = not c where id = 79482;


update t  set a = a+1 where id = 1;


delete from t where a = 557;

commit;

rollback;



select * from t order by id;

update t set a = 1000 where id in (1, 2);




begin; -- isolation level read committed;
set lock_timeout = 120000;
set deadlock_timeout = 120000;
--update t set a = a - 500 where id = 1;
update t set a = a + 500 where id = 2;

update t set a = a - 500 where id = 1;


rollback;


explain(analyze, buffers)
select a from t order by a desc limit 1;


explain(analyze, buffers)
select max(a) from t;


select ctid, id, * from t where id = 85889;


update t set b='qqq*' where id = 85889;

SHOW wal_level;


explain (analyze)
with 
t_1(id, val) as (
	select 1, 'x' union all
	select 2, 'b' union all
	select 3, 'c' union all
	select 4, 'd' union all
	select 5, 'e' union all
	select 1, 'x' union 
	select 6, 'f'
)
select *
from t_1 
order by id;



select id, a from t where not c;



begin isolation level serializable ;    
select id from t where c = true for update;
update t set c = false where id = 321;

commit;

rollback;

