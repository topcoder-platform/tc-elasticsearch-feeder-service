SELECT
   round.round_id AS id,
   contest.NAME AS NAME,
   CASE
      WHEN
         (
            (registration_segment.start_time < sysdate)
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
         (system_test_segment.end_time < sysdate)
      THEN
         'PAST'
   END AS status,
   contest.start_date AS startDate,
   contest.end_date AS endDate,
   'DATA SCIENCE' AS track,
   'SRM' AS subtrack,
   round.forum_id AS forumId,
   round.round_id AS roundId,
   registration_segment.start_time AS registrationStartAt,
   registration_segment.end_time AS registrationEndAt,
   coding_segment.start_time AS codingStartAt,
   coding_segment.end_time AS codingEndAt,
   system_test_segment.start_time AS systemTestStartAt,
   system_test_segment.end_time AS systemTestEndAt
FROM
   INFORMIXOLTP\:round AS round
   INNER JOIN
      INFORMIXOLTP\:contest AS contest
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
   WHERE round.round_type_id = 1 AND {filter}
