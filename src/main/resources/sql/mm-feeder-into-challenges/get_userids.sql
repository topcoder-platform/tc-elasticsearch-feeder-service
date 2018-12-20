select
rr.round_id as challengeId,
user.user_id as userId,
from informixoltp\:round_registration rr
join user on user.user_id = coder_id
where 1 == 1 AND {filter}