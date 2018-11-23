# Please update this field by asking in forum
export TC_JWT_KEY="secret"

export VALID_ISSUERS="https://topcoder-dev.auth0.com/,https://api.topcoder-dev.com,https://auth.topcoder-dev.com/"

# Please provide the AWS keys if you use AWS ElasticSearch and AWS_SIGNING_ENABLED is set to true
# export AWS_ACCESS_KEY=your key
# export AWS_SECRET_KEY=your secret

java -jar -Duser.timezone=America/New_York ../target/elasticsearch-feeder-service-*.jar server ../src/main/resources/elasticsearch-feeder-service.yaml
