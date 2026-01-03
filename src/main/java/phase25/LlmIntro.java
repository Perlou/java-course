package phase25;

/**
 * LLM 大模型原理入门
 * 
 * 本文件介绍大语言模型(LLM)的核心概念和工作原理
 * 
 * @author Java Course
 * @version 1.0
 */
public class LlmIntro {

    public static void main(String[] args) {
        System.out.println("=== Phase 25: 大模型基础 ===\n");

        // 1. 什么是大语言模型
        whatIsLLM();

        // 2. Transformer 架构核心
        transformerArchitecture();

        // 3. 注意力机制
        attentionMechanism();

        // 4. 预训练与微调
        pretrainingAndFinetuning();

        // 5. 推理过程
        inferenceProcess();
    }

    /**
     * 1. 什么是大语言模型 (LLM)
     */
    static void whatIsLLM() {
        System.out.println("📚 1. 什么是大语言模型 (LLM)");
        System.out.println("=".repeat(50));

        String definition = """
                【定义】
                大语言模型 (Large Language Model, LLM) 是一种基于深度学习的
                人工智能模型，通过海量文本数据训练，能够理解和生成人类语言。

                【核心特点】
                ┌─────────────────────────────────────────────────┐
                │  特点         │  说明                           │
                ├─────────────────────────────────────────────────┤
                │  规模大       │  参数量从数十亿到数万亿          │
                │  通用性强     │  一个模型可处理多种 NLP 任务     │
                │  上下文理解   │  能理解长文本的语义关系          │
                │  生成能力     │  可以创作、翻译、编程            │
                │  涌现能力     │  规模增大后出现意想不到的能力    │
                └─────────────────────────────────────────────────┘

                【主流大模型】
                • OpenAI GPT-4      - 最强通用模型，多模态支持
                • Anthropic Claude  - 长上下文，安全性好
                • Google Gemini     - 多模态，Google生态集成
                • Meta LLaMA        - 开源模型，可本地部署
                • 阿里通义千问      - 中文优化，国内首选

                【参数规模演进】
                GPT-1  (2018): 1.17 亿参数
                GPT-2  (2019): 15 亿参数
                GPT-3  (2020): 1750 亿参数
                GPT-4  (2023): 约 1.8 万亿参数 (未公开)
                """;
        System.out.println(definition);
    }

    /**
     * 2. Transformer 架构
     */
    static void transformerArchitecture() {
        System.out.println("\n🔧 2. Transformer 架构");
        System.out.println("=".repeat(50));

        String architecture = """
                【Transformer 是什么】
                2017年 Google 提出的神经网络架构，是现代大模型的基础。
                论文: "Attention Is All You Need"

                【架构图解】
                ┌────────────────────────────────────────────────────┐
                │                  Transformer                        │
                ├────────────────────────────────────────────────────┤
                │                                                     │
                │   输入: "今天天气很好"                               │
                │          ↓                                          │
                │   ┌──────────────┐                                 │
                │   │  Tokenizer   │  分词: [今天, 天气, 很, 好]      │
                │   └──────┬───────┘                                 │
                │          ↓                                          │
                │   ┌──────────────┐                                 │
                │   │  Embedding   │  词向量化: 词 → 向量            │
                │   └──────┬───────┘                                 │
                │          ↓                                          │
                │   ┌──────────────┐                                 │
                │   │  Positional  │  位置编码: 加入位置信息          │
                │   │  Encoding    │                                 │
                │   └──────┬───────┘                                 │
                │          ↓                                          │
                │   ┌──────────────────────────────────────┐         │
                │   │        Transformer Blocks (N层)       │         │
                │   │  ┌─────────────────────────────────┐ │         │
                │   │  │   Multi-Head Self-Attention     │ │         │
                │   │  └─────────────────────────────────┘ │         │
                │   │  ┌─────────────────────────────────┐ │         │
                │   │  │   Feed Forward Neural Network   │ │         │
                │   │  └─────────────────────────────────┘ │         │
                │   └──────────────────────────────────────┘         │
                │          ↓                                          │
                │   ┌──────────────┐                                 │
                │   │  Output      │  输出下一个词的概率分布          │
                │   └──────────────┘                                 │
                │                                                     │
                └────────────────────────────────────────────────────┘

                【核心组件】
                1. Tokenizer (分词器)
                   - 将文本拆分成 token (词/子词)
                   - 常用算法: BPE, WordPiece, SentencePiece

                2. Embedding (嵌入层)
                   - 将离散的 token 转换为连续向量
                   - 维度通常: 768, 1024, 4096 等

                3. Positional Encoding (位置编码)
                   - 注入位置信息 (Transformer 本身不感知顺序)
                   - 使用正弦余弦函数或可学习位置向量

                4. Self-Attention (自注意力)
                   - 核心机制！让每个词关注其他所有词

                5. Feed Forward Network (前馈网络)
                   - 两层全连接 + 激活函数
                   - 提供非线性变换能力
                """;
        System.out.println(architecture);
    }

    /**
     * 3. 注意力机制 (Self-Attention)
     */
    static void attentionMechanism() {
        System.out.println("\n⚡ 3. 自注意力机制 (Self-Attention)");
        System.out.println("=".repeat(50));

        String attention = """
                【核心思想】
                让每个词都能"看到"句子中的其他词，并计算与它们的相关程度。

                【计算过程】

                输入句子: "我 爱 中国"

                Step 1: 生成 Q, K, V 向量
                ┌─────────────────────────────────────────┐
                │  每个词生成三个向量:                     │
                │  • Query (Q): 我要查询什么              │
                │  • Key (K):   我能提供什么              │
                │  • Value (V): 我的实际内容              │
                │                                         │
                │  "我"  → Q₁, K₁, V₁                     │
                │  "爱"  → Q₂, K₂, V₂                     │
                │  "中国"→ Q₃, K₃, V₃                     │
                └─────────────────────────────────────────┘

                Step 2: 计算注意力分数
                ┌─────────────────────────────────────────┐
                │  Score = Q · Kᵀ / √d                    │
                │                                         │
                │  例: 计算"我"对其他词的注意力:          │
                │  Score(我→我)   = Q₁ · K₁ᵀ             │
                │  Score(我→爱)   = Q₁ · K₂ᵀ             │
                │  Score(我→中国) = Q₁ · K₃ᵀ             │
                └─────────────────────────────────────────┘

                Step 3: Softmax 归一化
                ┌─────────────────────────────────────────┐
                │  将分数转换为概率分布 (和为1)            │
                │                                         │
                │  例: "我" 的注意力权重:                 │
                │  α(我→我)   = 0.1                       │
                │  α(我→爱)   = 0.3                       │
                │  α(我→中国) = 0.6  ← 最相关            │
                └─────────────────────────────────────────┘

                Step 4: 加权求和得到输出
                ┌─────────────────────────────────────────┐
                │  Output = Σ αᵢ × Vᵢ                     │
                │                                         │
                │  "我"的新表示 = 0.1×V₁ + 0.3×V₂ + 0.6×V₃│
                │  融合了上下文信息！                      │
                └─────────────────────────────────────────┘

                【Multi-Head Attention】
                ┌─────────────────────────────────────────┐
                │  使用多个"注意力头"并行计算:             │
                │                                         │
                │  Head 1: 关注语法关系                   │
                │  Head 2: 关注语义关系                   │
                │  Head 3: 关注指代关系                   │
                │  ...                                    │
                │  Head N: 其他模式                       │
                │                                         │
                │  最后拼接所有头的输出                   │
                │  典型配置: 8头, 12头, 32头, 64头        │
                └─────────────────────────────────────────┘

                【为什么注意力机制这么重要】
                1. 捕获长距离依赖 - 不像 RNN 会遗忘
                2. 并行计算     - 比 RNN 快得多
                3. 可解释性     - 可以可视化注意力权重
                """;
        System.out.println(attention);

        // 简化的注意力计算示例
        demonstrateAttention();
    }

    /**
     * 简化的注意力机制演示
     */
    static void demonstrateAttention() {
        System.out.println("\n【Java 代码模拟注意力计算】");

        // 假设词向量维度为 3 (实际中是 768 或更大)
        double[][] embeddings = {
                { 1.0, 0.5, 0.2 }, // "我"
                { 0.8, 0.9, 0.3 }, // "爱"
                { 0.6, 0.7, 1.0 } // "中国"
        };
        String[] tokens = { "我", "爱", "中国" };

        // 简化: 使用 embedding 本身作为 Q, K
        // 计算 "我" 对所有词的注意力
        System.out.println("\n计算 '我' 对其他词的注意力:");

        double[] scores = new double[3];
        double sumExp = 0;

        // 计算点积分数
        for (int i = 0; i < 3; i++) {
            scores[i] = dotProduct(embeddings[0], embeddings[i]);
            sumExp += Math.exp(scores[i]);
        }

        // Softmax 归一化
        System.out.println("┌──────────────────────────────────┐");
        for (int i = 0; i < 3; i++) {
            double attention = Math.exp(scores[i]) / sumExp;
            System.out.printf("│  %s: 注意力权重 = %.3f %s│%n",
                    tokens[i], attention,
                    attention > 0.4 ? " ← 高关注 " : "          ");
        }
        System.out.println("└──────────────────────────────────┘");
    }

    /**
     * 点积计算
     */
    static double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * 4. 预训练与微调
     */
    static void pretrainingAndFinetuning() {
        System.out.println("\n\n📖 4. 预训练与微调");
        System.out.println("=".repeat(50));

        String training = """
                【预训练 (Pre-training)】
                ┌─────────────────────────────────────────────────┐
                │  目标: 让模型学习语言的通用知识                  │
                ├─────────────────────────────────────────────────┤
                │                                                 │
                │  训练数据: 互联网文本、书籍、维基百科...         │
                │  数据量:   TB 级别 (万亿 token)                 │
                │  计算量:   数千 GPU × 数月                      │
                │  成本:     数百万到数千万美元                   │
                │                                                 │
                │  训练任务:                                      │
                │  ┌────────────────────────────────────────┐    │
                │  │ 下一个词预测 (Next Token Prediction)   │    │
                │  │                                        │    │
                │  │ 输入: "今天天气很"                     │    │
                │  │ 预测: "好" (概率最高的下一个词)         │    │
                │  └────────────────────────────────────────┘    │
                │                                                 │
                └─────────────────────────────────────────────────┘

                【微调 (Fine-tuning)】
                ┌─────────────────────────────────────────────────┐
                │  目标: 让模型适应特定任务或领域                  │
                ├─────────────────────────────────────────────────┤
                │                                                 │
                │  常见微调方法:                                  │
                │                                                 │
                │  1. 全量微调 (Full Fine-tuning)                │
                │     - 更新所有参数                             │
                │     - 效果最好，但成本高                       │
                │                                                 │
                │  2. LoRA (Low-Rank Adaptation)                 │
                │     - 只训练少量额外参数                       │
                │     - 性价比高，主流选择                       │
                │                                                 │
                │  3. Prompt Tuning                              │
                │     - 只优化提示词参数                         │
                │     - 成本最低                                 │
                │                                                 │
                │  4. RLHF (人类反馈强化学习)                    │
                │     - 用人类偏好对齐模型                       │
                │     - ChatGPT 的关键技术                       │
                │                                                 │
                └─────────────────────────────────────────────────┘

                【训练流程示意】

                步骤1: 预训练 (Self-Supervised)
                ┌─────────────────────────────────┐
                │ 海量文本 → 基座模型 (Base Model)│
                └─────────────────────────────────┘
                              ↓
                步骤2: 指令微调 (Instruction Tuning)
                ┌─────────────────────────────────┐
                │ 问答数据 → 指令模型              │
                └─────────────────────────────────┘
                              ↓
                步骤3: RLHF 对齐
                ┌─────────────────────────────────┐
                │ 人类反馈 → Chat 模型            │
                └─────────────────────────────────┘
                """;
        System.out.println(training);
    }

    /**
     * 5. 推理过程
     */
    static void inferenceProcess() {
        System.out.println("\n🚀 5. 推理过程 (Inference)");
        System.out.println("=".repeat(50));

        String inference = """
                【自回归生成】
                大模型采用"自回归"方式生成文本，即逐个 token 生成。

                示例: 问 "什么是Java?"

                ┌─────────────────────────────────────────────────┐
                │ Step 1: 输入 "什么是Java?"                      │
                │         ↓                                       │
                │ Step 2: 预测下一个token → "Java"               │
                │         输出: "Java"                            │
                │         ↓                                       │
                │ Step 3: 输入 "什么是Java? Java"                │
                │         预测下一个token → "是"                 │
                │         输出: "Java是"                          │
                │         ↓                                       │
                │ Step 4: 输入 "什么是Java? Java是"              │
                │         预测下一个token → "一种"               │
                │         输出: "Java是一种"                      │
                │         ↓                                       │
                │ ... 持续生成直到输出 <EOS> 或达到长度限制 ...   │
                │         ↓                                       │
                │ 最终: "Java是一种面向对象的编程语言..."         │
                └─────────────────────────────────────────────────┘

                【温度参数 (Temperature)】
                控制输出的随机性:

                Temperature = 0: 确定性输出，每次相同
                Temperature = 0.7: 平衡创造性和一致性（推荐）
                Temperature = 1.0+: 高随机性，创意写作

                【解码策略】
                ┌──────────────────────────────────────────────────┐
                │ 方法          │ 说明                  │ 适用场景 │
                ├──────────────────────────────────────────────────┤
                │ Greedy        │ 选概率最高的token     │ 简单任务 │
                │ Top-K         │ 从前K个中随机选       │ 通用     │
                │ Top-P (Nucleus)│ 从累积概率P内选      │ 推荐     │
                │ Beam Search   │ 保留多个候选序列      │ 翻译     │
                └──────────────────────────────────────────────────┘

                【关键指标】
                • Latency (延迟): 首 token 响应时间
                • Throughput (吞吐): 每秒生成 token 数
                • Context Length (上下文长度): 能处理的最大 token 数

                📌 提示:
                虽然我们在 Java 中不直接实现模型推理，
                但理解这些原理有助于:
                1. 优化 Prompt 设计
                2. 合理设置 API 参数
                3. 理解模型输出的特点
                """;
        System.out.println(inference);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ 大模型基于 Transformer 架构
                ✅ 自注意力机制是核心，让词之间能互相"看见"
                ✅ 预训练学通用知识，微调学特定任务
                ✅ 推理是逐 token 自回归生成
                ✅ Temperature 控制创造性/确定性

                下一节: PromptEngineering.java - 学习提示词工程技巧
                """);
    }
}
