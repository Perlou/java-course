package phase26;

/**
 * 向量嵌入原理
 * 
 * 向量嵌入是将文本、图像等非结构化数据转换为高维向量的技术，
 * 是 RAG 系统的核心基础。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class VectorIntro {

    /**
     * ========================================
     * 第一部分：什么是向量嵌入
     * ========================================
     */
    public static void explainEmbedding() {
        System.out.println("=== 向量嵌入概述 ===");
        System.out.println();

        System.out.println("【什么是向量嵌入 (Embedding)】");
        System.out.println("  将非结构化数据（文本、图像、音频）转换为数值向量");
        System.out.println("  向量维度通常为 384 ~ 4096 维");
        System.out.println("  相似的内容在向量空间中距离更近");
        System.out.println();

        System.out.println("【为什么需要向量化】");
        System.out.println("  • 计算机无法直接理解文本语义");
        System.out.println("  • 向量可以进行数学运算（距离、相似度）");
        System.out.println("  • 支持语义搜索而非关键词匹配");
        System.out.println();

        System.out.println("【示例】");
        System.out.println("```");
        System.out.println("\"我喜欢猫\" → [0.12, -0.45, 0.78, ..., 0.33]  (384维)");
        System.out.println("\"I love cats\" → [0.11, -0.44, 0.79, ..., 0.32]  (语义相近，向量相似)");
        System.out.println("\"今天天气很好\" → [0.89, 0.21, -0.55, ..., -0.12]  (语义不同，向量差异大)");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：嵌入模型
     * ========================================
     */
    public static void explainEmbeddingModels() {
        System.out.println("=== 常用嵌入模型 ===");
        System.out.println();

        System.out.println("【主流嵌入模型】");
        System.out.println("┌───────────────────────┬──────────┬────────────┬─────────────┐");
        System.out.println("│ 模型                  │ 维度     │ 语言       │ 特点        │");
        System.out.println("├───────────────────────┼──────────┼────────────┼─────────────┤");
        System.out.println("│ text-embedding-ada-002│ 1536     │ 多语言     │ OpenAI      │");
        System.out.println("│ text-embedding-3-small│ 512-1536 │ 多语言     │ OpenAI 新版  │");
        System.out.println("│ text-embedding-3-large│ 256-3072 │ 多语言     │ 最强性能     │");
        System.out.println("│ bge-large-zh         │ 1024     │ 中文       │ 智源开源     │");
        System.out.println("│ m3e-base             │ 768      │ 中英       │ 国产优选     │");
        System.out.println("│ sentence-transformers│ 384-768  │ 多语言     │ 开源免费     │");
        System.out.println("└───────────────────────┴──────────┴────────────┴─────────────┘");
        System.out.println();

        System.out.println("【选型建议】");
        System.out.println("  • 商业项目: OpenAI text-embedding-3-small (性价比高)");
        System.out.println("  • 中文场景: bge-large-zh 或 m3e-base");
        System.out.println("  • 开源部署: sentence-transformers");
        System.out.println("  • 私有化: 选择可本地部署的开源模型");
    }

    /**
     * ========================================
     * 第三部分：相似度计算
     * ========================================
     */
    public static void explainSimilarity() {
        System.out.println("=== 相似度计算 ===");
        System.out.println();

        System.out.println("【常用相似度度量】");
        System.out.println();

        System.out.println("1. 余弦相似度 (Cosine Similarity)");
        System.out.println("   cos(A, B) = (A · B) / (||A|| × ||B||)");
        System.out.println("   范围: [-1, 1]，1 表示完全相同");
        System.out.println("   最常用于文本相似度");
        System.out.println();

        System.out.println("2. 欧氏距离 (Euclidean Distance)");
        System.out.println("   d(A, B) = √Σ(Ai - Bi)²");
        System.out.println("   范围: [0, ∞)，0 表示完全相同");
        System.out.println("   对向量长度敏感");
        System.out.println();

        System.out.println("3. 点积 (Dot Product)");
        System.out.println("   A · B = Σ(Ai × Bi)");
        System.out.println("   当向量已归一化时，等同于余弦相似度");
        System.out.println("   计算效率最高");
        System.out.println();

        System.out.println("【代码示例】");
        System.out.println("```java");
        System.out.println("// 余弦相似度计算");
        System.out.println("public static double cosineSimilarity(float[] a, float[] b) {");
        System.out.println("    double dotProduct = 0.0;");
        System.out.println("    double normA = 0.0, normB = 0.0;");
        System.out.println("    for (int i = 0; i < a.length; i++) {");
        System.out.println("        dotProduct += a[i] * b[i];");
        System.out.println("        normA += a[i] * a[i];");
        System.out.println("        normB += b[i] * b[i];");
        System.out.println("    }");
        System.out.println("    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：向量索引算法
     * ========================================
     */
    public static void explainIndexAlgorithms() {
        System.out.println("=== 向量索引算法 ===");
        System.out.println();

        System.out.println("【为什么需要索引】");
        System.out.println("  暴力搜索: O(n × d) 复杂度，n=100万时极慢");
        System.out.println("  索引搜索: 近似最近邻 (ANN)，牺牲少量精度换取速度");
        System.out.println();

        System.out.println("【主流索引算法】");
        System.out.println();

        System.out.println("1. HNSW (Hierarchical Navigable Small World)");
        System.out.println("   • 基于图的索引，多层导航");
        System.out.println("   • 查询速度快，召回率高");
        System.out.println("   • 内存占用较大");
        System.out.println("   • 推荐用于：高精度场景");
        System.out.println();

        System.out.println("2. IVF (Inverted File Index)");
        System.out.println("   • 聚类 + 倒排索引");
        System.out.println("   • 支持大规模数据");
        System.out.println("   • 可与 PQ 组合压缩");
        System.out.println("   • 推荐用于：大数据量场景");
        System.out.println();

        System.out.println("3. PQ (Product Quantization)");
        System.out.println("   • 向量量化压缩");
        System.out.println("   • 内存占用小");
        System.out.println("   • 精度有损失");
        System.out.println("   • 推荐用于：内存受限场景");
        System.out.println();

        System.out.println("【性能对比】");
        System.out.println("┌────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ 算法       │ 查询速度   │ 召回率     │ 内存占用   │");
        System.out.println("├────────────┼────────────┼────────────┼────────────┤");
        System.out.println("│ Flat (暴力)│ 最慢       │ 100%       │ 基准       │");
        System.out.println("│ HNSW       │ 快         │ >95%       │ 较大       │");
        System.out.println("│ IVF        │ 中等       │ 90-95%     │ 中等       │");
        System.out.println("│ IVF+PQ     │ 快         │ 85-90%     │ 小         │");
        System.out.println("└────────────┴────────────┴────────────┴────────────┘");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: 向量嵌入原理                          ║");
        System.out.println("║          RAG 系统的核心基础                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainEmbedding();
        System.out.println();

        explainEmbeddingModels();
        System.out.println();

        explainSimilarity();
        System.out.println();

        explainIndexAlgorithms();
    }
}
