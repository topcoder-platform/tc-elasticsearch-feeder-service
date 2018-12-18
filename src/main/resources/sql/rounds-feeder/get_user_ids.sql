SELECT DISTINCT
    round.round_id AS id,
    round_registrations.coder_id AS userId,
    round_registrations.coder_id || (SELECT CASE
                WHEN q.count > 0 THEN 'T'
                ELSE 'F'
            END
     FROM
       (SELECT count(*) AS count
        FROM INFORMIXOLTP\:room_result AS room_result
        WHERE room_result.round_id = round.round_id
          AND room_result.coder_id = round_registrations.coder_id AND room_result.rated_flag=1) AS q) AS isRated
	FROM
	 INFORMIXOLTP\:round AS round
	 INNER JOIN
		INFORMIXOLTP\:contest AS contest
		ON round.contest_id = contest.contest_id
	 LEFT JOIN
	    INFORMIXOLTP\:round_registration AS round_registrations
		ON round_registrations.round_id = round.round_id
	  LEFT JOIN
         TCS_CATALOG\:user AS user_table
        ON user_table.user_id = round_registrations.coder_id
    WHERE (round.round_type_id = 1 OR round.round_type_id = 2) AND {filter}

UNION

SELECT DISTINCT
   round.round_id AS id,
   round_registration.coder_id AS userId,
   round_registration.coder_id || (SELECT CASE
                 WHEN q.count > 0 THEN 'T'
                 ELSE 'F'
             END
      FROM
        (SELECT count(*) AS count
         FROM INFORMIXOLTP\:long_comp_result AS long_comp_result
         WHERE long_comp_result.round_id = round.round_id
           AND long_comp_result.coder_id = round_registration.coder_id and long_comp_result.rated_ind = 1) AS q) AS isRated
FROM
   informixoltp\:round AS round
   LEFT JOIN
      informixoltp\:round_registration AS round_registration
      ON round_registration.round_id = round.round_id
   LEFT JOIN
      tcs_catalog\:user AS user_table
      ON user_table.user_id = round_registration.coder_id
WHERE
   (round.round_type_id = 13 OR round.round_type_id = 19) AND {filter}