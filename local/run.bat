REM Please update this field by asking in forum
set TC_JWT_KEY=secret

REM Please provide the AWS keys if you use AWS ElasticSearch and AWS_SIGNING_ENABLED is set to true
REM set AWS_ACCESS_KEY=your key
REM set AWS_SECRET_KEY=your secret

java -jar -Duser.timezone=America/New_York ../target/elasticsearch-feeder-service-1.0.0.jar server ../src/main/resources/elasticsearch-feeder-service.yaml
