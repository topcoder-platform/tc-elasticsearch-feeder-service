SELECT distinct
         p.project_id AS challengeId,
         gce.group_id AS groupId
  FROM project p
  INNER JOIN project_category_lu pcl ON pcl.project_category_id = p.project_category_id
  LEFT JOIN contest_eligibility ce ON ce.contest_id = p.project_id
  LEFT JOIN group_contest_eligibility gce ON gce.contest_eligibility_id = ce.contest_eligibility_id
  WHERE pcl.project_category_id NOT IN (27,37)  AND {filter}