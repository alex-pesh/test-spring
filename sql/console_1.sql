

select invoice_no
from invoice
where invoice_no = 1

;

explain analyse
update invoice set invoice_amt =  invoice_amt + 1 where invoice_no in (
    select invoice_no from invoice for update skip locked
) ;



select salary, min(vacation_days), max(vacation_days)
from job_position
group by salary
-- having min(salary) >= 23000;


create table ids (
    uuid_v7 UUID DEFAULT uuid_generate_v7()
);


select uuid_generate_v7 ();

select uuid_v7_to_timestamptz('00000000-4a80-7000-8000-000000000000');

SELECT
    uuid_timestamptz_to_v7('2023-01-02 04:26:40.638+00', true),
    uuid_timestamptz_to_v7('2023-01-02 04:26:40.640+00', true);


SELECT uuid_generate_v7();


select * from pg_extension;
create extension "uuid-ossp";
CREATE EXTENSION "pg_uuidv7";



update t4 
set c = false 
where t4.b = 'Tzh18tcjy+eUwy8UFJ79Y044bCrw';


select * from t4
where t4.b = 'Tzh18tcjy+eUwy8UFJ79Y044bCrw'
for share; 


select * from t4
where t4.b = 'Tzh18tcjy+eUwy8UFJ79Y044bCrw'
for update; 


explain (analyse, buffers)
select * from t 
order by a desc;


vacuum t;
vacuum full t;


SELECT * 
FROM heap_page_items(get_raw_page('t', 2));



select ctid, xmin, xmax, pg_backend_pid(), *  
from t where id = 1 FOR UPDATE;


update t  set a = a where id = 1;


rollback;

SELECT * FROM heap_page_items(get_raw_page('t', 0));

