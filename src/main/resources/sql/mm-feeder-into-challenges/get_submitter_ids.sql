select
round_id as challengeId,
coder_id as submitterId
from informixoltp\:long_component_state
where status_id in(130, 131, 140, 150, 160)
AND {filter}