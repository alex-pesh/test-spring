-- Обработка запросов
-- https://postgrespro.ru/docs/postgrespro/current/textsearch-features#TEXTSEARCH-MANIPULATE-TSVECTOR


select to_tsvector('russian', 'Обход машинного зала') @@ to_tsquery('fat & rat');

SELECT 'fat cats ate fat rats'::tsvector 	@@ to_tsquery('fat & rat');



select 'fat & rat'::tsquery && 'full | join'::tsquery;



-- найти десять наиболее часто используемых слов в коллекции документов
SELECT * FROM ts_stat('SELECT full_text_search FROM route')
ORDER BY nentry DESC, ndoc DESC, word
LIMIT 10;



SELECT plainto_tsquery('russian', 'я пришел') && !!plainto_tsquery('russian', 'к тебе с приветом');


-- Сколько документов содержат лексему
select (unnest(full_text_search)).lexeme, count(*) from route group by 1 order by 2 desc;


-- для большого объема данных
select * from ts_stat('select full_text_search from route') order by ndoc desc limit 3;



select pg_try_advisory_lock(111);
select pg_advisory_unlock(111);

select * from pg_locks join pg_stat_activity using (pid) where locktype='advisory';



-- To retrieve the operators associated with a PostgreSQL index, you need to query the system catalogs, 
-- specifically pg_opclass, pg_opfamily, and pg_am.
SELECT
    o.opcname AS operator_class_name,
    o.opcintype::regtype AS indexed_data_type,
    am.amname AS index_access_method
FROM
    pg_index AS idx
JOIN
    pg_opclass AS o ON o.oid = ANY(idx.indclass)
JOIN
    pg_am AS am ON am.oid = o.opcmethod
WHERE
    idx.indexrelid = 'full_text_search_2_idx'::regclass;





-- https://habr.com/ru/companies/postgrespro/articles/576100/

SELECT 
	reltuples, 		-- число строк в отношении. Обновляется при ANALYZE
	relpages,  		-- размер отношения в страницах. Обновляется при ANALYZE
	relallvisible	-- количество страниц, отмеченных в карте видимости. Используется при оценке стоимости Index Only Scan. Обновляется при VACUUM 
FROM pg_class WHERE relname = 'route'; 

vacuum route;

select 
	n_distinct, 		-- значений столбца среди всех записей
	correlation, 		-- упорядоченность значений по колонке
	null_frac,			-- доля записей с NULL
	most_common_vals,	-- Список самых частых значений в столбце. (NULL, если не находятся значения, встречающиеся чаще других.)
	most_common_freqs,	-- Список частот самых частых значений, то есть число их вхождений, делённое на общее количество строк
	*
from pg_stats
where tablename = 'route';



select
	count(*),
	date_trunc('month', created_date) as created_month
from route
group by created_month ;




