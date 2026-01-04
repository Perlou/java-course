package phase26;

/**
 * PostgreSQL pgvector
 * 
 * pgvector 是 PostgreSQL 的向量搜索扩展，
 * 让关系数据库具备向量检索能力。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class PgVectorDemo {

    /**
     * ========================================
     * 第一部分：pgvector 概述
     * ========================================
     */
    public static void explainOverview() {
        System.out.println("=== pgvector 概述 ===");
        System.out.println();

        System.out.println("【什么是 pgvector】");
        System.out.println("  • PostgreSQL 的向量扩展");
        System.out.println("  • 开源免费");
        System.out.println("  • 与 SQL 完美结合");
        System.out.println("  • 适合中小规模向量场景");
        System.out.println();

        System.out.println("【优势】");
        System.out.println("  • 无需额外向量数据库");
        System.out.println("  • 事务支持 (ACID)");
        System.out.println("  • 向量 + 标量一体查询");
        System.out.println("  • 复用 PostgreSQL 生态");
        System.out.println();

        System.out.println("【适用场景】");
        System.out.println("  • 向量数量 < 1000 万");
        System.out.println("  • 已有 PostgreSQL 基础设施");
        System.out.println("  • 需要事务一致性");
        System.out.println("  • 团队熟悉 SQL");
    }

    /**
     * ========================================
     * 第二部分：安装与配置
     * ========================================
     */
    public static void explainSetup() {
        System.out.println("=== pgvector 安装配置 ===");
        System.out.println();

        System.out.println("【Docker 快速启动】");
        System.out.println("```bash");
        System.out.println("docker run -d \\");
        System.out.println("  --name pgvector \\");
        System.out.println("  -e POSTGRES_PASSWORD=postgres \\");
        System.out.println("  -p 5432:5432 \\");
        System.out.println("  pgvector/pgvector:pg16");
        System.out.println("```");
        System.out.println();

        System.out.println("【启用扩展】");
        System.out.println("```sql");
        System.out.println("CREATE EXTENSION IF NOT EXISTS vector;");
        System.out.println("```");
        System.out.println();

        System.out.println("【创建向量表】");
        System.out.println("```sql");
        System.out.println("CREATE TABLE documents (");
        System.out.println("    id SERIAL PRIMARY KEY,");
        System.out.println("    content TEXT NOT NULL,");
        System.out.println("    embedding VECTOR(1536),  -- 1536 维向量");
        System.out.println("    metadata JSONB,");
        System.out.println("    created_at TIMESTAMP DEFAULT NOW()");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：索引与查询
     * ========================================
     */
    public static void explainIndexAndQuery() {
        System.out.println("=== pgvector 索引与查询 ===");
        System.out.println();

        System.out.println("【创建索引】");
        System.out.println("```sql");
        System.out.println("-- HNSW 索引 (推荐)");
        System.out.println("CREATE INDEX ON documents USING hnsw (embedding vector_cosine_ops);");
        System.out.println();
        System.out.println("-- IVFFlat 索引 (大数据量)");
        System.out.println("CREATE INDEX ON documents USING ivfflat (embedding vector_l2_ops)");
        System.out.println("  WITH (lists = 100);");
        System.out.println("```");
        System.out.println();

        System.out.println("【距离函数】");
        System.out.println("  • <->    欧氏距离 (L2)");
        System.out.println("  • <#>    负内积");
        System.out.println("  • <=>    余弦距离");
        System.out.println();

        System.out.println("【相似度查询】");
        System.out.println("```sql");
        System.out.println("-- 查询最相似的 10 条");
        System.out.println("SELECT id, content,");
        System.out.println("       1 - (embedding <=> '[0.1, 0.2, ...]') AS similarity");
        System.out.println("FROM documents");
        System.out.println("ORDER BY embedding <=> '[0.1, 0.2, ...]'");
        System.out.println("LIMIT 10;");
        System.out.println("```");
        System.out.println();

        System.out.println("【混合查询】");
        System.out.println("```sql");
        System.out.println("-- 向量 + 标量过滤");
        System.out.println("SELECT id, content");
        System.out.println("FROM documents");
        System.out.println("WHERE metadata->>'category' = 'tech'");
        System.out.println("  AND created_at > '2024-01-01'");
        System.out.println("ORDER BY embedding <=> $1");
        System.out.println("LIMIT 10;");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Java 使用
     * ========================================
     */
    public static void explainJavaUsage() {
        System.out.println("=== Java 使用 pgvector ===");
        System.out.println();

        System.out.println("【JDBC 方式】");
        System.out.println("```java");
        System.out.println("// 插入向量");
        System.out.println("String sql = \"INSERT INTO documents (content, embedding) VALUES (?, ?::vector)\";");
        System.out.println("try (PreparedStatement ps = conn.prepareStatement(sql)) {");
        System.out.println("    ps.setString(1, \"文档内容\");");
        System.out.println("    ps.setString(2, Arrays.toString(embedding));  // 转字符串");
        System.out.println("    ps.executeUpdate();");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【Spring Data JPA】");
        System.out.println("```java");
        System.out.println("// 自定义查询");
        System.out.println("@Query(value = \"SELECT * FROM documents \" +");
        System.out.println("       \"ORDER BY embedding <=> cast(:vector as vector) \" +");
        System.out.println("       \"LIMIT :limit\", nativeQuery = true)");
        System.out.println("List<Document> findSimilar(@Param(\"vector\") String vector,");
        System.out.println("                          @Param(\"limit\") int limit);");
        System.out.println("```");
        System.out.println();

        System.out.println("【pgvector-java 库】");
        System.out.println("```xml");
        System.out.println("<dependency>");
        System.out.println("    <groupId>com.pgvector</groupId>");
        System.out.println("    <artifactId>pgvector</artifactId>");
        System.out.println("    <version>0.1.4</version>");
        System.out.println("</dependency>");
        System.out.println("```");
        System.out.println();
        System.out.println("```java");
        System.out.println("import com.pgvector.PGvector;");
        System.out.println();
        System.out.println("PGvector.addVectorType(conn);");
        System.out.println("PGvector vector = new PGvector(new float[]{0.1f, 0.2f, ...});");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: PostgreSQL pgvector                   ║");
        System.out.println("║          关系数据库的向量扩展                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainOverview();
        System.out.println();

        explainSetup();
        System.out.println();

        explainIndexAndQuery();
        System.out.println();

        explainJavaUsage();
    }
}
