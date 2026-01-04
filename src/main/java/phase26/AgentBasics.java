package phase26;

/**
 * AI Agent 架构
 * 
 * AI Agent 是能够自主规划、决策和执行任务的智能体，
 * 是大模型应用的高级形态。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class AgentBasics {

    /**
     * ========================================
     * 第一部分：Agent 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== AI Agent 概述 ===");
        System.out.println();

        System.out.println("【什么是 AI Agent】");
        System.out.println("  能够感知环境、自主决策、执行动作的智能体");
        System.out.println("  不只是问答，而是完成复杂任务");
        System.out.println();

        System.out.println("【Agent vs ChatBot】");
        System.out.println("┌─────────────────┬────────────────┬────────────────┐");
        System.out.println("│                 │ ChatBot        │ Agent          │");
        System.out.println("├─────────────────┼────────────────┼────────────────┤");
        System.out.println("│ 交互模式        │ 一问一答       │ 自主执行       │");
        System.out.println("│ 任务复杂度      │ 单轮简单       │ 多步复杂       │");
        System.out.println("│ 工具使用        │ 无/有限        │ 丰富           │");
        System.out.println("│ 规划能力        │ 无             │ 有             │");
        System.out.println("│ 环境感知        │ 无             │ 有             │");
        System.out.println("└─────────────────┴────────────────┴────────────────┘");
        System.out.println();

        System.out.println("【Agent 核心组件】");
        System.out.println("  🧠 大脑 (LLM): 推理和决策");
        System.out.println("  🔧 工具 (Tools): 执行能力");
        System.out.println("  📝 记忆 (Memory): 上下文保持");
        System.out.println("  📋 规划 (Planning): 任务分解");
    }

    /**
     * ========================================
     * 第二部分：Agent 架构
     * ========================================
     */
    public static void explainArchitecture() {
        System.out.println("=== Agent 架构 ===");
        System.out.println();

        System.out.println("【基础架构】");
        System.out.println("```");
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│                    Agent 控制循环                       │");
        System.out.println("│                                                         │");
        System.out.println("│    ┌─────────────────────────────────────────┐         │");
        System.out.println("│    │              LLM (大脑)                  │         │");
        System.out.println("│    │    理解任务 → 制定计划 → 选择工具        │         │");
        System.out.println("│    └────────────────┬────────────────────────┘         │");
        System.out.println("│                      │                                  │");
        System.out.println("│         ┌───────────┼───────────┐                      │");
        System.out.println("│         ↓           ↓           ↓                      │");
        System.out.println("│    ┌────────┐ ┌────────┐ ┌────────┐                    │");
        System.out.println("│    │ 工具 1 │ │ 工具 2 │ │ 工具 N │                    │");
        System.out.println("│    │ 搜索   │ │ 计算   │ │ 代码   │                    │");
        System.out.println("│    └────┬───┘ └────┬───┘ └────┬───┘                    │");
        System.out.println("│         └──────────┼──────────┘                        │");
        System.out.println("│                    ↓                                    │");
        System.out.println("│              执行结果反馈                               │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【记忆系统】");
        System.out.println("  • 短期记忆: 对话上下文");
        System.out.println("  • 长期记忆: 向量数据库存储");
        System.out.println("  • 工作记忆: 当前任务状态");
    }

    /**
     * ========================================
     * 第三部分：工具调用 (Function Calling)
     * ========================================
     */
    public static void explainToolCalling() {
        System.out.println("=== 工具调用 ===");
        System.out.println();

        System.out.println("【工具定义】");
        System.out.println("```java");
        System.out.println("// LangChain4j 工具定义");
        System.out.println("@Tool(\"搜索网络获取最新信息\")");
        System.out.println("public String webSearch(");
        System.out.println("    @P(\"搜索关键词\") String query");
        System.out.println(") {");
        System.out.println("    // 调用搜索 API");
        System.out.println("    return searchService.search(query);");
        System.out.println("}");
        System.out.println();
        System.out.println("@Tool(\"执行数学计算\")");
        System.out.println("public double calculate(");
        System.out.println("    @P(\"数学表达式\") String expression");
        System.out.println(") {");
        System.out.println("    return calculator.evaluate(expression);");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【常见工具类型】");
        System.out.println("  • 信息检索: 搜索、知识库查询");
        System.out.println("  • 数据处理: 计算、格式转换");
        System.out.println("  • 代码执行: 运行 Python/SQL");
        System.out.println("  • 外部 API: 天气、地图、邮件");
        System.out.println("  • 文件操作: 读写、生成文档");
        System.out.println();

        System.out.println("【OpenAI Function Calling】");
        System.out.println("```json");
        System.out.println("{");
        System.out.println("  \"name\": \"get_weather\",");
        System.out.println("  \"description\": \"获取城市天气\",");
        System.out.println("  \"parameters\": {");
        System.out.println("    \"type\": \"object\",");
        System.out.println("    \"properties\": {");
        System.out.println("      \"city\": { \"type\": \"string\" }");
        System.out.println("    }");
        System.out.println("  }");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：多 Agent 系统
     * ========================================
     */
    public static void explainMultiAgent() {
        System.out.println("=== 多 Agent 系统 ===");
        System.out.println();

        System.out.println("【为什么需要多 Agent】");
        System.out.println("  • 单个 Agent 能力有限");
        System.out.println("  • 专业化分工提高质量");
        System.out.println("  • 复杂任务需要协作");
        System.out.println();

        System.out.println("【协作模式】");
        System.out.println();
        System.out.println("1. 顺序执行");
        System.out.println("   Agent A → Agent B → Agent C");
        System.out.println("   适合：流水线任务");
        System.out.println();
        System.out.println("2. 主从模式");
        System.out.println("   Orchestrator 调度 Worker Agents");
        System.out.println("   适合：任务分解与汇总");
        System.out.println();
        System.out.println("3. 辩论模式");
        System.out.println("   多 Agent 讨论达成共识");
        System.out.println("   适合：需要多角度验证");
        System.out.println();

        System.out.println("【应用场景】");
        System.out.println("  • 代码生成: 设计 + 编码 + 测试 Agent");
        System.out.println("  • 研究分析: 检索 + 分析 + 总结 Agent");
        System.out.println("  • 客户服务: 分类 + 问答 + 升级 Agent");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: AI Agent 架构                         ║");
        System.out.println("║          自主智能体开发                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainArchitecture();
        System.out.println();

        explainToolCalling();
        System.out.println();

        explainMultiAgent();
    }
}
