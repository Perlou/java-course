package phase26;

/**
 * Spring AI 集成
 * 
 * Spring AI 是 Spring 官方的 AI 集成框架，
 * 提供统一的编程模型接入各种 AI 服务。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class SpringAiDemo {

    /**
     * ========================================
     * 第一部分：Spring AI 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== Spring AI 概述 ===");
        System.out.println();

        System.out.println("【什么是 Spring AI】");
        System.out.println("  • Spring 官方的 AI 集成框架");
        System.out.println("  • 提供统一的 API 抽象");
        System.out.println("  • 支持多种 AI 模型供应商");
        System.out.println("  • 与 Spring 生态无缝集成");
        System.out.println();

        System.out.println("【支持的模型】");
        System.out.println("  • OpenAI (GPT-4, GPT-3.5)");
        System.out.println("  • Azure OpenAI");
        System.out.println("  • Ollama (本地模型)");
        System.out.println("  • HuggingFace");
        System.out.println("  • Anthropic Claude");
        System.out.println("  • Google Vertex AI");
        System.out.println();

        System.out.println("【核心能力】");
        System.out.println("  • Chat 对话");
        System.out.println("  • Embedding 向量化");
        System.out.println("  • Image 图像生成");
        System.out.println("  • Function Calling");
        System.out.println("  • RAG 支持");
    }

    /**
     * ========================================
     * 第二部分：快速开始
     * ========================================
     */
    public static void explainQuickStart() {
        System.out.println("=== 快速开始 ===");
        System.out.println();

        System.out.println("【Maven 依赖】");
        System.out.println("```xml");
        System.out.println("<dependency>");
        System.out.println("    <groupId>org.springframework.ai</groupId>");
        System.out.println("    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>");
        System.out.println("    <version>1.0.0-M4</version>");
        System.out.println("</dependency>");
        System.out.println("```");
        System.out.println();

        System.out.println("【配置】");
        System.out.println("```yaml");
        System.out.println("spring:");
        System.out.println("  ai:");
        System.out.println("    openai:");
        System.out.println("      api-key: ${OPENAI_API_KEY}");
        System.out.println("      chat:");
        System.out.println("        options:");
        System.out.println("          model: gpt-4o");
        System.out.println("          temperature: 0.7");
        System.out.println("```");
        System.out.println();

        System.out.println("【基础使用】");
        System.out.println("```java");
        System.out.println("@RestController");
        System.out.println("public class ChatController {");
        System.out.println();
        System.out.println("    private final ChatClient chatClient;");
        System.out.println();
        System.out.println("    public ChatController(ChatClient.Builder builder) {");
        System.out.println("        this.chatClient = builder.build();");
        System.out.println("    }");
        System.out.println();
        System.out.println("    @GetMapping(\"/chat\")");
        System.out.println("    public String chat(@RequestParam String message) {");
        System.out.println("        return chatClient.prompt()");
        System.out.println("            .user(message)");
        System.out.println("            .call()");
        System.out.println("            .content();");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：向量存储与 RAG
     * ========================================
     */
    public static void explainRAG() {
        System.out.println("=== 向量存储与 RAG ===");
        System.out.println();

        System.out.println("【支持的向量存储】");
        System.out.println("  • Milvus");
        System.out.println("  • Chroma");
        System.out.println("  • Pinecone");
        System.out.println("  • Redis");
        System.out.println("  • pgvector");
        System.out.println("  • Qdrant");
        System.out.println();

        System.out.println("【文档处理】");
        System.out.println("```java");
        System.out.println("// 加载文档");
        System.out.println("List<Document> documents = new TikaDocumentReader(resource).get();");
        System.out.println();
        System.out.println("// 切分文档");
        System.out.println("TextSplitter splitter = new TokenTextSplitter();");
        System.out.println("List<Document> chunks = splitter.split(documents);");
        System.out.println();
        System.out.println("// 存入向量库");
        System.out.println("vectorStore.add(chunks);");
        System.out.println("```");
        System.out.println();

        System.out.println("【RAG 查询】");
        System.out.println("```java");
        System.out.println("@GetMapping(\"/rag\")");
        System.out.println("public String ragQuery(@RequestParam String question) {");
        System.out.println("    // 检索相关文档");
        System.out.println("    List<Document> documents = vectorStore.similaritySearch(");
        System.out.println("        SearchRequest.query(question).withTopK(4)");
        System.out.println("    );");
        System.out.println();
        System.out.println("    // 构建 Prompt");
        System.out.println("    String context = documents.stream()");
        System.out.println("        .map(Document::getContent)");
        System.out.println("        .collect(Collectors.joining(\"\\n\"));");
        System.out.println();
        System.out.println("    return chatClient.prompt()");
        System.out.println("        .system(\"基于以下内容回答问题:\\n\" + context)");
        System.out.println("        .user(question)");
        System.out.println("        .call()");
        System.out.println("        .content();");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Function Calling
     * ========================================
     */
    public static void explainFunctionCalling() {
        System.out.println("=== Function Calling ===");
        System.out.println();

        System.out.println("【定义函数】");
        System.out.println("```java");
        System.out.println("@Bean");
        System.out.println("@Description(\"获取城市当前天气\")");
        System.out.println("public Function<WeatherRequest, WeatherResponse> getWeather() {");
        System.out.println("    return request -> {");
        System.out.println("        // 调用天气 API");
        System.out.println("        return weatherService.getWeather(request.city());");
        System.out.println("    };");
        System.out.println("}");
        System.out.println();
        System.out.println("record WeatherRequest(String city) {}");
        System.out.println("record WeatherResponse(String temp, String desc) {}");
        System.out.println("```");
        System.out.println();

        System.out.println("【使用函数】");
        System.out.println("```java");
        System.out.println("String response = chatClient.prompt()");
        System.out.println("    .user(\"北京今天天气怎么样？\")");
        System.out.println("    .functions(\"getWeather\")  // 启用函数");
        System.out.println("    .call()");
        System.out.println("    .content();");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: Spring AI 集成                        ║");
        System.out.println("║          Spring 官方 AI 框架                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainQuickStart();
        System.out.println();

        explainRAG();
        System.out.println();

        explainFunctionCalling();
    }
}
