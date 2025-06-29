# 多阶段构建 Dockerfile for 汇丰银行交易管理系统

# 第一阶段：构建阶段
FROM openjdk:21-jdk-slim as builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .

# 复制 Maven Wrapper（如果存在）
COPY .mvn .mvn
COPY mvnw .

# 下载依赖（利用 Docker 缓存）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 第二阶段：运行阶段
FROM openjdk:21-jre-slim

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN groupadd -r hsbc && useradd -r -g hsbc hsbc

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 更改文件所有者
RUN chown -R hsbc:hsbc /app

# 切换到非 root 用户
USER hsbc

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+PrintGC"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# 元数据标签
LABEL maintainer="HSBC Development Team <dev@hsbc.com>"
LABEL description="汇丰银行交易管理系统"
LABEL version="1.0.0"
LABEL application="hsbc-transaction-management" 