SELECT 
   u.project_id as challengeId,
   ri.value as userId,
   user.handle as handle,
   s.placement as placement,
   CASE WHEN s.submission_type_id = 1 THEN CAST('final' AS VARCHAR(10)) ELSE CAST('checkpoint' AS VARCHAR(10)) END as submissionType,
  (SELECT p.path || i.file_name 
         FROM informixoltp\:coder_image_xref mi 
             INNER JOIN informixoltp\:image i ON mi.image_id = i.image_id 
             INNER JOIN informixoltp\:path p ON p.path_id = i.path_id 
         WHERE mi.coder_id = ri.value AND mi.display_flag = 1 AND i.image_type_id = 1) as photoURL
  FROM upload u
       INNER JOIN submission s ON s.upload_id = u.upload_id
       INNER JOIN prize p ON p.prize_id = s.prize_id
       INNER JOIN resource_info ri ON ri.resource_id = u.resource_id
       INNER JOIN user ON user.user_id = ri.value::DECIMAL(10,0)
  WHERE resource_info_type_id = 1
       AND ((s.submission_type_id = 1 AND p.prize_type_id = 15) OR (s.submission_type_id = 3 AND p.prize_type_id = 14))
       AND {filter}