select tt.technology_name as name, p.project_id as challengeId
from comp_technology ct
left join technology_types tt on ct.technology_type_id = tt.technology_type_id
left join project_info pi1 on pi1.value = ct.comp_vers_id and pi1.project_info_type_id = 1
left join project p on p.project_id = pi1.project_id  
where {filter}
order by tt.technology_name
