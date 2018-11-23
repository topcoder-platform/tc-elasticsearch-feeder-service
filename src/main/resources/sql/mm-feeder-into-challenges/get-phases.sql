select
round_id as challengeId,
rs.segment_id as phaseId,
CASE
  WHEN
     (
        ( rs.start_time < sysdate)
        AND
        (rs.end_time > sysdate)
     )
  THEN
     'Open'
  WHEN
     (rs.start_time > sysdate)
  THEN
     'Scheduled'
  WHEN
     (rs.end_time < sysdate )
  THEN
     'Closed'
 END AS status,
s.segment_desc as type,
start_time as fixedStartTime,
start_time as scheduledStartTime,
start_time as actualStartTime,
end_time as actualEndTime,
end_time as scheduledEndTime
from informixoltp\:round_segment rs
left join informixoltp\:segment s on rs.segment_id = s.segment_id
where s.segment_id in (1,2,5) and {filter}