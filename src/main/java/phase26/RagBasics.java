package phase26;

/**
 * RAG 架构详解
 * 
 * RAG (Retrieval-Augmented Generation) 检索增强生成
 * 是解决大模型知识局限的核心技术。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class RagBasics {

    /**
     * ========================================
     * 第一部分：RAG 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== RAG 概述 ===");
        System.out.println();

        System.out.println("【什么是 RAG】");
        System.out.println("  Retrieval-Augmented Generation");
        System.out.println("  检索增强生成 = 检索 + 生成");
        System.out.println("  让大模型基于外部知识库回答问题");
        System.out.println();

        System.out.println("【为什么需要 RAG】");
        System.out.println("  • 知识截止: 模型训练数据有时间限制");
        System.out.println("  • 幻觉问题: 模型可能编造不存在的信息");
        System.out.println("  • 私有知识: 模型不了解企业内部数据");
        System.out.println("  • 可解释性: 生成结果需要有据可查");
        System.out.println();

        System.out.println("【RAG vs 微调】");
        System.out.println("┌─────────────┬─────────────────┬─────────────────┐");
        System.out.println("│             │ RAG             │ 微调 Fine-tune  │");
        System.out.println("├─────────────┼─────────────────┼─────────────────┤");
        System.out.println("│ 成本        │ 低              │ 高              │");
        System.out.println("│ 知识更新    │ 实时            │ 需重新训练      │");
        System.out.println("│ 可解释性    │ 高（有来源）    │ 低              │");
        System.out.println("│ 适用场景    │ 知识问答        │ 风格/能力改变   │");
        System.out.println("└─────────────┴─────────────────┴─────────────────┘");
    }

    /**
     * ========================================
     * 第二部分：RAG 架构
     * ========================================
     */
    public static void explainArchitecture() {
        System.out.println("=== RAG 架构 ===");
        System.out.println();

        System.out.println("【基础 RAG 流程】");
        System.out.println("```");
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│  用户问题                                                  │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  ① 问题向量化 (Embedding)                                  │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  ② 向量检索 (Vector Search)                                │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  ③ 获取相关文档 (Top-K Documents)                          │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  ④ 构建 Prompt (Context + Question)                        │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  ⑤ LLM 生成回答                                            │");
        System.out.println("│     ↓                                                      │");
        System.out.println("│  最终答案 (带来源引用)                                      │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【索引阶段 (Indexing)】");
        System.out.println("  1. 加载文档 (PDF, Word, HTML...)");
        System.out.println("  2. 文档切分 (Chunking)");
        System.out.println("  3. 向量化 (Embedding)");
        System.out.println("  4. 存储到向量数据库");
        System.out.println();

        System.out.println("【检索阶段 (Retrieval)】");
        System.out.println("  1. 问题向量化");
        System.out.println("  2. 相似度搜索 Top-K");
        System.out.println("  3. 重排序 (Reranking) [可选]");
        System.out.println("  4. 返回相关 Chunks");
        System.out.println();

        System.out.println("【生成阶段 (Generation)】");
        System.out.println("  1. 构建 Prompt (系统提示 + 上下文 + 问题)");
        System.out.println("  2. 调用 LLM");
        System.out.println("  3. 后处理 (格式化、引用标注)");
    }

    /**
     * ========================================
     * 第三部分：高级 RAG 策略
     * ========================================
     */
    public static void explainAdvancedStrategies() {
        System.out.println("=== 高级 RAG 策略 ===");
        System.out.println();

        System.out.println("【1. 查询增强】");
        System.out.println("  • Query Rewriting: 改写用户问题");
        System.out.println("  • HyDE: 假设性文档嵌入");
        System.out.println("  • Multi-Query: 生成多个查询变体");
        System.out.println();

        System.out.println("【2. 检索增强】");
        System.out.println("  • 混合检索: 向量 + 关键词 (BM25)");
        System.out.println("  • 重排序: Cross-Encoder 精排");
        System.out.println("  • 父子文档: 检索小块，返回大块");
        System.out.println();

        System.out.println("【3. 上下文压缩】");
        System.out.println("  • 提取关键句子");
        System.out.println("  • 去除冗余信息");
        System.out.println("  • 摘要压缩");
        System.out.println();

        System.out.println("【4. 自适应 RAG】");
        System.out.println("  • 路由决策: 是否需要检索");
        System.out.println("  • 多轮迭代: 检索→评估→再检索");
        System.out.println("  • Self-RAG: 自我反思与修正");
    }

    /**
     * ========================================
     * 第四部分：RAG 评估
     * ========================================
     */
    public static void explainEvaluation() {
        System.out.println("=== RAG 评估指标 ===");
        System.out.println();

        System.out.println("【检索质量】");
        System.out.println("  • Precision@K: 前 K 个结果的准确率");
        System.out.println("  • Recall@K: 前 K 个结果的召回率");
        System.out.println("  • MRR: 平均倒数排名");
        System.out.println("  • NDCG: 归一化折损累积增益");
        System.out.println();

        System.out.println("【生成质量】");
        System.out.println("  • Faithfulness: 是否忠于检索内容");
        System.out.println("  • Relevance: 回答是否相关");
        System.out.println("  • Coherence: 回答是否连贯");
        System.out.println();

        System.out.println("【评估框架】");
        System.out.println("  • RAGAS: 自动化 RAG 评估");
        System.out.println("  • LlamaIndex: 内置评估工具");
        System.out.println("  • DeepEval: 端到端评估");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: RAG 架构详解                          ║");
        System.out.println("║          检索增强生成技术                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainArchitecture();
        System.out.println();

        explainAdvancedStrategies();
        System.out.println();

        explainEvaluation();
    }
}
