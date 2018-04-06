select rr.round_id as roundId,
    rr.contest_id as contestId,
    rc.component_id as componentId,
    (select sum(submission_number) from informixoltp\:long_component_state where round_id = rr.round_id and status_id in(130, 131, 140, 150, 160)) as numSubmissions,
    (select count(*) from informixoltp\:round_registration reg where rr.round_id = reg.round_id) as numRegistrants
from informixoltp\:round rr
    left join informixoltp\:round_component as rc on rc.round_id = rr.round_id
where {filter}