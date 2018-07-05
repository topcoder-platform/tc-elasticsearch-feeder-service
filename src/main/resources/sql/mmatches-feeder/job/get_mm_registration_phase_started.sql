SELECT DISTINCT round.round_id AS id, registration_segment.start_time
FROM
   informixoltp\:round AS round
   LEFT JOIN
      informixoltp\:round_segment AS registration_segment
      ON registration_segment.round_id = round.round_id
      AND registration_segment.segment_id = 1
   LEFT JOIN tcs_catalog\:project_info pi on pi.project_info_type_id = 56 and pi.value::decimal = round.round_id
WHERE
   pi.project_id is null AND round.round_type_id = 13
   AND (
            (1 = :lastRunTimestamp)
            OR 
            (registration_segment.start_time > :initialDate)
        )
ORDER BY registration_segment.start_time  DESC
