
CREATE EXTENSION IF NOT EXISTS pgrowlocks;



show config_file;
show track_io_timing;


-- Query to retrieve a list of objects owned by a particular user
SELECT
    n.nspname AS schema_name,
    c.relname AS object_name,
    CASE c.relkind
        WHEN 'r' THEN 'TABLE'
        WHEN 'v' THEN 'VIEW'
        WHEN 'm' THEN 'MATERIALIZED VIEW'
        WHEN 'i' THEN 'INDEX'
        WHEN 'S' THEN 'SEQUENCE'
        WHEN 'f' THEN 'FOREIGN TABLE'
        WHEN 'p' THEN 'PARTITIONED TABLE'
        ELSE c.relkind::text
    END AS object_type
FROM
    pg_class c
JOIN
    pg_namespace n ON n.oid = c.relnamespace
JOIN
    pg_roles r ON r.oid = c.relowner
WHERE
    r.rolname = 'postgres'
    AND n.nspname NOT IN ('pg_catalog', 'information_schema')
    AND n.nspname NOT LIKE 'pg_toast%';




SELECT
    locktype,
    mode,
    relation::regclass AS table,
    tuple,
    granted
FROM pg_locks;
--WHERE pid = pg_backend_pid();

------------------------------------------------------------------






-- Список активных транзакций 
--https://postgrespro.ru/docs/postgrespro/current/monitoring-stats#MONITORING-PG-STAT-ACTIVITY-VIEW

SELECT
	pid, backend_xid, xact_start, query, ' |'_,
	state, usename, application_name, client_addr 
FROM pg_stat_activity
WHERE backend_xid IS NOT NULL OR xact_start IS NOT NULL;	


select * from pg_stat_activity;




-- Список всех активных блокировок с именами таблиц
select c.relname, l.* 
from pg_locks l 
join pg_class c on c.oid = l.relation; 





-- Текущие блокировки конкретной таблицы
SELECT locktype, mode, transactionid, relation::regclass, *
FROM pg_locks 
WHERE relation::regclass = 'speakers'::regclass;



SELECT * FROM pgrowlocks('speakers');








