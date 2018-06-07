SELECT
       u.project_id as challengeId,
       ri.value::DECIMAL as submitterId
   FROM
       submission s, upload u, resource_info ri
   WHERE
        u.upload_id = s.upload_id
        AND s.submission_status_id <> 5
        AND s.submission_type_id in (1,3)
        AND u.upload_type_id = 1
        AND u.upload_status_id = 1
        AND u.resource_id = ri.resource_id
        AND ri.resource_info_type_id = 1
        AND {filter}