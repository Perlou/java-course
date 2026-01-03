# 智能客服机器人 (Smart Customer Service Chatbot)

> Phase 25 实战项目：基于 LangChain4j 的智能客服系统

## 🎯 项目简介

这是一个综合运用 Phase 25 所学技术构建的智能客服机器人：

- ✅ **多轮对话**: 支持上下文记忆的连续对话
- ✅ **意图识别**: 自动识别用户意图并路由处理
- ✅ **Function Calling**: 通过工具调用查询订单、商品、处理工单
- ✅ **知识库问答**: 基于 FAQ 知识库回答常见问题
- ✅ **多用户支持**: 不同用户会话完全隔离

## 🏗️ 项目结构

```
smart-chatbot/
├── pom.xml
└── src/main/java/com/example/chatbot/
    ├── ChatbotApplication.java      # 主入口
    ├── config/
    │   └── AppConfig.java           # 模型配置
    ├── service/
    │   └── CustomerServiceBot.java  # AI Service
    ├── tools/
    │   ├── OrderTools.java          # 订单查询
    │   ├── ProductTools.java        # 商品查询
    │   └── TicketTools.java         # 工单处理
    ├── memory/
    │   └── ChatMemoryManager.java   # 会话管理
    ├── rag/
    │   ├── KnowledgeLoader.java     # 知识加载
    │   └── FaqRetriever.java        # FAQ检索
    └── model/
        ├── Order.java
        ├── Product.java
        └── Ticket.java
```

## 🚀 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+
- OpenAI API Key 或 本地 Ollama

### 运行方式

**方式 1: 使用 OpenAI**

```bash
export OPENAI_API_KEY=sk-your-key-here
mvn compile exec:java -Dexec.mainClass="com.example.chatbot.ChatbotApplication"
```

**方式 2: 使用本地 Ollama**

```bash
# 先启动 Ollama 并拉取模型
ollama pull llama3

# 运行项目
mvn compile exec:java -Dexec.mainClass="com.example.chatbot.ChatbotApplication" -Dmodel=ollama
```

**方式 3: 演示模式 (无需 API)**

```bash
mvn compile exec:java -Dexec.mainClass="com.example.chatbot.ChatbotApplication" -Dmodel=demo
```

## 💬 使用示例

```
╔══════════════════════════════════════════════════════════╗
║         智能客服机器人 - Phase 25 实战项目               ║
╚══════════════════════════════════════════════════════════╝

您好！我是智能客服小助手，可以帮您：
• 查询订单状态和物流
• 了解商品信息和库存
• 处理退款和换货申请
• 回答常见问题

请输入您的问题 (输入 'quit' 退出):

> 我的订单 ORD-12345 到哪了？
🤖 您的订单 ORD-12345 目前正在配送中，预计明天下午送达。
   物流单号：SF1234567890

> iPhone 15 还有货吗？
🤖 iPhone 15 目前有货，库存充足。
   - 价格：¥6999
   - 颜色：黑色/白色/蓝色
   - 配送：下单后24小时内发货

> 我想退货
🤖 好的，我帮您创建退货申请。请问您要退的是哪个订单？

> ORD-12345
🤖 已为订单 ORD-12345 创建退货工单 T-20240101-001。
   预计3个工作日内审核完成，届时会短信通知您。

> 你们的退货政策是什么？
🤖 根据我们的退货政策：
   • 商品签收后7天内可申请无理由退货
   • 退货商品需保持原包装完好
   • 电子产品若已激活则不支持退货
   • 退款将在审核通过后3-5个工作日内原路返还
```

## 🔧 技术要点

### 1. AI Service 声明式接口

```java
interface CustomerServiceBot {
    @SystemMessage("""
        你是一个专业的电商客服。
        态度友好，回复简洁。
        """)
    String chat(@MemoryId String userId, String message);
}
```

### 2. Function Calling

```java
@Tool("查询订单状态和物流信息")
public String queryOrder(@P("订单号") String orderId) {
    return orderService.getStatus(orderId);
}
```

### 3. 对话记忆

```java
ChatMemoryProvider memoryProvider = userId ->
    MessageWindowChatMemory.builder()
        .id(userId)
        .maxMessages(20)
        .build();
```

## 📚 学习收获

通过这个项目，你将掌握：

1. **LangChain4j AI Service** - 声明式 AI 接口设计
2. **Function Calling** - 让 AI 调用外部工具
3. **对话记忆管理** - 多用户会话隔离
4. **简化版 RAG** - 基于内存的知识检索
5. **Prompt Engineering** - 系统提示词设计

---

> 📖 相关学习资料: [Phase 25 学习笔记](../../src/main/java/phase25/README.md)
