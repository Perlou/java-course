package phase25;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token 与分词 Demo
 * 
 * 理解 Token 是使用大模型的基础
 * Token 直接影响 API 成本和上下文长度
 * 
 * @author Java Course
 * @version 1.0
 */
public class TokenizerDemo {

    public static void main(String[] args) {
        System.out.println("=== Token 与分词原理 ===\n");

        // 1. Token 基础概念
        tokenBasics();

        // 2. 分词算法
        tokenizerAlgorithms();

        // 3. 模拟 BPE 算法
        bpeDemo();

        // 4. Token 计数与成本
        tokenCounting();

        // 5. 优化技巧
        optimizationTips();
    }

    /**
     * 1. Token 基础概念
     */
    static void tokenBasics() {
        System.out.println("📚 1. Token 基础概念");
        System.out.println("=".repeat(50));

        String basics = """
                【什么是 Token】
                Token 是大模型处理文本的基本单位。
                模型不直接理解"字"或"词"，而是理解 Token。

                ┌─────────────────────────────────────────────────┐
                │  文本          →  分词  →  Token ID           │
                │  "Hello"       →  Hello →  [15496]            │
                │  "你好"        →  你/好  →  [1628, 233]       │
                │  "Java 编程"   →  Java/编/程 → [12389, 32, 45]│
                └─────────────────────────────────────────────────┘

                【Token ≠ 字 ≠ 词】

                英文示例:
                "Hello, World!" = 4 tokens: ["Hello", ",", " World", "!"]

                中文示例:
                "你好世界" = 4 tokens: ["你", "好", "世", "界"]
                或 = 2 tokens: ["你好", "世界"] (取决于分词器)

                【Token 的重要性】

                1. 计费单位: API 按 token 数量收费
                   ┌────────────────────────────────────────┐
                   │ GPT-4: $30/百万输出 tokens            │
                   │ 一次对话 1000 tokens ≈ $0.03          │
                   └────────────────────────────────────────┘

                2. 上下文限制: 模型有最大 token 数限制
                   ┌────────────────────────────────────────┐
                   │ GPT-3.5:  16K tokens                  │
                   │ GPT-4:    128K tokens                 │
                   │ Claude 3: 200K tokens                 │
                   └────────────────────────────────────────┘

                3. 处理速度: token 越少，响应越快

                【经验法则】
                ┌─────────────────────────────────────────────────┐
                │ 英文: 1 token ≈ 4 个字符 ≈ 0.75 个单词         │
                │ 中文: 1 token ≈ 1-2 个汉字                     │
                │ 代码: 1 token ≈ 4-5 个字符 (含符号)            │
                │                                                 │
                │ 1000 tokens ≈ 750 英文单词 ≈ 500 中文字        │
                └─────────────────────────────────────────────────┘
                """;
        System.out.println(basics);
    }

    /**
     * 2. 分词算法介绍
     */
    static void tokenizerAlgorithms() {
        System.out.println("\n🔧 2. 主流分词算法");
        System.out.println("=".repeat(50));

        String algorithms = """
                【三大主流算法】

                ┌───────────────────────────────────────────────────────┐
                │  算法         │  使用者              │  特点          │
                ├───────────────────────────────────────────────────────┤
                │  BPE          │  GPT, LLaMA          │  最常用        │
                │  WordPiece    │  BERT                │  Google 出品   │
                │  SentencePiece│  T5, mT5            │  语言无关      │
                └───────────────────────────────────────────────────────┘

                【BPE (Byte Pair Encoding) 算法】

                核心思想: 从字符级别开始，逐步合并最频繁出现的字符对

                训练过程示例:
                ┌─────────────────────────────────────────────────┐
                │ 初始词表: [a, b, c, d, ..., z]                 │
                │                                                 │
                │ 训练语料: "ab abc abcd ab abc"                 │
                │                                                 │
                │ 第1轮: "ab" 出现最多 → 合并为 "ab"             │
                │        词表: [a, b, c, d, ..., z, ab]          │
                │                                                 │
                │ 第2轮: "abc" 出现次多 → 合并为 "abc"           │
                │        词表: [a, b, c, d, ..., z, ab, abc]     │
                │                                                 │
                │ ... 持续合并直到达到目标词表大小 ...           │
                └─────────────────────────────────────────────────┘

                【分词示例】

                输入: "playing"

                使用 GPT-2 分词器:
                "playing" → ["play", "ing"] → [1159, 278]

                使用字符级分词:
                "playing" → ["p","l","a","y","i","n","g"] → 7 tokens

                ✅ BPE 的优势: 减少 token 数量，提高效率
                """;
        System.out.println(algorithms);
    }

    /**
     * 3. 模拟 BPE 算法
     */
    static void bpeDemo() {
        System.out.println("\n⚡ 3. BPE 算法模拟");
        System.out.println("=".repeat(50));

        System.out.println("【Java 实现简化版 BPE】\n");

        // 简化的 BPE 演示
        String corpus = "abab abcd abab abcd abc";
        System.out.println("训练语料: \"" + corpus + "\"\n");

        // 初始化: 字符级别分割
        List<String> tokens = new ArrayList<>();
        for (char c : corpus.toCharArray()) {
            tokens.add(String.valueOf(c));
        }

        System.out.println("初始 tokens: " + tokens);
        System.out.println("初始 token 数量: " + tokens.size());

        // 执行 3 轮合并
        for (int round = 1; round <= 3; round++) {
            System.out.println("\n--- 第 " + round + " 轮合并 ---");

            // 统计相邻 token 对的频率
            Map<String, Integer> pairFreq = new HashMap<>();
            for (int i = 0; i < tokens.size() - 1; i++) {
                String pair = tokens.get(i) + tokens.get(i + 1);
                if (!pair.contains(" ")) { // 不跨词合并
                    pairFreq.merge(pair, 1, Integer::sum);
                }
            }

            // 找出最高频的对
            String bestPair = "";
            int maxFreq = 0;
            for (Map.Entry<String, Integer> entry : pairFreq.entrySet()) {
                if (entry.getValue() > maxFreq) {
                    maxFreq = entry.getValue();
                    bestPair = entry.getKey();
                }
            }

            if (bestPair.isEmpty())
                break;

            System.out.println("最高频对: \"" + bestPair + "\" (出现 " + maxFreq + " 次)");

            // 合并
            List<String> newTokens = new ArrayList<>();
            int i = 0;
            while (i < tokens.size()) {
                if (i < tokens.size() - 1 &&
                        (tokens.get(i) + tokens.get(i + 1)).equals(bestPair)) {
                    newTokens.add(bestPair);
                    i += 2;
                } else {
                    newTokens.add(tokens.get(i));
                    i++;
                }
            }
            tokens = newTokens;

            System.out.println("合并后: " + tokens);
            System.out.println("token 数量: " + tokens.size());
        }

        System.out.println("\n【最终词表 (模拟)】");
        System.out.println("基础字符: a, b, c, d, 空格");
        System.out.println("学习到的子词: ab, cd, abcd (根据语料频率)");
    }

    /**
     * 4. Token 计数与成本
     */
    static void tokenCounting() {
        System.out.println("\n\n💰 4. Token 计数与成本估算");
        System.out.println("=".repeat(50));

        String counting = """
                【如何计算 Token 数量】

                方法1: 使用官方工具
                - OpenAI Tokenizer: https://platform.openai.com/tokenizer
                - tiktoken 库 (Python)

                方法2: 经验估算
                ┌─────────────────────────────────────────────────┐
                │ 类型        │ 估算规则                         │
                ├─────────────────────────────────────────────────┤
                │ 英文        │ 字符数 ÷ 4                       │
                │ 中文        │ 字符数 × 1.5                     │
                │ 代码        │ 字符数 ÷ 3.5                     │
                │ 混合        │ 英文÷4 + 中文×1.5               │
                └─────────────────────────────────────────────────┘
                """;
        System.out.println(counting);

        // 演示估算
        System.out.println("【估算示例】\n");

        String englishText = "Hello, this is a sample text for token counting.";
        String chineseText = "你好，这是一段用于Token计数的示例文本。";
        String codeText = "public static void main(String[] args) { }";

        System.out.println("英文文本: \"" + englishText + "\"");
        System.out.println("  - 字符数: " + englishText.length());
        System.out.println("  - 估算 Token: ~" + (englishText.length() / 4));

        System.out.println("\n中文文本: \"" + chineseText + "\"");
        System.out.println("  - 字符数: " + chineseText.length());
        System.out.println("  - 估算 Token: ~" + (int) (chineseText.length() * 1.5));

        System.out.println("\n代码文本: \"" + codeText + "\"");
        System.out.println("  - 字符数: " + codeText.length());
        System.out.println("  - 估算 Token: ~" + (int) (codeText.length() / 3.5));

        // 成本计算
        System.out.println("\n【成本估算表】");
        System.out.println("─".repeat(50));
        System.out.println("""
                假设每次对话: 输入 500 tokens, 输出 1000 tokens

                ┌─────────────────────────────────────────────────────────┐
                │ 模型         │ 单次成本     │ 1000次/天   │ 月成本     │
                ├─────────────────────────────────────────────────────────┤
                │ GPT-4 Turbo  │ $0.035       │ $35         │ $1,050     │
                │ GPT-3.5      │ $0.002       │ $2          │ $60        │
                │ Claude 3     │ $0.018       │ $18         │ $540       │
                │ 通义千问     │ ¥0.012       │ ¥12         │ ¥360       │
                └─────────────────────────────────────────────────────────┘

                💡 选择模型时要权衡效果和成本！
                """);
    }

    /**
     * 5. Token 优化技巧
     */
    static void optimizationTips() {
        System.out.println("\n✨ 5. Token 优化技巧");
        System.out.println("=".repeat(50));

        System.out.println("""
                【减少 Token 消耗的方法】

                1. 精简 Prompt
                ┌─────────────────────────────────────────────────┐
                │ ❌ 冗长: "我想请你帮我写一段Java代码，这段     │
                │    代码的作用是实现一个简单的计算器功能..."     │
                │                                                 │
                │ ✅ 精简: "用Java实现计算器，支持加减乘除"      │
                └─────────────────────────────────────────────────┘

                2. 使用简洁指令
                ┌─────────────────────────────────────────────────┐
                │ ❌ "可以请你用JSON格式返回以下信息吗?"         │
                │ ✅ "返回JSON格式"                              │
                └─────────────────────────────────────────────────┘

                3. 限制输出长度
                ┌─────────────────────────────────────────────────┐
                │ "请用不超过100字回答"                          │
                │ "只返回代码，不要解释"                         │
                │ API参数: max_tokens=500                        │
                └─────────────────────────────────────────────────┘

                4. 压缩上下文
                ┌─────────────────────────────────────────────────┐
                │ 对话历史太长时:                                 │
                │ - 只保留最近N轮对话                            │
                │ - 用摘要替代完整历史                           │
                │ - 只保留关键信息                               │
                └─────────────────────────────────────────────────┘

                5. 选择合适的模型
                ┌─────────────────────────────────────────────────┐
                │ 简单任务 → GPT-3.5 (便宜)                      │
                │ 复杂推理 → GPT-4 (贵但准确)                    │
                │ 中文任务 → 国产模型 (性价比高)                 │
                └─────────────────────────────────────────────────┘

                【实战建议】

                ┌─────────────────────────────────────────────────┐
                │ 1. 正式使用前先测试 token 消耗                 │
                │ 2. 设置 API 调用预算告警                       │
                │ 3. 缓存常用查询的结果                          │
                │ 4. 批量处理减少 API 调用次数                   │
                │ 5. 监控每日/每月 token 使用量                  │
                └─────────────────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ Token 是模型处理文本的基本单位
                ✅ BPE 是最常用的分词算法
                ✅ 1000 tokens ≈ 750英文单词 ≈ 500中文字
                ✅ Token 影响 API 成本和上下文限制
                ✅ 精简 Prompt 可以显著节省成本

                下一节: ModelComparison.java - 主流模型对比
                """);
    }
}
