# Backend: a Web Application developed using Spring Boot

The Backend service uses Spring Boot 2.7.0 as web service application. 

### Prerequisites
* OracleJDK 11. [Oracle Java SE Development Kit](https://www.oracle.com/java/technologies/downloads/)
* Apache Maven. [Maven 3.8.4](http://archive.apache.org/dist/maven/maven-3/3.8.4/)

### Compile
``` shell
mvn clean package spring-boot:repackage
```

### Start the backend
``` shell
java -jar target/backend-1.0-SNAPSHOT.jar 
```

### Test
The project integrates a standard spring boot health monitoring system<br>
Open a terminal and use the following command to check if the web service started successfully.
``` shell
curl -X GET http://127.0.0.1:8080/actuator/health
```
If you receive the response json，it means that the backend service has been successfully built.
``` json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```
