SELECT
      prize.prize_id as prizeId,
      prize.place as place,
      prize.prize_amount as amount,
      prize.number_of_submissions as numberOfSubmissions, 
      prize.project_id as challengeId
   FROM
       prize AS prize
   INNER JOIN project AS project ON prize.project_id = project.project_id
   WHERE prize.prize_type_id = 14 and {filter}