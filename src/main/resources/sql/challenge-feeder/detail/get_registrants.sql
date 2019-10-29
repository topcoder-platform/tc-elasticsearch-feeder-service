select
   u.handle AS handle,
   rur.create_date AS registrationDate,
   (select max(s.create_date) from submission s, upload u
                 where u.project_id = p.project_id and s.upload_id = u.upload_id
                 and upload_status_id !=2 and s.submission_status_id != 5
                 and u.resource_id = rur.resource_id) AS submissionDate,
   CASE
       WHEN (pcl.description = 'Marathon Match') THEN ar.rating
       ELSE decode(ri4.value, 'N/A', '0', ri4.value)::int
   END AS rating,
   ri5.value::int AS reliability,
   ct.iso_alpha3_code AS countryCode,
   p.project_id AS challengeId
  from resource rur
     , resource_info ri1
     , project p
     , user u
     , project_category_lu pcl
     , outer resource_info ri4
     , outer resource_info ri5
     , outer informixoltp\:algo_rating ar
     , outer common_oltp\:country ct
     , coder c
 where
   p.project_id = rur.project_id
  and rur.resource_id = ri1.resource_id
  and rur.resource_role_id = 1
  and ri1.resource_info_type_id = 1
  and ri4.resource_id = rur.resource_id
  and ri4.resource_info_type_id = 4
  and ri5.resource_id = rur.resource_id
  and ri5.resource_info_type_id = 5
  and ri1.value = u.user_id
  and pcl.project_category_id = p.project_category_id
  and ar.coder_id = u.user_id
  and ar.algo_rating_type_id=3
  and c.coder_id = u.user_id
  and c.comp_country_code = ct.country_code
  and {filter}
