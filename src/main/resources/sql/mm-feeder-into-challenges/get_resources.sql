select 
rr.round_id as challengeId,
rr.coder_id as userId,
to_char(rr.timestamp, '%Y-%m-%dT%H:%M:%S%F3T') as registrationDate,
user.handle as handle,
'Submitter' as role,
ar.rating
from informixoltp\:round_registration rr
left join user on user.user_id = coder_id
left join informixoltp\:algo_rating ar on ar.coder_id = rr.coder_id and ar.algo_rating_type_id=3
where {filter}