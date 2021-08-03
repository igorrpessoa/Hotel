FROM openjdk:11
EXPOSE 8080:8080
MAINTAINER hotel.com
COPY target/hotel-0.0.1-SNAPSHOT.jar hotel-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/hotel-server-1.0.0.jar", "--server.port=8080"]