select 
r.round_id as id,
r.round_id as challengeId
from informixoltp\:round r
left join project_info pi on pi.project_info_type_id = 56 and pi.value::decimal = r.round_id
where pi.project_id is null and r.round_type_id = 13 and {filter} 