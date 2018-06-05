select
rr.round_id as challengeId,
rr.timestamp as registrationDate,
CASE
  WHEN ls.submit_time IS NOT NULL THEN extend(dbinfo("UTC_TO_DATETIME",ls.submit_time/1000), year to fraction)
  ELSE NULL
END as submissionDate,
al.rating as rating,
u.handle as handle
from informixoltp\:round_registration rr
left join user u on u.user_id = rr.coder_id
left join informixoltp\:long_component_state lcs on lcs.round_id = rr.round_id
left join informixoltp\:long_submission ls on ls.long_component_state_id = lcs.long_component_state_id
left join informixoltp\:algo_rating al on al.coder_id = u.user_id
where
{filter}
