SELECT distinct
         p.project_id AS challengeId,
         r.user_id AS userId
  FROM RESOURCE r
  INNER JOIN project p ON p.project_id = r.project_id
  WHERE p.project_category_id NOT IN (27,37) AND {filter}
