select
    pi2.project_id  as challengeId,
    cd.document_name as documentName,
    cd.url as url
from
    project p, comp_versions cv, comp_documentation cd, comp_catalog cc, project_info pi2, project_info pi3
where
    p.project_id = pi2.project_id
    and pi2.project_info_type_id = 2
    and p.project_id = pi3.project_id
    and pi3.project_info_type_id = 3
    and cc.component_id = pi2.value
    and cc.component_id = cv.component_id
    and cv.comp_vers_id = cd.comp_vers_id
    and cv.version = pi3.value
    and {filter}