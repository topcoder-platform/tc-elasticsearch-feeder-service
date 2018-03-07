SELECT DISTINCT round.round_id AS id, registration_segment.start_time
FROM
   informixoltp\:contest AS contest
   INNER JOIN informixoltp\:round AS round
      ON round.contest_id = contest.contest_id
   LEFT JOIN
      INFORMIXOLTP\:round_segment AS registration_segment
      ON registration_segment.round_id = round.round_id
      AND registration_segment.segment_id = 1
WHERE
   round.round_type_id = 13
   AND (
            (1 = :lastRunTimestamp)
            OR 
            (registration_segment.start_time > :initialDate)
        )
ORDER BY registration_segment.start_time  DESC
