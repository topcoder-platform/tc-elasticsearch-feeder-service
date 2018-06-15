select
r.round_id as id,
r.round_id as challengeId,
'DATA_SCIENCE' as track,
'Marathon Match' as subTrack,
c.component_text as detailRequirements
from informixoltp\:round r
join informixoltp\:round_component rc on r.round_id = rc.round_id
join informixoltp\:component c on rc.component_id = c.component_id
left join tcs_catalog\:project_info pi on pi.project_info_type_id = 56 and pi.value::decimal = r.round_id
where pi.project_id is null and r.round_type_id = 13 and {filter}