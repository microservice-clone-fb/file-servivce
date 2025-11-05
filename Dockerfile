# =========================
# 🔨 Build stage
# =========================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước (tối ưu cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code và build jar
COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# 🚀 Runtime stage
# =========================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo user không phải root để tăng bảo mật
RUN addgroup -S spring && adduser -S spring -G spring

# Tạo thư mục upload và phân quyền
RUN mkdir -p /tmp/upload && chown -R spring:spring /tmp/upload

USER spring:spring

# Copy file jar đã build từ stage trước
COPY --from=build /app/target/*.jar app.jar

# Render tự set biến PORT (thường là 10000)
# Nếu chạy local thì fallback về 8080
ENV PORT=${PORT:-8080}

# Expose port (thông tin cho Docker)
EXPOSE ${PORT}

# Chạy ứng dụng Spring Boot
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]