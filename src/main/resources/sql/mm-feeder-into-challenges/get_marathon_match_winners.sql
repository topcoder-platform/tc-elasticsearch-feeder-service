select 
ls.long_component_state_id as submissionId,
user.handle as submitter,
extend(dbinfo("UTC_TO_DATETIME",ls.submit_time/1000), year to fraction) as submissionTime,
lcr.placed as rank,
ls.submission_points as points,
lcs.round_id as challengeId
from informixoltp\:long_submission ls
left join informixoltp\:long_component_state lcs on lcs.long_component_state_id = ls.long_component_state_id
left join user on user.user_id = lcs.coder_id
left join informixoltp\:long_comp_result lcr on lcr.round_id = lcs.round_id
where lcr.placed is not null
and {filter}