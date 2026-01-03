package com.example.chatbot.config;

import com.example.chatbot.memory.ChatMemoryManager;
import com.example.chatbot.rag.FaqRetriever;
import com.example.chatbot.service.CustomerServiceBot;
import com.example.chatbot.tools.OrderTools;
import com.example.chatbot.tools.ProductTools;
import com.example.chatbot.tools.TicketTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.time.Duration;

/**
 * 应用配置类
 * 
 * 负责创建和配置所有组件
 */
public class AppConfig {

    // 模型类型
    public enum ModelType {
        OPENAI, // OpenAI API
        OLLAMA, // 本地 Ollama
        DEMO // 演示模式（Mock）
    }

    private final ModelType modelType;
    private final ChatLanguageModel chatModel;
    private final ChatMemoryManager memoryManager;
    private final OrderTools orderTools;
    private final ProductTools productTools;
    private final TicketTools ticketTools;
    private final FaqRetriever faqRetriever;

    public AppConfig(ModelType modelType) {
        this.modelType = modelType;
        this.chatModel = createChatModel();
        this.memoryManager = new ChatMemoryManager(20);
        this.orderTools = new OrderTools();
        this.productTools = new ProductTools();
        this.ticketTools = new TicketTools();
        this.faqRetriever = new FaqRetriever();
    }

    /**
     * 根据配置创建 Chat 模型
     */
    private ChatLanguageModel createChatModel() {
        return switch (modelType) {
            case OPENAI -> createOpenAiModel();
            case OLLAMA -> createOllamaModel();
            case DEMO -> createDemoModel();
        };
    }

    /**
     * 创建 OpenAI 模型
     */
    private ChatLanguageModel createOpenAiModel() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("⚠️ 未设置 OPENAI_API_KEY，切换到演示模式");
            return createDemoModel();
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4")
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(3)
                .build();
    }

    /**
     * 创建 Ollama 本地模型
     */
    private ChatLanguageModel createOllamaModel() {
        String baseUrl = System.getenv().getOrDefault("OLLAMA_URL", "http://localhost:11434");
        String modelName = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3");

        try {
            return OllamaChatModel.builder()
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(120))
                    .build();
        } catch (Exception e) {
            System.err.println("⚠️ 无法连接 Ollama，切换到演示模式: " + e.getMessage());
            return createDemoModel();
        }
    }

    /**
     * 创建演示模式模型（简单的规则匹配）
     */
    private ChatLanguageModel createDemoModel() {
        System.out.println("📌 运行在演示模式（无实际 LLM 调用）");
        // 返回一个简单的 Mock 模型
        return new DemoChatModel(orderTools, productTools, ticketTools, faqRetriever);
    }

    /**
     * 创建智能客服机器人
     */
    public CustomerServiceBot createChatbot() {
        if (modelType == ModelType.DEMO) {
            // 演示模式使用特殊处理
            return createDemoChatbot();
        }

        return AiServices.builder(CustomerServiceBot.class)
                .chatLanguageModel(chatModel)
                .chatMemoryProvider(memoryManager::getOrCreateMemory)
                .tools(orderTools, productTools, ticketTools, faqRetriever)
                .build();
    }

    /**
     * 创建演示模式机器人
     */
    private CustomerServiceBot createDemoChatbot() {
        return (userId, message) -> {
            DemoChatModel demo = (DemoChatModel) chatModel;
            return demo.processMessage(message);
        };
    }

    // Getters
    public ChatLanguageModel getChatModel() {
        return chatModel;
    }

    public ChatMemoryManager getMemoryManager() {
        return memoryManager;
    }

    public OrderTools getOrderTools() {
        return orderTools;
    }

    public ProductTools getProductTools() {
        return productTools;
    }

    public TicketTools getTicketTools() {
        return ticketTools;
    }

    public FaqRetriever getFaqRetriever() {
        return faqRetriever;
    }

    public ModelType getModelType() {
        return modelType;
    }
}
