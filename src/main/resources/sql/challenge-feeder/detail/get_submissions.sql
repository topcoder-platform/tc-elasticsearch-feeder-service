SELECT
       u.project_id as challengeId,
       s.submission_id as submissionId,
       s.create_date as submissionTime,
       s.placement as placement,
       s.submission_type_id as submissionTypeId,
       s.create_user as submitterId,
       usr.handle as submitter,
       s.screening_score as screeningScore,
       s.initial_score as initialScore,
       s.final_score as finalScore,
       ssl.name AS submissionStatus
   FROM
       upload u, submission_status_lu ssl, user usr, submission s
   WHERE
        u.upload_id = s.upload_id
        AND s.create_user = usr.user_id
        AND s.submission_status_id = ssl.submission_status_id
        AND s.submission_status_id <> 5
        AND s.submission_type_id in (1,3)
        AND u.upload_type_id = 1
        AND u.upload_status_id = 1
        AND {filter}