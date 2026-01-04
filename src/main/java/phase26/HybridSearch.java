package phase26;

/**
 * 混合检索策略
 * 
 * 结合向量检索和关键词检索的优势，
 * 提升 RAG 系统的检索质量。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class HybridSearch {

    /**
     * ========================================
     * 第一部分：检索方法对比
     * ========================================
     */
    public static void explainSearchMethods() {
        System.out.println("=== 检索方法对比 ===");
        System.out.println();

        System.out.println("【关键词检索 (Lexical Search)】");
        System.out.println("  • 代表: BM25, TF-IDF");
        System.out.println("  • 原理: 词频统计 + 逆文档频率");
        System.out.println("  • 优点: 精确匹配、专有名词、代码");
        System.out.println("  • 缺点: 无法理解语义");
        System.out.println();

        System.out.println("【向量检索 (Semantic Search)】");
        System.out.println("  • 代表: 向量相似度搜索");
        System.out.println("  • 原理: 语义嵌入 + 向量距离");
        System.out.println("  • 优点: 理解语义、同义词、换句话说");
        System.out.println("  • 缺点: 精确匹配弱、专业术语差");
        System.out.println();

        System.out.println("【对比表】");
        System.out.println("┌─────────────────┬───────────────┬───────────────┐");
        System.out.println("│ 查询类型        │ 关键词检索    │ 向量检索      │");
        System.out.println("├─────────────────┼───────────────┼───────────────┤");
        System.out.println("│ \"error 404\"    │ ✓ 精确匹配    │ △ 可能偏移    │");
        System.out.println("│ \"网页打不开\"   │ ✗ 词不匹配    │ ✓ 语义相近    │");
        System.out.println("│ \"API 调用失败\" │ ✓ 较好        │ ✓ 较好        │");
        System.out.println("│ \"张三的合同\"   │ ✓ 实体精确    │ △ 可能泛化    │");
        System.out.println("└─────────────────┴───────────────┴───────────────┘");
    }

    /**
     * ========================================
     * 第二部分：混合检索原理
     * ========================================
     */
    public static void explainHybridSearch() {
        System.out.println("=== 混合检索原理 ===");
        System.out.println();

        System.out.println("【混合检索 (Hybrid Search)】");
        System.out.println("  结合关键词检索和向量检索");
        System.out.println("  取两者优势，弥补各自短板");
        System.out.println();

        System.out.println("【工作流程】");
        System.out.println("```");
        System.out.println("     用户查询");
        System.out.println("        │");
        System.out.println("   ┌────┴────┐");
        System.out.println("   ↓         ↓");
        System.out.println("关键词检索  向量检索");
        System.out.println("(BM25)      (Embedding)");
        System.out.println("   ↓         ↓");
        System.out.println("结果集A     结果集B");
        System.out.println("   └────┬────┘");
        System.out.println("        ↓");
        System.out.println("   分数融合 (Fusion)");
        System.out.println("        ↓");
        System.out.println("   最终排序结果");
        System.out.println("```");
        System.out.println();

        System.out.println("【分数融合方法】");
        System.out.println();

        System.out.println("1. 加权求和");
        System.out.println("   score = α × bm25_score + β × vector_score");
        System.out.println("   需要分数归一化");
        System.out.println();

        System.out.println("2. RRF (Reciprocal Rank Fusion)");
        System.out.println("   score = Σ 1/(k + rank_i)");
        System.out.println("   不需要归一化，更稳定");
        System.out.println("   k 通常取 60");
    }

    /**
     * ========================================
     * 第三部分：重排序 (Reranking)
     * ========================================
     */
    public static void explainReranking() {
        System.out.println("=== 重排序 (Reranking) ===");
        System.out.println();

        System.out.println("【为什么需要重排序】");
        System.out.println("  • 初步检索追求召回率");
        System.out.println("  • 重排序优化精确度");
        System.out.println("  • Cross-Encoder 效果更好但计算量大");
        System.out.println();

        System.out.println("【二阶段检索】");
        System.out.println("  第一阶段: 粗检索 (向量/关键词)");
        System.out.println("            → 召回 50-100 条");
        System.out.println("  第二阶段: 精排序 (Reranker)");
        System.out.println("            → 返回 Top 5-10");
        System.out.println();

        System.out.println("【常用 Reranker 模型】");
        System.out.println("  • Cohere Rerank");
        System.out.println("  • bge-reranker-large (中文)");
        System.out.println("  • cross-encoder/ms-marco");
        System.out.println();

        System.out.println("【代码示例】");
        System.out.println("```java");
        System.out.println("// LangChain4j 重排序");
        System.out.println("ContentRetriever retriever = DefaultContentRetriever.builder()");
        System.out.println("    .embeddingStore(embeddingStore)");
        System.out.println("    .embeddingModel(embeddingModel)");
        System.out.println("    .contentAggregator(ReRankingContentAggregator.builder()");
        System.out.println("        .scoringModel(scoringModel)");
        System.out.println("        .minScore(0.5)");
        System.out.println("        .build())");
        System.out.println("    .build();");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：实现方案
     * ========================================
     */
    public static void explainImplementation() {
        System.out.println("=== 混合检索实现方案 ===");
        System.out.println();

        System.out.println("【方案对比】");
        System.out.println();

        System.out.println("1. Elasticsearch + 向量插件");
        System.out.println("   • ES 8.x 原生支持向量");
        System.out.println("   • knn_query + must 混合");
        System.out.println("   • 适合已有 ES 基础设施");
        System.out.println();

        System.out.println("2. Milvus + BM25");
        System.out.println("   • 分开执行再融合");
        System.out.println("   • 或使用 Milvus 全文检索功能");
        System.out.println();

        System.out.println("3. Weaviate");
        System.out.println("   • 原生混合检索支持");
        System.out.println("   • hybrid 参数控制权重");
        System.out.println();

        System.out.println("4. Qdrant");
        System.out.println("   • 稀疏向量 (Sparse) + 密集向量");
        System.out.println("   • 一套系统解决");
        System.out.println();

        System.out.println("【推荐选择】");
        System.out.println("  • 入门: pgvector + pg_trgm");
        System.out.println("  • 中等: Elasticsearch 8.x");
        System.out.println("  • 专业: Qdrant / Weaviate");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: 混合检索策略                          ║");
        System.out.println("║          向量 + 关键词的最佳组合                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainSearchMethods();
        System.out.println();

        explainHybridSearch();
        System.out.println();

        explainReranking();
        System.out.println();

        explainImplementation();
    }
}
