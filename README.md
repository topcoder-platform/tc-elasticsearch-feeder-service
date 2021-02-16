# # Topcoder - Elastic Feeder service 

## Prerequisites
- Java 8 with Maven 3
- Docker and Docker Compose
- Git

## Environment Setup
Clone the repository using the below command.
```
git clone https://github.com/topcoder-platform/tc-elasticsearch-feeder-service.git
```

# Dependent Services
The elastic search feeder service depends upon services defined in https://github.com/appirio-tech/tc-common-tutorials/tree/master/docker/direct-app.

Notes, You need to add the following entry to hosts file:
```
<docker-box-ip> cockpit.cloud.topcoder.com

```
Where `<docker-box-ip>`  is the ip address of your docker box. It's 192.168.99.100 for my Windows and Mac docker box, and it should be 127.0.0.1 for linux docker box.

* If you just need to start the dependent services, you can run 

```
docker-compose up -d tc-cache tc-informix elasticsearch
```

* If you want to use direct app to create challenges for local testing,  you can run 
```
docker-compose up -d tc-direct elasticsearch 
```

Please verify the services are properly running like 

```
  docker ps
```

the related containers should be in running status.

# Build and Run the service
## Build And Package:

```
mvn clean compile package
```

Move to the 'local' then start the service using the following command. Make sure that `DOCKER_IP` environment variable is set to the docker box ip.

```
./run.sh
```

## Verification

If the maven build process was successful.

Create an new challenge using the direct-app.
Then you can use the Postman collections elasticFeederVerification.postman_collection.json which is `docs/` directory to test with the APIs. Please make sure to update the challenge ids and service URL and elastic cluster IP in the postman collection.

Refer to the Swagger docs for API spec:  https://github.com/topcoder-platform/tc-elasticsearch-feeder-service/blob/dev/swagger.yaml

