-- https://habr.com/ru/companies/postgrespro/articles/444536/


-- metadata slice is the CTID pointer. It contains the address of the page plus the address of the tuple inside this page.
select ctid from public.t;


-- количество страниц диска и кол-во строк (может быть неточным сразу после вставки, пока не засинкались буферы):
SELECT relname, relpages, reltuples FROM pg_class WHERE relname = 'idx_t_a';


select pg_relation_filepath('t');

select
    c.oid,
    c.relname,
    c.relpages,
    c.reltuples,
    c.relfilenode,
    pg_relation_filepath(c.oid),
    pg_size_pretty(pg_relation_size(c.oid) ),
    ns.nspname,
    auth.rolname

from pg_class c
inner join pg_namespace ns
    on ns.oid = c.relnamespace
inner join pg_authid auth
    on auth.oid = c.relowner
--inner join pg_tablespace tblsp
--    on tblsp.oid = c.reltablespace

where c.relname = 't';


select * from pg_tablespace where oid = 25079;




--  to view statistics of page's tuples
CREATE EXTENSION pgstattuple;

SELECT * FROM pgstattuple(25079);



-- see what’s inside the page.
CREATE EXTENSION pageinspect;


--	       0  +-----------------------------------+
--	          | заголовок                         |
--	      24  +-----------------------------------+
--	          | массив указателей на версии строк |
--	   lower  +-----------------------------------+
--	          | свободное пространство            |
--	   upper  +-----------------------------------+
--	          | версии строк                      |
--	 special  +-----------------------------------+
--	          | специальная область               |
--	pagesize  +-----------------------------------+

SELECT * FROM page_header(get_raw_page('public.t',0));



--Получить выборку метаданных всех транзакций таблицы из указанной страницы, включая скрытые (после UPDATE|DELETE)
SELECT * FROM heap_page_items(get_raw_page('t', 0));



-- Информация об индексах
select * from pg_index;


select pg_size_pretty(pg_total_relation_size('idx_t_a'));


-- Нерабочие индексы (например, созданные с ошибкой в результате взаимоблокировки или 
-- нарушении ограничения уникальности при параллельном построении)
select indexrelid::regclass index_name, indrelid::regclass table_name from pg_index where not indisvalid;


-- Список всех установленных методов доступа (типов индексов)
-- https://habr.com/ru/companies/postgrespro/articles/326106/
select * from pg_am;


-----------------------------------------------------------------

-- Информация о блокировках
select mode, granted from pg_locks;

select c.relname, c.relkind, l.pid, l.mode, l.*
from  pg_locks l
join pg_class c on l.relation = c.oid;


-----------------------------------------------------------------








CREATE TABLE public.t (
 A INTEGER,
 B INTEGER,
 C VARCHAR
);

truncate table public.t;


insert into public.t values (1234, 10, 'String #2');


do
$do$
begin
 for i in 1..1000 loop
    with r as (select random()*1000000000 as val)
    insert into public.t
    select r.val , 10, 'String #' || r.val from r;
 end loop;
end
$do$;

SELECT * FROM pgstattuple(25079);


select * from public.t;

select * from users for share;


do
$do$
    begin
        alter table users add column data integer;
    end
$do$;


select invoice_no from invoice for update ;

update invoice set invoice_amt =  invoice_amt + 1 where invoice_no = 1;



CREATE TABLE invoice (
                         invoice_no    integer        PRIMARY KEY,
                         seller_no     integer,       -- ID of salesperson
                         invoice_date  date,          -- date of sale
                         invoice_amt   numeric(13,2)  -- amount of sale
);


CREATE MATERIALIZED VIEW sales_summary AS
SELECT
    seller_no,
    invoice_date,
    sum(invoice_amt)::numeric(13,2) as sales_amt
FROM invoice
WHERE invoice_date < CURRENT_DATE
GROUP BY
    seller_no,
    invoice_date;

-- drop index sales_summary_seller;
CREATE UNIQUE INDEX sales_summary_seller
    ON sales_summary (seller_no, invoice_date);


REFRESH MATERIALIZED VIEW sales_summary;

-- drop table mymatview;
CREATE TABLE mymatview AS
    SELECT * FROM invoice;

-- drop view myview
create view myview (invoice_no, seller_no) as
    SELECT * FROM invoice;

INSERT INTO myview(invoice_no, seller_no, invoice_date, invoice_amt)
-- values (1, 1, to_date('2025-01-02', 'YYYY-MM-DD'), 13.5),
values (3, 31, to_date('2025-02-23', 'YYYY-MM-DD'), 58.4);

select * from sales_summary;

select * from mymatview;

select * from myview;


-- CREATE TABLESPACE index_tablespace LOCATION '/var/lib/postgresql/data/indexes';
--ALTER INDEX sales_summary_seller SET TABLESPACE index_tablespace;
--ALTER INDEX sales_summary_seller SET TABLESPACE pg_default;



