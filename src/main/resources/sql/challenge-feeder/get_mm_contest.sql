select rr.round_id as roundId,
    rr.contest_id as contestId,
    rc.component_id as componentId
from informixoltp\:round rr
    left join informixoltp\:round_component as rc on rc.round_id = rr.round_id
where {filter}