SELECT 
	round_component.round_id AS roundId,
	round_component.component_id AS componentId,
	component.problem_id AS problemId,
	round_component.points AS points,
	difficulty.difficulty_level AS difficulty,
	division.division_desc AS division
FROM
	INFORMIXOLTP\:round_component AS round_component
	LEFT JOIN INFORMIXOLTP\:component AS component ON round_component.component_id = component.component_id
	LEFT JOIN INFORMIXOLTP\:division AS division ON round_component.division_id = division.division_id
	LEFT JOIN INFORMIXOLTP\:difficulty AS difficulty ON difficulty.difficulty_id = round_component.difficulty_id
WHERE 
    {filter}