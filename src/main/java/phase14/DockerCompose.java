package phase14;

/**
 * Phase 14: Docker Compose
 * 
 * 本课程涵盖：
 * 1. Compose 核心概念
 * 2. compose.yaml 编写
 * 3. 多服务编排
 * 4. 网络与数据卷
 */
public class DockerCompose {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 14: Docker Compose");
        System.out.println("=".repeat(60));

        composeConcepts();
        composeYamlGuide();
        multiServiceExample();
        composeCommands();
    }

    private static void composeConcepts() {
        System.out.println("\n【1. Docker Compose 概念】");
        System.out.println("-".repeat(50));

        String concepts = """

                ═══════════════════════════════════════════════════════════
                            Docker Compose 是什么？
                ═══════════════════════════════════════════════════════════

                Docker Compose 是一个用于定义和运行多容器 Docker 应用的工具

                使用场景：
                • 本地开发环境（应用 + 数据库 + Redis）
                • 测试环境快速搭建
                • 单机部署简单应用

                ⚠️ 生产环境推荐使用 Kubernetes


                ═══════════════════════════════════════════════════════════
                            Compose 文件版本
                ═══════════════════════════════════════════════════════════

                文件名：compose.yaml （推荐）
                        docker-compose.yml（旧版本）

                💡 Docker Compose V2 不再需要 version 字段
                """;
        System.out.println(concepts);
    }

    private static void composeYamlGuide() {
        System.out.println("\n【2. compose.yaml 编写指南】");
        System.out.println("-".repeat(50));

        String guide = """

                ═══════════════════════════════════════════════════════════
                            基础结构
                ═══════════════════════════════════════════════════════════

                # compose.yaml
                services:
                  app:                        # 服务名
                    image: myapp:1.0          # 使用镜像
                    # 或
                    build: .                  # 构建镜像
                    ports:
                      - "8080:8080"           # 端口映射
                    environment:              # 环境变量
                      - SPRING_PROFILES_ACTIVE=prod
                    depends_on:               # 依赖关系
                      - db
                      - redis

                  db:
                    image: mysql:8
                    volumes:
                      - mysql-data:/var/lib/mysql
                    environment:
                      MYSQL_ROOT_PASSWORD: root
                      MYSQL_DATABASE: mydb

                  redis:
                    image: redis:7-alpine

                volumes:
                  mysql-data:                 # 命名数据卷

                networks:
                  default:                    # 默认网络
                    driver: bridge


                ═══════════════════════════════════════════════════════════
                            常用配置项
                ═══════════════════════════════════════════════════════════

                services:
                  myapp:
                    # 镜像配置
                    image: myapp:1.0
                    build:
                      context: .
                      dockerfile: Dockerfile
                      args:
                        - APP_VERSION=1.0

                    # 容器配置
                    container_name: myapp
                    restart: unless-stopped    # 重启策略

                    # 网络配置
                    ports:
                      - "8080:8080"            # 端口映射
                      - "127.0.0.1:9090:9090"  # 仅本地访问
                    expose:
                      - "8080"                 # 仅内部访问

                    # 环境变量
                    environment:
                      - APP_ENV=prod
                    env_file:
                      - .env

                    # 数据卷
                    volumes:
                      - ./config:/app/config   # 绑定挂载
                      - app-data:/app/data     # 命名卷

                    # 资源限制
                    deploy:
                      resources:
                        limits:
                          cpus: '2'
                          memory: 2G
                        reservations:
                          cpus: '0.5'
                          memory: 512M

                    # 健康检查
                    healthcheck:
                      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
                      interval: 30s
                      timeout: 10s
                      retries: 3
                """;
        System.out.println(guide);
    }

    private static void multiServiceExample() {
        System.out.println("\n【3. 多服务编排示例】");
        System.out.println("-".repeat(50));

        String example = """

                ═══════════════════════════════════════════════════════════
                        Spring Boot + MySQL + Redis 完整示例
                ═══════════════════════════════════════════════════════════

                # compose.yaml
                services:
                  # ===== 应用服务 =====
                  app:
                    build: .
                    container_name: myapp
                    ports:
                      - "8080:8080"
                    environment:
                      - SPRING_PROFILES_ACTIVE=docker
                      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/mydb
                      - SPRING_DATASOURCE_USERNAME=root
                      - SPRING_DATASOURCE_PASSWORD=root123
                      - SPRING_REDIS_HOST=redis
                    depends_on:
                      db:
                        condition: service_healthy
                      redis:
                        condition: service_started
                    restart: unless-stopped

                  # ===== 数据库服务 =====
                  db:
                    image: mysql:8
                    container_name: myapp-db
                    volumes:
                      - mysql-data:/var/lib/mysql
                      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
                    environment:
                      MYSQL_ROOT_PASSWORD: root123
                      MYSQL_DATABASE: mydb
                      TZ: Asia/Shanghai
                    ports:
                      - "3306:3306"
                    healthcheck:
                      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
                      interval: 10s
                      timeout: 5s
                      retries: 5
                    restart: unless-stopped

                  # ===== 缓存服务 =====
                  redis:
                    image: redis:7-alpine
                    container_name: myapp-redis
                    command: redis-server --appendonly yes
                    volumes:
                      - redis-data:/data
                    ports:
                      - "6379:6379"
                    restart: unless-stopped

                  # ===== Nginx 反向代理 (可选) =====
                  nginx:
                    image: nginx:alpine
                    container_name: myapp-nginx
                    ports:
                      - "80:80"
                    volumes:
                      - ./nginx.conf:/etc/nginx/nginx.conf:ro
                    depends_on:
                      - app
                    restart: unless-stopped

                # ===== 数据卷 =====
                volumes:
                  mysql-data:
                  redis-data:

                # ===== 网络 =====
                networks:
                  default:
                    name: myapp-network


                ═══════════════════════════════════════════════════════════
                            .env 环境变量文件
                ═══════════════════════════════════════════════════════════

                # .env
                MYSQL_ROOT_PASSWORD=root123
                MYSQL_DATABASE=mydb
                REDIS_PASSWORD=redis123
                APP_VERSION=1.0

                # 在 compose.yaml 中引用
                environment:
                  - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
                """;
        System.out.println(example);
    }

    private static void composeCommands() {
        System.out.println("\n【4. Docker Compose 命令】");
        System.out.println("-".repeat(50));

        String commands = """

                ═══════════════════════════════════════════════════════════
                            常用命令
                ═══════════════════════════════════════════════════════════

                # 启动服务
                docker compose up          # 前台启动
                docker compose up -d       # 后台启动
                docker compose up --build  # 重新构建镜像

                # 停止服务
                docker compose stop        # 停止服务
                docker compose down        # 停止并删除容器
                docker compose down -v     # 同时删除数据卷

                # 查看状态
                docker compose ps          # 查看服务状态
                docker compose logs        # 查看所有日志
                docker compose logs -f app # 实时查看某服务日志

                # 执行命令
                docker compose exec app bash      # 进入容器
                docker compose run app npm test   # 运行一次性命令

                # 扩缩容
                docker compose up -d --scale app=3

                # 其他
                docker compose config      # 验证配置
                docker compose pull        # 拉取镜像
                docker compose build       # 构建镜像


                ═══════════════════════════════════════════════════════════
                            开发工作流
                ═══════════════════════════════════════════════════════════

                # 1. 启动开发环境
                docker compose up -d

                # 2. 查看日志
                docker compose logs -f app

                # 3. 修改代码后重新构建
                docker compose up -d --build app

                # 4. 进入容器调试
                docker compose exec app bash

                # 5. 下班关闭
                docker compose down
                """;
        System.out.println(commands);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Docker Compose 课程完成！");
        System.out.println("下一课: KubernetesBasics.java");
        System.out.println("=".repeat(60));
    }
}
