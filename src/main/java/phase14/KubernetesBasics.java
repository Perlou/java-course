package phase14;

/**
 * Phase 14: Kubernetes 基础
 * 
 * 本课程涵盖：
 * 1. K8s 核心概念
 * 2. Pod 和 Deployment
 * 3. Service 服务发现
 * 4. 配置管理
 */
public class KubernetesBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 14: Kubernetes 基础");
        System.out.println("=".repeat(60));

        k8sConcepts();
        podAndDeployment();
        serviceTypes();
        configAndSecret();
        kubectlCommands();
    }

    private static void k8sConcepts() {
        System.out.println("\n【1. Kubernetes 核心概念】");
        System.out.println("-".repeat(50));

        String concepts = """

                ═══════════════════════════════════════════════════════════
                            Kubernetes 是什么？
                ═══════════════════════════════════════════════════════════

                Kubernetes (K8s) 是容器编排平台，用于：
                • 自动化部署和扩缩容
                • 服务发现和负载均衡
                • 存储编排
                • 自我修复
                • 滚动更新和回滚


                ═══════════════════════════════════════════════════════════
                            核心资源对象
                ═══════════════════════════════════════════════════════════

                ┌─────────────────────────────────────────────────────────┐
                │                    K8s 资源层次                         │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  用户入口                                               │
                │  └── Ingress (HTTP 路由)                               │
                │                                                         │
                │  服务层                                                  │
                │  └── Service (服务发现、负载均衡)                       │
                │                                                         │
                │  工作负载                                                │
                │  ├── Deployment (无状态应用)                            │
                │  ├── StatefulSet (有状态应用)                           │
                │  └── DaemonSet (每节点运行)                             │
                │                                                         │
                │  最小单元                                                │
                │  └── Pod (容器组)                                       │
                │                                                         │
                │  配置存储                                                │
                │  ├── ConfigMap (配置)                                   │
                │  ├── Secret (敏感信息)                                  │
                │  └── PVC (持久化存储)                                   │
                │                                                         │
                └─────────────────────────────────────────────────────────┘


                ═══════════════════════════════════════════════════════════
                            本地开发环境
                ═══════════════════════════════════════════════════════════

                # 安装 Minikube
                brew install minikube

                # 启动集群
                minikube start

                # 查看状态
                minikube status
                kubectl cluster-info

                # 打开 Dashboard
                minikube dashboard
                """;
        System.out.println(concepts);
    }

    private static void podAndDeployment() {
        System.out.println("\n【2. Pod 和 Deployment】");
        System.out.println("-".repeat(50));

        String podDeploy = """

                ═══════════════════════════════════════════════════════════
                            Pod - 最小部署单元
                ═══════════════════════════════════════════════════════════

                Pod 是一组容器的集合，共享：
                • 网络命名空间（同一 IP）
                • 存储卷
                • 生命周期

                # pod.yaml
                apiVersion: v1
                kind: Pod
                metadata:
                  name: myapp-pod
                  labels:
                    app: myapp
                spec:
                  containers:
                  - name: myapp
                    image: myapp:1.0
                    ports:
                    - containerPort: 8080
                    resources:
                      requests:
                        memory: "256Mi"
                        cpu: "250m"
                      limits:
                        memory: "512Mi"
                        cpu: "500m"

                ⚠️ 不推荐直接创建 Pod，应使用 Deployment


                ═══════════════════════════════════════════════════════════
                            Deployment - 无状态应用管理
                ═══════════════════════════════════════════════════════════

                # deployment.yaml
                apiVersion: apps/v1
                kind: Deployment
                metadata:
                  name: myapp
                  labels:
                    app: myapp
                spec:
                  replicas: 3                    # 副本数
                  selector:
                    matchLabels:
                      app: myapp
                  strategy:
                    type: RollingUpdate          # 滚动更新
                    rollingUpdate:
                      maxSurge: 1                # 最多超出1个
                      maxUnavailable: 0          # 最少可用数
                  template:                      # Pod 模板
                    metadata:
                      labels:
                        app: myapp
                    spec:
                      containers:
                      - name: myapp
                        image: myapp:1.0
                        ports:
                        - containerPort: 8080
                        env:
                        - name: SPRING_PROFILES_ACTIVE
                          value: "prod"
                        resources:
                          requests:
                            memory: "256Mi"
                            cpu: "250m"
                          limits:
                            memory: "512Mi"
                            cpu: "500m"
                        livenessProbe:           # 存活探针
                          httpGet:
                            path: /actuator/health
                            port: 8080
                          initialDelaySeconds: 30
                          periodSeconds: 10
                        readinessProbe:          # 就绪探针
                          httpGet:
                            path: /actuator/health
                            port: 8080
                          initialDelaySeconds: 5
                          periodSeconds: 5


                ═══════════════════════════════════════════════════════════
                            健康检查探针
                ═══════════════════════════════════════════════════════════

                livenessProbe  - 存活探针
                • 检测容器是否存活
                • 失败则重启容器

                readinessProbe - 就绪探针
                • 检测容器是否准备好接收流量
                • 失败则从 Service 移除

                startupProbe   - 启动探针
                • 检测应用是否已启动
                • 适合启动慢的应用
                """;
        System.out.println(podDeploy);
    }

    private static void serviceTypes() {
        System.out.println("\n【3. Service 服务发现】");
        System.out.println("-".repeat(50));

        String service = """

                ═══════════════════════════════════════════════════════════
                            Service 类型
                ═══════════════════════════════════════════════════════════

                ┌─────────────────────────────────────────────────────────┐
                │  类型        │ 说明                                    │
                ├─────────────────────────────────────────────────────────┤
                │ ClusterIP    │ 集群内部访问（默认）                    │
                │ NodePort     │ 节点端口暴露（开发测试）                │
                │ LoadBalancer │ 云厂商负载均衡器（生产）                │
                │ ExternalName │ 映射外部服务                            │
                └─────────────────────────────────────────────────────────┘


                ═══════════════════════════════════════════════════════════
                            ClusterIP Service
                ═══════════════════════════════════════════════════════════

                # service.yaml
                apiVersion: v1
                kind: Service
                metadata:
                  name: myapp-service
                spec:
                  type: ClusterIP           # 默认类型
                  selector:
                    app: myapp              # 选择 Pod
                  ports:
                  - port: 80                # Service 端口
                    targetPort: 8080        # Pod 端口

                # 集群内访问
                http://myapp-service
                http://myapp-service.default.svc.cluster.local


                ═══════════════════════════════════════════════════════════
                            NodePort Service
                ═══════════════════════════════════════════════════════════

                apiVersion: v1
                kind: Service
                metadata:
                  name: myapp-nodeport
                spec:
                  type: NodePort
                  selector:
                    app: myapp
                  ports:
                  - port: 80
                    targetPort: 8080
                    nodePort: 30080         # 节点端口 (30000-32767)

                # 访问方式
                http://<node-ip>:30080


                ═══════════════════════════════════════════════════════════
                            Ingress - HTTP 路由
                ═══════════════════════════════════════════════════════════

                # ingress.yaml
                apiVersion: networking.k8s.io/v1
                kind: Ingress
                metadata:
                  name: myapp-ingress
                  annotations:
                    nginx.ingress.kubernetes.io/rewrite-target: /
                spec:
                  ingressClassName: nginx
                  rules:
                  - host: myapp.example.com
                    http:
                      paths:
                      - path: /
                        pathType: Prefix
                        backend:
                          service:
                            name: myapp-service
                            port:
                              number: 80
                  - host: api.example.com
                    http:
                      paths:
                      - path: /v1
                        pathType: Prefix
                        backend:
                          service:
                            name: api-service
                            port:
                              number: 80
                """;
        System.out.println(service);
    }

    private static void configAndSecret() {
        System.out.println("\n【4. 配置管理】");
        System.out.println("-".repeat(50));

        String config = """

                ═══════════════════════════════════════════════════════════
                            ConfigMap - 非敏感配置
                ═══════════════════════════════════════════════════════════

                # configmap.yaml
                apiVersion: v1
                kind: ConfigMap
                metadata:
                  name: myapp-config
                data:
                  # 键值对
                  APP_NAME: "MyApp"
                  LOG_LEVEL: "INFO"

                  # 配置文件
                  application.yml: |
                    server:
                      port: 8080
                    spring:
                      application:
                        name: myapp

                # 在 Pod 中使用
                spec:
                  containers:
                  - name: myapp
                    # 环境变量方式
                    envFrom:
                    - configMapRef:
                        name: myapp-config

                    # 挂载为文件
                    volumeMounts:
                    - name: config-volume
                      mountPath: /app/config
                  volumes:
                  - name: config-volume
                    configMap:
                      name: myapp-config


                ═══════════════════════════════════════════════════════════
                            Secret - 敏感信息
                ═══════════════════════════════════════════════════════════

                # 创建 Secret
                kubectl create secret generic db-secret \\
                  --from-literal=username=admin \\
                  --from-literal=password=secret123

                # 或使用 YAML
                apiVersion: v1
                kind: Secret
                metadata:
                  name: db-secret
                type: Opaque
                data:
                  # Base64 编码
                  username: YWRtaW4=
                  password: c2VjcmV0MTIz

                # 在 Pod 中使用
                spec:
                  containers:
                  - name: myapp
                    env:
                    - name: DB_USERNAME
                      valueFrom:
                        secretKeyRef:
                          name: db-secret
                          key: username
                    - name: DB_PASSWORD
                      valueFrom:
                        secretKeyRef:
                          name: db-secret
                          key: password
                """;
        System.out.println(config);
    }

    private static void kubectlCommands() {
        System.out.println("\n【5. kubectl 常用命令】");
        System.out.println("-".repeat(50));

        String commands = """

                ═══════════════════════════════════════════════════════════
                            资源操作
                ═══════════════════════════════════════════════════════════

                # 应用配置
                kubectl apply -f deployment.yaml
                kubectl apply -f .                    # 应用目录下所有

                # 查看资源
                kubectl get pods
                kubectl get pods -o wide              # 详细信息
                kubectl get svc,deploy,pods           # 多种资源
                kubectl get all                       # 所有资源
                kubectl get pods -n kube-system       # 指定命名空间

                # 删除资源
                kubectl delete -f deployment.yaml
                kubectl delete pod myapp-xxx
                kubectl delete deploy myapp


                ═══════════════════════════════════════════════════════════
                            调试命令
                ═══════════════════════════════════════════════════════════

                # 查看详情
                kubectl describe pod myapp-xxx
                kubectl describe deploy myapp

                # 查看日志
                kubectl logs myapp-xxx
                kubectl logs -f myapp-xxx             # 实时日志
                kubectl logs myapp-xxx -c container   # 指定容器
                kubectl logs -l app=myapp             # 按标签

                # 进入容器
                kubectl exec -it myapp-xxx -- bash
                kubectl exec -it myapp-xxx -- sh

                # 端口转发
                kubectl port-forward pod/myapp-xxx 8080:8080
                kubectl port-forward svc/myapp 8080:80


                ═══════════════════════════════════════════════════════════
                            扩缩容与更新
                ═══════════════════════════════════════════════════════════

                # 扩缩容
                kubectl scale deploy myapp --replicas=5

                # 更新镜像
                kubectl set image deploy/myapp myapp=myapp:v2

                # 查看更新状态
                kubectl rollout status deploy/myapp

                # 查看历史
                kubectl rollout history deploy/myapp

                # 回滚
                kubectl rollout undo deploy/myapp
                kubectl rollout undo deploy/myapp --to-revision=2
                """;
        System.out.println(commands);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Kubernetes 基础课程完成！");
        System.out.println("下一课: CICD.java");
        System.out.println("=".repeat(60));
    }
}
