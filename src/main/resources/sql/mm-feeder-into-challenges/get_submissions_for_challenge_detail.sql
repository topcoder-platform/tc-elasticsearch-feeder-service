select
lcs.round_id as challengeId,
lcs.long_component_state_id as submissionId,
user.handle as submitter,
ls.submission_points as finalScore,
ls.submission_points as initialScore,
CASE
  WHEN ls.submit_time IS NOT NULL THEN extend(dbinfo("UTC_TO_DATETIME",ls.submit_time/1000), year to fraction)
  ELSE NULL
END as submissionTime,
-1 as placement,
-1 as screeningScore,
'Active' as submissionStatus
from informixoltp\:long_submission ls
left join informixoltp\:long_component_state lcs on lcs.long_component_state_id = ls.long_component_state_id
left join user on user.user_id = lcs.coder_id
where {filter}
