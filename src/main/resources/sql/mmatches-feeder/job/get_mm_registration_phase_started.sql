SELECT DISTINCT round.round_id AS id, registration_segment.start_time
FROM
   informixoltp\:contest AS contest
   INNER JOIN informixoltp\:round AS round
      ON round.contest_id = contest.contest_id
   LEFT JOIN
      INFORMIXOLTP\:round_segment AS registration_segment
      ON registration_segment.round_id = round.round_id
      AND registration_segment.segment_id = 1
   LEFT JOIN
      INFORMIXOLTP\:round_segment AS coding_segment
      ON coding_segment.round_id = round.round_id
      AND coding_segment.segment_id = 2
   LEFT JOIN
      INFORMIXOLTP\:round_segment AS system_test_segment
      ON system_test_segment.round_id = round.round_id
      AND system_test_segment.segment_id = 5
   LEFT JOIN
      INFORMIXOLTP\:round_component AS round_component
      ON round_component.round_id = round.round_id
   LEFT JOIN
      INFORMIXOLTP\:component AS component
      ON component.component_id = round_component.component_id
   LEFT JOIN
      INFORMIXOLTP\:problem AS problem
      ON problem.problem_id = component.problem_id
WHERE
   round.round_type_id = 13
   AND (
            (1 = :lastRunTimestamp)
            OR 
            (registration_segment.start_time > :initialDate)
        )
ORDER BY registration_segment.start_time  DESC
