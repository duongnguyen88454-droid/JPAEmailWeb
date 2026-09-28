# ============================================================
# Giai đoạn 1: BUILD - Dùng Maven để biên dịch và đóng gói WAR
# ============================================================
FROM maven:3.8.6-openjdk-8 AS build

WORKDIR /app

# Copy toàn bộ source code vào container
COPY pom.xml .
COPY src ./src

# Build WAR file (bỏ qua test)
RUN mvn clean package -DskipTests

# ============================================================
# Giai đoạn 2: RUN - Dùng Tomcat 9 để chạy ứng dụng
# ============================================================
FROM tomcat:9.0-jdk8-openjdk

# Xóa ứng dụng mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR đã build vào thư mục webapps với tên ROOT.war
# → Truy cập tại http://host/ thay vì http://host/JPAPractic
COPY --from=build /app/target/JPAPractic-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# ============================================================
# Biến môi trường (sẽ được set trên Render Dashboard)
# ============================================================
ENV DB_URL=jdbc:sqlserver://localhost:1433;databaseName=murach;encrypt=true;trustServerCertificate=true
ENV DB_USER=sa
ENV DB_PASSWORD=1234
ENV MAIL_USER=phaty9147@gmail.com
ENV MAIL_PASSWORD=lmuvsqzzpwmxjvgh

# Expose port 8080 (Render yêu cầu)
EXPOSE 8080

# Khởi động Tomcat
CMD ["catalina.sh", "run"]
