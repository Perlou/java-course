package phase25;

/**
 * LangChain4j 对话记忆管理
 * 
 * Memory 组件让 AI 能够记住之前的对话内容
 * 实现连贯的多轮对话体验
 * 
 * @author Java Course
 * @version 1.0
 */
public class MemoryDemo {

    public static void main(String[] args) {
        System.out.println("=== LangChain4j 对话记忆管理 ===\n");

        // 1. 为什么需要 Memory
        whyMemory();

        // 2. Memory 类型
        memoryTypes();

        // 3. 使用 ChatMemory
        chatMemoryUsage();

        // 4. 多用户会话
        multiUserSessions();

        // 5. 持久化存储
        persistentStorage();
    }

    /**
     * 1. 为什么需要 Memory
     */
    static void whyMemory() {
        System.out.println("💭 1. 为什么需要对话记忆");
        System.out.println("=".repeat(50));

        String why = """
                【问题：LLM 本身没有记忆】

                每次 API 调用都是独立的，模型不知道之前聊过什么。

                第一轮: "我叫张三"
                AI回复: "你好张三！"

                第二轮: "我叫什么名字？"
                AI回复: "抱歉，我不知道你的名字..." ❌

                【解决方案：对话记忆】

                将历史对话存储下来，每次请求时一起发送给模型。

                ┌─────────────────────────────────────────────────┐
                │  请求内容 (带记忆):                            │
                │                                                 │
                │  messages: [                                    │
                │    {role: "user", content: "我叫张三"},        │
                │    {role: "assistant", content: "你好张三！"}, │
                │    {role: "user", content: "我叫什么名字？"}   │
                │  ]                                              │
                │                                                 │
                │  AI回复: "你叫张三。" ✅                        │
                └─────────────────────────────────────────────────┘

                【记忆管理的挑战】

                1. Token 限制: 历史太长会超出上下文窗口
                2. 成本控制: 历史越长，token 消耗越多
                3. 相关性:  不是所有历史都需要保留

                【LangChain4j 的解决方案】

                提供多种 ChatMemory 实现，自动管理对话历史。
                """;
        System.out.println(why);
    }

    /**
     * 2. Memory 类型
     */
    static void memoryTypes() {
        System.out.println("\n📦 2. Memory 类型");
        System.out.println("=".repeat(50));

        String types = """
                【LangChain4j 提供的 Memory 类型】

                ┌────────────────────────────────────────────────────────┐
                │  类型                    │  说明                       │
                ├────────────────────────────────────────────────────────┤
                │  MessageWindowChatMemory │  保留最近 N 条消息          │
                │  TokenWindowChatMemory   │  保留最近 N 个 token        │
                └────────────────────────────────────────────────────────┘

                【MessageWindowChatMemory】

                保留最近 N 条消息，超出后自动删除最早的。

                示例 (maxMessages = 10):
                - 第1-10条: 全部保留
                - 第11条加入时: 删除第1条，保留2-11条
                - 第12条加入时: 删除第2条，保留3-12条

                适用场景: 短对话、简单问答

                【TokenWindowChatMemory】

                按 token 数量限制，保证不超过指定 token 数。

                示例 (maxTokens = 2000):
                - 持续累积消息
                - 当总 token 接近 2000 时
                - 删除最早的消息腾出空间

                适用场景: 需要精确控制 token 消耗

                【选择建议】

                ┌───────────────────────────────────────────────────────┐
                │  场景                        │  推荐              │
                ├───────────────────────────────────────────────────────┤
                │  简单对话 (< 10轮)          │  MessageWindow(20) │
                │  长对话、知识密集型          │  TokenWindow(2000) │
                │  成本敏感                    │  TokenWindow       │
                │  需要精确控制上下文          │  TokenWindow       │
                └───────────────────────────────────────────────────────┘
                """;
        System.out.println(types);
    }

    /**
     * 3. ChatMemory 使用
     */
    static void chatMemoryUsage() {
        System.out.println("\n💻 3. ChatMemory 使用");
        System.out.println("=".repeat(50));

        String usage = """
                【基本用法】

                import dev.langchain4j.memory.chat.MessageWindowChatMemory;
                import dev.langchain4j.memory.ChatMemory;

                // 创建 Memory
                ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

                // 定义 AI Service
                interface Assistant {
                    String chat(String message);
                }

                // 创建带记忆的 AI Service
                Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(memory)  // 注入记忆
                    .build();

                // 多轮对话
                assistant.chat("我叫张三");          // "你好张三！"
                assistant.chat("我是一名Java开发");   // "作为Java开发..."
                assistant.chat("我叫什么名字？");     // "你叫张三。" ✅

                【手动管理消息】

                ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

                // 添加消息
                memory.add(UserMessage.from("你好"));
                memory.add(AiMessage.from("你好！有什么可以帮你的？"));

                // 获取所有消息
                List<ChatMessage> messages = memory.messages();

                // 清空记忆
                memory.clear();

                【带 System Prompt】

                interface JavaExpert {
                    @SystemMessage("你是一个Java专家，回答要简洁专业")
                    String chat(String message);
                }

                // System Message 会自动添加到每次请求的开头
                JavaExpert expert = AiServices.builder(JavaExpert.class)
                    .chatLanguageModel(model)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                    .build();

                expert.chat("什么是Stream API？");
                expert.chat("它和传统for循环有什么区别？");  // 记住了上下文

                【TokenWindowChatMemory 用法】

                import dev.langchain4j.memory.chat.TokenWindowChatMemory;
                import dev.langchain4j.model.Tokenizer;
                import dev.langchain4j.model.openai.OpenAiTokenizer;

                // 需要指定 Tokenizer 来计算 token
                Tokenizer tokenizer = new OpenAiTokenizer("gpt-4");

                ChatMemory memory = TokenWindowChatMemory.builder()
                    .maxTokens(2000)
                    .tokenizer(tokenizer)
                    .build();

                Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(memory)
                    .build();
                """;
        System.out.println(usage);
    }

    /**
     * 4. 多用户会话管理
     */
    static void multiUserSessions() {
        System.out.println("\n👥 4. 多用户会话管理");
        System.out.println("=".repeat(50));

        String multiUser = """
                【问题：单一 Memory 无法区分用户】

                如果多个用户共享一个 Memory，对话会混淆！

                用户A: "我是张三"
                用户B: "我叫什么？"
                AI回复给B: "你是张三"  ❌ 错误!

                【解决方案：ChatMemoryProvider】

                为每个用户/会话创建独立的 Memory。

                // 定义带 MemoryId 的接口
                interface Assistant {
                    String chat(@MemoryId String sessionId, String message);
                }

                // ChatMemoryProvider: 根据 ID 返回对应的 Memory
                ChatMemoryProvider memoryProvider = sessionId ->
                    MessageWindowChatMemory.builder()
                        .id(sessionId)
                        .maxMessages(20)
                        .build();

                Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemoryProvider(memoryProvider)  // 使用 Provider
                    .build();

                // 不同用户的对话互相隔离
                assistant.chat("user-A", "我是张三");
                assistant.chat("user-B", "我是李四");

                assistant.chat("user-A", "我叫什么？");  // "张三" ✅
                assistant.chat("user-B", "我叫什么？");  // "李四" ✅

                【完整示例】

                import dev.langchain4j.service.MemoryId;
                import java.util.Map;
                import java.util.concurrent.ConcurrentHashMap;

                public class ChatService {

                    private final Map<String, ChatMemory> memories =
                        new ConcurrentHashMap<>();
                    private final Assistant assistant;

                    public ChatService(ChatLanguageModel model) {
                        this.assistant = AiServices.builder(Assistant.class)
                            .chatLanguageModel(model)
                            .chatMemoryProvider(this::getOrCreateMemory)
                            .build();
                    }

                    private ChatMemory getOrCreateMemory(Object sessionId) {
                        return memories.computeIfAbsent(
                            sessionId.toString(),
                            id -> MessageWindowChatMemory.builder()
                                .id(id)
                                .maxMessages(20)
                                .build()
                        );
                    }

                    public String chat(String sessionId, String message) {
                        return assistant.chat(sessionId, message);
                    }

                    public void clearSession(String sessionId) {
                        memories.remove(sessionId);
                    }
                }

                // 使用
                ChatService service = new ChatService(model);

                // 每个 WebSocket 连接有自己的 sessionId
                service.chat("ws-conn-123", "你好");
                service.chat("ws-conn-456", "Hello");
                """;
        System.out.println(multiUser);
    }

    /**
     * 5. 持久化存储
     */
    static void persistentStorage() {
        System.out.println("\n💾 5. Memory 持久化");
        System.out.println("=".repeat(50));

        String persistence = """
                【为什么需要持久化】

                默认的 Memory 存储在内存中：
                - 应用重启后丢失
                - 无法跨服务器共享

                持久化后：
                - 用户可以继续之前的对话
                - 微服务架构下可共享会话

                【自定义 ChatMemoryStore】

                import dev.langchain4j.store.memory.chat.ChatMemoryStore;
                import dev.langchain4j.data.message.ChatMessage;

                // 实现 ChatMemoryStore 接口
                public class RedisChatMemoryStore implements ChatMemoryStore {

                    private final RedisTemplate<String, String> redis;
                    private final ObjectMapper mapper = new ObjectMapper();

                    @Override
                    public List<ChatMessage> getMessages(Object memoryId) {
                        String key = "chat:" + memoryId;
                        String json = redis.opsForValue().get(key);
                        if (json == null) return new ArrayList<>();
                        return deserialize(json);
                    }

                    @Override
                    public void updateMessages(Object memoryId,
                                               List<ChatMessage> messages) {
                        String key = "chat:" + memoryId;
                        String json = serialize(messages);
                        redis.opsForValue().set(key, json,
                            Duration.ofHours(24));  // 24小时过期
                    }

                    @Override
                    public void deleteMessages(Object memoryId) {
                        redis.delete("chat:" + memoryId);
                    }
                }

                // 使用自定义 Store
                ChatMemoryStore store = new RedisChatMemoryStore(redisTemplate);

                ChatMemory memory = MessageWindowChatMemory.builder()
                    .id("user-123")
                    .maxMessages(20)
                    .chatMemoryStore(store)  // 注入自定义 Store
                    .build();

                【数据库存储示例】

                @Entity
                @Table(name = "chat_messages")
                public class ChatMessageEntity {
                    @Id
                    private Long id;
                    private String sessionId;
                    private String role;      // USER, ASSISTANT, SYSTEM
                    private String content;
                    private LocalDateTime createdAt;
                }

                public class JpaChatMemoryStore implements ChatMemoryStore {

                    private final ChatMessageRepository repository;

                    @Override
                    public List<ChatMessage> getMessages(Object memoryId) {
                        return repository.findBySessionIdOrderByCreatedAt(
                            memoryId.toString())
                            .stream()
                            .map(this::toMessage)
                            .collect(Collectors.toList());
                    }

                    @Override
                    public void updateMessages(Object memoryId,
                                               List<ChatMessage> messages) {
                        // 清除旧消息，保存新消息
                        repository.deleteBySessionId(memoryId.toString());
                        messages.forEach(msg ->
                            repository.save(toEntity(memoryId, msg)));
                    }
                }

                【内存缓存 + 持久化组合】

                // 使用 Caffeine 缓存 + Redis 持久化
                LoadingCache<String, ChatMemory> cache = Caffeine.newBuilder()
                    .maximumSize(1000)
                    .expireAfterAccess(Duration.ofMinutes(30))
                    .build(sessionId -> loadFromRedis(sessionId));

                // 定期同步到 Redis
                @Scheduled(fixedRate = 60000)
                public void syncToRedis() {
                    cache.asMap().forEach(this::saveToRedis);
                }
                """;
        System.out.println(persistence);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ LLM 本身无记忆，需要 Memory 组件管理
                ✅ MessageWindowChatMemory - 按消息条数限制
                ✅ TokenWindowChatMemory - 按 token 数限制
                ✅ ChatMemoryProvider 实现多用户会话隔离
                ✅ ChatMemoryStore 实现对话持久化

                下一节: ToolsDemo.java - 工具调用 (Function Calling)
                """);
    }
}
