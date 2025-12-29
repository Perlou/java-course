package phase20;

/**
 * Phase 20: 技术债务管理
 * 
 * 本课程涵盖：
 * 1. 技术债务概念
 * 2. 识别与分类
 * 3. 量化评估
 * 4. 偿还策略
 */
public class TechDebtManagement {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          💳 Phase 20: 技术债务管理                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        techDebtConcept();
        debtIdentification();
        debtQuantification();
        repaymentStrategy();
        preventionMeasures();
    }

    // ==================== 1. 技术债务概念 ====================

    private static void techDebtConcept() {
        System.out.println("\n📌 1. 技术债务概念\n");

        System.out.println("技术债务 (Technical Debt) 类比：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                                                              │");
        System.out.println("│   金融债务                    技术债务                       │");
        System.out.println("│   ─────────                   ─────────                      │");
        System.out.println("│   本金      ←─────→          快速实现的代码                 │");
        System.out.println("│   利息      ←─────→          维护成本增加                   │");
        System.out.println("│   还款      ←─────→          重构/优化                      │");
        System.out.println("│   破产      ←─────→          系统无法维护                   │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("技术债务的来源：");
        System.out.println();
        System.out.println("  🔹 主动债务（有意识的权衡）");
        System.out.println("     • 为赶工期选择快速方案");
        System.out.println("     • 先实现功能,后续再优化");
        System.out.println("     • \"我们知道这样不好,但先这样做\"");
        System.out.println();
        System.out.println("  🔹 被动债务（无意识产生）");
        System.out.println("     • 技术能力不足");
        System.out.println("     • 需求变更导致设计过时");
        System.out.println("     • 依赖项老化");
        System.out.println();

        System.out.println("Martin Fowler 的技术债务象限：");
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│          │     鲁莽 (Reckless)    │    谨慎 (Prudent)       │");
        System.out.println("├──────────┼────────────────────────┼─────────────────────────┤");
        System.out.println("│ 故意     │ \"我们没时间做设计\"     │ \"先发布,后重构\"        │");
        System.out.println("│ (Deliberate)                       │ (有计划地承担)          │");
        System.out.println("├──────────┼────────────────────────┼─────────────────────────┤");
        System.out.println("│ 无意     │ \"什么是分层架构?\"      │ \"现在知道应该怎么做了\" │");
        System.out.println("│ (Inadvertent)                      │ (事后发现的更好方案)    │");
        System.out.println("└──────────┴────────────────────────┴─────────────────────────┘");
    }

    // ==================== 2. 债务识别与分类 ====================

    private static void debtIdentification() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. 技术债务识别与分类\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 技术债务分类                                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 1. 代码债务                                                  │");
        System.out.println("│    • 重复代码、过长函数                                      │");
        System.out.println("│    • 命名不规范、缺少注释                                    │");
        System.out.println("│    • 复杂度过高                                              │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 架构债务                                                  │");
        System.out.println("│    • 模块耦合过紧                                            │");
        System.out.println("│    • 缺少抽象层                                              │");
        System.out.println("│    • 架构风格不一致                                          │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 测试债务                                                  │");
        System.out.println("│    • 测试覆盖率不足                                          │");
        System.out.println("│    • 缺少自动化测试                                          │");
        System.out.println("│    • 测试用例过时                                            │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 文档债务                                                  │");
        System.out.println("│    • 文档缺失或过时                                          │");
        System.out.println("│    • API 文档不完整                                          │");
        System.out.println("│                                                              │");
        System.out.println("│ 5. 依赖债务                                                  │");
        System.out.println("│    • 依赖版本过旧                                            │");
        System.out.println("│    • 存在安全漏洞                                            │");
        System.out.println("│                                                              │");
        System.out.println("│ 6. 基础设施债务                                              │");
        System.out.println("│    • 部署流程手动                                            │");
        System.out.println("│    • 监控告警缺失                                            │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("识别方法：");
        System.out.println("  • 代码静态分析 (SonarQube)");
        System.out.println("  • 依赖漏洞扫描 (Dependabot)");
        System.out.println("  • 代码评审反馈");
        System.out.println("  • 开发团队访谈");
        System.out.println("  • 生产问题回顾");
    }

    // ==================== 3. 债务量化 ====================

    private static void debtQuantification() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 技术债务量化\n");

        System.out.println("量化维度：");
        System.out.println();
        System.out.println("┌────────────────┬──────────────────────────────────────────┐");
        System.out.println("│ 维度           │ 量化方式                                  │");
        System.out.println("├────────────────┼──────────────────────────────────────────┤");
        System.out.println("│ 修复成本       │ 预估修复所需人天/人周                    │");
        System.out.println("│ 利息成本       │ 因债务导致的额外维护时间                 │");
        System.out.println("│ 影响范围       │ 涉及的模块/功能数量                      │");
        System.out.println("│ 风险等级       │ 高/中/低 (故障概率 × 影响程度)           │");
        System.out.println("└────────────────┴──────────────────────────────────────────┘");
        System.out.println();

        System.out.println("技术债务登记表示例：");
        System.out.println();
        System.out.println("┌────┬─────────────────┬────────┬────────┬────────┬────────┐");
        System.out.println("│ ID │ 描述            │ 类型   │ 修复成本│ 利息/月│ 优先级 │");
        System.out.println("├────┼─────────────────┼────────┼────────┼────────┼────────┤");
        System.out.println("│ D01│ 订单服务单体化  │ 架构   │ 20人天 │ 5人天  │ P1     │");
        System.out.println("│ D02│ 缺少单元测试    │ 测试   │ 10人天 │ 3人天  │ P2     │");
        System.out.println("│ D03│ MySQL 版本过旧  │ 依赖   │ 5人天  │ 1人天  │ P2     │");
        System.out.println("│ D04│ 硬编码配置      │ 代码   │ 3人天  │ 0.5人天│ P3     │");
        System.out.println("└────┴─────────────────┴────────┴────────┴────────┴────────┘");
        System.out.println();

        System.out.println("SonarQube 技术债务指标：");
        System.out.println("  • 技术债务总量 (人天)");
        System.out.println("  • 技术债务比率 (债务时间/开发时间)");
        System.out.println("  • 代码异味 (Code Smells)");
        System.out.println("  • 复杂度热点");
    }

    // ==================== 4. 偿还策略 ====================

    private static void repaymentStrategy() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. 技术债务偿还策略\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 偿还策略一览                                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 1. 持续小额还款                                              │");
        System.out.println("│    每个迭代分配 10-20% 时间处理技术债务                      │");
        System.out.println("│    优点: 风险小,持续改善                                     │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 集中大额还款                                              │");
        System.out.println("│    专门迭代进行重构                                          │");
        System.out.println("│    优点: 效率高,可解决架构级问题                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 搭便车策略                                                │");
        System.out.println("│    在开发新功能时顺便重构相关代码                            │");
        System.out.println("│    \"童子军规则\": 让营地比来时更干净                          │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 破产清算                                                  │");
        System.out.println("│    系统无法维护时,重写或替换                                 │");
        System.out.println("│    最后手段,成本最高                                         │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("优先级排序 (修复顺序)：");
        System.out.println();
        System.out.println("  高优先级 (立即处理):");
        System.out.println("    • 安全漏洞");
        System.out.println("    • 影响生产稳定性");
        System.out.println("    • 高利息债务 (维护成本高)");
        System.out.println();
        System.out.println("  中优先级 (计划处理):");
        System.out.println("    • 阻碍新功能开发");
        System.out.println("    • 影响开发效率");
        System.out.println();
        System.out.println("  低优先级 (机会处理):");
        System.out.println("    • 代码风格问题");
        System.out.println("    • 可接受的复杂度");
    }

    // ==================== 5. 预防措施 ====================

    private static void preventionMeasures() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 技术债务预防\n");

        System.out.println("预防优于治疗：");
        System.out.println();
        System.out.println("  1️⃣  代码规范");
        System.out.println("      • 制定编码规范");
        System.out.println("      • 自动化检查 (Checkstyle, PMD)");
        System.out.println("      • 强制代码评审");
        System.out.println();
        System.out.println("  2️⃣  技术债务预算");
        System.out.println("      • 每迭代预留时间");
        System.out.println("      • 新债务需要\"贷款审批\"");
        System.out.println("      • 定期债务审计");
        System.out.println();
        System.out.println("  3️⃣  自动化防线");
        System.out.println("      • CI 质量门禁");
        System.out.println("      • 依赖自动更新");
        System.out.println("      • 安全扫描");
        System.out.println();
        System.out.println("  4️⃣  架构守护");
        System.out.println("      • 架构决策记录 (ADR)");
        System.out.println("      • 定期架构评审");
        System.out.println("      • 架构适应度函数");
        System.out.println();

        System.out.println("💡 记住：技术债务不是坏事，关键是\"可控\"");
    }
}
