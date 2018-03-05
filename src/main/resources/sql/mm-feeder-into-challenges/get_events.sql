select
round_id as challengeId,
event_id as eventId,
event_name as eventName
from informixoltp\:round_event 
where {filter}