FROM alpine:latest as build

ADD https://cdn.azul.com/zulu/bin/zulu16.30.15-ca-jdk16.0.1-linux_musl_x64.tar.gz /opt/jdk/jdk-all.tar.gz

RUN tar -xzvf /opt/jdk/jdk-all.tar.gz -C /opt/jdk/

RUN ["/opt/jdk/zulu16.30.15-ca-jdk16.0.1-linux_musl_x64/bin/jlink", "--compress=2", \
     "--module-path", "/opt/jdk/zulu16.30.15-ca-jdk16.0.1-linux_musl_x64/jmods/", \
     "--add-modules", "java.base", \
     "--add-modules", "java.instrument", \
     "--add-modules", "java.logging", \
     "--add-modules", "java.desktop", \
     "--add-modules", "java.prefs", \
     "--add-modules", "java.naming", \
     "--add-modules", "java.management", \
     "--add-modules", "java.net.http", \
     "--add-modules", "java.sql", \
     "--add-modules", "java.xml", \
     "--add-modules", "java.rmi", \
     "--add-modules", "java.security.jgss", \
     "--add-modules", "java.scripting", \
     "--add-modules", "jdk.httpserver", \
     "--add-modules", "jdk.unsupported", \
     "--output", "/jlinked"]

FROM alpine:latest
COPY --from=build /jlinked /opt/jdk/
CMD ["/opt/jdk/bin/java", "--version"]
