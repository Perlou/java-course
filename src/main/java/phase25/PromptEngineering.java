package phase25;

/**
 * Prompt Engineering 提示词工程
 * 
 * 提示词工程是与大模型交互的核心技能
 * 好的 Prompt 可以让模型输出质量提升数倍
 * 
 * @author Java Course
 * @version 1.0
 */
public class PromptEngineering {

    public static void main(String[] args) {
        System.out.println("=== Prompt Engineering 提示词工程 ===\n");

        // 1. 基础原则
        basicPrinciples();

        // 2. Prompt 模板
        promptTemplates();

        // 3. 高级技巧
        advancedTechniques();

        // 4. 常见模式
        commonPatterns();

        // 5. 最佳实践
        bestPractices();
    }

    /**
     * 1. Prompt 基础原则
     */
    static void basicPrinciples() {
        System.out.println("📌 1. Prompt 基础原则");
        System.out.println("=".repeat(50));

        String principles = """
                【为什么 Prompt 很重要】
                ┌─────────────────────────────────────────────────┐
                │  同样的模型，不同的 Prompt，效果天差地别！      │
                │                                                 │
                │  差的 Prompt:  "写个代码"                       │
                │  → 模型不知道写什么，输出可能不符合预期         │
                │                                                 │
                │  好的 Prompt:  "用 Java 写一个单例模式示例，    │
                │  要求使用双重检查锁，并添加注释说明原理"        │
                │  → 模型清楚知道要做什么，输出精准               │
                └─────────────────────────────────────────────────┘

                【六大核心原则】

                ┌────────────────────────────────────────────────────┐
                │  原则          │  说明                │  示例      │
                ├────────────────────────────────────────────────────┤
                │ 1. 清晰具体    │ 明确说明任务要求     │ 见下方    │
                │ 2. 角色设定    │ 赋予AI特定身份       │ 见下方    │
                │ 3. 示例引导    │ Few-shot Learning   │ 见下方    │
                │ 4. 格式约束    │ 指定输出格式         │ 见下方    │
                │ 5. 分步思考    │ Chain of Thought    │ 见下方    │
                │ 6. 边界约束    │ 设定限制条件         │ 见下方    │
                └────────────────────────────────────────────────────┘
                """;
        System.out.println(principles);

        // 详细示例
        System.out.println("【原则详解与示例】\n");

        System.out.println("1️⃣ 清晰具体");
        System.out.println("─".repeat(40));
        System.out.println("""
                ❌ 模糊: "帮我写个Java程序"

                ✅ 清晰: "用Java实现一个用户登录验证功能，要求：
                   1. 验证用户名长度为5-20个字符
                   2. 密码必须包含大小写字母和数字
                   3. 返回验证结果和错误信息"
                """);

        System.out.println("2️⃣ 角色设定");
        System.out.println("─".repeat(40));
        System.out.println("""
                ✅ "你是一个有10年经验的Java架构师，请评审以下代码..."

                ✅ "你是一个耐心的编程导师，用初学者能理解的方式解释..."

                ✅ "你是一个安全专家，从安全角度分析这段代码的风险..."
                """);

        System.out.println("3️⃣ 示例引导 (Few-shot)");
        System.out.println("─".repeat(40));
        System.out.println("""
                ✅ "将英文命名转换为中文注释，示例：

                   输入: getUserById
                   输出: 根据ID获取用户信息

                   输入: calculateTotalPrice
                   输出: 计算总价

                   请转换: validateEmailFormat"
                """);

        System.out.println("4️⃣ 格式约束");
        System.out.println("─".repeat(40));
        System.out.println("""
                ✅ "以JSON格式返回，包含以下字段: name, description, example"

                ✅ "用Markdown表格展示比较结果"

                ✅ "按以下格式输出:
                   【问题分析】...
                   【解决方案】...
                   【代码示例】..."
                """);
    }

    /**
     * 2. Prompt 模板
     */
    static void promptTemplates() {
        System.out.println("\n📋 2. 实用 Prompt 模板");
        System.out.println("=".repeat(50));

        System.out.println("""
                【通用任务模板】
                ┌─────────────────────────────────────────────────┐
                │ 角色: 你是一个{role}                            │
                │                                                 │
                │ 背景: {context}                                 │
                │                                                 │
                │ 任务: {task_description}                        │
                │                                                 │
                │ 输入: {user_input}                              │
                │                                                 │
                │ 要求:                                           │
                │ 1. {requirement_1}                              │
                │ 2. {requirement_2}                              │
                │                                                 │
                │ 输出格式: {format}                              │
                └─────────────────────────────────────────────────┘
                """);

        System.out.println("【代码生成模板】");
        System.out.println("─".repeat(40));
        String codeTemplate = """
                你是一个资深Java开发工程师。

                请根据以下需求编写代码：
                [需求描述]

                技术要求：
                - Java版本：17
                - 使用设计模式：{pattern}
                - 代码规范：遵循阿里巴巴Java开发手册

                输出要求：
                1. 完整可运行的代码
                2. 必要的注释说明
                3. 关键逻辑解释
                """;
        System.out.println(codeTemplate);

        System.out.println("【代码审查模板】");
        System.out.println("─".repeat(40));
        String reviewTemplate = """
                你是一个代码审查专家。请审查以下代码：

                ```java
                {code}
                ```

                请从以下维度进行审查：
                1. 代码规范性
                2. 潜在bug
                3. 性能问题
                4. 安全风险
                5. 可维护性

                输出格式：
                【问题等级】严重/警告/建议
                【问题描述】...
                【修改建议】...
                """;
        System.out.println(reviewTemplate);

        System.out.println("【技术方案模板】");
        System.out.println("─".repeat(40));
        String designTemplate = """
                你是一个系统架构师，请设计以下功能的技术方案：

                【需求】{requirement}

                【约束条件】
                - 技术栈：Spring Boot + MySQL
                - 日均请求量：100万
                - 响应时间：< 100ms

                请输出：
                1. 整体架构图（用ASCII绘制）
                2. 核心表结构设计
                3. 关键接口定义
                4. 注意事项和风险点
                """;
        System.out.println(designTemplate);
    }

    /**
     * 3. 高级技巧
     */
    static void advancedTechniques() {
        System.out.println("\n⚡ 3. 高级技巧");
        System.out.println("=".repeat(50));

        System.out.println("【Chain of Thought (思维链)】");
        System.out.println("─".repeat(40));
        System.out.println("""
                让模型"逐步思考"，提高推理准确性

                ❌ 直接问: "100个人排队，每人占1米，队伍多长?"
                → 容易出错

                ✅ 思维链: "让我们一步步分析这个问题：
                   1. 首先确定人数: 100人
                   2. 每人占用空间: 1米
                   3. 队伍总长度 = 人数 × 每人占用空间
                   4. 但要注意: 第一个人不占前面的空间
                   所以答案是..."

                💡 魔法短语: "Let's think step by step"
                """);

        System.out.println("【Self-Consistency (自一致性)】");
        System.out.println("─".repeat(40));
        System.out.println("""
                多次生成，取最一致的答案

                做法：
                1. 用较高 temperature (如0.7) 生成多个回答
                2. 统计哪个答案出现最多次
                3. 选择最一致的答案作为最终结果

                适用场景：数学问题、逻辑推理
                """);

        System.out.println("【ReAct (推理+行动)】");
        System.out.println("─".repeat(40));
        System.out.println("""
                结合推理(Reasoning)和行动(Action)

                ┌──────────────────────────────────────────┐
                │ 问题: 谁是2024年世界杯冠军?             │
                │                                          │
                │ Thought: 我需要查询最新体育新闻         │
                │ Action: search("2024世界杯冠军")        │
                │ Observation: 搜索结果显示...            │
                │ Thought: 根据搜索结果，答案是...        │
                │ Answer: ...                              │
                └──────────────────────────────────────────┘

                这是 Agent 的核心模式！
                """);

        System.out.println("【Few-shot vs Zero-shot】");
        System.out.println("─".repeat(40));
        System.out.println("""
                ┌────────────────────────────────────────────────┐
                │  方式       │  说明          │  适用场景      │
                ├────────────────────────────────────────────────┤
                │ Zero-shot  │ 不给示例       │ 简单通用任务   │
                │ One-shot   │ 给1个示例      │ 格式要求明确   │
                │ Few-shot   │ 给2-5个示例    │ 复杂特定任务   │
                └────────────────────────────────────────────────┘

                示例越多，效果越好，但消耗token也越多
                一般3个示例是性价比最高的选择
                """);
    }

    /**
     * 4. 常见模式
     */
    static void commonPatterns() {
        System.out.println("\n🎯 4. 常见 Prompt 模式");
        System.out.println("=".repeat(50));

        System.out.println("【模式1: 角色扮演】");
        System.out.println("""
                "你是一个资深Java架构师，有15年开发经验，
                 特别擅长微服务架构和性能优化。
                 请以这个角色回答我的问题..."
                """);

        System.out.println("【模式2: 约束输出】");
        System.out.println("""
                "请用以下JSON格式回答：
                 {
                   \"analysis\": \"问题分析\",
                   \"solution\": \"解决方案\",
                   \"code\": \"示例代码\"
                 }
                 不要输出任何其他内容。"
                """);

        System.out.println("【模式3: 分步任务】");
        System.out.println("""
                "请按以下步骤完成任务：
                 Step 1: 分析问题
                 Step 2: 列出所有可能的解决方案
                 Step 3: 评估每个方案的优缺点
                 Step 4: 推荐最佳方案并说明理由
                 Step 5: 给出实现代码"
                """);

        System.out.println("【模式4: 否定约束】");
        System.out.println("""
                "请解释这段代码的作用。
                 要求：
                 - 不要解释基础语法
                 - 不要给出改进建议
                 - 不要超过100字"
                """);

        System.out.println("【模式5: 迭代优化】");
        System.out.println("""
                第一轮: "请写一个排序算法"
                ↓
                第二轮: "请优化这个算法的时间复杂度"
                ↓
                第三轮: "请添加边界检查和异常处理"
                ↓
                第四轮: "请添加详细注释"
                """);
    }

    /**
     * 5. 最佳实践
     */
    static void bestPractices() {
        System.out.println("\n✅ 5. 最佳实践");
        System.out.println("=".repeat(50));

        System.out.println("""
                【DO - 应该做的】
                ┌─────────────────────────────────────────────────┐
                │ ✅ 明确具体的任务描述                          │
                │ ✅ 提供必要的上下文和背景                      │
                │ ✅ 使用示例引导 (Few-shot)                     │
                │ ✅ 指定输出格式                                │
                │ ✅ 设置合理的约束条件                          │
                │ ✅ 迭代优化 Prompt                             │
                │ ✅ 测试不同的表述方式                          │
                │ ✅ 记录好用的 Prompt 模板                      │
                └─────────────────────────────────────────────────┘

                【DON'T - 不应该做的】
                ┌─────────────────────────────────────────────────┐
                │ ❌ Prompt 太短太模糊                           │
                │ ❌ 一次要求太多事情                            │
                │ ❌ 前后矛盾的要求                              │
                │ ❌ 期望模型知道最新信息                        │
                │ ❌ 不验证输出结果                              │
                │ ❌ 在 Prompt 中包含敏感信息                    │
                └─────────────────────────────────────────────────┘

                【调试技巧】
                ┌─────────────────────────────────────────────────┐
                │ 1. 如果输出不符合预期，先检查 Prompt 是否清晰 │
                │ 2. 尝试添加"请只输出..."来约束格式           │
                │ 3. 用"请确认你理解了任务"让模型复述          │
                │ 4. 拆分复杂任务为多个简单任务                 │
                │ 5. 调整 temperature 参数                      │
                └─────────────────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ 清晰、具体、有示例是好 Prompt 的关键
                ✅ 角色设定能显著提升输出质量
                ✅ Chain of Thought 适合推理任务
                ✅ 格式约束保证输出可解析
                ✅ 迭代优化 Prompt 是常态

                下一节: TokenizerDemo.java - 理解分词与Token
                """);
    }
}
