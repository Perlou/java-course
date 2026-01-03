package phase25;

/**
 * Azure OpenAI 服务集成
 * 
 * Azure OpenAI 是微软提供的企业级 OpenAI 服务
 * 适合需要合规、SLA保障的企业场景
 * 
 * @author Java Course
 * @version 1.0
 */
public class AzureOpenAi {

    public static void main(String[] args) {
        System.out.println("=== Azure OpenAI 服务集成 ===\n");

        // 1. Azure OpenAI 简介
        introduction();

        // 2. 配置步骤
        setupSteps();

        // 3. API 调用示例
        apiExample();

        // 4. 与 OpenAI 的区别
        differences();

        // 5. 企业最佳实践
        enterprisePractices();
    }

    /**
     * 1. Azure OpenAI 简介
     */
    static void introduction() {
        System.out.println("☁️ 1. Azure OpenAI 简介");
        System.out.println("=".repeat(50));

        String intro = """
                【什么是 Azure OpenAI】

                Azure OpenAI Service 是微软与 OpenAI 合作推出的企业级服务，
                在 Azure 云平台上提供 OpenAI 模型的访问能力。

                【核心优势】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 企业级 SLA 保障 (99.9% 可用性)              │
                │ ✅ 数据隔离，不用于模型训练                    │
                │ ✅ 符合各种合规要求 (GDPR, SOC2 等)           │
                │ ✅ 与 Azure 生态无缝集成                       │
                │ ✅ 私有端点，网络隔离                          │
                │ ✅ 统一的 Azure 计费和管理                     │
                └─────────────────────────────────────────────────┘

                【可用模型】
                ┌────────────────────────────────────────────────────┐
                │  模型              │ 说明                         │
                ├────────────────────────────────────────────────────┤
                │  GPT-4 Turbo       │ 最新最强大模型               │
                │  GPT-4             │ 高级推理能力                 │
                │  GPT-4 Vision      │ 图片理解能力                 │
                │  GPT-3.5 Turbo     │ 快速且经济                   │
                │  Embeddings        │ 文本向量化                   │
                │  DALL-E 3          │ 图像生成                     │
                │  Whisper           │ 语音转文字                   │
                └────────────────────────────────────────────────────┘

                【部署区域】
                • 美国 (East US, West US, South Central US)
                • 欧洲 (France Central, UK South, Sweden Central)
                • 亚太 (Australia East, Japan East)

                📌 注意: 中国区暂不可用，需使用国际版 Azure
                """;
        System.out.println(intro);
    }

    /**
     * 2. 配置步骤
     */
    static void setupSteps() {
        System.out.println("\n🔧 2. 配置步骤");
        System.out.println("=".repeat(50));

        String steps = """
                【Step 1: 申请访问权限】

                1. 访问 Azure 门户: https://portal.azure.com
                2. 搜索 "Azure OpenAI"
                3. 填写申请表单，等待审批（通常1-3天）

                ⚠️ Azure OpenAI 需要企业账号申请，个人账号审批较难通过

                【Step 2: 创建 Azure OpenAI 资源】

                1. 在 Azure 门户点击 "创建资源"
                2. 搜索 "Azure OpenAI"
                3. 填写配置:
                   - 订阅: 选择你的订阅
                   - 资源组: 新建或选择现有
                   - 区域: 选择支持 GPT-4 的区域
                   - 名称: 如 "my-openai-service"
                   - 定价层: Standard S0

                【Step 3: 部署模型】

                1. 进入创建的 OpenAI 资源
                2. 点击 "模型部署" → "创建新部署"
                3. 选择模型 (如 gpt-4)
                4. 设置部署名称 (如 "gpt-4-deployment")
                5. 配置容量 (TPM: Tokens Per Minute)

                【Step 4: 获取访问凭证】

                需要的信息:
                ┌─────────────────────────────────────────────────┐
                │ • Endpoint: https://xxx.openai.azure.com        │
                │ • API Key: 在 "密钥和终结点" 页面获取         │
                │ • Deployment Name: 模型部署时设置的名称       │
                │ • API Version: 如 2024-02-15-preview          │
                └─────────────────────────────────────────────────┘
                """;
        System.out.println(steps);
    }

    /**
     * 3. API 调用示例
     */
    static void apiExample() {
        System.out.println("\n💻 3. Java 调用示例");
        System.out.println("=".repeat(50));

        String example = """
                【Maven 依赖】

                <dependency>
                    <groupId>dev.langchain4j</groupId>
                    <artifactId>langchain4j-azure-open-ai</artifactId>
                    <version>0.27.0</version>
                </dependency>

                【LangChain4j 调用示例】

                import dev.langchain4j.model.azure.AzureOpenAiChatModel;

                public class AzureOpenAiDemo {

                    public static void main(String[] args) {
                        // 1. 创建模型实例
                        AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                            .deploymentName("gpt-4-deployment")  // 你的部署名称
                            .temperature(0.7)
                            .build();

                        // 2. 发送消息
                        String response = model.generate("解释Java中的Stream API");
                        System.out.println(response);
                    }
                }

                【原生 HTTP 调用】

                // 端点格式与 OpenAI 不同
                String url = String.format(
                    "%s/openai/deployments/%s/chat/completions?api-version=%s",
                    endpoint,        // https://xxx.openai.azure.com
                    deploymentName,  // gpt-4-deployment
                    apiVersion       // 2024-02-15-preview
                );

                // 请求头使用 api-key 而非 Authorization
                conn.setRequestProperty("api-key", apiKey);

                // 请求体与 OpenAI 相同，但不需要 model 字段
                String body = \"""
                {
                    "messages": [
                        {"role": "user", "content": "Hello"}
                    ],
                    "temperature": 0.7
                }
                \""";

                【Spring Boot 集成】

                // application.yml
                azure:
                  openai:
                    endpoint: ${AZURE_OPENAI_ENDPOINT}
                    api-key: ${AZURE_OPENAI_KEY}
                    deployment-name: gpt-4-deployment

                // 配置类
                @Configuration
                public class AzureOpenAiConfig {

                    @Value("${azure.openai.endpoint}")
                    private String endpoint;

                    @Value("${azure.openai.api-key}")
                    private String apiKey;

                    @Value("${azure.openai.deployment-name}")
                    private String deploymentName;

                    @Bean
                    public AzureOpenAiChatModel chatModel() {
                        return AzureOpenAiChatModel.builder()
                            .endpoint(endpoint)
                            .apiKey(apiKey)
                            .deploymentName(deploymentName)
                            .build();
                    }
                }
                """;
        System.out.println(example);
    }

    /**
     * 4. 与 OpenAI 的区别
     */
    static void differences() {
        System.out.println("\n🔄 4. Azure OpenAI vs OpenAI");
        System.out.println("=".repeat(50));

        String diff = """
                【API 差异对比】

                ┌─────────────────────────────────────────────────────────────┐
                │  维度           │ OpenAI              │ Azure OpenAI        │
                ├─────────────────────────────────────────────────────────────┤
                │  认证方式       │ Authorization Header│ api-key Header      │
                │  端点格式       │ api.openai.com      │ xxx.openai.azure.com│
                │  模型指定       │ model 参数          │ URL 中的 deployment │
                │  API 版本       │ 自动更新            │ 需指定版本号        │
                │  模型可用性     │ 第一时间            │ 通常延迟1-3个月     │
                │  计费           │ 信用卡              │ Azure 订阅          │
                └─────────────────────────────────────────────────────────────┘

                【代码改动点】

                // OpenAI
                String url = "https://api.openai.com/v1/chat/completions";
                request.setHeader("Authorization", "Bearer sk-xxx");
                body = {"model": "gpt-4", "messages": [...]}

                // Azure OpenAI
                String url = "https://xxx.openai.azure.com/openai/deployments/gpt-4-deployment/chat/completions?api-version=2024-02-15-preview";
                request.setHeader("api-key", "xxxx");
                body = {"messages": [...]}  // 不需要 model 字段

                【切换策略】

                // 抽象层设计，方便切换
                public interface LLMProvider {
                    String chat(String message);
                }

                public class OpenAiProvider implements LLMProvider {
                    private final OpenAiChatModel model;
                    // ...
                }

                public class AzureOpenAiProvider implements LLMProvider {
                    private final AzureOpenAiChatModel model;
                    // ...
                }

                // 通过配置切换
                @Bean
                public LLMProvider llmProvider() {
                    if (useAzure) {
                        return new AzureOpenAiProvider(azureConfig);
                    }
                    return new OpenAiProvider(openAiConfig);
                }
                """;
        System.out.println(diff);
    }

    /**
     * 5. 企业最佳实践
     */
    static void enterprisePractices() {
        System.out.println("\n🏢 5. 企业最佳实践");
        System.out.println("=".repeat(50));

        System.out.println("""
                【安全配置】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 使用 Azure Key Vault 存储 API Key           │
                │ ✅ 配置私有端点 (Private Endpoint)             │
                │ ✅ 启用托管身份 (Managed Identity)            │
                │ ✅ 设置网络访问控制 (VNet)                     │
                │ ✅ 启用诊断日志和监控                          │
                └─────────────────────────────────────────────────┘

                【成本管理】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 设置配额限制 (TPM: Token Per Minute)        │
                │ ✅ 配置成本预警 (Cost Alert)                   │
                │ ✅ 定期审查使用报告                            │
                │ ✅ 开发/生产环境分离部署                       │
                └─────────────────────────────────────────────────┘

                【高可用部署】

                // 多区域容灾配置
                Primary Region: East US
                  └── gpt-4-deployment (PTU: 10)

                Secondary Region: West US
                  └── gpt-4-deployment (PTU: 10)

                // 负载均衡配置
                @Component
                public class AzureOpenAiLoadBalancer {

                    private final List<AzureOpenAiChatModel> models;
                    private final AtomicInteger counter = new AtomicInteger();

                    public String chat(String message) {
                        int index = counter.getAndIncrement() % models.size();
                        try {
                            return models.get(index).generate(message);
                        } catch (Exception e) {
                            // 故障转移到下一个区域
                            return models.get((index + 1) % models.size())
                                         .generate(message);
                        }
                    }
                }

                【合规检查清单】
                ┌─────────────────────────────────────────────────┐
                │ ☐ 确认数据驻留区域满足合规要求                 │
                │ ☐ 审查内容过滤策略                             │
                │ ☐ 配置审计日志保留策略                         │
                │ ☐ 确认没有敏感数据发送到模型                   │
                │ ☐ 签署 Data Processing Agreement              │
                └─────────────────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ Azure OpenAI 适合企业级应用
                ✅ 提供 SLA 保障和合规认证
                ✅ API 与 OpenAI 略有不同，需适配
                ✅ 利用 Azure 生态实现安全管控
                ✅ 设计抽象层便于切换提供商

                下一节: LocalLlm.java - 本地模型部署
                """);
    }
}
