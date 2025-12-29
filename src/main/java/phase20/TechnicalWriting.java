package phase20;

/**
 * Phase 20: 技术文档写作
 * 
 * 本课程涵盖：
 * 1. 架构决策记录 (ADR)
 * 2. RFC 提案
 * 3. 设计文档
 * 4. 技术规范
 */
public class TechnicalWriting {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          📝 Phase 20: 技术文档写作                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        adrFormat();
        rfcProcess();
        designDocument();
        writingTips();
    }

    // ==================== 1. 架构决策记录 (ADR) ====================

    private static void adrFormat() {
        System.out.println("\n📌 1. 架构决策记录 (ADR)\n");

        System.out.println("ADR (Architecture Decision Record) 记录重要的架构决策：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     ADR 模板                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ # ADR-001: 使用 Redis 作为会话存储                          │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 状态                                                      │");
        System.out.println("│ 已采纳 (Accepted) | 2024-01-15                               │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 背景                                                      │");
        System.out.println("│ 当前系统使用内存存储会话,多实例部署无法共享会话状态。        │");
        System.out.println("│ 用户切换实例需要重新登录,体验差。                            │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 决策                                                      │");
        System.out.println("│ 使用 Redis 集群作为分布式会话存储。                          │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 考虑的选项                                                │");
        System.out.println("│ 1. 粘性会话 (Sticky Session) - 简单但不够可靠               │");
        System.out.println("│ 2. Redis 存储 - 成熟方案,运维成本可控                        │");
        System.out.println("│ 3. JWT 无状态 - 改造成本高,Token 撤销复杂                    │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 后果                                                      │");
        System.out.println("│ 正面: 会话共享,支持水平扩展                                  │");
        System.out.println("│ 负面: 增加 Redis 运维成本,需要处理 Redis 故障场景            │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 关联                                                      │");
        System.out.println("│ 被 ADR-002 (会话安全策略) 影响                               │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("ADR 状态流转：");
        System.out.println("  提议 (Proposed) → 已采纳 (Accepted) → 已废弃 (Deprecated)");
        System.out.println("                  ↘ 已拒绝 (Rejected)");
    }

    // ==================== 2. RFC 提案 ====================

    private static void rfcProcess() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. RFC 技术提案\n");

        System.out.println("RFC (Request For Comments) 用于较大的技术变更：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      RFC 模板                                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ # RFC: 订单系统拆分为微服务                                  │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 元信息                                                    │");
        System.out.println("│ - 作者: @zhangsan                                            │");
        System.out.println("│ - 创建日期: 2024-01-10                                       │");
        System.out.println("│ - 状态: 讨论中                                               │");
        System.out.println("│ - 评审人: @lisi, @wangwu                                     │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 摘要                                                      │");
        System.out.println("│ 一句话描述本提案要解决的问题和方案                           │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 动机                                                      │");
        System.out.println("│ 为什么需要这个变更？当前的痛点是什么？                       │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 设计方案                                                  │");
        System.out.println("│ 详细的技术方案,包括架构图、接口定义等                        │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 替代方案                                                  │");
        System.out.println("│ 考虑过但未采用的方案及原因                                   │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 风险与缓解                                                │");
        System.out.println("│ 可能的风险及应对措施                                         │");
        System.out.println("│                                                              │");
        System.out.println("│ ## 实施计划                                                  │");
        System.out.println("│ 分阶段的实施步骤和时间表                                     │");
        System.out.println("│                                                              │");
        System.out.println("│ ## FAQ                                                       │");
        System.out.println("│ 常见问题解答                                                 │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("RFC 流程：");
        System.out.println("  1. 作者撰写 RFC 初稿");
        System.out.println("  2. 开放评论期 (1-2 周)");
        System.out.println("  3. 根据反馈修订");
        System.out.println("  4. 技术委员会审批");
        System.out.println("  5. 实施与跟踪");
    }

    // ==================== 3. 设计文档 ====================

    private static void designDocument() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 设计文档结构\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    设计文档大纲                              │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 1. 概述                                                      │");
        System.out.println("│    - 项目背景                                                │");
        System.out.println("│    - 目标与范围                                              │");
        System.out.println("│    - 术语定义                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 需求分析                                                  │");
        System.out.println("│    - 功能需求                                                │");
        System.out.println("│    - 非功能需求 (性能/可用性/安全)                           │");
        System.out.println("│    - 约束条件                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 系统架构                                                  │");
        System.out.println("│    - 架构图 (C4 模型)                                        │");
        System.out.println("│    - 核心组件说明                                            │");
        System.out.println("│    - 技术选型                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 详细设计                                                  │");
        System.out.println("│    - 数据模型                                                │");
        System.out.println("│    - 接口设计                                                │");
        System.out.println("│    - 核心流程                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 5. 部署方案                                                  │");
        System.out.println("│    - 部署架构                                                │");
        System.out.println("│    - 资源估算                                                │");
        System.out.println("│    - 监控告警                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 6. 风险评估                                                  │");
        System.out.println("│    - 技术风险                                                │");
        System.out.println("│    - 业务风险                                                │");
        System.out.println("│    - 缓解措施                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 7. 里程碑计划                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 8. 附录                                                      │");
        System.out.println("│    - 参考资料                                                │");
        System.out.println("│    - 评审记录                                                │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("C4 模型层级：");
        System.out.println("  Level 1: Context (系统上下文)");
        System.out.println("  Level 2: Container (容器/服务)");
        System.out.println("  Level 3: Component (组件)");
        System.out.println("  Level 4: Code (代码)");
    }

    // ==================== 4. 写作技巧 ====================

    private static void writingTips() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. 技术写作技巧\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 好的技术文档特点                                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 1. 目标明确                                                  │");
        System.out.println("│    - 开篇说明文档目的                                        │");
        System.out.println("│    - 明确读者是谁                                            │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 结构清晰                                                  │");
        System.out.println("│    - 层次分明的标题                                          │");
        System.out.println("│    - 适当使用图表                                            │");
        System.out.println("│    - 重点突出                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 内容准确                                                  │");
        System.out.println("│    - 技术细节正确                                            │");
        System.out.println("│    - 版本信息明确                                            │");
        System.out.println("│    - 及时更新                                                │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 简洁易读                                                  │");
        System.out.println("│    - 避免过长段落                                            │");
        System.out.println("│    - 使用简单句式                                            │");
        System.out.println("│    - 术语有解释                                              │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("常见问题：");
        System.out.println("  ❌ 文档过长无人读 → 精简,突出重点");
        System.out.println("  ❌ 过时不更新 → 与代码同步维护");
        System.out.println("  ❌ 缺少上下文 → 说明背景和动机");
        System.out.println("  ❌ 术语不一致 → 建立术语表");
        System.out.println();

        System.out.println("💡 架构师格言：");
        System.out.println("   \"如果团队成员看不懂你的设计文档，");
        System.out.println("    问题可能在你而不在他们\"");
    }
}
