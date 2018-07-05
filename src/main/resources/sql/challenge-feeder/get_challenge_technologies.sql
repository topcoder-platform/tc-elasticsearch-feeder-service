select tt.technology_name as name, pi1.project_id as challengeId
from comp_technology ct
inner join technology_types tt on ct.technology_type_id = tt.technology_type_id
inner join project_info pi1 on pi1.value = ct.comp_vers_id and pi1.project_info_type_id = 1
where {filter}
