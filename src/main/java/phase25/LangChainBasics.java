package phase25;

/**
 * LangChain4j 入门
 * 
 * LangChain4j 是 Java 生态最流行的 LLM 应用开发框架
 * 提供统一的 API 来对接各种大模型
 * 
 * @author Java Course
 * @version 1.0
 */
public class LangChainBasics {

    public static void main(String[] args) {
        System.out.println("=== LangChain4j 入门教程 ===\n");

        // 1. 框架介绍
        introduction();

        // 2. 核心组件
        coreComponents();

        // 3. 快速上手
        quickStart();

        // 4. AI Service
        aiService();

        // 5. 结构化输出
        structuredOutput();
    }

    /**
     * 1. 框架介绍
     */
    static void introduction() {
        System.out.println("📚 1. LangChain4j 简介");
        System.out.println("=".repeat(50));

        String intro = """
                【什么是 LangChain4j】

                LangChain4j 是 LangChain 的 Java 版本，专为 Java/Kotlin 开发者设计。
                它提供了一套优雅的 API 来构建 LLM 应用。

                GitHub: https://github.com/langchain4j/langchain4j

                【为什么选择 LangChain4j】

                ┌─────────────────────────────────────────────────┐
                │ ✅ 统一 API - 一套代码对接多种模型            │
                │ ✅ 类型安全 - 充分利用 Java 类型系统          │
                │ ✅ 简洁优雅 - AI Service 让代码像调用普通接口│
                │ ✅ 功能完善 - 支持 RAG、Agent、Tool 等       │
                │ ✅ 易于集成 - Spring Boot Starter 开箱即用   │
                └─────────────────────────────────────────────────┘

                【支持的模型提供商】

                ┌────────────────────────────────────────────────────┐
                │  提供商           │ 模块                          │
                ├────────────────────────────────────────────────────┤
                │  OpenAI           │ langchain4j-open-ai           │
                │  Azure OpenAI     │ langchain4j-azure-open-ai     │
                │  Anthropic Claude │ langchain4j-anthropic         │
                │  Google Gemini    │ langchain4j-google-ai-gemini  │
                │  阿里通义千问     │ langchain4j-dashscope         │
                │  Ollama (本地)    │ langchain4j-ollama            │
                │  HuggingFace      │ langchain4j-hugging-face      │
                └────────────────────────────────────────────────────┘

                【版本信息】
                当前稳定版: 0.27.0 (2024年)
                """;
        System.out.println(intro);
    }

    /**
     * 2. 核心组件
     */
    static void coreComponents() {
        System.out.println("\n🔧 2. 核心组件");
        System.out.println("=".repeat(50));

        String components = """
                【组件架构图】

                ┌─────────────────────────────────────────────────────────┐
                │                    LangChain4j 组件                      │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  ┌───────────────┐  ┌───────────────┐                  │
                │  │  AI Service   │  │  Chat Model   │                  │
                │  │  高级抽象接口  │  │  对话模型封装  │                  │
                │  └───────────────┘  └───────────────┘                  │
                │                                                         │
                │  ┌───────────────┐  ┌───────────────┐                  │
                │  │  Embedding    │  │  Memory       │                  │
                │  │  文本向量化    │  │  对话记忆管理  │                  │
                │  └───────────────┘  └───────────────┘                  │
                │                                                         │
                │  ┌───────────────┐  ┌───────────────┐                  │
                │  │  Tools        │  │  RAG          │                  │
                │  │  工具调用      │  │  检索增强生成  │                  │
                │  └───────────────┘  └───────────────┘                  │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                【核心概念】

                1. ChatLanguageModel (对话模型)
                   最基础的组件，负责与 LLM 通信

                2. AiServices (AI 服务)
                   声明式接口，像调用本地方法一样调用 AI

                3. ChatMemory (对话记忆)
                   管理多轮对话的上下文

                4. Tools (工具)
                   让 AI 能够调用外部功能

                5. EmbeddingModel (嵌入模型)
                   文本向量化，用于相似度搜索

                6. DocumentSplitter (文档分割)
                   将长文档切分为小块，用于 RAG
                """;
        System.out.println(components);
    }

    /**
     * 3. 快速上手
     */
    static void quickStart() {
        System.out.println("\n🚀 3. 快速上手");
        System.out.println("=".repeat(50));

        String quickStart = """
                【Maven 依赖】

                <!-- 核心库 -->
                <dependency>
                    <groupId>dev.langchain4j</groupId>
                    <artifactId>langchain4j</artifactId>
                    <version>0.27.0</version>
                </dependency>

                <!-- OpenAI 支持 -->
                <dependency>
                    <groupId>dev.langchain4j</groupId>
                    <artifactId>langchain4j-open-ai</artifactId>
                    <version>0.27.0</version>
                </dependency>

                【最简示例 - 单轮对话】

                import dev.langchain4j.model.openai.OpenAiChatModel;

                public class HelloLangChain {
                    public static void main(String[] args) {
                        // 创建模型
                        OpenAiChatModel model = OpenAiChatModel.builder()
                            .apiKey(System.getenv("OPENAI_API_KEY"))
                            .modelName("gpt-4")
                            .build();

                        // 发送消息
                        String answer = model.generate("Java 17 有哪些新特性？");
                        System.out.println(answer);
                    }
                }

                【带 System Prompt】

                import dev.langchain4j.model.chat.ChatLanguageModel;
                import dev.langchain4j.data.message.*;

                ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4")
                    .build();

                // 构建消息列表
                List<ChatMessage> messages = List.of(
                    SystemMessage.from("你是一个 Java 专家，回答要简洁专业"),
                    UserMessage.from("什么是 Stream API？")
                );

                // 发送并获取回复
                AiMessage response = model.generate(messages).content();
                System.out.println(response.text());

                【参数配置】

                OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4")
                    .temperature(0.7)      // 创造性 (0-2)
                    .maxTokens(1000)       // 最大输出
                    .timeout(Duration.ofSeconds(30))
                    .maxRetries(3)         // 重试次数
                    .logRequests(true)     // 日志请求
                    .logResponses(true)    // 日志响应
                    .build();
                """;
        System.out.println(quickStart);
    }

    /**
     * 4. AI Service (重点!)
     */
    static void aiService() {
        System.out.println("\n⭐ 4. AI Service (核心特性)");
        System.out.println("=".repeat(50));

        String aiService = """
                【什么是 AI Service】

                AI Service 是 LangChain4j 的杀手级特性！
                它让你用声明式接口来定义 AI 能力，代码极其简洁。

                【基本用法】

                // 1. 定义接口
                interface Assistant {
                    String chat(String userMessage);
                }

                // 2. 创建 AI Service
                ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey).modelName("gpt-4").build();

                Assistant assistant = AiServices.create(Assistant.class, model);

                // 3. 像调用普通方法一样使用
                String answer = assistant.chat("你好!");

                【使用注解】

                import dev.langchain4j.service.*;

                interface JavaExpert {

                    @SystemMessage("你是一个资深 Java 专家，擅长解释复杂概念")
                    String explain(String topic);

                    @SystemMessage("你是一个严格的代码审查者")
                    @UserMessage("请审查以下代码:\\n{{code}}")
                    String review(@V("code") String sourceCode);

                    @SystemMessage("你是一个 Java 面试官")
                    @UserMessage("请出一道关于 {{topic}} 的面试题，难度为 {{level}}")
                    String generateQuestion(
                        @V("topic") String topic,
                        @V("level") String level
                    );
                }

                // 使用
                JavaExpert expert = AiServices.create(JavaExpert.class, model);

                expert.explain("什么是 JVM 垃圾回收？");
                expert.review("public static void main(String[] args) {...}");
                expert.generateQuestion("多线程", "中等");

                【模板变量】

                @UserMessage("翻译以下{{lang}}代码为Java:\\n{{code}}")
                String translate(
                    @V("lang") String language,
                    @V("code") String code
                );

                // 调用
                expert.translate("Python", "print('hello')");

                【多模型切换】

                // 开发环境用便宜的模型
                ChatLanguageModel devModel = OpenAiChatModel.builder()
                    .apiKey(apiKey).modelName("gpt-3.5-turbo").build();

                // 生产环境用强大的模型
                ChatLanguageModel prodModel = OpenAiChatModel.builder()
                    .apiKey(apiKey).modelName("gpt-4").build();

                // 同一个接口，不同模型
                Assistant devAssistant = AiServices.create(Assistant.class, devModel);
                Assistant prodAssistant = AiServices.create(Assistant.class, prodModel);
                """;
        System.out.println(aiService);
    }

    /**
     * 5. 结构化输出
     */
    static void structuredOutput() {
        System.out.println("\n📋 5. 结构化输出");
        System.out.println("=".repeat(50));

        String structured = """
                【为什么需要结构化输出】

                默认情况下，AI 返回的是纯文本。
                但我们经常需要 JSON、对象等结构化数据。
                LangChain4j 提供了优雅的解决方案！

                【返回 Java 对象】

                // 定义数据类
                record Person(String name, int age, List<String> skills) {}

                // 定义接口
                interface PersonExtractor {
                    @UserMessage("从以下文本提取人物信息: {{text}}")
                    Person extract(@V("text") String text);
                }

                // 使用
                PersonExtractor extractor = AiServices.create(
                    PersonExtractor.class, model);

                Person person = extractor.extract(
                    "张三今年25岁，擅长Java、Python和SQL");

                System.out.println(person.name());   // 张三
                System.out.println(person.age());    // 25
                System.out.println(person.skills()); // [Java, Python, SQL]

                【返回枚举】

                enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }

                interface SentimentAnalyzer {
                    @UserMessage("分析以下文本的情感倾向: {{text}}")
                    Sentiment analyze(@V("text") String text);
                }

                SentimentAnalyzer analyzer = AiServices.create(
                    SentimentAnalyzer.class, model);

                Sentiment result = analyzer.analyze("这个产品太棒了！");
                // 返回: POSITIVE

                【返回列表】

                interface IdeaGenerator {
                    @UserMessage("给出5个关于{{topic}}的创意名称")
                    List<String> generate(@V("topic") String topic);
                }

                List<String> ideas = generator.generate("健身App");
                // 返回: [健身达人, FitPro, 运动伴侣, ...]

                【复杂对象】

                record CodeReview(
                    String summary,
                    List<Issue> issues,
                    int score
                ) {}

                record Issue(
                    String type,      // BUG, STYLE, PERFORMANCE
                    int lineNumber,
                    String description,
                    String suggestion
                ) {}

                interface CodeReviewer {
                    @SystemMessage("你是代码审查专家")
                    @UserMessage("审查以下代码:\\n{{code}}")
                    CodeReview review(@V("code") String code);
                }

                CodeReview result = reviewer.review(sourceCode);
                result.issues().forEach(issue ->
                    System.out.printf("第%d行: %s%n",
                        issue.lineNumber(), issue.description())
                );
                """;
        System.out.println(structured);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ LangChain4j 是 Java LLM 开发首选框架
                ✅ 统一 API 对接多种模型提供商
                ✅ AI Service 让调用 AI 像调用本地方法
                ✅ 结构化输出自动解析为 Java 对象
                ✅ 类型安全，编译期捕获错误

                下一节: ChainDemo.java - 学习链式调用
                """);
    }
}
