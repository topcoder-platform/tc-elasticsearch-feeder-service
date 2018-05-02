SELECT 
         p.create_user AS createdby,
         p.create_date AS createdat,
         p.modify_user AS updatedby,
         p.modify_date AS updatedat,
         p.project_id _id,
         p.project_id AS id,
         CASE WHEN rel_flag.value = 'true' THEN rel_cost.value::decimal ELSE NULL END AS reliabilityBonus,
         CASE WHEN dr_elligible.value = 'On' THEN dr_points.value::decimal ELSE NULL END AS drPoints,
         env.value AS environment,
         code_repo.value AS codeRepo,
/*         pspec.detailed_requirements_text AS softwareDetailRequirements,
         pspec.final_submission_guidelines_text AS softwareFinalSubmissionGuidelines,
         pspec.private_description_text AS copilotDetailRequirements,
         pss.contest_description_text AS studioDetailRequirements,
         pss.contest_introduction AS introduction,
         pss.round_one_introduction AS round1Introduction,
         pss.round_two_introduction AS round2Introduction,
         pmm_spec.match_details AS marathonMatchDetailRequirements,
         pmm_spec.match_rules AS marathonMatchRules,
*/
         pn.value AS name,
         CASE
             WHEN (ptl.description = 'Application') THEN 'DEVELOP'
             WHEN (ptl.description = 'Component') THEN 'DEVELOP'
             WHEN (ptl.description = 'Studio') THEN 'DESIGN'
             ELSE 'GENERIC'
         END AS track,
         pcl.description AS subTrack,
         pstatus.NAME AS status,
         Technology_list(pi1.value) AS technologies,
         Platform_list(p.project_id) AS platforms,
         Nvl(pp1.actual_start_time, pp1.scheduled_start_time) AS registrationStartDate,
         Nvl(pp1.actual_end_time, pp1.scheduled_end_time) AS registrationEndDate,
         Nvl(pp2.actual_end_time, pp2.scheduled_end_time) AS submissionEndDate,
         review_type_info.value AS reviewType,
         forum_id_info.value AS forumId,
    (SELECT Count(*)
     FROM submission s1
     INNER JOIN upload u1 ON s1.upload_id = u1.upload_id
     WHERE u1.project_id = p.project_id
       AND s1.submission_type_id = 1
       AND s1.submission_status_id <> 5) AS numSubmissions,
    (SELECT Count(*)
     FROM RESOURCE r
     WHERE r.project_id = p.project_id
       AND r.resource_role_id = 1) AS numregistrants,
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
         p.tc_direct_project_id AS directProjectId,
         tcdirect.NAME AS directProjectName,
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
    (pi87.value = 'Banner') AS isBanner,
    pi56.value::Decimal As roundId
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
  INNER JOIN project_info pi1 ON pi1.project_id = p.project_id
  AND pi1.project_info_type_id = 1
  LEFT JOIN TCS_CATALOG\:project_info AS forum_id_info ON forum_id_info.project_id = p.project_id
  AND forum_id_info.project_info_type_id = 4
  LEFT JOIN TCS_CATALOG\:project_info AS review_type_info ON review_type_info.project_id = p.project_id
  AND review_type_info.project_info_type_id = 79
  LEFT JOIN project_phase pp15 ON pp15.project_id = p.project_id
  AND pp15.phase_type_id = 15
  LEFT JOIN project_info pidr ON pidr.project_id = p.project_id
  AND pidr.project_info_type_id = 26
  LEFT JOIN CORPORATE_OLTP\:tc_direct_project AS tcdirect ON p.tc_direct_project_id = tcdirect.project_id
  LEFT JOIN project_info pvs ON pvs.project_id = p.project_id
  AND pvs.project_info_type_id = 53
  LEFT JOIN project_info rel_flag ON rel_flag.project_id = p.project_id AND rel_flag.project_info_type_id = 45
  LEFT JOIN project_info rel_cost ON rel_cost.project_id = p.project_id AND rel_cost.project_info_type_id = 38
  LEFT JOIN project_info dr_points ON dr_points.project_id = p.project_id AND dr_points.project_info_type_id = 30
  LEFT JOIN project_info dr_elligible ON dr_elligible.project_id = p.project_id AND dr_elligible.project_info_type_id = 26
  LEFT JOIN project_info env ON env.project_id = p.project_id AND env.project_info_type_id = 84
  LEFT JOIN project_info code_repo ON code_repo.project_id = p.project_id AND code_repo.project_info_type_id = 85
/*  LEFT JOIN project_spec pspec ON pspec.project_id = p.project_id AND pspec.version = (select MAX(project_spec.version) from project_spec where project_spec.project_id = p.project_id)
  LEFT JOIN project_studio_specification pss ON pss.project_studio_spec_id = p.project_studio_spec_id
  LEFT JOIN project_mm_specification pmm_spec ON pmm_spec.project_mm_spec_id = p.project_mm_spec_id
*/
  LEFT JOIN project_info pi87 ON pi87.project_id = p.project_id AND pi87.project_info_type_id = 87
  LEFT JOIN project_info pi56 ON pi56.project_id = p.project_id AND pi56.project_info_type_id = 56
  WHERE pcl.project_category_id NOT IN (27) 
  AND {filter}