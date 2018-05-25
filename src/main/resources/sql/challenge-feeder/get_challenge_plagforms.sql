select ppl.name as name, pp.project_id as challengeId
from project_platform_lu ppl
inner join project_platform pp
on ppl.project_platform_id = pp.project_platform_id
where {filter}
order by ppl.name