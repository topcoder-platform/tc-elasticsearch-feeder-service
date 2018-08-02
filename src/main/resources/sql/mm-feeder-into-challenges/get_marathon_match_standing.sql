SELECT rr.round_id as challengeId
     , rr.coder_id as userId
     , rr.point_total as provisionalScore
     , rr.placed as finalRank
     , rr.system_point_total as finalScore
  FROM informixoltp\:long_comp_result rr
 WHERE rr.attended = 'Y'
   AND {filter}
ORDER BY rr.point_total desc