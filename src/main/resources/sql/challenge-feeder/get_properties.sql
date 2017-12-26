SELECT
     pi.project_id as challengeId,
     pi.project_info_type_id as propertyId,
     pit.name as name, 
     pi.value as value
  FROM project_info pi
  INNER JOIN project_info_type_lu pit ON pit.project_info_type_id = pi.project_info_type_id  
  WHERE {filter}