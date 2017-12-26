SELECT 
      x.project_id as challengeId,
      x.file_type_id as fileTypeId,
      l.description 
  FROM project_file_type_xref x
   INNER JOIN file_type_lu l ON l.file_type_id = x.file_type_id  
  WHERE {filter}