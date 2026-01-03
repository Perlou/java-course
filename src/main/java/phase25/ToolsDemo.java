package phase25;

/**
 * LangChain4j 工具调用 (Function Calling)
 * 
 * Function Calling 让 AI 能够调用外部工具/函数
 * 是构建 Agent 的核心能力
 * 
 * @author Java Course
 * @version 1.0
 */
public class ToolsDemo {

    public static void main(String[] args) {
        System.out.println("=== LangChain4j 工具调用 (Function Calling) ===\n");

        // 1. 什么是 Function Calling
        whatIsFunctionCalling();

        // 2. 定义工具
        definingTools();

        // 3. 使用工具
        usingTools();

        // 4. 实战案例
        practicalExamples();

        // 5. 构建 Agent
        buildingAgent();
    }

    /**
     * 1. 什么是 Function Calling
     */
    static void whatIsFunctionCalling() {
        System.out.println("🔧 1. 什么是 Function Calling");
        System.out.println("=".repeat(50));

        String what = """
                【LLM 的局限性】

                大模型只能基于训练数据回答问题，无法：
                - 获取实时信息（天气、股票、新闻）
                - 执行具体操作（发邮件、查数据库）
                - 进行精确计算（复杂数学运算）

                【Function Calling 解决方案】

                让 AI "调用" 外部函数/工具来完成任务。

                ┌─────────────────────────────────────────────────────────┐
                │                  Function Calling 流程                   │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  用户: "北京今天天气怎么样？"                            │
                │         ↓                                               │
                │  LLM 分析意图 → 需要调用天气工具                        │
                │         ↓                                               │
                │  LLM 生成工具调用:                                      │
                │  ┌───────────────────────────────────────┐             │
                │  │ function: getWeather                  │             │
                │  │ arguments: {"city": "北京"}           │             │
                │  └───────────────────────────────────────┘             │
                │         ↓                                               │
                │  应用执行函数 → 获取结果: "晴，25°C"                    │
                │         ↓                                               │
                │  LLM 根据结果生成回复:                                  │
                │  "北京今天天气晴朗，气温25度，适合户外活动。"            │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                【核心概念】

                1. 工具定义: 告诉 LLM 有哪些工具可用
                2. 意图识别: LLM 决定是否调用工具
                3. 参数提取: LLM 从用户输入中提取参数
                4. 执行工具: 应用层执行实际函数
                5. 结果整合: LLM 根据结果生成自然语言回复

                【支持 Function Calling 的模型】

                ✅ GPT-4, GPT-3.5-turbo
                ✅ Claude 3
                ✅ Gemini Pro
                ✅ 通义千问
                ❌ 部分开源模型不支持
                """;
        System.out.println(what);
    }

    /**
     * 2. 定义工具
     */
    static void definingTools() {
        System.out.println("\n📋 2. 定义工具 (Tools)");
        System.out.println("=".repeat(50));

        String defining = """
                【使用 @Tool 注解】

                import dev.langchain4j.agent.tool.Tool;
                import dev.langchain4j.agent.tool.P;

                public class WeatherTools {

                    @Tool("获取指定城市的当前天气信息")
                    public String getWeather(
                            @P("城市名称，如：北京、上海") String city) {
                        // 实际调用天气 API
                        return callWeatherApi(city);
                    }

                    @Tool("获取未来N天的天气预报")
                    public String getForecast(
                            @P("城市名称") String city,
                            @P("预报天数，1-7") int days) {
                        return callForecastApi(city, days);
                    }
                }

                【@Tool 注解说明】

                @Tool("工具描述")  // 告诉 LLM 这个工具的用途
                @P("参数描述")     // 告诉 LLM 参数的含义

                ⚠️ 描述要清晰！LLM 根据描述决定何时使用工具

                【更多工具示例】

                public class CalculatorTools {

                    @Tool("计算两个数的四则运算")
                    public double calculate(
                            @P("第一个数") double a,
                            @P("运算符: +, -, *, /") String operator,
                            @P("第二个数") double b) {
                        return switch (operator) {
                            case "+" -> a + b;
                            case "-" -> a - b;
                            case "*" -> a * b;
                            case "/" -> a / b;
                            default -> throw new IllegalArgumentException();
                        };
                    }
                }

                public class DatabaseTools {

                    private final UserRepository userRepository;

                    @Tool("根据用户名查询用户信息")
                    public String findUser(
                            @P("用户名") String username) {
                        return userRepository.findByUsername(username)
                            .map(User::toString)
                            .orElse("未找到用户");
                    }

                    @Tool("统计指定日期范围内的订单数量")
                    public int countOrders(
                            @P("开始日期，格式：yyyy-MM-dd") String startDate,
                            @P("结束日期，格式：yyyy-MM-dd") String endDate) {
                        return orderRepository.countByDateRange(
                            LocalDate.parse(startDate),
                            LocalDate.parse(endDate)
                        );
                    }
                }

                public class EmailTools {

                    @Tool("发送邮件给指定收件人")
                    public String sendEmail(
                            @P("收件人邮箱地址") String to,
                            @P("邮件主题") String subject,
                            @P("邮件正文") String body) {
                        emailService.send(to, subject, body);
                        return "邮件已发送至 " + to;
                    }
                }
                """;
        System.out.println(defining);
    }

    /**
     * 3. 使用工具
     */
    static void usingTools() {
        System.out.println("\n🚀 3. 使用工具");
        System.out.println("=".repeat(50));

        String using = """
                【将工具注入 AI Service】

                // 1. 准备工具实例
                WeatherTools weatherTools = new WeatherTools();
                CalculatorTools calcTools = new CalculatorTools();

                // 2. 定义 AI Service 接口
                interface Assistant {
                    String chat(String message);
                }

                // 3. 创建 AI Service，注入工具
                Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .tools(weatherTools, calcTools)  // 注入工具
                    .build();

                // 4. 使用
                assistant.chat("北京今天天气怎么样？");
                // AI 自动调用 weatherTools.getWeather("北京")
                // 然后根据结果生成回复

                assistant.chat("计算 123 * 456");
                // AI 自动调用 calcTools.calculate(123, "*", 456)

                【带记忆的工具使用】

                Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                    .tools(weatherTools, calcTools, emailTools)
                    .build();

                // 多轮对话中使用工具
                assistant.chat("北京天气怎么样？");
                assistant.chat("那上海呢？");  // 记住了上下文
                assistant.chat("帮我算一下今天温差");  // 结合之前的信息

                【工具执行观察】

                // 添加日志查看工具调用过程
                OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4")
                    .logRequests(true)   // 查看请求
                    .logResponses(true)  // 查看响应
                    .build();

                // 请求日志会显示:
                // tools: [{name: "getWeather", arguments: {"city": "北京"}}]

                【异步工具】

                public class AsyncTools {

                    @Tool("执行耗时的数据分析任务")
                    public CompletableFuture<String> analyzeData(
                            @P("数据集ID") String datasetId) {
                        return CompletableFuture.supplyAsync(() -> {
                            // 耗时操作
                            return doAnalysis(datasetId);
                        });
                    }
                }
                """;
        System.out.println(using);
    }

    /**
     * 4. 实战案例
     */
    static void practicalExamples() {
        System.out.println("\n💼 4. 实战案例");
        System.out.println("=".repeat(50));

        String examples = """
                【案例1：智能客服】

                public class CustomerServiceTools {

                    private final OrderRepository orderRepo;
                    private final TicketService ticketService;

                    @Tool("查询订单状态")
                    public String queryOrder(@P("订单号") String orderId) {
                        return orderRepo.findById(orderId)
                            .map(o -> String.format(
                                "订单%s，状态：%s，预计%s送达",
                                o.getId(), o.getStatus(), o.getEta()))
                            .orElse("未找到订单");
                    }

                    @Tool("创建售后工单")
                    public String createTicket(
                            @P("订单号") String orderId,
                            @P("问题类型：退款/换货/维修") String type,
                            @P("问题描述") String description) {
                        String ticketId = ticketService.create(
                            orderId, type, description);
                        return "已创建工单：" + ticketId;
                    }

                    @Tool("查询退款进度")
                    public String queryRefund(@P("工单号") String ticketId) {
                        return ticketService.getRefundStatus(ticketId);
                    }
                }

                // 使用
                interface CustomerService {
                    @SystemMessage(\"""
                        你是电商客服，帮助用户处理订单相关问题。
                        态度友好，回复简洁专业。
                        \""")
                    String serve(@MemoryId String sessionId, String message);
                }

                CustomerService cs = AiServices.builder(CustomerService.class)
                    .chatLanguageModel(model)
                    .chatMemoryProvider(id -> MessageWindowChatMemory
                        .withMaxMessages(20))
                    .tools(new CustomerServiceTools())
                    .build();

                cs.serve("u123", "我的订单12345到哪了？");
                // AI: "您的订单12345目前正在配送中，预计明天下午送达。"

                cs.serve("u123", "我想申请退款");
                // AI: "好的，我帮您创建了退款工单T-789，预计3个工作日内处理完成。"

                【案例2：数据分析助手】

                public class DataAnalysisTools {

                    private final JdbcTemplate jdbc;

                    @Tool("执行SQL查询，返回结果")
                    public String executeQuery(@P("SQL查询语句") String sql) {
                        // ⚠️ 生产环境要做SQL安全检查！
                        if (!isSelectQuery(sql)) {
                            return "只允许SELECT查询";
                        }
                        List<Map<String, Object>> results =
                            jdbc.queryForList(sql);
                        return formatAsTable(results);
                    }

                    @Tool("获取数据库表结构")
                    public String getTableSchema(@P("表名") String table) {
                        return jdbc.queryForList(
                            "DESCRIBE " + table).toString();
                    }

                    @Tool("生成数据可视化图表")
                    public String generateChart(
                            @P("图表类型：bar/line/pie") String type,
                            @P("数据JSON") String dataJson) {
                        // 调用图表生成服务
                        return chartService.generate(type, dataJson);
                    }
                }

                【案例3：代码执行工具】

                public class CodeExecutionTools {

                    @Tool("执行Python代码并返回结果")
                    public String runPython(@P("Python代码") String code) {
                        // 使用沙箱环境执行
                        return pythonSandbox.execute(code,
                            Duration.ofSeconds(10));
                    }

                    @Tool("在Linux终端执行命令")
                    public String runCommand(@P("Shell命令") String command) {
                        // 白名单检查
                        if (!isAllowedCommand(command)) {
                            return "命令不在允许列表中";
                        }
                        return shellExecutor.run(command);
                    }
                }
                """;
        System.out.println(examples);
    }

    /**
     * 5. 构建 Agent
     */
    static void buildingAgent() {
        System.out.println("\n🤖 5. 构建 Agent");
        System.out.println("=".repeat(50));

        String agent = """
                【什么是 Agent】

                Agent = LLM + 工具 + 自主决策

                普通 AI: 用户问什么答什么
                Agent:   自主规划、调用工具、完成复杂任务

                ┌─────────────────────────────────────────────────────────┐
                │                      Agent 工作流程                      │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  用户: "帮我分析上周销售数据，并发邮件给领导"            │
                │                                                         │
                │  Agent 思考:                                            │
                │  1. 需要查询上周销售数据 → 调用 queryDatabase           │
                │  2. 需要分析数据 → 调用 analyzeData                     │
                │  3. 需要生成报告 → 自己总结                             │
                │  4. 需要发邮件 → 调用 sendEmail                         │
                │                                                         │
                │  Agent 执行:                                            │
                │  ┌─────────────────────────────────────────┐            │
                │  │ Step 1: queryDatabase("sales_2024_w01") │            │
                │  │         结果: [销售数据...]             │            │
                │  │                                         │            │
                │  │ Step 2: analyzeData(销售数据)           │            │
                │  │         结果: 本周销售额增长15%...      │            │
                │  │                                         │            │
                │  │ Step 3: 生成报告摘要                    │            │
                │  │                                         │            │
                │  │ Step 4: sendEmail(领导, "周报", 报告)   │            │
                │  │         结果: 邮件已发送                │            │
                │  └─────────────────────────────────────────┘            │
                │                                                         │
                │  Agent 回复: "已完成分析并将报告发送给领导..."           │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                【LangChain4j Agent 实现】

                // 准备工具
                public class AgentTools {

                    @Tool("查询数据库")
                    String queryDatabase(String query) {...}

                    @Tool("分析数据")
                    String analyzeData(String data) {...}

                    @Tool("发送邮件")
                    String sendEmail(String to, String subject, String body) {...}

                    @Tool("搜索网络信息")
                    String searchWeb(String query) {...}

                    @Tool("读取文件内容")
                    String readFile(String path) {...}
                }

                // 创建 Agent
                interface Agent {
                    @SystemMessage(\"""
                        你是一个智能助手。
                        可以使用各种工具完成用户的任务。
                        每次只执行一个步骤，观察结果后再决定下一步。
                        如果任务完成或无法完成，给出最终答案。
                        \""")
                    String execute(String task);
                }

                Agent agent = AiServices.builder(Agent.class)
                    .chatLanguageModel(model)
                    .tools(new AgentTools())
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                    .build();

                // 执行复杂任务
                String result = agent.execute(
                    "帮我分析上周的销售数据，并把报告邮件给 boss@company.com");

                【Agent 设计原则】

                ┌─────────────────────────────────────────────────┐
                │ 1. 工具描述要清晰，让 AI 知道何时使用         │
                │ 2. 工具粒度适中，不要太粗也不要太细           │
                │ 3. 做好错误处理，工具失败时返回清晰的错误信息 │
                │ 4. 设置超时和重试，避免无限循环               │
                │ 5. 记录日志，便于调试和审计                   │
                │ 6. 敏感操作要有确认机制                       │
                └─────────────────────────────────────────────────┘
                """;
        System.out.println(agent);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 Phase 25 总结");
        System.out.println("=".repeat(50));
        System.out.println("""

                🎉 恭喜完成 Phase 25: 大模型基础与 LangChain!

                第44周回顾:
                ✅ 理解了 Transformer 和注意力机制
                ✅ 掌握了 Prompt Engineering 技巧
                ✅ 了解了 Token 和成本控制
                ✅ 对比了主流大模型
                ✅ 学会了 OpenAI/Azure/本地模型调用

                第45周回顾:
                ✅ 掌握了 LangChain4j 核心组件
                ✅ 学会了 AI Service 声明式开发
                ✅ 理解了 Chain 链式调用
                ✅ 实现了对话记忆管理
                ✅ 掌握了 Function Calling 和 Agent

                下一阶段: Phase 26 - RAG 系统实战 🚀
                """);
    }
}
