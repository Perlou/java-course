package phase26;

/**
 * 文档加载与切分
 * 
 * 文档处理是 RAG 系统的第一步，
 * 合理的切分策略直接影响检索质量。
 * 
 * @author Java Course
 * @since Phase 26
 */
public class DocumentLoader {

    /**
     * ========================================
     * 第一部分：文档加载
     * ========================================
     */
    public static void explainDocumentLoading() {
        System.out.println("=== 文档加载 ===");
        System.out.println();

        System.out.println("【常见文档格式】");
        System.out.println("  • PDF: Apache PDFBox, PyPDF");
        System.out.println("  • Word: Apache POI");
        System.out.println("  • HTML: Jsoup");
        System.out.println("  • Markdown: Commonmark");
        System.out.println("  • Excel: Apache POI");
        System.out.println("  • 图片/扫描件: OCR (Tesseract)");
        System.out.println();

        System.out.println("【LangChain4j 文档加载器】");
        System.out.println("```java");
        System.out.println("// PDF 加载");
        System.out.println("Document doc = FileSystemDocumentLoader.loadDocument(");
        System.out.println("    Paths.get(\"document.pdf\"),");
        System.out.println("    new ApachePdfBoxDocumentParser()");
        System.out.println(");");
        System.out.println();
        System.out.println("// 目录批量加载");
        System.out.println("List<Document> docs = FileSystemDocumentLoader.loadDocuments(");
        System.out.println("    Paths.get(\"./documents\"),");
        System.out.println("    new ApachePdfBoxDocumentParser()");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：文档切分策略
     * ========================================
     */
    public static void explainChunkingStrategies() {
        System.out.println("=== 文档切分策略 ===");
        System.out.println();

        System.out.println("【为什么要切分】");
        System.out.println("  • 嵌入模型有 Token 限制 (通常 512)");
        System.out.println("  • 小块检索更精准");
        System.out.println("  • LLM 上下文窗口有限");
        System.out.println();

        System.out.println("【切分策略】");
        System.out.println();

        System.out.println("1. 固定大小切分");
        System.out.println("   • 按字符数/Token 数切分");
        System.out.println("   • 简单但可能切断句子");
        System.out.println("   • 需要设置重叠 (overlap)");
        System.out.println();

        System.out.println("2. 递归字符切分");
        System.out.println("   • 优先按段落 → 句子 → 字符切分");
        System.out.println("   • 保持语义完整性");
        System.out.println("   • LangChain 推荐方式");
        System.out.println();

        System.out.println("3. 语义切分");
        System.out.println("   • 按语义相似度分组");
        System.out.println("   • 更智能但计算成本高");
        System.out.println();

        System.out.println("4. 结构化切分");
        System.out.println("   • 按标题/章节切分");
        System.out.println("   • 保留文档结构信息");
        System.out.println("   • 适合有明确结构的文档");
    }

    /**
     * ========================================
     * 第三部分：切分参数调优
     * ========================================
     */
    public static void explainChunkingParams() {
        System.out.println("=== 切分参数调优 ===");
        System.out.println();

        System.out.println("【关键参数】");
        System.out.println();

        System.out.println("  chunk_size (块大小)");
        System.out.println("    • 太小: 丢失上下文");
        System.out.println("    • 太大: 检索不精准");
        System.out.println("    • 推荐: 200-1000 字符");
        System.out.println();

        System.out.println("  chunk_overlap (重叠)");
        System.out.println("    • 防止切断关键信息");
        System.out.println("    • 通常设置为 chunk_size 的 10-20%");
        System.out.println("    • 推荐: 50-200 字符");
        System.out.println();

        System.out.println("【LangChain4j 示例】");
        System.out.println("```java");
        System.out.println("DocumentSplitter splitter = DocumentSplitters.recursive(");
        System.out.println("    500,   // maxSegmentSize");
        System.out.println("    50     // maxOverlapSize");
        System.out.println(");");
        System.out.println();
        System.out.println("List<TextSegment> segments = splitter.split(document);");
        System.out.println("```");
        System.out.println();

        System.out.println("【Parent-Child 策略】");
        System.out.println("  • 检索: 用小块精准匹配");
        System.out.println("  • 返回: 返回小块所属的大块");
        System.out.println("  • 保留更多上下文");
    }

    /**
     * ========================================
     * 第四部分：元数据处理
     * ========================================
     */
    public static void explainMetadata() {
        System.out.println("=== 元数据处理 ===");
        System.out.println();

        System.out.println("【元数据的价值】");
        System.out.println("  • 过滤: 按来源/时间/类别筛选");
        System.out.println("  • 溯源: 回答附带出处");
        System.out.println("  • 权限: 基于元数据控制访问");
        System.out.println();

        System.out.println("【常见元数据】");
        System.out.println("  • 文件名/路径");
        System.out.println("  • 页码/章节");
        System.out.println("  • 创建/更新时间");
        System.out.println("  • 作者/部门");
        System.out.println("  • 文档类型/标签");
        System.out.println();

        System.out.println("【代码示例】");
        System.out.println("```java");
        System.out.println("TextSegment segment = TextSegment.from(");
        System.out.println("    \"文档内容...\",");
        System.out.println("    Metadata.from(Map.of(");
        System.out.println("        \"source\", \"handbook.pdf\",");
        System.out.println("        \"page\", \"15\",");
        System.out.println("        \"category\", \"HR\"");
        System.out.println("    ))");
        System.out.println(");");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 26: 文档加载与切分                        ║");
        System.out.println("║          RAG 数据处理流水线                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainDocumentLoading();
        System.out.println();

        explainChunkingStrategies();
        System.out.println();

        explainChunkingParams();
        System.out.println();

        explainMetadata();
    }
}
