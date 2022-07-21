#  A Tutorial on Integrating 6Estates IDP Platform into a Web Application

Here we provide a web project for your reference on how to use IDP SDK to intergrate 6Estates' IDP service into your web application.

![screenshot](./imgs/screenshot.png)

## 1 Backend: Spring Web Application

The Backend uses Spring Boot 2.7.0 as web service application. Go to the folder of `backend`.

### 1.1  Prerequisites
* OracleJDK 11. [Oracle Java SE Development Kit](https://www.oracle.com/java/technologies/downloads/)
* Apache Maven. [Maven 3.8.4](http://archive.apache.org/dist/maven/maven-3/3.8.4/)

### 1.2   Compile
``` shell
mvn clean package spring-boot:repackage
```

### 1.3   Start the backend
``` shell
java -jar target/backend-1.0-SNAPSHOT.jar 
```
## 2 Frontend: Dashboard 

The Frontend is a node.js project. Go to the folder of `frontend`.

### 2.1  Installation
You can use the following shell commands to install the lastest node.js.
``` shell
curl -sL https://deb.nodesource.com/setup_16.x | sudo -E bash -

sudo apt-get install -y nodejs

```
### 2.2  Set up the project 
``` shell
npm install
```
### 2.3 Compiles and hot-reloads for development
``` shell
npm run serve
```
If the project builds successfully, you will see the following log:
``` shell
DONE  Compiled successfully 


 App running at:
 - Local:   http://localhost:8080/ 
 - Network: http://your machine IP:8080/

 Note that the development build is not optimized.
 To create a production build, run $npm run build.

```
### 2.4 Compiles and minifies for production
``` shell
npm run build

```
## 3 Dashboard 

After you deploy both the frontend and the backend application successfully, you can open a browser, enter the Url: http://localhost:8080/ , then you will see the above dashboard! 

You may have a trial of the IDP platform by simply uploading a file.  

## 4 Any Issues & Feedbacks 

If you need help installing or using the web service, please feel free to contact us.

If you've instead found a bug in the project or would like new features added, go ahead and open issues or pull requests against this repo!
