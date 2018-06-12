select 
user.handle as submitter,
lcr.placed as rank,
lcr.round_id as challengeId,
lcr.system_point_total as points,
(select decode(submit_time, null, null, dbinfo("utc_to_datetime", submit_time/1000))
 from (select FIRST 1 ls.submit_time as submit_time from informixoltp\:long_component_state lcs, informixoltp\:long_submission ls
        where lcs.long_component_state_id = ls.long_component_state_id
        and lcs.round_id = lcr.round_id and lcs.coder_id = lcr.coder_id and ls.example = 0 ORDER BY ls.submission_number DESC)
 ) as submissionDate,
 (select decode(submissionId, null, null, submissionId)
 from (select FIRST 1 lcs.long_component_state_id as submissionId from informixoltp\:long_component_state lcs, informixoltp\:long_submission ls
        where lcs.long_component_state_id = ls.long_component_state_id
        and lcs.round_id = lcr.round_id and lcs.coder_id = lcr.coder_id and ls.example = 0 ORDER BY ls.submission_number DESC)
 ) as submissionId
from informixoltp\:long_comp_result lcr
   inner join user on user.user_id = lcr.coder_id
where lcr.placed is not null
and {filter}
