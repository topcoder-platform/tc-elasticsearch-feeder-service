SELECT  
      pp.project_id as challengeId,
      r.review_id as reviewId,
      r.submission_id as submissionId,
      r.initial_score as initialScore,
      r.score,
      submitter.user_id as submitterId,
      submitter.handle as submitterHandle,
      reviewer.user_id as reviewerId,
      reviewer.handle  as reviewerHandle
   FROM review r
     INNER JOIN submission s ON r.submission_id = s.submission_id
     INNER JOIN upload u ON s.upload_id = u.upload_id
     INNER JOIN resource rs_submitter ON u.resource_id = rs_submitter.resource_id AND rs_submitter.resource_role_id = 1
     INNER JOIN project_phase pp ON pp.project_phase_id = r.project_phase_id
     INNER JOIN user submitter ON rs_submitter.user_id = submitter.user_id
     INNER JOIN resource rs_reviewer ON r.resource_id = rs_reviewer.resource_id
     INNER JOIN user reviewer ON rs_reviewer.user_id = reviewer.user_id
   WHERE r.committed = 1 AND {filter}