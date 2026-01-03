package phase25;

/**
 * 本地大模型部署
 * 
 * 使用 Ollama 在本地运行开源大模型
 * 适合隐私敏感、离线环境、学习研究等场景
 * 
 * @author Java Course
 * @version 1.0
 */
public class LocalLlm {

    public static void main(String[] args) {
        System.out.println("=== 本地大模型部署 (Ollama) ===\n");

        // 1. 为什么需要本地部署
        whyLocalDeployment();

        // 2. Ollama 入门
        ollamaIntro();

        // 3. Java 集成
        javaIntegration();

        // 4. 模型选择指南
        modelSelection();

        // 5. 性能优化
        performanceOptimization();
    }

    /**
     * 1. 为什么需要本地部署
     */
    static void whyLocalDeployment() {
        System.out.println("💡 1. 为什么需要本地部署");
        System.out.println("=".repeat(50));

        String reasons = """
                【本地部署的优势】

                ┌─────────────────────────────────────────────────┐
                │ 🔒 隐私安全       数据完全不出本地             │
                │ 💰 零 API 成本    不限调用次数                 │
                │ 🌐 离线可用       无需网络连接                 │
                │ ⚡ 低延迟         本地推理更快                  │
                │ 🔧 可定制         可微调、修改模型              │
                │ 📚 学习研究       深入理解模型运行原理          │
                └─────────────────────────────────────────────────┘

                【适用场景】

                1. 敏感数据处理
                   - 医疗健康数据分析
                   - 金融交易数据
                   - 企业内部知识库

                2. 离线环境
                   - 安全隔离的内网
                   - 无网络的嵌入式设备
                   - 飞机/船舶等场景

                3. 成本敏感
                   - 高频调用场景
                   - 批量数据处理
                   - 研发测试环境

                4. 学习研究
                   - 了解模型工作原理
                   - 实验不同模型效果
                   - 模型微调和优化

                【硬件要求】
                ┌────────────────────────────────────────────────────┐
                │  模型大小    │ 最低配置        │ 推荐配置         │
                ├────────────────────────────────────────────────────┤
                │  7B (量化)   │ 8GB RAM         │ 16GB RAM         │
                │  13B (量化)  │ 16GB RAM        │ 32GB RAM         │
                │  34B (量化)  │ 32GB RAM        │ 64GB RAM         │
                │  GPU 加速    │ 6GB VRAM        │ 12GB+ VRAM       │
                └────────────────────────────────────────────────────┘
                """;
        System.out.println(reasons);
    }

    /**
     * 2. Ollama 入门
     */
    static void ollamaIntro() {
        System.out.println("\n🦙 2. Ollama 入门");
        System.out.println("=".repeat(50));

        String intro = """
                【什么是 Ollama】

                Ollama 是一个简单易用的本地大模型运行工具。
                支持 macOS、Windows、Linux，一行命令即可运行模型。

                官网: https://ollama.ai

                【安装 Ollama】

                macOS/Linux:
                curl -fsSL https://ollama.ai/install.sh | sh

                Windows:
                下载安装包: https://ollama.ai/download

                【基本命令】

                # 拉取模型
                ollama pull llama3       # 拉取 LLaMA 3
                ollama pull qwen:7b      # 拉取通义千问 7B
                ollama pull codellama    # 拉取 Code LLaMA

                # 运行模型 (交互式)
                ollama run llama3

                # 列出已下载的模型
                ollama list

                # 删除模型
                ollama rm llama3

                # 查看模型信息
                ollama show llama3

                【启动 API 服务】

                # 默认在 11434 端口启动
                ollama serve

                # 测试 API
                curl http://localhost:11434/api/generate -d '{
                  "model": "llama3",
                  "prompt": "你好"
                }'

                【常用模型推荐】

                ┌────────────────────────────────────────────────────────┐
                │  模型           │ 大小   │ 用途            │ 命令    │
                ├────────────────────────────────────────────────────────┤
                │  llama3:8b      │ 4.7GB  │ 通用对话        │ pull    │
                │  llama3:70b     │ 40GB   │ 高质量推理      │ pull    │
                │  qwen:7b        │ 4.4GB  │ 中文优化        │ pull    │
                │  codellama:7b   │ 3.8GB  │ 代码生成        │ pull    │
                │  mistral:7b     │ 4.1GB  │ 快速推理        │ pull    │
                │  phi:3b         │ 1.7GB  │ 轻量级          │ pull    │
                └────────────────────────────────────────────────────────┘
                """;
        System.out.println(intro);
    }

    /**
     * 3. Java 集成
     */
    static void javaIntegration() {
        System.out.println("\n☕ 3. Java 集成 Ollama");
        System.out.println("=".repeat(50));

        String integration = """
                【方式1: 使用 LangChain4j (推荐)】

                <dependency>
                    <groupId>dev.langchain4j</groupId>
                    <artifactId>langchain4j-ollama</artifactId>
                    <version>0.27.0</version>
                </dependency>

                import dev.langchain4j.model.ollama.OllamaChatModel;

                public class OllamaDemo {

                    public static void main(String[] args) {
                        // 1. 创建模型实例
                        OllamaChatModel model = OllamaChatModel.builder()
                            .baseUrl("http://localhost:11434")
                            .modelName("llama3")
                            .temperature(0.7)
                            .build();

                        // 2. 发送消息
                        String response = model.generate("用Java写一个快速排序");
                        System.out.println(response);
                    }
                }

                【方式2: 使用 AI Service】

                import dev.langchain4j.service.AiServices;
                import dev.langchain4j.service.SystemMessage;

                interface Coder {
                    @SystemMessage("你是一个Java编程专家")
                    String code(String task);
                }

                public class AiServiceDemo {
                    public static void main(String[] args) {
                        OllamaChatModel model = OllamaChatModel.builder()
                            .baseUrl("http://localhost:11434")
                            .modelName("codellama")
                            .build();

                        Coder coder = AiServices.create(Coder.class, model);

                        String code = coder.code("写一个单例模式");
                        System.out.println(code);
                    }
                }

                【方式3: 原生 HTTP 调用】

                public class OllamaHttpClient {

                    private static final String BASE_URL = "http://localhost:11434";

                    public static String generate(String model, String prompt)
                            throws Exception {

                        String url = BASE_URL + "/api/generate";
                        String body = String.format(
                            "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                            model, prompt);

                        HttpURLConnection conn = (HttpURLConnection)
                            new URI(url).toURL().openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(body.getBytes(StandardCharsets.UTF_8));
                        }

                        // 读取响应...
                        return readResponse(conn);
                    }
                }

                【流式输出】

                OllamaStreamingChatModel streamModel = OllamaStreamingChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("llama3")
                    .build();

                streamModel.generate("讲个笑话", new StreamingResponseHandler<>() {
                    @Override
                    public void onNext(String token) {
                        System.out.print(token); // 逐字输出
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        System.out.println("\\n完成!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
                """;
        System.out.println(integration);
    }

    /**
     * 4. 模型选择指南
     */
    static void modelSelection() {
        System.out.println("\n🎯 4. 本地模型选择指南");
        System.out.println("=".repeat(50));

        String guide = """
                【按场景选择】

                ┌─────────────────────────────────────────────────────────┐
                │  场景            │ 推荐模型         │ 原因             │
                ├─────────────────────────────────────────────────────────┤
                │  中文对话        │ qwen:7b          │ 中文优化          │
                │  代码生成        │ codellama:7b     │ 代码专精          │
                │  通用对话        │ llama3:8b        │ 综合能力强        │
                │  轻量级/边缘     │ phi:3b           │ 体积小速度快      │
                │  数学推理        │ deepseek-coder   │ 推理能力强        │
                │  高质量输出      │ llama3:70b       │ 效果最好          │
                └─────────────────────────────────────────────────────────┘

                【按硬件选择】

                ┌─────────────────────────────────────────────────────────┐
                │  你的配置           │ 推荐模型                         │
                ├─────────────────────────────────────────────────────────┤
                │  8GB RAM, 无GPU     │ phi:3b, qwen:1.8b               │
                │  16GB RAM, 无GPU    │ llama3:8b, qwen:7b              │
                │  32GB RAM, 无GPU    │ llama3:8b, codellama:13b        │
                │  8GB+ VRAM GPU      │ llama3:8b (GPU加速)             │
                │  24GB+ VRAM GPU     │ llama3:70b-q4 (GPU加速)         │
                └─────────────────────────────────────────────────────────┘

                【量化版本说明】

                模型名后缀代表量化程度:

                • 无后缀 / fp16: 原始精度，效果最好，占用最大
                • q8: 8位量化，效果接近原始，占用减半
                • q4: 4位量化，效果略降，占用约1/4
                • q2: 2位量化，效果下降明显，极限压缩

                示例:
                ollama pull llama3:8b-q4     # 4位量化版
                ollama pull llama3:8b        # 默认版本

                【推荐组合】

                开发机 (16GB RAM):
                - 日常对话: llama3:8b
                - 写代码: codellama:7b
                - 中文任务: qwen:7b

                服务器 (64GB RAM + A100):
                - 生产服务: llama3:70b-q4
                - 备用: codellama:34b-q4
                """;
        System.out.println(guide);
    }

    /**
     * 5. 性能优化
     */
    static void performanceOptimization() {
        System.out.println("\n⚡ 5. 性能优化");
        System.out.println("=".repeat(50));

        System.out.println("""
                【GPU 加速】

                Ollama 自动检测并使用 GPU，支持:
                - NVIDIA GPU (CUDA)
                - AMD GPU (ROCm)
                - Apple Silicon (Metal)

                # 检查是否使用 GPU
                ollama run llama3 --verbose

                # 指定 GPU 层数
                OLLAMA_NUM_GPU=35 ollama run llama3

                【内存优化】

                # 设置上下文长度 (默认 2048)
                ollama run llama3 --context-size 4096

                # 使用量化模型减少内存
                ollama pull llama3:8b-q4

                【并发处理】

                # 设置最大并发请求数
                OLLAMA_NUM_PARALLEL=4 ollama serve

                # Java 线程池配置
                ExecutorService executor = Executors.newFixedThreadPool(4);

                List<Future<String>> futures = prompts.stream()
                    .map(prompt -> executor.submit(() -> model.generate(prompt)))
                    .collect(Collectors.toList());

                【缓存优化】

                // 相同 system prompt 复用，减少计算
                OllamaChatModel model = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("llama3")
                    // 保持上下文
                    .numPredict(512)
                    .build();

                【生产部署建议】

                ┌─────────────────────────────────────────────────┐
                │ 1. 使用 Docker 容器化部署                       │
                │    docker run -d --gpus=all ollama/ollama     │
                │                                                 │
                │ 2. 配置负载均衡                                 │
                │    多个 Ollama 实例 + Nginx                    │
                │                                                 │
                │ 3. 健康检查                                     │
                │    GET http://localhost:11434/api/tags         │
                │                                                 │
                │ 4. 监控指标                                     │
                │    - 响应时间                                   │
                │    - 内存/GPU 使用率                           │
                │    - 队列长度                                   │
                └─────────────────────────────────────────────────┘

                【Docker Compose 示例】

                version: '3.8'
                services:
                  ollama:
                    image: ollama/ollama
                    ports:
                      - "11434:11434"
                    volumes:
                      - ollama_data:/root/.ollama
                    deploy:
                      resources:
                        reservations:
                          devices:
                            - driver: nvidia
                              count: 1
                              capabilities: [gpu]

                volumes:
                  ollama_data:
                """);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📝 本节小结");
        System.out.println("=".repeat(50));
        System.out.println("""

                ✅ Ollama 是最简单的本地部署方案
                ✅ 根据硬件和场景选择合适的模型
                ✅ LangChain4j 提供简洁的 Java 集成
                ✅ GPU 加速可显著提升性能
                ✅ 生产环境注意容器化和监控

                🎉 第44周学习完成！
                下一节: LangChainBasics.java - 开始第45周 LangChain4j 学习
                """);
    }
}
