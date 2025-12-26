package phase14;

/**
 * Phase 14: Docker 基础
 * 
 * 本课程涵盖：
 * 1. Docker 核心概念
 * 2. Dockerfile 编写
 * 3. 镜像构建与优化
 * 4. 常用命令
 */
public class DockerBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 14: Docker 基础");
        System.out.println("=".repeat(60));

        dockerConcepts();
        dockerfileGuide();
        imageOptimization();
        dockerCommands();
        javaDockerfileExample();
    }

    private static void dockerConcepts() {
        System.out.println("\n【1. Docker 核心概念】");
        System.out.println("-".repeat(50));

        String concepts = """

                ═══════════════════════════════════════════════════════════
                                  Docker 核心组件
                ═══════════════════════════════════════════════════════════

                1. 镜像 (Image)
                ─────────────────────────────────────────────────────────
                • 只读模板，包含运行应用所需的一切
                • 分层存储，共享公共层
                • 类似于 Java 中的 Class

                2. 容器 (Container)
                ─────────────────────────────────────────────────────────
                • 镜像的运行实例
                • 有自己的文件系统、网络、进程空间
                • 类似于 Java 中的 Object (new Class())

                3. 仓库 (Registry)
                ─────────────────────────────────────────────────────────
                • 存储和分发镜像
                • Docker Hub (公共)
                • Harbor (私有企业级)


                ═══════════════════════════════════════════════════════════
                                  镜像分层结构
                ═══════════════════════════════════════════════════════════

                ┌─────────────────────────────────────┐
                │  Layer 4: COPY app.jar              │  ← 应用代码 (可变)
                ├─────────────────────────────────────┤
                │  Layer 3: RUN apt-get install       │  ← 依赖安装
                ├─────────────────────────────────────┤
                │  Layer 2: ENV JAVA_HOME=/opt/java   │  ← 环境变量
                ├─────────────────────────────────────┤
                │  Layer 1: FROM openjdk:17-slim      │  ← 基础镜像
                └─────────────────────────────────────┘

                ⚠️ 每个指令创建一层，层越少越好
                """;
        System.out.println(concepts);
    }

    private static void dockerfileGuide() {
        System.out.println("\n【2. Dockerfile 编写指南】");
        System.out.println("-".repeat(50));

        String dockerfile = """

                ═══════════════════════════════════════════════════════════
                                  Dockerfile 指令
                ═══════════════════════════════════════════════════════════

                FROM        # 基础镜像
                WORKDIR     # 工作目录
                COPY        # 复制文件
                ADD         # 复制文件（支持URL和解压）
                RUN         # 执行命令（构建时）
                CMD         # 容器启动命令（可被覆盖）
                ENTRYPOINT  # 容器入口点（不被覆盖）
                ENV         # 环境变量
                EXPOSE      # 声明端口
                VOLUME      # 数据卷
                ARG         # 构建参数
                LABEL       # 元数据标签
                USER        # 运行用户
                HEALTHCHECK # 健康检查


                ═══════════════════════════════════════════════════════════
                                  基础 Dockerfile 示例
                ═══════════════════════════════════════════════════════════

                # 使用官方 Java 基础镜像
                FROM openjdk:17-slim

                # 设置工作目录
                WORKDIR /app

                # 复制 jar 包
                COPY target/myapp.jar app.jar

                # 暴露端口
                EXPOSE 8080

                # 设置环境变量
                ENV JAVA_OPTS="-Xms512m -Xmx512m"

                # 启动命令
                ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


                ═══════════════════════════════════════════════════════════
                                  CMD vs ENTRYPOINT
                ═══════════════════════════════════════════════════════════

                CMD ["java", "-jar", "app.jar"]
                # 可以被 docker run 参数覆盖
                # docker run myapp echo hello  → 执行 echo hello

                ENTRYPOINT ["java", "-jar", "app.jar"]
                # 不会被覆盖，参数会追加
                # docker run myapp --debug  → java -jar app.jar --debug

                💡 最佳实践：ENTRYPOINT + CMD 配合使用
                ENTRYPOINT ["java", "-jar"]
                CMD ["app.jar"]
                """;
        System.out.println(dockerfile);
    }

    private static void imageOptimization() {
        System.out.println("\n【3. 镜像优化】");
        System.out.println("-".repeat(50));

        String optimization = """

                ═══════════════════════════════════════════════════════════
                                  多阶段构建（推荐）
                ═══════════════════════════════════════════════════════════

                # 构建阶段
                FROM maven:3.9-eclipse-temurin-17 AS builder
                WORKDIR /build
                COPY pom.xml .
                RUN mvn dependency:go-offline
                COPY src ./src
                RUN mvn package -DskipTests

                # 运行阶段
                FROM eclipse-temurin:17-jre-alpine
                WORKDIR /app
                COPY --from=builder /build/target/*.jar app.jar
                EXPOSE 8080
                ENTRYPOINT ["java", "-jar", "app.jar"]

                💡 优势：
                • 构建环境与运行环境分离
                • 最终镜像不包含构建工具
                • 镜像体积从 500MB+ 减少到 100MB+


                ═══════════════════════════════════════════════════════════
                                  镜像优化技巧
                ═══════════════════════════════════════════════════════════

                1. 使用精简基础镜像
                ─────────────────────────────────────────────────────────
                ❌ FROM openjdk:17           # ~400MB
                ✅ FROM openjdk:17-slim      # ~200MB
                ✅ FROM eclipse-temurin:17-jre-alpine  # ~100MB

                2. 合并 RUN 指令
                ─────────────────────────────────────────────────────────
                ❌ RUN apt-get update
                   RUN apt-get install -y curl
                   RUN apt-get clean

                ✅ RUN apt-get update && \\
                       apt-get install -y curl && \\
                       apt-get clean && \\
                       rm -rf /var/lib/apt/lists/*

                3. 利用构建缓存
                ─────────────────────────────────────────────────────────
                # 先复制依赖文件，再复制源码
                COPY pom.xml .
                RUN mvn dependency:go-offline  # 依赖不变则使用缓存
                COPY src ./src
                RUN mvn package

                4. 使用 .dockerignore
                ─────────────────────────────────────────────────────────
                # .dockerignore 文件
                target/
                *.log
                .git/
                .idea/
                *.md
                """;
        System.out.println(optimization);
    }

    private static void dockerCommands() {
        System.out.println("\n【4. Docker 常用命令】");
        System.out.println("-".repeat(50));

        String commands = """

                ═══════════════════════════════════════════════════════════
                                  镜像操作
                ═══════════════════════════════════════════════════════════

                # 拉取镜像
                docker pull nginx:latest
                docker pull registry.cn-hangzhou.aliyuncs.com/myapp:1.0

                # 构建镜像
                docker build -t myapp:1.0 .
                docker build -t myapp:1.0 -f Dockerfile.prod .

                # 查看镜像
                docker images
                docker image ls

                # 删除镜像
                docker rmi myapp:1.0
                docker image prune       # 删除悬空镜像
                docker image prune -a    # 删除未使用镜像

                # 推送镜像
                docker tag myapp:1.0 registry/myapp:1.0
                docker push registry/myapp:1.0


                ═══════════════════════════════════════════════════════════
                                  容器操作
                ═══════════════════════════════════════════════════════════

                # 运行容器
                docker run nginx                      # 前台运行
                docker run -d nginx                   # 后台运行
                docker run -d -p 8080:80 nginx        # 端口映射
                docker run -d --name web nginx        # 指定名称
                docker run -d -e APP_ENV=prod nginx   # 环境变量
                docker run -v /host:/container nginx  # 挂载目录

                # 查看容器
                docker ps              # 运行中的容器
                docker ps -a           # 所有容器

                # 容器生命周期
                docker stop <id>       # 停止
                docker start <id>      # 启动
                docker restart <id>    # 重启
                docker rm <id>         # 删除
                docker rm -f <id>      # 强制删除

                # 调试
                docker logs <id>           # 查看日志
                docker logs -f <id>        # 实时日志
                docker logs --tail 100 <id>  # 最后100行
                docker exec -it <id> bash  # 进入容器
                docker inspect <id>        # 详细信息
                docker stats               # 资源使用


                ═══════════════════════════════════════════════════════════
                                  数据卷
                ═══════════════════════════════════════════════════════════

                # 创建数据卷
                docker volume create mydata

                # 使用数据卷
                docker run -v mydata:/data nginx
                docker run -v $(pwd)/config:/app/config nginx

                # 查看数据卷
                docker volume ls
                docker volume inspect mydata

                # 删除数据卷
                docker volume rm mydata
                docker volume prune   # 删除未使用的卷
                """;
        System.out.println(commands);
    }

    private static void javaDockerfileExample() {
        System.out.println("\n【5. Java 项目完整 Dockerfile 示例】");
        System.out.println("-".repeat(50));

        String example = """

                ═══════════════════════════════════════════════════════════
                            Spring Boot 多阶段构建 Dockerfile
                ═══════════════════════════════════════════════════════════

                # ===== 构建阶段 =====
                FROM maven:3.9-eclipse-temurin-17 AS builder

                WORKDIR /build

                # 先复制 pom.xml，利用缓存
                COPY pom.xml .
                RUN mvn dependency:go-offline -B

                # 复制源码并构建
                COPY src ./src
                RUN mvn package -DskipTests -B

                # ===== 运行阶段 =====
                FROM eclipse-temurin:17-jre-alpine

                # 创建非 root 用户
                RUN addgroup -S spring && adduser -S spring -G spring
                USER spring:spring

                WORKDIR /app

                # 从构建阶段复制 jar
                COPY --from=builder /build/target/*.jar app.jar

                # JVM 参数
                ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

                # 健康检查
                HEALTHCHECK --interval=30s --timeout=3s \\
                  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

                EXPOSE 8080

                ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


                ═══════════════════════════════════════════════════════════
                                  构建和运行
                ═══════════════════════════════════════════════════════════

                # 构建镜像
                docker build -t myapp:1.0 .

                # 运行容器
                docker run -d \\
                  --name myapp \\
                  -p 8080:8080 \\
                  -e SPRING_PROFILES_ACTIVE=prod \\
                  -e JAVA_OPTS="-Xmx1g" \\
                  myapp:1.0

                # 查看日志
                docker logs -f myapp
                """;
        System.out.println(example);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Docker 基础课程完成！");
        System.out.println("下一课: DockerCompose.java");
        System.out.println("=".repeat(60));
    }
}
