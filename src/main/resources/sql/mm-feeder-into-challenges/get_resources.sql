select 
rr.round_id as challengeId,
rr.coder_id as userId,
rr.timestamp as registrationDate,
user.handle as handle,
'Submitter' as role,
ar.rating,
(select count(*) from informixoltp\:long_component_state lcs, informixoltp\:long_submission ls
    where lcs.long_component_state_id = ls.long_component_state_id
    and lcs.round_id = rr.round_id and lcs.coder_id = rr.coder_id and ls.example = 0) as submissionCount
from informixoltp\:round_registration rr
left join user on user.user_id = coder_id
left join informixoltp\:algo_rating ar on ar.coder_id = rr.coder_id and ar.algo_rating_type_id=3
where {filter}