SELECT
    pr.project_id as challengeId,
    tou.terms_of_use_id as termsOfUseId,
    tou.title, 
    tou.url,
    touat.name as agreeabilityType,
    dtx.docusign_template_id as templateId,
    rrl.name role
  FROM project_role_terms_of_use_xref pr
    INNER JOIN terms_of_use tou ON pr.terms_of_use_id = tou.terms_of_use_id
    INNER JOIN common_oltp\:terms_of_use_agreeability_type_lu touat ON touat.terms_of_use_agreeability_type_id = tou.terms_of_use_agreeability_type_id
    LEFT JOIN common_oltp\:terms_of_use_docusign_template_xref dtx ON dtx.terms_of_use_id = pr.terms_of_use_id
    LEFT JOIN resource_role_lu rrl on rrl.resource_role_id = pr.resource_role_id
  WHERE pr.resource_role_id IN (select resource_role_id from resource_role_lu)  AND {filter}
  
  