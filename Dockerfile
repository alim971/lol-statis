FROM openjdk:13-alpine
VOLUME /tmp
ADD /target/*.jar lol-statistic-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/lol-statistic-0.0.1-SNAPSHOT.jar"]