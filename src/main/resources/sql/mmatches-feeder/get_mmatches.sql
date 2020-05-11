SELECT
   round.round_id AS id,
   contest.name AS name,
   CASE
      WHEN
         (
            ( registration_segment.start_time < sysdate)
            AND
            (system_test_segment.end_time > sysdate)
         )
      THEN
         'ACTIVE'
      WHEN
         (registration_segment.start_time > sysdate)
      THEN
         'FUTURE'
      WHEN
         (system_test_segment.end_time < sysdate )
      THEN
         'PAST'
   END AS status,
   contest.start_date AS startDate,
   contest.end_date AS endDate,
   'DATA SCIENCE' AS track,
   'MARATHON_MATCH' AS subTrack,
   round.forum_id AS forumId,
   round.round_id AS roundId,
   registration_segment.start_time AS registrationStartAt,
   registration_segment.end_time AS registrationEndAt,
   coding_segment.start_time AS codingStartAt,
   coding_segment.end_time AS codingEndAt,
   system_test_segment.start_time AS systemTestStartAt,
   system_test_segment.end_time AS systemTestEndAt,
   problem.problem_id AS problemId,
   contest.contest_id AS contestId,
   component.component_id AS componentId
FROM
   informixoltp\:round AS round
   INNER JOIN
      informixoltp\:contest AS contest
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
   round.round_type_id in (13, 19)
   AND {filter}
