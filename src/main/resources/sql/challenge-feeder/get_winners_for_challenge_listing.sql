SELECT 
   u.project_id as challengeId,
   user.handle as submitter,
   s.placement as rank,
   s.submission_id as submissionId,
   s.create_date as submissionTime,
   s.final_score as points
  FROM upload u
       INNER JOIN submission s ON s.upload_id = u.upload_id
       INNER JOIN prize p ON p.prize_id = s.prize_id
       INNER JOIN user ON user.user_id = s.create_user
  WHERE ((s.submission_type_id = 1 AND p.prize_type_id = 15) OR (s.submission_type_id = 3 AND p.prize_type_id = 14))
       AND {filter}
       