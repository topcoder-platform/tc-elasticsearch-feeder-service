SELECT distinct
         p.project_id AS challengeId,
         r.user_id AS userId,
         r.user_id ||
    (SELECT CASE
                WHEN q.count > 0 THEN 'T'
                ELSE 'F'
            END AS hasSubmittedForReview
     FROM
       (SELECT count(*) AS count
        FROM upload u,
             submission s,
             RESOURCE rs
        WHERE u.upload_id = s.upload_id
          AND rs.user_id = r.user_id
          AND rs.resource_role_id = 1
          AND rs.project_id = u.project_id
          AND u.project_id = p.project_id
          AND rs.resource_id = u.resource_id) AS q) AS hasUserSubmittedForReview
  FROM RESOURCE r
  INNER JOIN project p ON p.project_id = r.project_id
  WHERE p.project_category_id NOT IN (27,37) AND {filter}