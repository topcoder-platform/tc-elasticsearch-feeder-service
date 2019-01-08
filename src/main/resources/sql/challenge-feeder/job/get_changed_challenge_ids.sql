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
	  UNION
      SELECT DISTINCT
        pi.project_id as project_id
      FROM
        project_info pi, informixoltp\:round_registration rr
      WHERE pi.value::decimal = rr.round_id
        AND pi.project_info_type_id = 56
        AND rr.timestamp < sysdate AND rr.timestamp > :lastRunTimestamp
      UNION
      SELECT DISTINCT
        pi.project_id as project_id
      FROM
        informixoltp\:long_submission ls,
        informixoltp\:long_component_state lcs,
        informixoltp\:round rr,
        project_info pi
      WHERE ls.long_component_state_id = lcs.long_component_state_id
        AND ls.example = 0
        AND rr.round_id = lcs.round_id
        AND pi.value::decimal = rr.round_id
        AND pi.project_info_type_id = 56
        AND ls.submit_time IS NOT NULL
        AND dbinfo('utc_to_datetime',ls.submit_time/1000) < sysdate
        AND dbinfo('utc_to_datetime',ls.submit_time/1000) > :lastRunTimestamp
  ) cc
  INNER JOIN project p ON cc.project_id = p.project_id order by p.project_id desc