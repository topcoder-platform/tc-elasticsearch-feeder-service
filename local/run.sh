# Please update this field by asking in forum
export TC_JWT_KEY="secret"

# Please provide the AWS keys if you use AWS ElasticSearch and AWS_SIGNING_ENABLED is set to true
# export AWS_ACCESS_KEY=your key
# export AWS_SECRET_KEY=your secret

export DOCKER_IP=127.0.0.1
export REDISSON_JOB_CLUSTER_ENABLED=false
export REDISSON_JOB_FORCE_INITIAL_LOAD=true
export ELASTIC_SEARCH_URL="http://$DOCKER_IP:9200"

java -jar ../target/elasticsearch-feeder-service-*.jar server ../src/main/resources/elasticsearch-feeder-service.yaml
