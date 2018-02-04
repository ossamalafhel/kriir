FROM java:8
VOLUME /tmp
ADD ./target/demo-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-Dapp.port=${app.port}", "-jar","/app.jar"]