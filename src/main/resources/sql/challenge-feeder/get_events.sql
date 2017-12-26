SELECT
     x.project_id AS challengeId,
     e.event_id AS eventId,
     e.event_short_desc AS eventName
   FROM
     common_oltp\:event e,
     contest_project_xref  x,
     contest  c
   WHERE e.event_id = c.event_id and c.contest_id = x.contest_id
   AND {filter}