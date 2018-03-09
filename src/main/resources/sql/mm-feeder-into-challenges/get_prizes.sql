select
place,
amount,
round_id as challengeId
from informixoltp\:round_prize
where {filter}