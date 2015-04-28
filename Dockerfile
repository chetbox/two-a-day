FROM yaronr/openjdk-7-jre

ADD target/two-a-day-0.1.0-SNAPSHOT-standalone.jar standalone.jar
ADD config-production.edn config.edn
CMD ["java", "-jar", "standalone.jar", "config.edn"]

EXPOSE 3001

