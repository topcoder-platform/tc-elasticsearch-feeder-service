SELECT DISTINCT
    round.round_id id,
    round_registrations.coder_id userId,
    round_registrations.coder_id || (SELECT CASE
                WHEN q.count > 0 THEN 'T'
                ELSE 'F'
            END
     FROM
       (SELECT count(*) AS count
        FROM INFORMIXOLTP\:room_result AS room_result
        WHERE room_result.round_id = round.round_id
          AND room_result.coder_id = round_registrations.coder_id AND room_result.rated_flag=1) AS q) isRatedForSRM
	FROM
	   INFORMIXOLTP\:round AS round
	 LEFT JOIN
	    INFORMIXOLTP\:round_registration AS round_registrations
		ON round_registrations.round_id = round.round_id
	  LEFT JOIN
         TCS_CATALOG\:user AS user_table
         ON user_table.user_id = round_registrations.coder_id
    WHERE round.round_type_id = 1 AND {filter}