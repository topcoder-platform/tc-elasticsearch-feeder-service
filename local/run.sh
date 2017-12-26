# IP to which the docker container ports are mapped
export IP="cockpit.cloud.topcoder.com"
export DOCKER_IP="$IP"

export AUTH_DOMAIN="topcoder-dev.com"
# Please update this field by asking in forum
export TC_JWT_KEY="secret"

export ELASTIC_SEARCH_URL="http://$DOCKER_IP:9200"
#export ELASTIC_SEARCH_URL="https://search-testfortc-pkdmysot5ydi6ta3kdtcqmjuv4.us-east-2.es.amazonaws.com"
export CHALLENGES_INDEX_NAME="challenges"
export AWS_SIGNING_ENABLED=false
export AWS_REGION=us-east-1
export AWS_SERVICE=es
export AWS_ACCESS_KEY=your key
export AWS_SECRET_KEY=your secret


export OLTP_PW="1nf0rm1x"
export OLTP_USER="informix"
export OLTP_URL="jdbc:informix-sqli://$DOCKER_IP:2021/tcs_catalog:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"

java -Ddw.authDomain="$AUTH_DOMAIN" -Ddw.databases[0].user="$OLTP_USER" -Ddw.databases[0].password="$OLTP_PW" -Ddw.databases[0].url="$OLTP_URL" -Ddw.jestClientConfiguration.elasticSearchUrl="$ELASTIC_SEARCH_URL" -Ddw.jestClientConfiguration.challengesIndexName="$CHALLENGES_INDEX_NAME" -Ddw.jestClientConfiguration.awsSigningEnabled="$AWS_SIGNING_ENABLED" -jar ../target/challenge-feeder-microservice-*.jar server ../src/main/resources/challenge-feeder-service.yaml
