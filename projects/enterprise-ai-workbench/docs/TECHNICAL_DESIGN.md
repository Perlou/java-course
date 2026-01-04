# 企业级 AI 智能工作台 - 技术设计文档

> **文档版本**：v1.0  
> **更新日期**：2026-01-04  
> **项目代号**：Enterprise AI Workbench

---

## 1. 技术架构

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Presentation Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         │
│  │   Web Client    │  │  Mobile Client  │  │   API Client    │         │
│  │  (React/Vue)    │  │   (Optional)    │  │   (SDK/CLI)     │         │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘         │
│           └─────────────────────┼─────────────────────┘                 │
└─────────────────────────────────┼───────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                           API Gateway Layer                             │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    Spring Cloud Gateway                          │   │
│  │         认证鉴权 │ 限流熔断 │ 路由转发 │ 日志记录                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────┼───────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                         Application Layer                               │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    Orchestrator Agent                            │   │
│  │    ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │   │
│  │    │ 意图识别  │  │ 任务分解  │  │ 调度引擎  │  │ 结果汇总  │      │   │
│  │    └──────────┘  └──────────┘  └──────────┘  └──────────┘      │   │
│  └───────────────────────────┬─────────────────────────────────────┘   │
│                              │                                          │
│     ┌────────────────────────┼────────────────────────┐                │
│     ↓           ↓            ↓            ↓           ↓                │
│  ┌──────┐  ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐              │
│  │Knowledge│ │ Code │    │ Data │    │ Doc  │    │ Tool │              │
│  │ Agent │  │Agent │    │Agent │    │Agent │    │Agent │              │
│  └───┬──┘  └───┬──┘    └───┬──┘    └───┬──┘    └───┬──┘              │
│      │         │           │           │           │                    │
└──────┼─────────┼───────────┼───────────┼───────────┼────────────────────┘
       │         │           │           │           │
┌──────┼─────────┼───────────┼───────────┼───────────┼────────────────────┐
│      ↓         ↓           ↓           ↓           ↓                    │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                       Domain Services                            │  │
│  │  ChatService │ KnowledgeService │ CodeService │ DataService      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                       Infrastructure                              │  │
│  │                                                                   │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐                 │  │
│  │  │ LLM Client │  │ RAG Engine │  │ Tool Engine│                 │  │
│  │  │ (多模型)    │  │ (检索增强)  │  │ (函数调用) │                 │  │
│  │  └────────────┘  └────────────┘  └────────────┘                 │  │
│  │                                                                   │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐                 │  │
│  │  │  Memory    │  │  Security  │  │ Monitoring │                 │  │
│  │  │ (会话记忆)  │  │ (安全过滤)  │  │ (监控追踪) │                 │  │
│  │  └────────────┘  └────────────┘  └────────────┘                 │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              Domain Layer                               │
└─────────────────────────────────────────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                         Data Access Layer                               │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐       │
│  │ PostgreSQL │  │  pgvector  │  │   Redis    │  │   Kafka    │       │
│  │  (主存储)   │  │  (向量库)   │  │  (缓存)    │  │  (消息)    │       │
│  └────────────┘  └────────────┘  └────────────┘  └────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                         External Services                               │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐       │
│  │ Gemini API │  │ OpenAI API │  │ Ollama     │  │ 其他 API   │       │
│  └────────────┘  └────────────┘  └────────────┘  └────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈选型

| 层次     | 技术        | 版本  | 说明            |
| -------- | ----------- | ----- | --------------- |
| 后端框架 | Spring Boot | 3.2.x | 主框架          |
| AI 框架  | LangChain4j | 0.35+ | LLM 集成        |
| 数据库   | PostgreSQL  | 16+   | 主存储          |
| 向量扩展 | pgvector    | 0.5+  | 向量检索        |
| 缓存     | Redis       | 7+    | 会话缓存        |
| 消息队列 | Kafka       | 3+    | 异步处理 (可选) |
| API 文档 | SpringDoc   | 2.3+  | Swagger         |
| 监控     | Micrometer  | 1.12+ | 指标采集        |

---

## 2. 核心模块设计

### 2.1 Orchestrator 编排中心

#### 2.1.1 设计目标

- 统一入口，处理所有用户请求
- 智能路由到合适的 Agent
- 支持复杂任务的分解与组合

#### 2.1.2 核心类设计

```java
@Service
public class OrchestratorAgent {

    private final IntentClassifier intentClassifier;
    private final TaskDecomposer taskDecomposer;
    private final AgentRegistry agentRegistry;
    private final ResponseAggregator responseAggregator;

    /**
     * 处理用户请求的主入口
     */
    public AgentResponse process(UserRequest request) {
        // 1. 意图识别
        Intent intent = intentClassifier.classify(request.getMessage());

        // 2. 任务分解
        List<SubTask> tasks = taskDecomposer.decompose(intent, request);

        // 3. 任务执行
        List<TaskResult> results = executeTasks(tasks);

        // 4. 结果汇总
        return responseAggregator.aggregate(results);
    }

    private List<TaskResult> executeTasks(List<SubTask> tasks) {
        // 根据依赖关系，串行或并行执行任务
        return tasks.stream()
            .map(task -> {
                Agent agent = agentRegistry.getAgent(task.getAgentType());
                return agent.execute(task);
            })
            .toList();
    }
}
```

#### 2.1.3 意图分类

```java
public enum Intent {
    KNOWLEDGE_QUERY,    // 知识查询
    CODE_GENERATION,    // 代码生成
    CODE_REVIEW,        // 代码审查
    DATA_QUERY,         // 数据查询
    DOCUMENT_SUMMARY,   // 文档摘要
    DOCUMENT_TRANSLATE, // 文档翻译
    TOOL_CALL,          // 工具调用
    GENERAL_CHAT        // 通用对话
}
```

### 2.2 RAG 检索引擎

#### 2.2.1 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                      RAG Pipeline                           │
│                                                             │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐ │
│  │ Loader  │ → │ Splitter│ → │Embedder │ → │  Store  │ │
│  │ 文档加载 │    │ 文本切分 │    │ 向量化  │    │ 向量存储 │ │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘ │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                  Retrieval Chain                     │   │
│  │                                                      │   │
│  │  Query → Embedding → VectorSearch → Reranker → Top-K│   │
│  │    ↓                      ↓                          │   │
│  │  BM25 ──────────────→ Fusion ────────────────────────│   │
│  │                                                      │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

#### 2.2.2 核心接口

```java
public interface RagEngine {

    /**
     * 索引文档
     */
    void indexDocument(Document document);

    /**
     * 检索相关内容
     */
    List<RetrievedChunk> retrieve(String query, RetrieveOptions options);

    /**
     * RAG 问答
     */
    RagResponse answer(String question, RagOptions options);
}

@Builder
public record RetrieveOptions(
    int topK,           // 返回数量
    double minScore,    // 最低相似度
    boolean useHybrid,  // 是否混合检索
    boolean useRerank,  // 是否重排序
    List<String> categories  // 限定分类
) {}
```

#### 2.2.3 切分策略

```java
public enum ChunkingStrategy {
    FIXED_SIZE,         // 固定大小
    RECURSIVE,          // 递归切分
    SEMANTIC,           // 语义切分
    MARKDOWN_HEADER     // 按标题切分
}

@Configuration
public class ChunkingConfig {

    @Bean
    public DocumentSplitter documentSplitter() {
        return DocumentSplitters.recursive(
            500,    // maxChunkSize
            50,     // overlap
            new SentenceSplitter()
        );
    }
}
```

### 2.3 多模型适配器

#### 2.3.1 设计模式

采用策略模式 + 工厂模式，实现多模型的灵活切换。

```java
public interface LlmAdapter {

    String generate(String prompt);

    Flux<String> generateStream(String prompt);

    String getProviderName();

    ModelInfo getModelInfo();
}

@Component
public class LlmAdapterFactory {

    private final Map<String, LlmAdapter> adapters;

    public LlmAdapter getAdapter(String provider) {
        return adapters.getOrDefault(provider,
            adapters.get("gemini"));  // 默认 Gemini
    }
}
```

#### 2.3.2 模型配置

```yaml
llm:
  providers:
    gemini:
      enabled: true
      api-key: ${GEMINI_API_KEY}
      models:
        chat: gemini-1.5-flash
        embedding: text-embedding-004
    openai:
      enabled: true
      api-key: ${OPENAI_API_KEY}
      models:
        chat: gpt-4o
        embedding: text-embedding-3-small
    ollama:
      enabled: true
      base-url: http://localhost:11434
      models:
        chat: qwen2
        embedding: nomic-embed-text

  routing:
    default: gemini
    code: openai # 代码生成用 OpenAI
    local: ollama # 敏感数据用本地
```

### 2.4 工具调用引擎

#### 2.4.1 工具注册

```java
@Tool("搜索企业知识库")
public class KnowledgeSearchTool implements ToolExecutor {

    @Override
    @ToolMethod(description = "根据关键词搜索企业内部知识")
    public ToolResult execute(
        @ToolParam(name = "query", description = "搜索关键词") String query,
        @ToolParam(name = "category", description = "知识分类", required = false) String category
    ) {
        List<Document> results = knowledgeService.search(query, category);
        return ToolResult.success(formatResults(results));
    }
}
```

#### 2.4.2 工具调度

```java
@Service
public class ToolEngine {

    @Autowired
    private List<ToolExecutor> tools;

    public ToolResult invoke(String toolName, Map<String, Object> params) {
        ToolExecutor tool = findTool(toolName);

        // 参数验证
        validateParams(tool, params);

        // 权限检查
        checkPermission(tool);

        // 执行并记录
        ToolResult result = tool.execute(params);
        auditLog.record(toolName, params, result);

        return result;
    }
}
```

---

## 3. 数据库设计

### 3.1 ER 图

```
┌─────────────────────────────────────────────────────────────────────┐
│                           用户与会话                                │
│                                                                     │
│  ┌──────────────┐       ┌──────────────┐       ┌──────────────┐   │
│  │    users     │       │   sessions   │       │   messages   │   │
│  ├──────────────┤       ├──────────────┤       ├──────────────┤   │
│  │ id           │──1:N──│ user_id      │──1:N──│ session_id   │   │
│  │ username     │       │ title        │       │ role         │   │
│  │ email        │       │ agent_type   │       │ content      │   │
│  │ department   │       │ created_at   │       │ tokens       │   │
│  │ role         │       │ updated_at   │       │ created_at   │   │
│  └──────────────┘       └──────────────┘       └──────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                           知识库                                    │
│                                                                     │
│  ┌──────────────┐       ┌──────────────┐       ┌──────────────┐   │
│  │  documents   │       │   chunks     │       │  embeddings  │   │
│  ├──────────────┤       ├──────────────┤       ├──────────────┤   │
│  │ id           │──1:N──│ document_id  │──1:1──│ chunk_id     │   │
│  │ title        │       │ content      │       │ vector       │   │
│  │ content      │       │ position     │       │ (vector 384) │   │
│  │ category     │       │ metadata     │       └──────────────┘   │
│  │ file_path    │       └──────────────┘                          │
│  │ status       │                                                  │
│  └──────────────┘                                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                           审计日志                                  │
│                                                                     │
│  ┌──────────────┐       ┌──────────────┐                          │
│  │ audit_logs   │       │ token_usage  │                          │
│  ├──────────────┤       ├──────────────┤                          │
│  │ id           │       │ id           │                          │
│  │ user_id      │       │ user_id      │                          │
│  │ action       │       │ session_id   │                          │
│  │ resource     │       │ prompt_tokens│                          │
│  │ details      │       │ completion   │                          │
│  │ ip_address   │       │ model        │                          │
│  │ created_at   │       │ cost         │                          │
│  └──────────────┘       └──────────────┘                          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 核心表结构

```sql
-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    department VARCHAR(50),
    role VARCHAR(20) DEFAULT 'user',
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 会话表
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200),
    agent_type VARCHAR(50) DEFAULT 'general',
    message_count INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 消息表
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES sessions(id),
    role VARCHAR(20) NOT NULL,  -- user/assistant/system
    content TEXT NOT NULL,
    tokens INT DEFAULT 0,
    model VARCHAR(50),
    tool_calls JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 文档表
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    category VARCHAR(100),
    file_path VARCHAR(500),
    file_type VARCHAR(20),
    file_size BIGINT,
    status VARCHAR(20) DEFAULT 'pending',
    chunk_count INT DEFAULT 0,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 文档片段表
CREATE TABLE chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES documents(id),
    content TEXT NOT NULL,
    position INT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 向量嵌入表 (使用 pgvector)
CREATE TABLE embeddings (
    id BIGSERIAL PRIMARY KEY,
    chunk_id BIGINT NOT NULL REFERENCES chunks(id),
    vector vector(384),  -- AllMiniLmL6V2 维度
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建向量索引
CREATE INDEX idx_embeddings_vector ON embeddings
    USING ivfflat (vector vector_cosine_ops) WITH (lists = 100);

-- Token 使用统计表
CREATE TABLE token_usage (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    session_id BIGINT REFERENCES sessions(id),
    model VARCHAR(50) NOT NULL,
    prompt_tokens INT NOT NULL,
    completion_tokens INT NOT NULL,
    cost DECIMAL(10, 6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 审计日志表
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(100),
    resource_id BIGINT,
    details JSONB,
    ip_address INET,
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 4. API 设计

### 4.1 API 规范

- RESTful 风格
- 统一响应格式
- JWT 认证
- 版本控制 (/api/v1/)

### 4.2 响应格式

```json
{
    "code": 0,
    "message": "success",
    "data": { ... },
    "timestamp": "2026-01-04T19:00:00Z",
    "traceId": "abc123"
}
```

### 4.3 核心 API

#### 4.3.1 对话 API

```yaml
POST /api/v1/chat
Content-Type: application/json

Request:
{
    "sessionId": "optional-session-id",
    "message": "用户消息",
    "stream": false,
    "options": {
        "model": "gemini",
        "temperature": 0.7
    }
}

Response:
{
    "code": 0,
    "data": {
        "sessionId": "sess-xxx",
        "messageId": "msg-xxx",
        "content": "AI 回复内容",
        "sources": [...],
        "tokensUsed": 150,
        "responseTime": 1234
    }
}
```

#### 4.3.2 知识库 API

```yaml
# 上传文档
POST /api/v1/knowledge/documents
Content-Type: multipart/form-data

# 搜索知识
GET /api/v1/knowledge/search?q={query}&category={category}&topK=5

# 获取文档
GET /api/v1/knowledge/documents/{id}

# 删除文档
DELETE /api/v1/knowledge/documents/{id}
```

---

## 5. 安全设计

### 5.1 认证授权

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
    }
}
```

### 5.2 内容安全

```java
@Component
public class ContentSecurityFilter {

    // 输入过滤
    public String filterInput(String input) {
        // 1. 检测 Prompt 注入
        if (detectPromptInjection(input)) {
            throw new SecurityException("检测到 Prompt 注入攻击");
        }

        // 2. 敏感信息脱敏
        return sensitiveDataMasker.mask(input);
    }

    // 输出过滤
    public String filterOutput(String output) {
        // 检测并过滤敏感内容
        return contentModerator.moderate(output);
    }
}
```

### 5.3 审计日志

```java
@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(Audited)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        AuditLog log = new AuditLog();
        log.setAction(getAction(pjp));
        log.setUserId(getCurrentUserId());
        log.setIpAddress(getClientIp());

        try {
            Object result = pjp.proceed();
            log.setStatus("SUCCESS");
            return result;
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            auditLogRepository.save(log);
        }
    }
}
```

---

## 6. 监控设计

### 6.1 指标采集

```java
@Component
public class LlmMetrics {

    private final MeterRegistry registry;

    // 请求计数
    private final Counter requestCounter;

    // 响应时间
    private final Timer responseTimer;

    // Token 使用
    private final DistributionSummary tokenUsage;

    public void recordRequest(String model, String status) {
        requestCounter.increment(
            Tags.of("model", model, "status", status)
        );
    }

    public void recordTokens(String model, int tokens) {
        tokenUsage.record(tokens);
    }
}
```

### 6.2 健康检查

```java
@Component
public class LlmHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // 检查模型可用性
            llmClient.ping();
            return Health.up()
                .withDetail("model", "available")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

---

## 7. 部署架构

### 7.1 开发环境

```yaml
# docker-compose.yml
version: "3.8"
services:
  postgres:
    image: pgvector/pgvector:pg16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: aiworkbench
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

### 7.2 生产环境

```
┌─────────────────────────────────────────────────────────────┐
│                        Load Balancer                         │
│                         (Nginx/ALB)                          │
└─────────────────────────┬───────────────────────────────────┘
                          │
         ┌───────────────┬┴┬───────────────┐
         ↓               ↓               ↓
    ┌─────────┐     ┌─────────┐     ┌─────────┐
    │ App-1   │     │ App-2   │     │ App-3   │
    │ (Pod)   │     │ (Pod)   │     │ (Pod)   │
    └────┬────┘     └────┬────┘     └────┬────┘
         └───────────────┼───────────────┘
                         ↓
    ┌─────────────────────────────────────────────┐
    │               Data Layer                     │
    │  ┌─────────┐  ┌─────────┐  ┌─────────┐    │
    │  │ PG主    │  │ PG从    │  │ Redis   │    │
    │  │ (RDS)   │  │ (RDS)   │  │ Cluster │    │
    │  └─────────┘  └─────────┘  └─────────┘    │
    └─────────────────────────────────────────────┘
```

---

## 附录

### A. 环境变量

```bash
# 数据库
DATABASE_URL=postgresql://user:pass@localhost:5432/aiworkbench

# Redis
REDIS_URL=redis://localhost:6379

# LLM API Keys
GEMINI_API_KEY=xxx
OPENAI_API_KEY=xxx

# 安全
JWT_SECRET=xxx
ENCRYPTION_KEY=xxx
```

### B. 参考资料

- LangChain4j 文档
- Spring AI 文档
- pgvector 使用指南
- OWASP LLM Top 10
