SELECT 
DISTINCT (project_id) as id 
FROM (
	SELECT DISTINCT
	   (project_id) 
	  FROM
	     project 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	    (project_id) 
	  FROM
	     project_info 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	  (project_id) 
	  FROM
	     project_phase 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	    (project_id) 
	  FROM
	     upload 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	   (project_id) 
	  FROM
	     resource 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	   (project_id) 
	  FROM
	     prize 
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
  ) where project_id is not null order by project_id desc