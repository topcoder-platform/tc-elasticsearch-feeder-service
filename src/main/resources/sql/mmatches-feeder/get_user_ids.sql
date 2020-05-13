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
           AND long_comp_result.coder_id = round_registration.coder_id and long_comp_result.rated_ind = 1) AS q) AS isRatedForMM
FROM
   informixoltp\:round AS round
   LEFT JOIN
      informixoltp\:round_registration AS round_registration
      ON round_registration.round_id = round.round_id
   LEFT JOIN
      tcs_catalog\:user AS user_table
      ON user_table.user_id = round_registration.coder_id
WHERE
   round.round_type_id in (13, 15, 16, 19, 22, 23) AND {filter}