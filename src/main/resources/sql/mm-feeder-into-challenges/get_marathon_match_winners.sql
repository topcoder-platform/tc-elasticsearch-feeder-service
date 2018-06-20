SELECT u.handle as submitter,
       rr.round_id as challengeId,
       decode(s.submit_time, null, null, dbinfo("utc_to_datetime", submit_time/1000)) as submissionDate,
       cs.long_component_state_id || s.submission_number as submissionId,
       rr.point_total as points,
       u.handle_lower
  FROM long_comp_result rr
     , user u
     , long_component_state cs
     , outer (long_submission s)
 WHERE rr.attended = 'Y'
   and rr.coder_id = u.user_id
   and cs.coder_id = u.user_id
   AND rr.round_id = cs.round_id
   and s.long_component_state_id = cs.long_component_state_id
   and s.submission_number = cs.submission_number
   and s.example = 0
   and {filter}
ORDER BY points desc, u.handle_lower asc
and
