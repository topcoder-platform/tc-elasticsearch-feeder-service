select
rr.round_id as challengeId,
rr.timestamp as registrationDate,
user.handle as handle,
ar.rating as rating,
(select decode(submit_time, null, null, dbinfo("utc_to_datetime", submit_time/1000))
 from (select min(ls.submit_time) as submit_time from informixoltp\:long_component_state lcs, informixoltp\:long_submission ls
        where lcs.long_component_state_id = ls.long_component_state_id
        and lcs.round_id = rr.round_id and lcs.coder_id = rr.coder_id and ls.example = 0)
 ) as submissionDate
from informixoltp\:round_registration rr
join user on user.user_id = coder_id
left join informixoltp\:algo_rating ar on ar.coder_id = rr.coder_id and ar.algo_rating_type_id=3
where
{filter}
