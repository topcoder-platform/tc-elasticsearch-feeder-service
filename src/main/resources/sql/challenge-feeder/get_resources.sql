SELECT
      r.project_id AS challengeId,
      r.resource_id AS resourceId,
      r.user_id AS userId,
      rrl.name AS role,
      r.project_phase_id AS projectPhaseId,
      ri_handle.value AS handle,
      ri_reg_date.value AS registrationDate,
      decode(ri_rating.value, 'N/A', '0', ri_rating.value)::int AS rating,
      ri_reliability.value::int AS reliability
  FROM resource r
    INNER JOIN resource_role_lu rrl ON r.resource_role_id = rrl.resource_role_id
    INNER JOIN resource_info ri_handle ON ri_handle.resource_id = r.resource_id and ri_handle.resource_info_type_id = 2
    INNER JOIN resource_info ri_reg_date ON ri_reg_date.resource_id = r.resource_id and ri_reg_date.resource_info_type_id = 6
    LEFT  JOIN resource_info ri_rating ON ri_rating.resource_id = r.resource_id and ri_rating.resource_info_type_id = 4
    LEFT  JOIN resource_info ri_reliability ON ri_reliability.resource_id = r.resource_id and ri_reliability.resource_info_type_id = 5
  WHERE {filter} 