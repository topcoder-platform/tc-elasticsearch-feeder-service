select
round_id as challengeId,
rs.segment_id as phaseId,
CASE
     WHEN (rs.status = 'P') THEN 'Closed'
     WHEN (rs.status = 'A') THEN 'Open'
     WHEN (rs.status = 'F') THEN 'Scheduled'
     ELSE 'Scheduled'
 END as phaseStatus,
s.segment_desc as phaseType,
start_time as fixedStartTime,
start_time as scheduledStartTime,
start_time as actualStartTime,
end_time as actualEndTime,
end_time as scheduledEndTime
from informixoltp\:round_segment rs
left join informixoltp\:segment s on rs.segment_id = s.segment_id
where {filter}