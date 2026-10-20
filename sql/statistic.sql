-- https://postgrespro.ru/docs/postgrespro/current/monitoring-stats
-- https://postgrespro.ru/docs/postgrespro/9.5/pgstatstatements


ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
CREATE EXTENSION pg_stat_statements;


SHOW TRANSACTION ISOLATION LEVEL;
SHOW search_path;
SELECT CURRENT_USER;


SELECT * FROM pg_namespace;


select *
from pg_catalog.pg_get_keywords()
where word <> ALL
      ('{a,abs,absolute,action,ada,add,admin,after,all,allocate,alter,always,and,any,are,array,as,asc,asensitive,assertion,assignment,asymmetric,at,atomic,attribute,attributes,authorization,avg,before,begin,bernoulli,between,bigint,binary,blob,boolean,both,breadth,by,c,call,called,cardinality,cascade,cascaded,case,cast,catalog,catalog_name,ceil,ceiling,chain,char,char_length,character,character_length,character_set_catalog,character_set_name,character_set_schema,characteristics,characters,check,checked,class_origin,clob,close,coalesce,cobol,code_units,collate,collation,collation_catalog,collation_name,collation_schema,collect,column,column_name,command_function,command_function_code,commit,committed,condition,condition_number,connect,connection_name,constraint,constraint_catalog,constraint_name,constraint_schema,constraints,constructors,contains,continue,convert,corr,corresponding,count,covar_pop,covar_samp,create,cross,cube,cume_dist,current,current_collation,current_date,current_default_transform_group,current_path,current_role,current_time,current_timestamp,current_transform_group_for_type,current_user,cursor,cursor_name,cycle,data,date,datetime_interval_code,datetime_interval_precision,day,deallocate,dec,decimal,declare,default,defaults,deferrable,deferred,defined,definer,degree,delete,dense_rank,depth,deref,derived,desc,describe,descriptor,deterministic,diagnostics,disconnect,dispatch,distinct,domain,double,drop,dynamic,dynamic_function,dynamic_function_code,each,element,else,end,end-exec,equals,escape,every,except,exception,exclude,excluding,exec,execute,exists,exp,external,extract,false,fetch,filter,final,first,float,floor,following,for,foreign,fortran,found,free,from,full,function,fusion,g,general,get,global,go,goto,grant,granted,group,grouping,having,hierarchy,hold,hour,identity,immediate,implementation,in,including,increment,indicator,initially,inner,inout,input,insensitive,insert,instance,instantiable,int,integer,intersect,intersection,interval,into,invoker,is,isolation,join,k,key,key_member,key_type,language,large,last,lateral,leading,left,length,level,like,ln,local,localtime,localtimestamp,locator,lower,m,map,match,matched,max,maxvalue,member,merge,message_length,message_octet_length,message_text,method,min,minute,minvalue,mod,modifies,module,month,more,multiset,mumps,name,names,national,natural,nchar,nclob,nesting,new,next,no,none,normalize,normalized,not,"null",nullable,nullif,nulls,number,numeric,object,octet_length,octets,of,old,on,only,open,option,options,or,order,ordering,ordinality,others,out,outer,output,over,overlaps,overlay,overriding,pad,parameter,parameter_mode,parameter_name,parameter_ordinal_position,parameter_specific_catalog,parameter_specific_name,parameter_specific_schema,partial,partition,pascal,path,percent_rank,percentile_cont,percentile_disc,placing,pli,position,power,preceding,precision,prepare,preserve,primary,prior,privileges,procedure,public,range,rank,read,reads,real,recursive,ref,references,referencing,regr_avgx,regr_avgy,regr_count,regr_intercept,regr_r2,regr_slope,regr_sxx,regr_sxy,regr_syy,relative,release,repeatable,restart,result,return,returned_cardinality,returned_length,returned_octet_length,returned_sqlstate,returns,revoke,right,role,rollback,rollup,routine,routine_catalog,routine_name,routine_schema,row,row_count,row_number,rows,savepoint,scale,schema,schema_name,scope_catalog,scope_name,scope_schema,scroll,search,second,section,security,select,self,sensitive,sequence,serializable,server_name,session,session_user,set,sets,similar,simple,size,smallint,some,source,space,specific,specific_name,specifictype,sql,sqlexception,sqlstate,sqlwarning,sqrt,start,state,statement,static,stddev_pop,stddev_samp,structure,style,subclass_origin,submultiset,substring,sum,symmetric,system,system_user,table,table_name,tablesample,temporary,then,ties,time,timestamp,timezone_hour,timezone_minute,to,top_level_count,trailing,transaction,transaction_active,transactions_committed,transactions_rolled_back,transform,transforms,translate,translation,treat,trigger,trigger_catalog,trigger_name,trigger_schema,trim,true,type,uescape,unbounded,uncommitted,under,union,unique,unknown,unnamed,unnest,update,upper,usage,user,user_defined_type_catalog,user_defined_type_code,user_defined_type_name,user_defined_type_schema,using,value,values,var_pop,var_samp,varchar,varying,view,when,whenever,where,width_bucket,window,with,within,without,work,write,year,zone}'::text[])
;



SELECT * from pg_stat_statements;


SELECT
    (total_exec_time / 1000) as total_sec,
    (total_exec_time/calls) as average_time,
    query
FROM pg_stat_statements
ORDER BY 2 DESC;



SELECT query,
       calls,
       total_exec_time,
       rows,
       100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 5;



---- pg_stats
---- https://postgrespro.ru/docs/postgresql/current/view-pg-stats

select * from pg_stats;

-- Статистика, которая показывает степень упорядоченности данных в таблице
select attname, correlation from pg_stats where tablename = 't';



-- Кластеризация таблицы по заданному индексу
cluster verbose t using idx_t_a;
-- Обновляет статистику после кластеризации
ANALYZE t; 




-- perform a "select pg_stat_reset();" when you want to reset counter statistics
with all_tables as (
	SELECT  *
	FROM    (
	    SELECT  'all'::text as table_name, 
	        sum( (coalesce(heap_blks_read,0) + coalesce(idx_blks_read,0) + coalesce(toast_blks_read,0) + coalesce(tidx_blks_read,0)) ) as from_disk, 
	        sum( (coalesce(heap_blks_hit,0)  + coalesce(idx_blks_hit,0)  + coalesce(toast_blks_hit,0)  + coalesce(tidx_blks_hit,0))  ) as from_cache    
	    FROM    pg_statio_all_tables  --> change to pg_statio_USER_tables if you want to check only user tables (excluding postgres's own tables)
	    ) a
	WHERE   (from_disk + from_cache) > 0 -- discard tables without hits
),

tables as (
	SELECT *
	FROM     (
	    SELECT  relname as table_name, 
	        ( (coalesce(heap_blks_read,0) + coalesce(idx_blks_read,0) + coalesce(toast_blks_read,0) + coalesce(tidx_blks_read,0)) ) as from_disk, 
	        ( (coalesce(heap_blks_hit,0)  + coalesce(idx_blks_hit,0)  + coalesce(toast_blks_hit,0)  + coalesce(tidx_blks_hit,0))  ) as from_cache    
	    FROM    pg_statio_all_tables --> change to pg_statio_USER_tables if you want to check only user tables (excluding postgres's own tables)
	    ) a
	WHERE   (from_disk + from_cache) > 0 -- discard tables without hits
)
SELECT table_name as "table name",
    from_disk as "disk hits",
    round((from_disk::numeric / (from_disk + from_cache)::numeric)*100.0,2) as "% disk hits",
    round((from_cache::numeric / (from_disk + from_cache)::numeric)*100.0,2) as "% cache hits",
    (from_disk + from_cache) as "total hits"
FROM    (SELECT * FROM all_tables UNION ALL SELECT * FROM tables) a
ORDER   BY (case when table_name = 'all' then 0 else 1 end), from_disk desc;


-----------------------


--https://wiki.postgresql.org/wiki/Index_Maintenance

-- Index summary
-- Here's a sample query to pull the number of rows, indexes, and some info about those indexes for each table.

SELECT
    pg_class.relname,
    pg_size_pretty(pg_class.reltuples::bigint)            AS rows_in_bytes,
    pg_class.reltuples                                     AS num_rows,
    COUNT(*)                                              AS total_indexes,
    COUNT(*) FILTER ( WHERE indisunique)                  AS unique_indexes,
    COUNT(*) FILTER ( WHERE indnatts = 1 )                AS single_column_indexes,
    COUNT(*) FILTER ( WHERE indnatts IS DISTINCT FROM 1 ) AS multi_column_indexes
FROM
    pg_namespace
    LEFT JOIN pg_class ON pg_namespace.oid = pg_class.relnamespace
    LEFT JOIN pg_index ON pg_class.oid = pg_index.indrelid
WHERE
    pg_namespace.nspname = 'public' AND
    pg_class.relkind = 'r'
GROUP BY pg_class.relname, pg_class.reltuples
ORDER BY pg_class.reltuples DESC;




-- Index size/usage statistics
-- Table & index sizes along which indexes are being scanned and how many tuples are fetched. 
-- See Disk Usage for another view that includes both table and index sizes.

SELECT
    t.schemaname,
    t.tablename,
    c.reltuples::bigint                            AS num_rows,
    pg_size_pretty(pg_relation_size(c.oid))        AS table_size,
    psai.indexrelname                              AS index_name,
    pg_size_pretty(pg_relation_size(i.indexrelid)) AS index_size,
    CASE WHEN i.indisunique THEN 'Y' ELSE 'N' END  AS "unique",
    psai.idx_scan                                  AS number_of_scans,
    psai.idx_tup_read                              AS tuples_read,
    psai.idx_tup_fetch                             AS tuples_fetched
FROM
    pg_tables t
    LEFT JOIN pg_class c ON t.tablename = c.relname
    LEFT JOIN pg_index i ON c.oid = i.indrelid
    LEFT JOIN pg_stat_all_indexes psai ON i.indexrelid = psai.indexrelid
WHERE
    t.schemaname NOT IN ('pg_catalog', 'information_schema')
ORDER BY 1, 2;




-- Duplicate indexes
-- Finds multiple indexes that have the same set of columns, same opclass, expression and predicate which make them equivalent. 
-- Usually it's safe to drop one of them, but I give no guarantees

SELECT pg_size_pretty(sum(pg_relation_size(idx))::bigint) as size,
       (array_agg(idx))[1] as idx1, (array_agg(idx))[2] as idx2,
       (array_agg(idx))[3] as idx3, (array_agg(idx))[4] as idx4
FROM (
    SELECT indexrelid::regclass as idx, (indrelid::text ||E'\n'|| indclass::text ||E'\n'|| indkey::text ||E'\n'||
                                         coalesce(indexprs::text,'')||E'\n' || coalesce(indpred::text,'')) as key
    FROM pg_index) sub
GROUP BY key HAVING count(*)>1
ORDER BY sum(pg_relation_size(idx)) DESC;
 
 


-----------------------------------------------------------------

-- Информация о блокировках
--https://wiki.postgresql.org/wiki/Lock_Monitoring


select mode, granted from pg_locks;

select c.relname, c.relkind, l.pid, l.mode, l.*
from  pg_locks l
join pg_class c on l.relation = c.oid;



-- Сombination of blocked and blocking activity
-- The following query may be helpful to see what processes are blocking SQL statements (these only find row-level locks, not object-level locks).

--SET application_name='%your_logical_name%';


SELECT blocked_locks.pid     AS blocked_pid,
         blocked_activity.usename  AS blocked_user,
         blocking_locks.pid     AS blocking_pid,
         blocking_activity.usename AS blocking_user,
         blocked_activity.query    AS blocked_statement,
         blocking_activity.query   AS current_statement_in_blocking_process,
         blocked_activity.application_name AS blocked_application,
         blocking_activity.application_name AS blocking_application
   FROM  pg_catalog.pg_locks         blocked_locks
    JOIN pg_catalog.pg_stat_activity blocked_activity  ON blocked_activity.pid = blocked_locks.pid
    JOIN pg_catalog.pg_locks         blocking_locks 
        ON blocking_locks.locktype = blocked_locks.locktype
        AND blocking_locks.DATABASE IS NOT DISTINCT FROM blocked_locks.DATABASE
        AND blocking_locks.relation IS NOT DISTINCT FROM blocked_locks.relation
        AND blocking_locks.page IS NOT DISTINCT FROM blocked_locks.page
        AND blocking_locks.tuple IS NOT DISTINCT FROM blocked_locks.tuple
        AND blocking_locks.virtualxid IS NOT DISTINCT FROM blocked_locks.virtualxid
        AND blocking_locks.transactionid IS NOT DISTINCT FROM blocked_locks.transactionid
        AND blocking_locks.classid IS NOT DISTINCT FROM blocked_locks.classid
        AND blocking_locks.objid IS NOT DISTINCT FROM blocked_locks.objid
        AND blocking_locks.objsubid IS NOT DISTINCT FROM blocked_locks.objsubid
        AND blocking_locks.pid != blocked_locks.pid
 
    JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid
   WHERE NOT blocked_locks.GRANTED;




select datname, deadlocks from pg_stat_database; 


CREATE EXTENSION pgrowlocks;

SELECT * FROM pgrowlocks('t');

---------- 




begin;
set lock_timeout = 120000;
set deadlock_timeout = 120000;
update t set a = a - 100 where id = 1;

update t set a = a - 500 where id = 2;


rollback;
 

alter table t 
add column t2_id integer;
alter table t 
add constraint t2_fk foreign key (t2_id) references t2(id); 
update t set t2_id = t.id;



--EXPLAIN (ANALYZE, VERBOSE)

select * from t where id = 1 for update;

commit;

rollback ;



------------------------

select * from department
                  cross join job_position jp;
      

EXPLAIN -- ANALYSE
select * from job_position jp
where   jp.department_id between 1 and 4
  and     jp.department_id between 3 and 6;



SELECT '01:17:27.612383+01'::timetz - '01:17:21.356247+00'::timetz;



select * from t order by b;


SELECT *
FROM pg_stat_user_indexes
WHERE relname = 't';



SELECT *
FROM pg_stat_all_indexes
WHERE indexrelid = 'idx_t_b'::regclass::oid;


-- Статистика по таблицам. число живых/мёртвых кортежей, автоанализ
SELECT * 
FROM pg_stat_user_tables;



-- Приблизительные оценки по статистике планировщика вместо явного использования count на больших таблицах 
SELECT reltuples::bigint AS approx_count
FROM pg_class
WHERE relname = 't';


select count(*) from t;



