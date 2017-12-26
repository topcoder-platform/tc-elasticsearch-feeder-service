# IP to which the docker container ports are mapped
set IP=cockpit.cloud.topcoder.com
set DOCKER_IP=%IP%

set AUTH_DOMAIN=topcoder-dev.com
# Please update this field by asking in forum
set TC_JWT_KEY=secret

set ELASTIC_SEARCH_URL="http://$DOCKER_IP:9200"
#set ELASTIC_SEARCH_URL="https://your-domain-url-here.us-east-1.es.amazonaws.com"
set CHALLENGES_INDEX_NAME="challenges"
set AWS_SERVICE=es
set AWS_SIGNING_ENABLED=false
set AWS_REGION=us-east-1
set AWS_ACCESS_KEY=your key
set AWS_SECRET_KEY=your secret


set OLTP_PW=1nf0rm1x
set OLTP_USER=informix
set OLTP_URL=jdbc:informix-sqli://%DOCKER_IP%:2021/tcs_catalog:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;

java -Ddw.authDomain=%AUTH_DOMAIN% -Ddw.databases[0].user=%OLTP_USER% -Ddw.databases[0].password=%OLTP_PW% -Ddw.databases[0].url=%OLTP_URL% -jar ../target/challenge-feeder-microservice-1.0.0.jar server ../src/main/resources/challenge-feeder-service.yaml
