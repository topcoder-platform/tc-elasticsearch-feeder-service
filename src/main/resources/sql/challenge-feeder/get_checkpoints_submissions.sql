SELECT
       u.project_id as challengeId,
       count(s.submission_id) as numberOfSubmissions
   FROM
       submission s LEFT JOIN upload u on u.upload_id = s.upload_id
   WHERE s.submission_status_id <> 5
        AND s.submission_type_id = 3
        AND u.upload_type_id = 1
        AND u.upload_status_id = 1 
        AND {filter}
        group by u.project_id
        
        