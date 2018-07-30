SELECT lcr.coder_id AS userId
     , lcr.round_id AS challengeId
     , lcr.point_total AS provisionalScore
     , lcr.placed AS finalRank
     , lcr.system_point_total as finalScore
     , lcr.provisional_placed as provisionalRank
  FROM long_comp_result lcr
 WHERE {filter}
