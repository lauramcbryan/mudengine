FROM alpine:latest as build

ADD https://download.java.net/java/early_access/alpine/15/binaries/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz /opt/jdk/
# COPY ./openjdk-14-ea+15_linux-x64-musl_bin.tar.gz /opt/jdk/
RUN tar -xzvf /opt/jdk/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz -C /opt/jdk/

RUN ["/opt/jdk/jdk-14/bin/jlink", "--compress=2", \
     "--module-path", "/opt/jdk/jdk-14/jmods/", \
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