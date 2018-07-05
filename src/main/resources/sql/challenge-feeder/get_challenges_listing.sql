SELECT 
         p.create_user AS createdby,
         p.create_date AS createdat,
         p.modify_user AS updatedby,
         p.modify_date AS updatedat,
         p.project_id _id,
         p.project_id AS id,
         p.project_id AS challengeId,
         CASE WHEN rel_flag.value = 'true' THEN rel_cost.value::decimal ELSE NULL END AS reliabilityBonus,
         CASE WHEN dr_elligible.value = 'On' THEN dr_points.value::decimal ELSE NULL END AS drPoints,
         env.value AS environment,
         code_repo.value AS codeRepo,
         pn.value AS challengeTitle,
         CASE
             WHEN (ptl.description = 'Application') THEN 'DEVELOP'
             WHEN (ptl.description = 'Component') THEN 'DEVELOP'
             WHEN (ptl.description = 'Studio') THEN 'DESIGN'
             ELSE 'GENERIC'
         END AS track,
         pcl.description AS subTrack,
         pstatus.NAME AS status,
         Nvl(pp1.actual_start_time, pp1.scheduled_start_time) AS registrationStartDate,
         Nvl(pp1.actual_end_time, pp1.scheduled_end_time) AS registrationEndDate,
         Nvl(pp2.actual_end_time, pp2.scheduled_end_time) AS submissionEndDate,
         review_type_info.value AS reviewType,
         forum_id_info.value AS forumId,
    (SELECT Count(unique s1.create_user)
     FROM submission s1
     INNER JOIN upload u1 ON s1.upload_id = u1.upload_id
     WHERE u1.project_id = p.project_id
       AND s1.submission_type_id = 1
       AND s1.submission_status_id <> 5) AS numberOfSubmissions,
    (SELECT Count(*)
     FROM RESOURCE r
     INNER JOIN resource_info ri1 ON ri1.resource_info_type_id = 1 and r.resource_id = ri1.resource_id
     INNER JOIN user u ON ri1.value = u.user_id
     WHERE r.project_id = p.project_id
       AND r.resource_role_id = 1) AS numberOfRegistrants,
         Nvl(pp15.actual_end_time, pp15.scheduled_end_time) AS checkpointSubmissionEndDate,
         Nvl(
               (SELECT Sum(pr.number_of_submissions)
                FROM prize pr
                WHERE pr.project_id = p.project_id
                  AND pr.prize_type_id = 14), 0) AS numberOfCheckpointPrizes,
    (SELECT Sum(prize_amount*number_of_submissions)
     FROM prize pr
     WHERE pr.project_id = p.project_id
       AND pr.prize_type_id = 14) AS totalCheckPointPrize,
         Nvl(
               (SELECT sum(prize_amount)
                FROM prize pr
                WHERE pr.project_id = p.project_id
                  AND pr.prize_type_id = 15),0) AS totalPrize,
         Nvl(
               (SELECT Cast('t' AS BOOLEAN)
                FROM contest_eligibility
                WHERE contest_id = p.project_id
                  AND contest_id NOT IN
                    (SELECT contest_id
                     FROM contest_eligibility
                     GROUP BY contest_id
                     HAVING Count(*) > 1)), Cast('f' AS BOOLEAN)) AS isPrivate,
         p.tc_direct_project_id AS projectId,
         tcdirect.NAME AS projectName,
         pvs.value As submissionViewable,
    (SELECT CASE
                WHEN t.count > 0 THEN Cast('t' AS BOOLEAN)
                ELSE Cast('f' AS BOOLEAN)
            END
     FROM
       (SELECT count(*) AS COUNT
        FROM project_info pti
        WHERE pti.project_id = p.project_id
          AND pti.project_info_type_id = 82
          AND pti.value = '1') AS t) AS isTask,
  pi70.value AS cmcTaskId, 
  pc3.parameter AS screeningScorecardId, 
  pc4.parameter AS reviewScorecardId,
  pi52.value AS allowStockArt,
  CASE WHEN pi51.value = '' THEN null ELSE pi51.value END AS submissionLimit,
  p.tc_direct_project_id AS projectId,
  (SELECT NVL(NVL(ppd.actual_start_time, psd.actual_start_time), ppd.scheduled_start_time)
          FROM project proj
             , OUTER project_phase psd
             , OUTER project_phase ppd
         WHERE psd.project_id = proj.project_id
           AND psd.phase_type_id = 2
           AND ppd.project_id = proj.project_id
           AND proj.project_id = p.project_id
           AND ppd.phase_type_id = 1) AS postingDate,
  (SELECT pr.prize_amount FROM prize pr WHERE pr.project_id = p.project_id AND pr.prize_type_id = 14 AND pr.place = 1)::DECIMAL(10,2) AS topCheckpointPrize,
  NVL(NVL(pp6.actual_end_time, pp6.scheduled_end_time), NVL(pp4.actual_end_time, pp4.scheduled_end_time)) AS appealsEndDate, 
  NVL(pp9.actual_end_time, pp9.scheduled_end_time) AS finalFixEndDate,
  pi78.value AS forumType
  FROM project p
  INNER JOIN project_status_lu pstatus ON pstatus.project_status_id = p.project_status_id
  INNER JOIN project_category_lu pcl ON pcl.project_category_id = p.project_category_id
  INNER JOIN project_type_lu ptl ON ptl.project_type_id = pcl.project_type_id
  INNER JOIN project_phase pp1 ON pp1.project_id = p.project_id
  AND pp1.phase_type_id = 1
  INNER JOIN project_phase pp2 ON pp2.project_id = p.project_id
  AND pp2.phase_type_id = 2
  INNER JOIN project_info pn ON pn.project_id = p.project_id
  AND pn.project_info_type_id = 6
  LEFT JOIN project_info AS forum_id_info ON forum_id_info.project_id = p.project_id
  AND forum_id_info.project_info_type_id = 4
  LEFT JOIN project_info AS review_type_info ON review_type_info.project_id = p.project_id
  AND review_type_info.project_info_type_id = 79
  LEFT JOIN project_phase pp15 ON pp15.project_id = p.project_id
  AND pp15.phase_type_id = 15
  LEFT JOIN project_info pidr ON pidr.project_id = p.project_id
  AND pidr.project_info_type_id = 26
  LEFT JOIN tc_direct_project AS tcdirect ON p.tc_direct_project_id = tcdirect.project_id
  LEFT JOIN project_info pvs ON pvs.project_id = p.project_id
  AND pvs.project_info_type_id = 53
  LEFT JOIN project_info rel_flag ON rel_flag.project_id = p.project_id AND rel_flag.project_info_type_id = 45
  LEFT JOIN project_info rel_cost ON rel_cost.project_id = p.project_id AND rel_cost.project_info_type_id = 38
  LEFT JOIN project_info dr_points ON dr_points.project_id = p.project_id AND dr_points.project_info_type_id = 30
  LEFT JOIN project_info dr_elligible ON dr_elligible.project_id = p.project_id AND dr_elligible.project_info_type_id = 26
  LEFT JOIN project_info env ON env.project_id = p.project_id AND env.project_info_type_id = 84
  LEFT JOIN project_info code_repo ON code_repo.project_id = p.project_id AND code_repo.project_info_type_id = 85
  LEFT JOIN project_info pi70 ON pi70.project_id = p.project_id AND pi70.project_info_type_id = 70 
  LEFT JOIN project_phase pp4 ON pp4.project_id = p.project_id AND (pp4.phase_type_id = 4 OR (pp4.phase_type_id = 18 AND p.project_category_id = 38)) AND pp4.project_phase_id = (SELECT MAX(project_phase_id) FROM project_phase WHERE project_id = p.project_id AND phase_type_id IN (4,18))
  LEFT JOIN phase_criteria pc4 ON pp4.project_phase_id = pc4.project_phase_id AND pc4.phase_criteria_type_id = 1 AND pc4.phase_criteria_type_id = 1 
  LEFT JOIN project_phase pp3 ON pp3.project_id = p.project_id AND pp3.phase_type_id = 3  
  LEFT JOIN phase_criteria pc3 ON pp3.project_phase_id = pc3.project_phase_id AND pc3.phase_criteria_type_id = 1
  LEFT JOIN project_info pi51 ON pi51.project_info_type_id = 51 AND pi51.project_id = p.project_id
  LEFT JOIN project_info pi52 ON pi52.project_info_type_id = 52 AND pi52.project_id = p.project_id
  LEFT JOIN project_info pi78 ON pi78.project_info_type_id = 78 AND pi78.project_id = p.project_id
  LEFT JOIN project_phase pp6 ON pp6.project_id = p.project_id AND pp6.phase_type_id = 6  
  LEFT JOIN project_phase pp9 ON pp9.project_id = p.project_id AND pp9.phase_type_id = 9 AND pp9.project_phase_id = (SELECT MAX(project_phase_id) FROM project_phase WHERE project_id = p.project_id AND phase_type_id = 9)
  WHERE pcl.project_category_id NOT IN (27)
  AND {filter}
  