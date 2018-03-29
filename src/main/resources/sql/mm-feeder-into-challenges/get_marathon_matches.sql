select 
r.round_id as id,
c.name || ' - ' || r.name as name,
'DATA SCIENCE' as track, 
'Marathon Match' as subTrack,
'mmatches' as type,
r.tc_direct_project_id as directProjectId,
tcdirect.name as directProjectName,
comp.component_text as marathonMatchDetailRequirements,
'system' as createdBy,
r.forum_id as forumId,
CASE
     WHEN (r.status = 'P') THEN 'Completed'
     WHEN (r.status = 'A') THEN 'Active'
     WHEN (r.status = 'F') THEN 'Draft'
     WHEN (r.status = 'X') THEN 'Deleted'
     WHEN (r.status = 'T') THEN 'Draft'
     ELSE 'Open'
 END as status,
rs_reg.start_time as registrationStartDate,
rs_reg.end_time as registrationEndDate,
rs_sub.end_time as submissionEndDate,
(select count(coder_id) from informixoltp\:round_registration where round_id = r.round_id) as numRegistrants,
(select sum(submission_number) from informixoltp\:long_component_state where round_id = r.round_id and status_id in(130, 131, 140, 150, 160)) as numSubmissions,
(select sum(amount) from informixoltp\:round_prize rp where rp.round_id = r.round_id) as totalPrize,
(select pi.project_id from project_info pi where pi.project_info_type_id = 56 and pi.value::decimal = r.round_id) is null as isLegacy,
(select pi87.value from project_info pi87 where pi87.project_id = (select pi.project_id from project_info pi where pi.project_info_type_id = 56 and pi.value::decimal = r.round_id)
    and pi87.project_info_type_id = 87) = 'Banner' as isBanner
from informixoltp\:round r
left join informixoltp\:contest c on r.contest_id = c.contest_id
left join corporate_oltp\:tc_direct_project AS tcdirect ON r.tc_direct_project_id = tcdirect.project_id
left join informixoltp\:round_component as rc on rc.round_id = r.round_id
left join informixoltp\:component comp on comp.component_id = rc.component_id
left join informixoltp\:round_segment rs_reg on rs_reg.round_id = r.round_id and rs_reg.segment_id = 1
left join informixoltp\:round_segment rs_sub on rs_sub.round_id = r.round_id and rs_sub.segment_id = 2
where r.round_type_id = 13 and {filter} 