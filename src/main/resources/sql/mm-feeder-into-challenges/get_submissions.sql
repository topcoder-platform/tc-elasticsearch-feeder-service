select
lcs.round_id as challengeId,
'Final' as submissionType,
user.handle as submitter,
lcs.coder_id as submitterId,
ls.submission_points as finalScore,
ls.submission_points as initialScore,
extend(dbinfo("UTC_TO_DATETIME",ls.submit_time/1000), year to fraction) as submittedAt,
-1 as placement,
-1 as screeningScore,
'Active' as status
from informixoltp\:long_submission ls
left join informixoltp\:long_component_state lcs on lcs.long_component_state_id = ls.long_component_state_id
left join user on user.user_id = lcs.coder_id
where {filter}