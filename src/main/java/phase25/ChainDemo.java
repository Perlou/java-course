package phase25;

/**
 * LangChain4j 链式调用
 * 
 * Chain 是 LangChain 的核心概念
 * 将多个处理步骤组合成一个流水线
 * 
 * @author Java Course
 * @version 1.0
 */
public class ChainDemo {

    public static void main(String[] args) {
        System.out.println("=== LangChain4j 链式调用 ===\n");

        // 1. Chain 概念
        chainConcept();

        // 2. 简单 Chain
        simpleChain();

        // 3. 顺序 Chain
        sequentialChain();

        // 4. 条件 Chain
        conditionalChain();

        // 5. RAG Chain
        ragChain();
    }

    /**
     * 1. Chain 概念
     */
    static void chainConcept() {
        System.out.println("🔗 1. Chain 概念");
        System.out.println("=".repeat(50));

        String concept = """
                【什么是 Chain】

                Chain 是将多个处理步骤串联起来的流水线。
                每个步骤的输出成为下一个步骤的输入。

                ┌───────────────────────────────────────────────────┐
                │                    Chain 流水线                    │
                ├───────────────────────────────────────────────────┤
                │                                                   │
                │   输入 ─→ [步骤1] ─→ [步骤2] ─→ [步骤3] ─→ 输出  │
                │              │          │          │              │
                │           提取信息    分析处理    生成结果        │
                │                                                   │
                └───────────────────────────────────────────────────┘

                【为什么需要 Chain】

                1. 分而治之 - 复杂任务拆解为简单步骤
                2. 可复用   - 每个步骤可以独立复用
                3. 可测试   - 每个步骤可以单独测试
                4. 可扩展   - 容易添加新的处理步骤

                【常见 Chain 类型】

                ┌──────────────────────────────────────────────────┐
                │  类型              │  说明                       │
                ├──────────────────────────────────────────────────┤
                │  Simple Chain      │  单一 LLM 调用             │
                │  Sequential Chain  │  多步骤顺序执行             │
                │  Router Chain      │  根据条件路由到不同分支     │
                │  RAG Chain         │  检索 + 生成               │
                │  Agent Chain       │  自主决策执行工具           │
                └──────────────────────────────────────────────────┘
                """;
        System.out.println(concept);
    }

    /**
     * 2. 简单 Chain
     */
    static void simpleChain() {
        System.out.println("\n⛓️ 2. 简单 Chain");
        System.out.println("=".repeat(50));

        String simple = """
                【Prompt Template Chain】

                最基础的 Chain，就是一个带模板的 LLM 调用。

                import dev.langchain4j.model.input.PromptTemplate;

                // 创建 Prompt 模板
                PromptTemplate template = PromptTemplate.from(
                    "你是一个{{role}}。请回答以下问题：{{question}}"
                );

                // 填充变量
                Map<String, Object> variables = Map.of(
                    "role", "Java专家",
                    "question", "什么是多态？"
                );

                Prompt prompt = template.apply(variables);

                // 调用模型
                String response = model.generate(prompt.text());

                【使用 AI Service 简化】

                interface QAAssistant {
                    @UserMessage("你是一个{{role}}。请回答：{{question}}")
                    String answer(
                        @V("role") String role,
                        @V("question") String question
                    );
                }

                QAAssistant assistant = AiServices.create(QAAssistant.class, model);
                String answer = assistant.answer("Java专家", "什么是多态？");

                【链式处理示例】

                // 步骤1: 翻译
                interface Translator {
                    @UserMessage("将以下英文翻译成中文：{{text}}")
                    String translate(@V("text") String englishText);
                }

                // 步骤2: 总结
                interface Summarizer {
                    @UserMessage("用一句话总结：{{text}}")
                    String summarize(@V("text") String text);
                }

                // 组合使用
                Translator translator = AiServices.create(Translator.class, model);
                Summarizer summarizer = AiServices.create(Summarizer.class, model);

                String englishArticle = "Java is a programming language...";
                String chinese = translator.translate(englishArticle);
                String summary = summarizer.summarize(chinese);
                """;
        System.out.println(simple);
    }

    /**
     * 3. 顺序 Chain
     */
    static void sequentialChain() {
        System.out.println("\n📋 3. 顺序 Chain (Sequential)");
        System.out.println("=".repeat(50));

        String sequential = """
                【多步骤顺序处理】

                将复杂任务分解为多个步骤，依次执行。

                ┌─────────────────────────────────────────────────────┐
                │  用户提问 ─→ 意图识别 ─→ 查询知识库 ─→ 生成回答    │
                └─────────────────────────────────────────────────────┘

                【代码实现】

                // 定义各个步骤的接口

                enum Intent { QUESTION, COMPLAINT, FEEDBACK, OTHER }

                interface IntentClassifier {
                    @UserMessage("分类用户意图：{{message}}")
                    Intent classify(@V("message") String message);
                }

                interface KnowledgeQuerier {
                    @UserMessage("根据问题查询相关知识：{{question}}")
                    String query(@V("question") String question);
                }

                interface ResponseGenerator {
                    @SystemMessage("你是一个友好的客服")
                    @UserMessage("根据知识：{{knowledge}}\\n回答问题：{{question}}")
                    String generate(
                        @V("knowledge") String knowledge,
                        @V("question") String question
                    );
                }

                // 组合成 Chain
                public class CustomerServiceChain {

                    private final IntentClassifier classifier;
                    private final KnowledgeQuerier querier;
                    private final ResponseGenerator generator;

                    public CustomerServiceChain(ChatLanguageModel model) {
                        this.classifier = AiServices.create(IntentClassifier.class, model);
                        this.querier = AiServices.create(KnowledgeQuerier.class, model);
                        this.generator = AiServices.create(ResponseGenerator.class, model);
                    }

                    public String process(String userMessage) {
                        // Step 1: 意图识别
                        Intent intent = classifier.classify(userMessage);
                        System.out.println("识别意图: " + intent);

                        if (intent == Intent.QUESTION) {
                            // Step 2: 查询知识
                            String knowledge = querier.query(userMessage);

                            // Step 3: 生成回答
                            return generator.generate(knowledge, userMessage);
                        }

                        return "感谢您的反馈，我们会认真处理。";
                    }
                }

                // 使用
                CustomerServiceChain chain = new CustomerServiceChain(model);
                String response = chain.process("如何重置密码？");

                【流式处理版本】

                // 使用 CompletableFuture 实现异步流水线
                public CompletableFuture<String> processAsync(String message) {
                    return CompletableFuture.supplyAsync(() -> classifier.classify(message))
                        .thenCompose(intent -> {
                            if (intent == Intent.QUESTION) {
                                return CompletableFuture.supplyAsync(() ->
                                    querier.query(message))
                                    .thenApply(knowledge ->
                                        generator.generate(knowledge, message));
                            }
                            return CompletableFuture.completedFuture("感谢反馈");
                        });
                }
                """;
        System.out.println(sequential);
    }

    /**
     * 4. 条件 Chain
     */
    static void conditionalChain() {
        System.out.println("\n🔀 4. 条件 Chain (Router)");
        System.out.println("=".repeat(50));

        String conditional = """
                【根据条件路由到不同处理分支】

                ┌─────────────────────────────────────────────────────┐
                │                                                     │
                │                    ┌─→ [代码生成器] ──→ 代码       │
                │                    │                               │
                │  用户输入 → [路由] ├─→ [翻译器] ──────→ 翻译结果   │
                │                    │                               │
                │                    └─→ [问答助手] ────→ 回答       │
                │                                                     │
                └─────────────────────────────────────────────────────┘

                【路由器实现】

                enum TaskType { CODE, TRANSLATE, QA, OTHER }

                interface TaskRouter {
                    @UserMessage(\"""
                        分析用户请求类型:
                        - CODE: 需要写代码
                        - TRANSLATE: 需要翻译
                        - QA: 问答
                        - OTHER: 其他

                        用户请求: {{request}}
                        \""")
                    TaskType route(@V("request") String request);
                }

                interface CodeGenerator {
                    @SystemMessage("你是代码专家")
                    @UserMessage("{{request}}")
                    String generate(@V("request") String request);
                }

                interface TranslatorService {
                    @SystemMessage("你是翻译专家")
                    @UserMessage("{{request}}")
                    String translate(@V("request") String request);
                }

                interface QAService {
                    @SystemMessage("你是知识问答专家")
                    @UserMessage("{{request}}")
                    String answer(@V("request") String request);
                }

                【组合成路由 Chain】

                public class RouterChain {

                    private final TaskRouter router;
                    private final CodeGenerator codeGen;
                    private final TranslatorService translator;
                    private final QAService qa;

                    public RouterChain(ChatLanguageModel model) {
                        this.router = AiServices.create(TaskRouter.class, model);
                        this.codeGen = AiServices.create(CodeGenerator.class, model);
                        this.translator = AiServices.create(TranslatorService.class, model);
                        this.qa = AiServices.create(QAService.class, model);
                    }

                    public String process(String request) {
                        // 路由决策
                        TaskType type = router.route(request);
                        System.out.println("路由到: " + type);

                        // 分发到对应处理器
                        return switch (type) {
                            case CODE -> codeGen.generate(request);
                            case TRANSLATE -> translator.translate(request);
                            case QA -> qa.answer(request);
                            case OTHER -> "抱歉，我无法处理这个请求";
                        };
                    }
                }

                // 使用
                RouterChain chain = new RouterChain(model);

                chain.process("用Java写一个快速排序");  // → CODE
                chain.process("把Hello翻译成中文");      // → TRANSLATE
                chain.process("什么是设计模式？");        // → QA
                """;
        System.out.println(conditional);
    }

    /**
     * 5. RAG Chain
     */
    static void ragChain() {
        System.out.println("\n📚 5. RAG Chain (检索增强生成)");
        System.out.println("=".repeat(50));

        String rag = """
                【什么是 RAG】

                RAG = Retrieval Augmented Generation (检索增强生成)
                先从知识库检索相关内容，再让 LLM 基于这些内容生成回答。

                ┌─────────────────────────────────────────────────────────┐
                │                       RAG 流程                          │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  用户问题                                               │
                │      ↓                                                  │
                │  ┌─────────────┐                                       │
                │  │  Embedding  │  将问题转换为向量                     │
                │  └──────┬──────┘                                       │
                │         ↓                                               │
                │  ┌─────────────┐                                       │
                │  │ 向量检索    │  从知识库找相似文档                   │
                │  └──────┬──────┘                                       │
                │         ↓                                               │
                │  ┌─────────────────────────────────────────┐           │
                │  │  Context: 检索到的相关文档片段          │           │
                │  │  Question: 用户原始问题                 │           │
                │  │           ↓                             │           │
                │  │  ┌─────────┐                           │           │
                │  │  │   LLM   │  基于 Context 回答问题    │           │
                │  │  └─────────┘                           │           │
                │  └─────────────────────────────────────────┘           │
                │         ↓                                               │
                │  最终回答                                               │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                【LangChain4j RAG 实现】

                // 1. 准备 Embedding 模型和向量存储
                EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .build();

                EmbeddingStore<TextSegment> embeddingStore =
                    new InMemoryEmbeddingStore<>();

                // 2. 导入文档
                Document document = FileSystemDocumentLoader.loadDocument(
                    Path.of("knowledge.txt"));

                DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
                List<TextSegment> segments = splitter.split(document);

                List<Embedding> embeddings = embeddingModel.embedAll(segments)
                    .content();
                embeddingStore.addAll(embeddings, segments);

                // 3. 创建 RAG 链
                ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(3)
                    .build();

                // 4. 创建带 RAG 的 AI Service
                interface KnowledgeAssistant {
                    String answer(String question);
                }

                KnowledgeAssistant assistant = AiServices.builder(KnowledgeAssistant.class)
                    .chatLanguageModel(chatModel)
                    .contentRetriever(retriever)  // 注入检索器
                    .build();

                // 5. 使用
                String answer = assistant.answer("公司的请假流程是什么？");
                // AI 会基于知识库中的相关内容回答

                【简化版 RAG (Easy RAG)】

                // LangChain4j 提供的简化 API
                import dev.langchain4j.rag.easy.EasyRag;

                // 一行代码创建 RAG
                EasyRag rag = EasyRag.builder()
                    .chatLanguageModel(model)
                    .embeddingModel(embeddingModel)
                    .document(document)
                    .build();

                String answer = rag.ask("什么是公司的核心价值观？");
                """;
        System.out.println(rag);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ Chain 将多个处理步骤串联成流水线
                ✅ Sequential Chain 适合多步骤顺序任务
                ✅ Router Chain 根据条件路由到不同分支
                ✅ RAG Chain 结合检索和生成，解决知识局限
                ✅ AI Service 让链式调用代码更简洁

                下一节: MemoryDemo.java - 对话记忆管理
                """);
    }
}
