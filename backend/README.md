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
Open a terminal and use the following command to check if the web service started successfully.
``` shell
curl -X POST "localhost:8080/taskSubmit"  \
-H "accept: */*" \
-H "Content-Type: multipart/form-data" \
-F "file=@/home/Documents/7364028.png" \
-F "mode=1" \
-F "token=xxxxx" \
-F "fileType=CBKS" 
```
If you receive the response json，it means that the backend service has been successfully built.
``` json
{
  "data": 12345,
  "errorCode": 0,
  "message": "",
  "status": 200
}
```
