package phase26;

/**
 * Milvus 向量数据库
 * 
 * Milvus 是开源的云原生向量数据库，
 * 专为 AI 应用场景设计。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class MilvusDemo {

    /**
     * ========================================
     * 第一部分：Milvus 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== Milvus 概述 ===");
        System.out.println();

        System.out.println("【什么是 Milvus】");
        System.out.println("  • 开源的云原生向量数据库");
        System.out.println("  • 专为 AI 场景设计");
        System.out.println("  • 支持万亿级向量");
        System.out.println("  • 由 Zilliz 公司开发");
        System.out.println();

        System.out.println("【核心特性】");
        System.out.println("  • 高性能: 毫秒级查询响应");
        System.out.println("  • 高可用: 分布式架构");
        System.out.println("  • 弹性伸缩: 计算存储分离");
        System.out.println("  • 多索引: HNSW, IVF, DiskANN");
        System.out.println("  • 混合查询: 向量 + 标量过滤");
        System.out.println();

        System.out.println("【版本选择】");
        System.out.println("  • Milvus Lite: 嵌入式，单机测试");
        System.out.println("  • Milvus Standalone: 单机部署");
        System.out.println("  • Milvus Cluster: 分布式集群");
        System.out.println("  • Zilliz Cloud: 全托管云服务");
    }

    /**
     * ========================================
     * 第二部分：核心概念
     * ========================================
     */
    public static void explainConcepts() {
        System.out.println("=== Milvus 核心概念 ===");
        System.out.println();

        System.out.println("【数据模型】");
        System.out.println("┌─────────────────────────────────────────────────────┐");
        System.out.println("│  Database (数据库)                                  │");
        System.out.println("│    └── Collection (集合) ≈ 表                       │");
        System.out.println("│          └── Partition (分区) 可选                  │");
        System.out.println("│                └── Segment (段) 存储单元            │");
        System.out.println("│                      └── Entity (实体) ≈ 行         │");
        System.out.println("└─────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("【Collection Schema】");
        System.out.println("  必须包含:");
        System.out.println("  • 主键字段 (INT64 或 VARCHAR)");
        System.out.println("  • 向量字段 (FLOAT_VECTOR 或 BINARY_VECTOR)");
        System.out.println("  可选包含:");
        System.out.println("  • 标量字段 (INT, VARCHAR, BOOL, FLOAT, JSON, ARRAY)");
        System.out.println();

        System.out.println("【索引类型】");
        System.out.println("  • FLAT: 暴力搜索，精度最高");
        System.out.println("  • IVF_FLAT: 倒排索引");
        System.out.println("  • IVF_PQ: 倒排 + 量化压缩");
        System.out.println("  • HNSW: 图索引，查询最快");
        System.out.println("  • DISKANN: 磁盘索引，大规模");
    }

    /**
     * ========================================
     * 第三部分：Java SDK 使用
     * ========================================
     */
    public static void explainJavaSDK() {
        System.out.println("=== Milvus Java SDK ===");
        System.out.println();

        System.out.println("【Maven 依赖】");
        System.out.println("```xml");
        System.out.println("<dependency>");
        System.out.println("    <groupId>io.milvus</groupId>");
        System.out.println("    <artifactId>milvus-sdk-java</artifactId>");
        System.out.println("    <version>2.3.4</version>");
        System.out.println("</dependency>");
        System.out.println("```");
        System.out.println();

        System.out.println("【连接示例】");
        System.out.println("```java");
        System.out.println("import io.milvus.v2.client.*;");
        System.out.println();
        System.out.println("// 创建客户端");
        System.out.println("MilvusClientV2 client = new MilvusClientV2(");
        System.out.println("    ConnectConfig.builder()");
        System.out.println("        .uri(\"http://localhost:19530\")");
        System.out.println("        .build()");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("【创建 Collection】");
        System.out.println("```java");
        System.out.println("CreateCollectionReq request = CreateCollectionReq.builder()");
        System.out.println("    .collectionName(\"documents\")");
        System.out.println("    .dimension(1536)  // 向量维度");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("client.createCollection(request);");
        System.out.println("```");
        System.out.println();

        System.out.println("【插入数据】");
        System.out.println("```java");
        System.out.println("List<JsonObject> data = Arrays.asList(");
        System.out.println("    new JsonObject()");
        System.out.println("        .addProperty(\"id\", 1L)");
        System.out.println("        .add(\"vector\", vectorJsonArray)");
        System.out.println("        .addProperty(\"text\", \"example document\")");
        System.out.println(");");
        System.out.println();
        System.out.println("InsertReq insertReq = InsertReq.builder()");
        System.out.println("    .collectionName(\"documents\")");
        System.out.println("    .data(data)");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("client.insert(insertReq);");
        System.out.println("```");
        System.out.println();

        System.out.println("【向量搜索】");
        System.out.println("```java");
        System.out.println("SearchReq searchReq = SearchReq.builder()");
        System.out.println("    .collectionName(\"documents\")");
        System.out.println("    .data(Collections.singletonList(queryVector))");
        System.out.println("    .topK(10)");
        System.out.println("    .outputFields(Arrays.asList(\"id\", \"text\"))");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("SearchResp resp = client.search(searchReq);");
        System.out.println("List<List<SearchResp.SearchResult>> results = resp.getSearchResults();");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：最佳实践
     * ========================================
     */
    public static void explainBestPractices() {
        System.out.println("=== Milvus 最佳实践 ===");
        System.out.println();

        System.out.println("【索引选择】");
        System.out.println("  • 数据量 < 100万: HNSW");
        System.out.println("  • 100万 ~ 1000万: IVF_FLAT");
        System.out.println("  • > 1000万: IVF_PQ 或 DISKANN");
        System.out.println();

        System.out.println("【性能优化】");
        System.out.println("  • 批量插入: 每批 1000-10000 条");
        System.out.println("  • 分区: 按时间/类别分区");
        System.out.println("  • 预加载: 热数据 load 到内存");
        System.out.println("  • 标量过滤: 配合向量搜索缩小范围");
        System.out.println();

        System.out.println("【生产部署】");
        System.out.println("  • 使用 K8s 部署 Milvus Cluster");
        System.out.println("  • 配置持久化存储 (MinIO/S3)");
        System.out.println("  • 监控: Prometheus + Grafana");
        System.out.println("  • 备份: 定期 Collection 快照");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: Milvus 向量数据库                     ║");
        System.out.println("║          云原生向量存储解决方案                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainConcepts();
        System.out.println();

        explainJavaSDK();
        System.out.println();

        explainBestPractices();
    }
}
