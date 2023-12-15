#FROM ibm-semeru-runtimes:open-17-jdk
#FROM container-registry.oracle.com/graalvm/jdk:21.0.0-ol9
FROM mikevivi/semeru21.01:v1
WORKDIR /app
COPY  target/*.jar /app/app.jar
ENV JAVA_OPTS="\
    -Xmx256m "
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -jar  /app/app.jar
