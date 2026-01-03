package phase25;

/**
 * OpenAI API 调用示例
 * 
 * 演示如何使用 Java 调用 OpenAI API
 * 包含原生 HTTP 调用和推荐的库调用方式
 * 
 * @author Java Course
 * @version 1.0
 */
public class OpenAiClient {

    // API 配置 (实际使用时从环境变量或配置文件读取)
    private static final String API_KEY = "your-api-key-here";
    private static final String API_BASE = "https://api.openai.com/v1";

    public static void main(String[] args) {
        System.out.println("=== OpenAI API 调用教程 ===\n");

        // 1. API 基础知识
        apiBasics();

        // 2. 请求格式
        requestFormat();

        // 3. 原生 HTTP 调用示例
        httpExample();

        // 4. 使用 LangChain4j 调用
        langchain4jExample();

        // 5. 错误处理
        errorHandling();

        // 6. 最佳实践
        bestPractices();
    }

    /**
     * 1. API 基础知识
     */
    static void apiBasics() {
        System.out.println("📚 1. OpenAI API 基础");
        System.out.println("=".repeat(50));

        String basics = """
                【获取 API Key】
                1. 访问 https://platform.openai.com
                2. 注册/登录账号
                3. 进入 API Keys 页面
                4. 创建新的 Secret Key
                5. 保存好！只显示一次

                【API 端点】
                ┌───────────────────────────────────────────────────┐
                │  端点                    │ 用途                  │
                ├───────────────────────────────────────────────────┤
                │  /v1/chat/completions   │ 对话 (主要使用)       │
                │  /v1/embeddings         │ 文本向量化            │
                │  /v1/images/generations │ 图片生成              │
                │  /v1/audio/speech       │ 文字转语音            │
                │  /v1/audio/transcriptions│ 语音转文字           │
                └───────────────────────────────────────────────────┘

                【认证方式】
                HTTP Header: Authorization: Bearer sk-xxxxxxxxxxxx

                【使用限制】
                ┌─────────────────────────────────────────────────┐
                │  • RPM (Requests Per Minute): 请求频率限制     │
                │  • TPM (Tokens Per Minute): Token频率限制      │
                │  • 不同模型限制不同                            │
                │  • 付费用户限制更宽松                          │
                └─────────────────────────────────────────────────┘
                """;
        System.out.println(basics);
    }

    /**
     * 2. 请求格式说明
     */
    static void requestFormat() {
        System.out.println("\n📋 2. Chat Completions 请求格式");
        System.out.println("=".repeat(50));

        String format = """
                【请求示例 (JSON)】

                POST https://api.openai.com/v1/chat/completions

                {
                  "model": "gpt-4",
                  "messages": [
                    {
                      "role": "system",
                      "content": "你是一个友好的助手"
                    },
                    {
                      "role": "user",
                      "content": "你好，请介绍一下Java"
                    }
                  ],
                  "temperature": 0.7,
                  "max_tokens": 1000,
                  "top_p": 1,
                  "frequency_penalty": 0,
                  "presence_penalty": 0
                }

                【参数说明】
                ┌───────────────────────────────────────────────────────┐
                │  参数               │ 说明                │ 推荐值   │
                ├───────────────────────────────────────────────────────┤
                │  model             │ 模型名称            │ gpt-4    │
                │  messages          │ 对话历史            │ -        │
                │  temperature       │ 随机性 (0-2)        │ 0.7      │
                │  max_tokens        │ 最大输出长度        │ 1000     │
                │  top_p             │ 核采样概率          │ 1        │
                │  frequency_penalty │ 重复惩罚 (-2 to 2)  │ 0        │
                │  presence_penalty  │ 主题惩罚 (-2 to 2)  │ 0        │
                └───────────────────────────────────────────────────────┘

                【消息角色】
                ┌─────────────────────────────────────────────────┐
                │  role      │ 说明                             │
                ├─────────────────────────────────────────────────┤
                │  system    │ 系统提示，设定AI行为             │
                │  user      │ 用户消息                         │
                │  assistant │ AI的回复（用于多轮对话）         │
                └─────────────────────────────────────────────────┘

                【响应示例】

                {
                  "id": "chatcmpl-xxx",
                  "object": "chat.completion",
                  "created": 1677858242,
                  "model": "gpt-4",
                  "usage": {
                    "prompt_tokens": 13,
                    "completion_tokens": 150,
                    "total_tokens": 163
                  },
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "Java 是一种..."
                      },
                      "finish_reason": "stop",
                      "index": 0
                    }
                  ]
                }
                """;
        System.out.println(format);
    }

    /**
     * 3. 原生 HTTP 调用示例
     */
    static void httpExample() {
        System.out.println("\n💻 3. Java 原生 HTTP 调用示例");
        System.out.println("=".repeat(50));

        System.out.println("【完整代码示例】\n");

        String code = """
                /**
                 * 使用 Java 原生 HttpURLConnection 调用 OpenAI API
                 */
                public class OpenAiHttpClient {

                    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
                    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

                    public static String chat(String userMessage) throws Exception {
                        // 1. 构建请求体
                        String requestBody = \"""
                            {
                                "model": "gpt-4",
                                "messages": [
                                    {"role": "system", "content": "你是一个有帮助的助手"},
                                    {"role": "user", "content": "%s"}
                                ],
                                "temperature": 0.7
                            }
                            \""".formatted(userMessage);

                        // 2. 创建连接
                        URI uri = URI.create(API_URL);
                        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                        conn.setDoOutput(true);

                        // 3. 发送请求
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        // 4. 读取响应
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                response.append(line);
                            }
                        }

                        // 5. 解析响应 (实际应用中使用 JSON 库)
                        return response.toString();
                    }

                    public static void main(String[] args) {
                        try {
                            String response = chat("用一句话解释什么是多态");
                            System.out.println(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                """;
        System.out.println(code);

        System.out.println("""

                ⚠️ 注意事项:
                1. API Key 不要硬编码，使用环境变量
                2. 实际项目建议使用 JSON 库解析响应
                3. 要处理超时、重试等异常情况
                """);
    }

    /**
     * 4. 使用 LangChain4j 调用 (推荐)
     */
    static void langchain4jExample() {
        System.out.println("\n🔗 4. 使用 LangChain4j 调用 (推荐)");
        System.out.println("=".repeat(50));

        String example = """
                【Maven 依赖】

                <dependency>
                    <groupId>dev.langchain4j</groupId>
                    <artifactId>langchain4j-open-ai</artifactId>
                    <version>0.27.0</version>
                </dependency>

                【简单示例】

                import dev.langchain4j.model.openai.OpenAiChatModel;

                public class LangChain4jDemo {

                    public static void main(String[] args) {
                        // 1. 创建模型实例
                        OpenAiChatModel model = OpenAiChatModel.builder()
                            .apiKey(System.getenv("OPENAI_API_KEY"))
                            .modelName("gpt-4")
                            .temperature(0.7)
                            .build();

                        // 2. 简单对话
                        String response = model.generate("Java中的接口和抽象类有什么区别？");
                        System.out.println(response);
                    }
                }

                【使用 AI Service (高级用法)】

                import dev.langchain4j.service.AiServices;
                import dev.langchain4j.service.SystemMessage;
                import dev.langchain4j.service.UserMessage;

                // 定义接口
                interface JavaExpert {

                    @SystemMessage("你是一个资深Java专家，擅长解释复杂概念")
                    String explain(@UserMessage String topic);

                    @SystemMessage("你是一个代码审查专家")
                    String review(@UserMessage String code);
                }

                // 使用
                public class AiServiceDemo {
                    public static void main(String[] args) {
                        OpenAiChatModel model = OpenAiChatModel.builder()
                            .apiKey(System.getenv("OPENAI_API_KEY"))
                            .modelName("gpt-4")
                            .build();

                        // 创建 AI Service
                        JavaExpert expert = AiServices.create(JavaExpert.class, model);

                        // 像调用普通方法一样使用
                        String explanation = expert.explain("什么是 JVM 垃圾回收？");
                        System.out.println(explanation);
                    }
                }

                ✅ 优势:
                - 代码简洁，类型安全
                - 自动处理请求/响应序列化
                - 内置重试、超时等机制
                - 支持流式输出
                """;
        System.out.println(example);
    }

    /**
     * 5. 错误处理
     */
    static void errorHandling() {
        System.out.println("\n⚠️ 5. 错误处理");
        System.out.println("=".repeat(50));

        String errors = """
                【常见错误码】

                ┌────────────────────────────────────────────────────────┐
                │  错误码  │ 含义                │ 解决方案             │
                ├────────────────────────────────────────────────────────┤
                │  401    │ 认证失败            │ 检查 API Key         │
                │  429    │ 请求过多（限流）    │ 降低频率/等待重试    │
                │  500    │ 服务器错误          │ 稍后重试             │
                │  503    │ 服务过载            │ 稍后重试             │
                └────────────────────────────────────────────────────────┘

                【重试策略示例】

                public class RetryableOpenAiClient {

                    private static final int MAX_RETRIES = 3;
                    private static final long INITIAL_DELAY_MS = 1000;

                    public String chatWithRetry(String message) {
                        int retries = 0;
                        long delay = INITIAL_DELAY_MS;

                        while (retries < MAX_RETRIES) {
                            try {
                                return chat(message);
                            } catch (RateLimitException e) {
                                retries++;
                                if (retries >= MAX_RETRIES) throw e;

                                // 指数退避
                                try {
                                    Thread.sleep(delay);
                                    delay *= 2; // 1s, 2s, 4s...
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        throw new RuntimeException("Max retries exceeded");
                    }
                }

                【超时设置】

                OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .maxRetries(3)
                    .build();
                """;
        System.out.println(errors);
    }

    /**
     * 6. 最佳实践
     */
    static void bestPractices() {
        System.out.println("\n✅ 6. 最佳实践");
        System.out.println("=".repeat(50));

        System.out.println("""
                【安全性】
                ┌─────────────────────────────────────────────────┐
                │ ✅ API Key 存储在环境变量或密钥管理服务        │
                │ ✅ 不要在前端暴露 API Key                      │
                │ ✅ 设置 API Key 的权限范围                     │
                │ ✅ 定期轮换 API Key                            │
                │ ✅ 监控 API 使用量，设置预算告警               │
                └─────────────────────────────────────────────────┘

                【性能优化】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 使用连接池复用 HTTP 连接                    │
                │ ✅ 对常见查询结果做缓存                        │
                │ ✅ 批量请求减少 API 调用次数                   │
                │ ✅ 使用流式输出提升用户体验                    │
                │ ✅ 异步调用避免阻塞主线程                      │
                └─────────────────────────────────────────────────┘

                【成本控制】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 根据任务复杂度选择合适的模型                │
                │ ✅ 精简 Prompt 减少 token 消耗                 │
                │ ✅ 设置 max_tokens 防止意外长输出              │
                │ ✅ 监控每日/每月 token 使用量                  │
                │ ✅ 简单任务用 GPT-3.5，复杂任务用 GPT-4        │
                └─────────────────────────────────────────────────┘

                【代码组织】

                // 推荐的项目结构
                src/
                ├── main/java/com/example/
                │   ├── config/
                │   │   └── OpenAiConfig.java      // API配置
                │   ├── client/
                │   │   └── OpenAiClient.java      // API客户端
                │   ├── service/
                │   │   └── ChatService.java       // 业务逻辑
                │   └── controller/
                │       └── ChatController.java    // API接口
                └── resources/
                    └── application.yml            // 配置文件
                """);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ 了解 OpenAI API 的请求/响应格式
                ✅ 推荐使用 LangChain4j 简化开发
                ✅ 重视错误处理和重试机制
                ✅ API Key 安全存储很重要
                ✅ 注意成本控制和性能优化

                下一节: AzureOpenAi.java - Azure OpenAI 服务集成
                """);
    }
}
