# 企业级 AI 智能工作台

> **Enterprise AI Workbench**  
> Phase 25-26 AI 大模型毕业实战项目

---

## 🎯 项目简介

一个综合运用 RAG、AI Agent、多模型集成等技术的企业级智能平台。

### 核心能力

| 模块                   | 功能               | 技术             |
| ---------------------- | ------------------ | ---------------- |
| 🧠 **Orchestrator**    | 意图识别、任务编排 | ReAct Pattern    |
| 📚 **Knowledge Agent** | 企业知识问答       | RAG + 混合检索   |
| 💻 **Code Agent**      | 代码生成/Review    | Function Calling |
| 📊 **Data Agent**      | Text-to-SQL        | LLM + SQL Parser |
| 📝 **Document Agent**  | 摘要/翻译/提取     | Long Context     |
| 🔧 **Tool Agent**      | 外部服务集成       | MCP Protocol     |

### 技术亮点

- ✅ 多模型支持 (Gemini/OpenAI/Ollama)
- ✅ 混合检索 (向量 + BM25)
- ✅ 多 Agent 协作架构
- ✅ 企业级安全审计
- ✅ 可观测性 (监控/日志/追踪)

---

## 📁 项目结构

```
enterprise-ai-workbench/
├── docs/
│   ├── REQUIREMENTS.md         # 需求规格说明书
│   ├── TECHNICAL_DESIGN.md     # 技术设计文档
│   └── DEVELOPMENT_SCHEDULE.md # 开发进度表
├── src/main/java/...           # 源代码 (待实现)
├── pom.xml                     # Maven 配置 (待创建)
├── docker-compose.yml          # 本地环境 (待创建)
└── README.md                   # 本文档
```

---

## 📚 文档导航

| 文档                                       | 说明                           | 阅读顺序 |
| ------------------------------------------ | ------------------------------ | -------- |
| [需求规格说明书](docs/REQUIREMENTS.md)     | 功能需求、用例设计、接口需求   | 1️⃣       |
| [技术设计文档](docs/TECHNICAL_DESIGN.md)   | 架构设计、数据库设计、API 设计 | 2️⃣       |
| [开发进度表](docs/DEVELOPMENT_SCHEDULE.md) | 4 周开发计划、任务分解、里程碑 | 3️⃣       |

---

## 🚀 快速开始 (实现后)

```bash
# 1. 启动依赖服务
docker-compose up -d

# 2. 配置环境变量
export GEMINI_API_KEY=your-key

# 3. 启动应用
mvn spring-boot:run

# 4. 访问
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
```

---

## 🎓 学习目标

通过本项目，你将掌握：

1. **RAG 系统开发** - 完整的检索增强生成流程
2. **AI Agent 架构** - 多 Agent 协作与编排
3. **LangChain4j 实战** - 企业级 AI 应用开发
4. **安全与合规** - Prompt 注入防护、审计日志
5. **可观测性** - 监控、日志、追踪

---

## 📅 开发计划

| 阶段   | 周期     | 核心任务                              |
| ------ | -------- | ------------------------------------- |
| Week 1 | 基础架构 | 项目骨架 + 多模型集成 + 基础对话      |
| Week 2 | RAG 系统 | 文档处理 + 向量检索 + Knowledge Agent |
| Week 3 | 多 Agent | Orchestrator + Code/Data/Doc Agent    |
| Week 4 | 企业特性 | 安全审计 + 监控统计 + 部署文档        |

---

## 🔗 相关资料

- [Phase 25 学习笔记](../../src/main/java/phase25/README.md) - 大模型基础
- [Phase 26 学习笔记](../../src/main/java/phase26/README.md) - RAG 与 Agent
- [LangChain4j 官方文档](https://docs.langchain4j.dev)

---

> 📌 **状态**：文档规划完成，等待开发启动
