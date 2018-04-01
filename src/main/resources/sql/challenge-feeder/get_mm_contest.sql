select r.round_id as roundId,
    c.contest_id as contestId,
    rc.component_id as componentId
from informixoltp\:round r
    left join informixoltp\:contest c on r.contest_id = c.contest_id
    left join informixoltp\:round_component as rc on rc.round_id = r.round_id
where {filter}