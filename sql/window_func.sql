-- https://tapoueh.org/blog/2013/08/understanding-window-functions/
-- https://www.sqlshack.com/overview-of-sql-rank-functions/
-- https://habr.com/ru/articles/809733/


WITH T(StyleID, ID)
     AS (SELECT 1, 1 UNION ALL
         SELECT 1, 2 UNION ALL
         SELECT 1, 3 UNION ALL
         SELECT 1, 4 UNION ALL
         
         SELECT 2, 5 UNION ALL
         SELECT 2, 6 UNION ALL
         
         SELECT 3, 7 UNION ALL
         SELECT 3, 8 UNION ALL
         SELECT 3, 9 UNION ALL
         
         SELECT 4, 10)
SELECT *, ' ' AS "--",
       	RANK() 		OVER(PARTITION BY StyleID order BY ID) AS RANK,
       	DENSE_RANK() OVER(PARTITION BY StyleID order BY ID) AS DENSE_RANK,
       	ROW_NUMBER() OVER(PARTITION BY StyleID order BY ID) AS ROW_NUMBER,
       	NTILE(2) OVER(PARTITION BY StyleID  order BY ID) AS NTILE,
       

		sum(id)  OVER(PARTITION BY StyleID order by id rows between UNBOUNDED PRECEDING and current row) AS sum,
--       	sum(id) filter (where id > 2) OVER(PARTITION BY StyleID order by id) AS sum,
		
		id, styleid

FROM   T;



WITH T(StyleID, ID) AS 
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
	*, ' ' AS "  ",
	format('%s / %s',        
		ROW_NUMBER() OVER(PARTITION BY StyleID ORDER BY id),
		count(*) 	 OVER(PARTITION BY StyleID) 
	),
	first_value(id) OVER(PARTITION BY StyleID ORDER BY id) AS first_id,
	last_value(id) OVER(PARTITION BY StyleID ORDER BY id) AS last_id,
	nth_value(id, 3) OVER(PARTITION BY StyleID ORDER BY id) AS third_id

FROM T;
--WHERE styleid = 1;




-- Reusing the same window definition several times

--explain (analyze)
WITH T(StyleID, ID) AS 
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
	*, ' ' AS "  ",
	format('%s / %s',        
		ROW_NUMBER() 	OVER win_style_id,
		count(*) 	 	OVER win_style 
	),
	first_value(id) 	OVER win_style_id AS first_id,
	last_value(id) 		OVER win_style_id AS last_id,
	nth_value(id, 3) 	OVER win_style_id AS third_id

FROM T
--WHERE styleid = 1
WINDOW 
	win_style_id AS (PARTITION BY StyleID ORDER BY id),
 	win_style AS (PARTITION BY StyleID);




--------------------------- Frames ---------------------------------

/*
https://habr.com/ru/companies/otus/articles/490296/
 
фрейм бывает 2х видов ROWS и RANGE. 
Разница заключается в том, что ROWS оперирует строкой, а RANGE диапазоном. 
Ддиапазоном называются строки с одинаковым значением параметра сортировки.


 Варианты ограничения фрейма:
1. Все, что до текущей строки/диапазона и само значение текущей строки
	BETWEEN UNBOUNDED PRECEDING
	BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
2. Текущая строка/диапазон и все, что после нее
	BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING
3. С конкретным указазнием сколько строк до и после включать (не поддерживается для RANGE)
	BETWEEN N Preceding AND N Following
	BETWEEN CURRENT ROW AND N Following
	BETWEEN N Preceding AND CURRENT ROW
*/

-- https://stackoverflow.com/questions/30861919/what-is-rows-unbounded-preceding-used-for-in-teradata
-- https://tapoueh.org/blog/2013/08/understanding-window-functions/

-- each average is calculated "over" an ordered frame consisting of the range between the previous row (1 preceding) 
-- and the subsequent row (1 following):

WITH data (t, a) AS (
  VALUES(1, 1),
        (2, 5),
        (3, 3),
        (4, 5),
        (5, 4),
        (6, 11)
)
SELECT t, a, 
avg(a) OVER (ORDER BY t ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING),
array_agg(a) OVER (ORDER BY t ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING)
FROM data
ORDER BY t;



-- When you write ROWS UNBOUNDED PRECEDING, then the frame's lower bound is simply infinite. 
-- This is useful when calculating sums (i.e. "running totals"), for instance:


WITH data (t, a) AS (
  VALUES(1, 1),
        (2, 5),
        (3, 3),
        (4, 5),
        (5, 4),
        (6, 11)
)
SELECT t, a, 
	sum(a) OVER (ORDER BY t ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) sum_1,
	sum(a) OVER (ORDER BY t ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING) sum_2
FROM data
ORDER BY t;




WITH data (t, a) AS (
  VALUES(1, 100),
        (1, 550),
        (1, 300),
        (2, 250),
        (2, 450),
        (3, 100),
        (3, 150),
        (3, 300),        
        (3, 600)        
)
SELECT t, a,
sum(a) OVER (PARTITION BY t ORDER BY a RANGE BETWEEN 200 PRECEDING AND CURRENT ROW) sum_range, -- суммировать только те строки, значения столбца "a" которых лежат в диапазоне <текущее значение> - 200
sum(a) OVER (PARTITION BY t ORDER BY a ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) sum_rows, -- суммировать строки с начала партиции до текущей
sum(a) FILTER (WHERE a > 200) OVER (PARTITION BY t) sum_filter -- суммировать строки с учетом фильтра

FROM data
ORDER BY t;
