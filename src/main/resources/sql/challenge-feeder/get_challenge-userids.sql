SELECT distinct
         p.project_id AS challengeId,
         r.user_id AS userId
  FROM RESOURCE r
  INNER JOIN project p ON p.project_id = r.project_id
  WHERE 1 = 1 AND {filter}
