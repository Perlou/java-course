package phase26;

/**
 * ReAct 模式
 * 
 * ReAct (Reasoning + Acting) 是一种结合推理和行动的 Agent 模式，
 * 让 LLM 能够交替进行思考和执行。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class ReActAgent {

    /**
     * ========================================
     * 第一部分：ReAct 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== ReAct 概述 ===");
        System.out.println();

        System.out.println("【什么是 ReAct】");
        System.out.println("  Reasoning + Acting = ReAct");
        System.out.println("  推理与行动交替进行的 Agent 范式");
        System.out.println("  来自论文: \"ReAct: Synergizing Reasoning and Acting in LLMs\"");
        System.out.println();

        System.out.println("【核心思想】");
        System.out.println("  • Thought: 思考下一步该做什么");
        System.out.println("  • Action: 选择并执行工具");
        System.out.println("  • Observation: 观察执行结果");
        System.out.println("  • 循环直到任务完成");
        System.out.println();

        System.out.println("【优势】");
        System.out.println("  • 可解释性强: 每步都有推理过程");
        System.out.println("  • 错误可追溯: 容易定位问题");
        System.out.println("  • 灵活性高: 动态调整策略");
    }

    /**
     * ========================================
     * 第二部分：ReAct 循环
     * ========================================
     */
    public static void explainReActLoop() {
        System.out.println("=== ReAct 循环 ===");
        System.out.println();

        System.out.println("【执行流程】");
        System.out.println("```");
        System.out.println("用户问题: \"北京今天天气如何？适合户外运动吗？\"");
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ Thought 1: 用户想知道北京天气，我需要查询天气信息       │");
        System.out.println("│ Action 1: get_weather(city=\"北京\")                      │");
        System.out.println("│ Observation 1: 晴天，25°C，空气质量良                   │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ Thought 2: 晴天25度，还需判断是否适合户外运动           │");
        System.out.println("│            天气不错，空气质量良，应该适合                │");
        System.out.println("│ Action 2: finish(answer=\"今天北京晴天...\")             │");
        System.out.println("│ Observation 2: (任务完成)                               │");
        System.out.println("└──────────────────────────────────────────────────────────┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【伪代码】");
        System.out.println("```python");
        System.out.println("while not done:");
        System.out.println("    # 1. 思考");
        System.out.println("    thought = llm.think(context)");
        System.out.println("    ");
        System.out.println("    # 2. 选择行动");
        System.out.println("    action = llm.choose_action(thought)");
        System.out.println("    ");
        System.out.println("    # 3. 执行");
        System.out.println("    observation = execute(action)");
        System.out.println("    ");
        System.out.println("    # 4. 更新上下文");
        System.out.println("    context.append(thought, action, observation)");
        System.out.println("    ");
        System.out.println("    # 5. 检查是否完成");
        System.out.println("    if action.type == 'finish':");
        System.out.println("        done = True");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：LangChain4j 实现
     * ========================================
     */
    public static void explainLangChain4jImpl() {
        System.out.println("=== LangChain4j 实现 ===");
        System.out.println();

        System.out.println("【定义工具】");
        System.out.println("```java");
        System.out.println("public class WeatherTools {");
        System.out.println("    ");
        System.out.println("    @Tool(\"获取城市当前天气\")");
        System.out.println("    public String getWeather(@P(\"城市名\") String city) {");
        System.out.println("        return weatherService.query(city);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Tool(\"搜索互联网信息\")");
        System.out.println("    public String search(@P(\"搜索词\") String query) {");
        System.out.println("        return searchService.search(query);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【创建 Agent】");
        System.out.println("```java");
        System.out.println("// 定义 Agent 接口");
        System.out.println("interface Assistant {");
        System.out.println("    String chat(String message);");
        System.out.println("}");
        System.out.println();
        System.out.println("// 构建 Agent");
        System.out.println("Assistant agent = AiServices.builder(Assistant.class)");
        System.out.println("    .chatLanguageModel(chatModel)");
        System.out.println("    .tools(new WeatherTools())");
        System.out.println("    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("// 使用 Agent");
        System.out.println("String answer = agent.chat(\"北京今天天气如何？\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：其他 Agent 模式
     * ========================================
     */
    public static void explainOtherPatterns() {
        System.out.println("=== 其他 Agent 模式 ===");
        System.out.println();

        System.out.println("【Plan and Execute】");
        System.out.println("  先完整规划，再依次执行");
        System.out.println("  适合：步骤明确的任务");
        System.out.println();

        System.out.println("【ReWOO (Reasoning Without Observation)】");
        System.out.println("  先生成所有步骤，批量执行");
        System.out.println("  减少 LLM 调用次数");
        System.out.println("  适合：工具调用成本高场景");
        System.out.println();

        System.out.println("【Self-Ask】");
        System.out.println("  自问自答式推理");
        System.out.println("  分解复杂问题为子问题");
        System.out.println();

        System.out.println("【Reflexion】");
        System.out.println("  失败后自我反思");
        System.out.println("  从错误中学习改进");
        System.out.println();

        System.out.println("【模式选择】");
        System.out.println("┌─────────────────┬────────────────────────────────┐");
        System.out.println("│ 场景            │ 推荐模式                       │");
        System.out.println("├─────────────────┼────────────────────────────────┤");
        System.out.println("│ 交互式问答      │ ReAct                          │");
        System.out.println("│ 复杂任务编排    │ Plan and Execute               │");
        System.out.println("│ 高并发低延迟    │ ReWOO                          │");
        System.out.println("│ 需要高可靠性    │ Reflexion                      │");
        System.out.println("└─────────────────┴────────────────────────────────┘");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: ReAct 模式                            ║");
        System.out.println("║          推理与行动的交替执行                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainReActLoop();
        System.out.println();

        explainLangChain4jImpl();
        System.out.println();

        explainOtherPatterns();
    }
}
