# Please update this field by asking in forum
export TC_JWT_KEY="secret"

# Please provide the AWS keys if you use AWS ElasticSearch and AWS_SIGNING_ENABLED is set to true
# export AWS_ACCESS_KEY=your key
# export AWS_SECRET_KEY=your secret

# export REDISSON_LOAD_CHANGED_CHALLENGES_JOB_CLUSTER_ENABLED=true
export REDISSON_LOAD_CHANGED_CHALLENGES_JOB_FORCE_INITIAL_LOAD=true

java -jar ../target/elasticsearch-feeder-service-*.jar server ../src/main/resources/elasticsearch-feeder-service.yaml
