# Phase 14: 云原生技术

> **目标**：掌握容器化与云原生开发  
> **预计时长**：1 周  
> **前置条件**：Phase 13 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 使用 Docker 容器化 Java 应用
2. 编写高效的 Dockerfile
3. 使用 Docker Compose 编排多容器
4. 理解 Kubernetes 核心概念
5. 部署应用到 K8s 集群
6. 搭建 CI/CD 流水线

---

## 📚 核心概念

### 云原生技术栈

```
                    ┌─────────┐
                    │  应用   │  ← 微服务、API 网关
                   ─┴─────────┴─
                  ┌─────────────┐
                  │ Kubernetes  │  ← 容器编排
                 ─┴─────────────┴─
                ┌─────────────────┐
                │     Docker      │  ← 容器化
                └─────────────────┘
```

### 技术栈

| 技术           | 用途            |
| -------------- | --------------- |
| Docker         | 应用容器化      |
| Docker Compose | 本地多容器编排  |
| Kubernetes     | 生产容器编排    |
| GitHub Actions | CI/CD 流水线    |
| ArgoCD         | GitOps 持续部署 |

---

## 📁 文件列表

| #   | 文件                                             | 描述           | 知识点                   |
| --- | ------------------------------------------------ | -------------- | ------------------------ |
| 0   | [CONCEPT.md](./CONCEPT.md)                       | 核心概念       | 云原生全景图             |
| 1   | [DockerBasics.java](./DockerBasics.java)         | Docker 基础    | Dockerfile, 镜像构建     |
| 2   | [DockerCompose.java](./DockerCompose.java)       | Docker Compose | 多容器编排               |
| 3   | [KubernetesBasics.java](./KubernetesBasics.java) | Kubernetes     | Pod, Deployment, Service |
| 4   | [CICD.java](./CICD.java)                         | CI/CD          | GitHub Actions, GitOps   |

### 示例配置文件

| 文件                                                         | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [examples/Dockerfile](./examples/Dockerfile)                 | Spring Boot 多阶段构建 |
| [examples/compose.yaml](./examples/compose.yaml)             | 完整 Compose 配置      |
| [examples/k8s-manifests.yaml](./examples/k8s-manifests.yaml) | K8s 部署配置           |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程
mvn exec:java -Dexec.mainClass="phase14.DockerBasics"
mvn exec:java -Dexec.mainClass="phase14.DockerCompose"
mvn exec:java -Dexec.mainClass="phase14.KubernetesBasics"
mvn exec:java -Dexec.mainClass="phase14.CICD"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1**: 阅读 CONCEPT.md，理解云原生全景图
2. **Day 2**: Docker 基础 - 安装 Docker，编写 Dockerfile
3. **Day 3**: Docker Compose - 编排多容器应用
4. **Day 4**: Kubernetes 入门 - 安装 Minikube，部署第一个应用
5. **Day 5**: K8s 进阶 - Service, ConfigMap, Ingress
6. **Day 6**: CI/CD - GitHub Actions 工作流
7. **Day 7**: 实践 - 将项目容器化并部署

### 环境准备

```bash
# 安装 Docker
brew install docker

# 安装 Minikube (本地 K8s)
brew install minikube

# 安装 kubectl
brew install kubectl

# 启动 Minikube
minikube start

# 验证
docker --version
kubectl version --client
minikube status
```

---

## ✅ 完成检查

- [ ] 能够编写 Dockerfile 并构建镜像
- [ ] 能够使用 Docker Compose 启动多容器应用
- [ ] 理解 K8s Pod, Deployment, Service 概念
- [ ] 能够使用 kubectl 部署应用
- [ ] 能够编写 GitHub Actions 工作流
- [ ] 完成示例应用的容器化和 K8s 部署

---

## 🛠️ 常用工具

| 工具           | 用途        | 安装                                                   |
| -------------- | ----------- | ------------------------------------------------------ |
| Docker Desktop | 本地 Docker | [下载](https://www.docker.com/products/docker-desktop) |
| Minikube       | 本地 K8s    | `brew install minikube`                                |
| kubectl        | K8s CLI     | `brew install kubectl`                                 |
| k9s            | K8s TUI     | `brew install k9s`                                     |
| Lens           | K8s IDE     | [下载](https://k8slens.dev/)                           |

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 15](../phase15/README.md)
