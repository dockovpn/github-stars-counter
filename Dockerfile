FROM openjdk:11.0-jre
COPY /target/scala-2.13 /usr/src/myapp
WORKDIR /usr/src/myapp
ENTRYPOINT [ "java", "-jar", "github-stars-counter-assembly-0.1.0.jar" ]
CMD [ "" ]