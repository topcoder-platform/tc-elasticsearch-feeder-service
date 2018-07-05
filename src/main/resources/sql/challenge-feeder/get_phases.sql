SELECT
     project_phase.project_id AS challengeId,
     project_phase.project_phase_id AS phaseId,
     phase_type_lu.description AS type,
     phase_status_lu.description AS status,
     project_phase.create_user AS createdBy,
     project_phase.create_date AS createdAt,
     project_phase.modify_user AS updatedBy,
     project_phase.modify_date AS updatedAt,
     project_phase.fixed_start_time AS fixedStartTime,
     project_phase.scheduled_start_time AS scheduledStartTime,
     project_phase.actual_start_time AS actualStartTime,
     project_phase.actual_end_time AS actualEndTime,
     project_phase.scheduled_end_time AS scheduledEndTime,
     project_phase.duration AS duration
 FROM project_phase
    INNER JOIN project p ON p.project_id = project_phase.project_id
    INNER JOIN phase_type_lu ON phase_type_lu.phase_type_id = project_phase.phase_type_id
    INNER JOIN phase_status_lu ON phase_status_lu.phase_status_id = project_phase.phase_status_id
 WHERE 
    {filter}