SELECT
DISTINCT p.project_id as id
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
	     upload
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
	  UNION
      	  SELECT DISTINCT
      	   (project_id)
      	  FROM
      	     project_role_terms_of_use_xref
      	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
      UNION
      	  SELECT DISTINCT
      	   (project_id)
      	  FROM
      	     project_spec
      	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
      UNION
      	  SELECT DISTINCT
      	   (p.project_id) AS project_id
      	  FROM
      	     project p, project_studio_specification pss
      	  WHERE p.project_studio_spec_id = pss.project_studio_spec_id
      	  AND pss.modify_date < sysdate AND pss.modify_date > :lastRunTimestamp
	  UNION
	  SELECT DISTINCT
	   (project_id)
	  FROM
	     resource
	  WHERE modify_date < sysdate AND modify_date > :lastRunTimestamp
  ) cc
  INNER JOIN project p ON cc.project_id = p.project_id order by p.project_id desc