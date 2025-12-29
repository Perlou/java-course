package phase20;

/**
 * Phase 20: ATAM 架构评估方法
 * 
 * 本课程涵盖：
 * 1. ATAM 概述
 * 2. 质量属性场景
 * 3. 评估流程
 * 4. 风险识别与权衡
 */
public class ATAMDemo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          📋 Phase 20: ATAM 架构评估方法                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        atamOverview();
        qualityAttributes();
        scenarioAnalysis();
        evaluationProcess();
        tradeoffAnalysis();
    }

    // ==================== 1. ATAM 概述 ====================

    private static void atamOverview() {
        System.out.println("\n📌 1. ATAM 概述\n");

        System.out.println("ATAM (Architecture Tradeoff Analysis Method)");
        System.out.println("架构权衡分析方法 - SEI 提出的系统化架构评估框架");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     ATAM 核心目标                            │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│  1️⃣  识别风险 - 发现架构中的潜在问题                        │");
        System.out.println("│                                                              │");
        System.out.println("│  2️⃣  分析权衡 - 理解架构决策的代价                          │");
        System.out.println("│                                                              │");
        System.out.println("│  3️⃣  验证适配 - 确认架构满足质量需求                        │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("ATAM 产出物：");
        System.out.println("  • 架构风险清单");
        System.out.println("  • 非风险点（已确认的设计决策）");
        System.out.println("  • 敏感点（影响单个质量属性的决策）");
        System.out.println("  • 权衡点（影响多个质量属性的决策）");
    }

    // ==================== 2. 质量属性 ====================

    private static void qualityAttributes() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. 质量属性\n");

        System.out.println("架构设计核心关注的质量属性：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 质量属性        │ 关注点                                    │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 性能            │ 响应时间、吞吐量、资源利用率              │");
        System.out.println("│ Performance     │                                           │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 可用性          │ 故障恢复时间、服务级别、冗余度            │");
        System.out.println("│ Availability    │                                           │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 可修改性        │ 变更成本、变更范围、可维护性              │");
        System.out.println("│ Modifiability   │                                           │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 安全性          │ 认证、授权、数据保护、审计                │");
        System.out.println("│ Security        │                                           │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 可测试性        │ 测试覆盖率、自动化程度、可观测性          │");
        System.out.println("│ Testability     │                                           │");
        System.out.println("├─────────────────┼───────────────────────────────────────────┤");
        System.out.println("│ 可扩展性        │ 水平扩展、垂直扩展、弹性伸缩              │");
        System.out.println("│ Scalability     │                                           │");
        System.out.println("└─────────────────┴───────────────────────────────────────────┘");
    }

    // ==================== 3. 场景分析 ====================

    private static void scenarioAnalysis() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 质量属性场景\n");

        System.out.println("场景六要素：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     场景六要素                               │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   刺激源 ──→ 刺激 ──→ 制品 ──→ 响应 ──→ 响应度量          │");
        System.out.println("│     │                   ↑                                   │");
        System.out.println("│     │              ┌────┴────┐                              │");
        System.out.println("│     └─────────────→│  环境   │                              │");
        System.out.println("│                    └─────────┘                              │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("场景示例 - 可用性：");
        System.out.println("┌────────────┬──────────────────────────────────────────────┐");
        System.out.println("│ 要素       │ 描述                                          │");
        System.out.println("├────────────┼──────────────────────────────────────────────┤");
        System.out.println("│ 刺激源     │ 订单服务                                      │");
        System.out.println("│ 刺激       │ 发生宕机故障                                  │");
        System.out.println("│ 环境       │ 正常运行时间                                  │");
        System.out.println("│ 制品       │ 系统                                          │");
        System.out.println("│ 响应       │ 自动故障转移，不影响用户                      │");
        System.out.println("│ 响应度量   │ 故障恢复时间 < 30 秒，用户无感知              │");
        System.out.println("└────────────┴──────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("场景示例 - 性能：");
        System.out.println("┌────────────┬──────────────────────────────────────────────┐");
        System.out.println("│ 要素       │ 描述                                          │");
        System.out.println("├────────────┼──────────────────────────────────────────────┤");
        System.out.println("│ 刺激源     │ 用户                                          │");
        System.out.println("│ 刺激       │ 发起秒杀请求                                  │");
        System.out.println("│ 环境       │ 高峰期（10 万并发）                           │");
        System.out.println("│ 制品       │ 秒杀服务                                      │");
        System.out.println("│ 响应       │ 处理请求并返回结果                            │");
        System.out.println("│ 响应度量   │ P99 响应时间 < 200ms                          │");
        System.out.println("└────────────┴──────────────────────────────────────────────┘");
    }

    // ==================== 4. 评估流程 ====================

    private static void evaluationProcess() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. ATAM 评估流程\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    ATAM 九步评估流程                         │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 第一阶段：准备 (与评估团队)                                  │");
        System.out.println("│ ─────────────────────────────                                │");
        System.out.println("│ 1. 介绍 ATAM 方法                                            │");
        System.out.println("│ 2. 介绍业务驱动因素                                          │");
        System.out.println("│ 3. 介绍架构                                                  │");
        System.out.println("│                                                              │");
        System.out.println("│ 第二阶段：评估 (与项目团队)                                  │");
        System.out.println("│ ─────────────────────────────                                │");
        System.out.println("│ 4. 识别架构方法/风格                                         │");
        System.out.println("│ 5. 生成质量属性效用树                                        │");
        System.out.println("│ 6. 分析架构方法                                              │");
        System.out.println("│                                                              │");
        System.out.println("│ 第三阶段：测试 (与利益相关者)                                │");
        System.out.println("│ ─────────────────────────────────                            │");
        System.out.println("│ 7. 头脑风暴场景                                              │");
        System.out.println("│ 8. 分析架构方法 (续)                                         │");
        System.out.println("│ 9. 呈现结果                                                  │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("效用树 (Utility Tree) 示例：");
        System.out.println("```");
        System.out.println("系统质量");
        System.out.println("├── 性能");
        System.out.println("│   ├── [H,H] 秒杀请求 P99 < 200ms");
        System.out.println("│   └── [M,M] 普通查询 P99 < 500ms");
        System.out.println("├── 可用性");
        System.out.println("│   ├── [H,H] 核心服务 99.99%");
        System.out.println("│   └── [M,L] 非核心服务 99.9%");
        System.out.println("└── 安全性");
        System.out.println("    └── [H,M] 防止刷单攻击");
        System.out.println();
        System.out.println("标注: [重要性, 难度]  H=高 M=中 L=低");
        System.out.println("```");
    }

    // ==================== 5. 权衡分析 ====================

    private static void tradeoffAnalysis() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 权衡分析\n");

        System.out.println("常见的架构权衡：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│              性能 vs 可维护性                                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 场景: 是否引入缓存层？                                       │");
        System.out.println("│                                                              │");
        System.out.println("│ 引入缓存：                                                   │");
        System.out.println("│   ✓ 性能提升 10x                                            │");
        System.out.println("│   ✗ 增加缓存一致性复杂度                                    │");
        System.out.println("│   ✗ 增加运维成本                                            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│              一致性 vs 可用性 (CAP)                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 场景: 分布式系统数据同步                                     │");
        System.out.println("│                                                              │");
        System.out.println("│ 强一致性 (CP)：                                              │");
        System.out.println("│   ✓ 数据准确                                                │");
        System.out.println("│   ✗ 网络分区时不可用                                        │");
        System.out.println("│                                                              │");
        System.out.println("│ 高可用性 (AP)：                                              │");
        System.out.println("│   ✓ 始终可访问                                              │");
        System.out.println("│   ✗ 可能读到旧数据                                          │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("权衡决策记录格式：");
        System.out.println("```");
        System.out.println("## 权衡点: 订单服务同步/异步");
        System.out.println();
        System.out.println("### 选项 A: 同步处理");
        System.out.println("- 优点: 实现简单,数据一致");
        System.out.println("- 缺点: 响应时间长,吞吐量受限");
        System.out.println();
        System.out.println("### 选项 B: 异步处理");
        System.out.println("- 优点: 响应快,高吞吐");
        System.out.println("- 缺点: 实现复杂,最终一致");
        System.out.println();
        System.out.println("### 决策: 选择 B");
        System.out.println("### 理由: 秒杀场景吞吐量优先");
        System.out.println("```");
    }
}
