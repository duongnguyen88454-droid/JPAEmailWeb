# ============================================================
# Giai đoạn 1: BUILD - Maven biên dịch và đóng gói WAR
# ============================================================
FROM maven:3.8.6-openjdk-8 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ============================================================
# Giai đoạn 2: RUN - Tomcat 9 chạy ứng dụng
# ============================================================
FROM tomcat:9.0-jdk8-openjdk

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/JPAPractic-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# ============================================================
# Biến môi trường - TiDB Cloud + Gmail
# ============================================================
ENV DB_URL=jdbc:mysql://gateway01.ap-northeast-1.prod.aws.tidbcloud.com:4000/sys?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
ENV DB_USER=3jFXnZFCiEtXj44.root
ENV DB_PASSWORD=zvyBC4EGcOH0HQ0s
ENV MAIL_USER=phaty9147@gmail.com
ENV MAIL_PASSWORD=lmuvsqzzpwmxjvgh

EXPOSE 8080
CMD ["catalina.sh", "run"]
