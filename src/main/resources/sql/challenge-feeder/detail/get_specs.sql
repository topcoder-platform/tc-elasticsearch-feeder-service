SELECT
    p.project_id AS id,
    pspec.detailed_requirements_text AS softwareDetailRequirements,
    pspec.final_submission_guidelines_text AS finalSubmissionGuidelines,
    pss.contest_description_text AS studioDetailRequirements,
    pss.contest_introduction AS introduction,
    pss.round_one_introduction AS round1Introduction,
    pss.round_two_introduction AS round2Introduction,
    pcl.project_type_id AS type
FROM
    project p
    INNER JOIN project_category_lu pcl ON pcl.project_category_id = p.project_category_id
    LEFT JOIN project_spec pspec ON pspec.project_id = p.project_id
        AND pspec.version = (select MAX(project_spec.version) from project_spec where project_spec.project_id = p.project_id)
    LEFT JOIN project_studio_specification pss ON pss.project_studio_spec_id = p.project_studio_spec_id
    LEFT JOIN project_mm_specification pmm_spec ON pmm_spec.project_mm_spec_id = p.project_mm_spec_id
WHERE {filter}