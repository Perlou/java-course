# 🏦 TurboX Exchange - 分布式交易平台

> 一个企业级、高性能的分布式数字资产交易平台  
> A high-performance distributed digital asset exchange platform

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)

---

## 📋 项目概述

TurboX Exchange 是开源的分布式交易平台，采用微服务架构，实现了撮合引擎、订单管理、账户系统、行情推送等核心交易功能。

### 🎯 项目目标

- **开源分享**：提供可参考的交易系统实现方案
- **生产可用**：遵循企业级开发规范，可作为二次开发基础

### ✨ 核心特性

- 🚀 **高性能撮合**：内存撮合引擎，10 万+ TPS
- 📊 **实时行情**：WebSocket 推送，毫秒级延迟
- 💰 **资金安全**：分布式事务，资金操作幂等
- 🔐 **安全可靠**：OAuth2 认证，完善的风控
- 📈 **可观测性**：全链路追踪，指标监控
- 🐳 **云原生**：Docker/K8s 部署，弹性伸缩

---

## 📁 项目结构

```
exchange-system/
├── docs/                          # 项目文档
│   ├── PRD.md                     # 产品需求文档
│   ├── ARCHITECTURE.md            # 技术架构文档
│   ├── API.md                     # API 接口文档
│   ├── DEPLOYMENT.md              # 部署文档
│   ├── DATA-DICTIONARY.md         # 数据字典
│   └── ROADMAP.md                 # 开发进度表
├── exchange-gateway/              # API 网关服务
├── exchange-user/                 # 用户服务
├── exchange-account/              # 账户服务
├── exchange-order/                # 订单服务
├── exchange-match/                # 撮合引擎服务
├── exchange-market/               # 行情服务
├── exchange-risk/                 # 风控服务
├── exchange-admin/                # 管理后台服务
├── exchange-common/               # 公共模块
├── exchange-web/                  # 前端项目
├── docker/                        # Docker 配置
├── k8s/                           # Kubernetes 配置
└── scripts/                       # 脚本工具
```

---

## 🛠️ 技术栈

### 后端

| 分类     | 技术                               |
| -------- | ---------------------------------- |
| 语言     | Java 21                            |
| 框架     | Spring Boot 3.2, Spring Cloud 2023 |
| 数据库   | MySQL 8.0, Redis 7.0               |
| 消息队列 | Apache Kafka                       |
| 搜索引擎 | Elasticsearch 8.x                  |
| 注册中心 | Nacos                              |
| 网关     | Spring Cloud Gateway               |
| 分库分表 | ShardingSphere-JDBC 5.x            |
| 撮合队列 | LMAX Disruptor                     |
| 序列化   | Protocol Buffers                   |
| 文档     | SpringDoc OpenAPI                  |

### 前端

| 分类     | 技术                   |
| -------- | ---------------------- |
| 框架     | React 18 / Next.js 14  |
| 状态管理 | Zustand                |
| 图表     | TradingView Charts     |
| 实时通信 | WebSocket              |
| UI 组件  | Tailwind CSS, Radix UI |

### 基础设施

| 分类     | 技术                   |
| -------- | ---------------------- |
| 容器化   | Docker, Docker Compose |
| 编排     | Kubernetes             |
| 监控     | Prometheus + Grafana   |
| 链路追踪 | Jaeger / SkyWalking    |
| 日志     | ELK Stack              |
| CI/CD    | GitHub Actions         |

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+

### 本地开发

```bash
# 克隆项目
git clone https://github.com/your-username/turbox-exchange.git
cd turbox-exchange

# 启动基础设施
docker-compose -f docker/docker-compose.infra.yml up -d

# 启动后端服务
./scripts/start-all.sh

# 启动前端
cd exchange-web
npm install
npm run dev
```

### Docker 部署

```bash
docker-compose up -d
```

---

## 📖 文档

- [产品需求文档 (PRD)](docs/PRD.md)
- [技术架构文档](docs/ARCHITECTURE.md)
- [API 接口文档](docs/API.md)
- [部署指南](docs/DEPLOYMENT.md)
- [数据字典](docs/DATA-DICTIONARY.md)
- [开发进度表](docs/ROADMAP.md)

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

---

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

---

## 🙏 致谢

本项目作为 Java 架构师课程的毕业实战项目，感谢所有课程内容的支持。

**免责声明**：本项目仅供学习研究，不构成任何投资建议，请勿用于真实交易。
