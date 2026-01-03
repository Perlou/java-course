package com.example.chatbot;

import com.example.chatbot.config.AppConfig;
import com.example.chatbot.config.AppConfig.ModelType;
import com.example.chatbot.service.CustomerServiceBot;

import java.util.Scanner;
import java.util.UUID;

/**
 * 智能客服机器人 - 主入口
 * 
 * Phase 25 实战项目：综合运用大模型技术构建客服系统
 * 
 * 运行方式：
 * 1. OpenAI 模式: 设置 OPENAI_API_KEY 环境变量
 * 2. Ollama 模式: 启动本地 Ollama 服务
 * 3. 演示模式: 无需任何配置
 * 
 * @author Java Course
 * @version 1.0
 */
public class ChatbotApplication {

    private static final String BANNER = """

            ╔══════════════════════════════════════════════════════════╗
            ║         🤖 智能客服机器人 - Phase 25 实战项目            ║
            ╚══════════════════════════════════════════════════════════╝
            """;

    private static final String WELCOME = """

            您好！我是智能客服小助手，可以帮您：
            • 📦 查询订单状态和物流
            • 🛍️ 了解商品信息和库存
            • 💰 处理退款和换货申请
            • ❓ 回答常见问题

            请输入您的问题 (输入 'quit' 退出, 'clear' 清除对话):

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            """;

    public static void main(String[] args) {
        // 打印横幅
        System.out.println(BANNER);

        // 确定模型类型
        ModelType modelType = determineModelType(args);
        System.out.println("🔧 当前模式: " + getModelDescription(modelType));

        // 创建配置和机器人
        AppConfig config = new AppConfig(modelType);
        CustomerServiceBot chatbot = config.createChatbot();

        // 生成用户会话ID
        String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);
        System.out.println("📝 会话ID: " + userId);

        // 打印欢迎信息
        System.out.println(WELCOME);

        // 开始交互
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                // 读取用户输入
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                // 检查退出命令
                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                    System.out.println("\n感谢使用，再见！👋\n");
                    break;
                }

                // 检查清除命令
                if (input.equalsIgnoreCase("clear")) {
                    config.getMemoryManager().clearMemory(userId);
                    System.out.println("✅ 对话记录已清除");
                    continue;
                }

                // 检查帮助命令
                if (input.equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                }

                // 空输入跳过
                if (input.isEmpty()) {
                    continue;
                }

                try {
                    // 调用 AI 获取回复
                    System.out.println("\n🤔 思考中...");
                    long startTime = System.currentTimeMillis();

                    String response = chatbot.chat(userId, input);

                    long elapsed = System.currentTimeMillis() - startTime;

                    // 打印回复
                    System.out.println("\n🤖 " + response);
                    System.out.printf("\n   [耗时: %dms]%n", elapsed);

                } catch (Exception e) {
                    System.err.println("\n❌ 处理出错: " + e.getMessage());
                    if (System.getenv("DEBUG") != null) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 确定模型类型
     */
    private static ModelType determineModelType(String[] args) {
        // 从命令行参数获取
        for (String arg : args) {
            if (arg.startsWith("-Dmodel=")) {
                String model = arg.substring("-Dmodel=".length()).toLowerCase();
                return switch (model) {
                    case "openai" -> ModelType.OPENAI;
                    case "ollama" -> ModelType.OLLAMA;
                    default -> ModelType.DEMO;
                };
            }
        }

        // 从系统属性获取
        String modelProp = System.getProperty("model", "").toLowerCase();
        if (!modelProp.isEmpty()) {
            return switch (modelProp) {
                case "openai" -> ModelType.OPENAI;
                case "ollama" -> ModelType.OLLAMA;
                default -> ModelType.DEMO;
            };
        }

        // 自动检测
        if (System.getenv("OPENAI_API_KEY") != null) {
            return ModelType.OPENAI;
        }

        // 默认演示模式
        return ModelType.DEMO;
    }

    /**
     * 获取模型描述
     */
    private static String getModelDescription(ModelType modelType) {
        return switch (modelType) {
            case OPENAI -> "OpenAI GPT-4";
            case OLLAMA -> "Ollama 本地模型";
            case DEMO -> "演示模式 (规则匹配)";
        };
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("""

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                📖 使用帮助
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                【命令】
                quit  - 退出程序
                clear - 清除对话记录
                help  - 显示帮助信息

                【功能示例】
                • "查询订单 ORD-12345"
                • "iPhone 15 有货吗？"
                • "我要退款"
                • "你们的退货政策是什么？"

                【测试订单号】
                • ORD-12345 (配送中)
                • ORD-12346 (已签收)
                • ORD-12347 (已付款)

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """);
    }
}
