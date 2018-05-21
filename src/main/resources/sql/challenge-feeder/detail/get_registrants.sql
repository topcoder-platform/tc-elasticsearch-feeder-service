SELECT
    r.project_id AS challengeId,
    registrationDate.value AS registrationDateStr,
    reliability.value::DECIMAL AS reliability,
    handle.value AS handle,
    decode(rating.value, 'N/A', '0', rating.value)::DECIMAL rating,
    (SELECT MAX(u.create_date) FROM upload u WHERE u.project_id = r.project_id
        AND u.resource_id = r.resource_id AND u.upload_type_id = 1
        AND u.upload_status_id = 1) AS submissionDate
  FROM resource r
    INNER JOIN resource_info registrationDate ON registrationDate.resource_id = r.resource_id AND registrationDate.resource_info_type_id = 6
    INNER JOIN resource_info handle ON handle.resource_id = r.resource_id AND handle.resource_info_type_id = 2
    LEFT JOIN resource_info reliability ON reliability.resource_id = r.resource_id AND reliability.resource_info_type_id = 5
    LEFT JOIN resource_info rating ON rating.resource_id = r.resource_id AND rating.resource_info_type_id = 4
  WHERE
   r.resource_role_id = 1 AND {filter}