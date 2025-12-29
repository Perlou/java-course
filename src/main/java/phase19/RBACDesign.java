package phase19;

/**
 * Phase 19: RBAC 权限设计
 * 
 * 本课程涵盖：
 * 1. RBAC 模型基础
 * 2. RBAC 扩展模型
 * 3. ABAC 属性权限
 * 4. 权限设计最佳实践
 */
public class RBACDesign {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          👥 Phase 19: RBAC 权限设计                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        rbacBasics();
        rbacModels();
        abacModel();
        implementation();
        bestPractices();
    }

    // ==================== 1. RBAC 基础 ====================

    private static void rbacBasics() {
        System.out.println("\n📌 1. RBAC 基础概念\n");

        System.out.println("RBAC (Role-Based Access Control) 基于角色的访问控制：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      RBAC 核心概念                           │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   ┌──────┐       ┌──────┐       ┌──────────┐               │");
        System.out.println("│   │ 用户 │ ───→  │ 角色 │ ───→  │   权限   │               │");
        System.out.println("│   │ User │  分配 │ Role │  授予 │Permission│               │");
        System.out.println("│   └──────┘       └──────┘       └──────────┘               │");
        System.out.println("│                                       │                     │");
        System.out.println("│                                       ↓                     │");
        System.out.println("│                                 ┌──────────┐               │");
        System.out.println("│                                 │   资源   │               │");
        System.out.println("│                                 │ Resource │               │");
        System.out.println("│                                 └──────────┘               │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("核心元素：");
        System.out.println("  • 用户 (User)       - 系统使用者");
        System.out.println("  • 角色 (Role)       - 权限的集合，如 admin、editor、viewer");
        System.out.println("  • 权限 (Permission) - 对资源的操作许可，如 user:read");
        System.out.println("  • 资源 (Resource)   - 受保护的对象，如用户数据、订单");
        System.out.println();

        System.out.println("示例：");
        System.out.println("```");
        System.out.println("用户 张三 ──→ 角色 [编辑员]");
        System.out.println("              │");
        System.out.println("              └──→ 权限 [article:read, article:write]");
        System.out.println("                           │          │");
        System.out.println("                           └──→ 可以读取文章、编辑文章");
        System.out.println("```");
    }

    // ==================== 2. RBAC 模型层级 ====================

    private static void rbacModels() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. RBAC 模型层级\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ RBAC0 - 基础模型                                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 用户 ←→ 角色 ←→ 权限                                        │");
        System.out.println("│                                                              │");
        System.out.println("│ 特点: 最简单的 RBAC，用户通过角色获得权限                    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ RBAC1 - 角色继承                                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│           ┌───────────┐                                     │");
        System.out.println("│           │   管理员   │                                     │");
        System.out.println("│           └─────┬─────┘                                     │");
        System.out.println("│                 │ 继承                                       │");
        System.out.println("│       ┌─────────┼─────────┐                                 │");
        System.out.println("│       ↓         ↓         ↓                                 │");
        System.out.println("│   ┌───────┐ ┌───────┐ ┌───────┐                            │");
        System.out.println("│   │ 编辑员 │ │ 审核员 │ │ 财务  │                            │");
        System.out.println("│   └───────┘ └───────┘ └───────┘                            │");
        System.out.println("│                                                              │");
        System.out.println("│ 特点: 角色可以继承其他角色的权限（角色层级）                 │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ RBAC2 - 约束模型                                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 互斥角色 (Separation of Duties)                           │");
        System.out.println("│    用户不能同时拥有「出纳」和「会计」角色                    │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 角色数量限制                                              │");
        System.out.println("│    一个用户最多拥有 N 个角色                                 │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 先决条件                                                  │");
        System.out.println("│    必须先有「员工」角色才能被分配「经理」角色                │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ RBAC3 - 统一模型 (RBAC1 + RBAC2)                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 同时支持角色继承和约束                                       │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 3. ABAC 模型 ====================

    private static void abacModel() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. ABAC 属性权限控制\n");

        System.out.println("ABAC (Attribute-Based Access Control) 基于属性的访问控制：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      ABAC 模型                               │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   ┌────────────────┐                                        │");
        System.out.println("│   │   Policy Engine │ ←── 策略规则                          │");
        System.out.println("│   └────────────────┘                                        │");
        System.out.println("│          ↑                                                  │");
        System.out.println("│          │ 输入属性                                         │");
        System.out.println("│   ┌──────┴──────┬────────────┬────────────┐                │");
        System.out.println("│   ↓             ↓            ↓            ↓                │");
        System.out.println("│ ┌──────┐  ┌──────────┐ ┌──────────┐ ┌──────────┐          │");
        System.out.println("│ │ 主体 │  │   资源   │ │   操作   │ │   环境   │          │");
        System.out.println("│ │属性  │  │   属性   │ │   属性   │ │   属性   │          │");
        System.out.println("│ └──────┘  └──────────┘ └──────────┘ └──────────┘          │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("属性类型：");
        System.out.println("  • 主体属性 - 用户部门、职级、地理位置");
        System.out.println("  • 资源属性 - 数据分类、敏感级别");
        System.out.println("  • 操作属性 - 读、写、删除");
        System.out.println("  • 环境属性 - 时间、IP 地址、设备类型");
        System.out.println();

        System.out.println("策略示例：");
        System.out.println("```");
        System.out.println("IF   subject.department == \"财务\"");
        System.out.println("AND  resource.type == \"财务报表\"");
        System.out.println("AND  action == \"read\"");
        System.out.println("AND  environment.time BETWEEN 9:00 AND 18:00");
        System.out.println("AND  environment.ip IN [\"10.0.0.0/8\"]");
        System.out.println("THEN permit");
        System.out.println("```");
        System.out.println();

        System.out.println("RBAC vs ABAC：");
        System.out.println("┌────────────┬─────────────────────────┬─────────────────────┐");
        System.out.println("│            │         RBAC            │        ABAC         │");
        System.out.println("├────────────┼─────────────────────────┼─────────────────────┤");
        System.out.println("│ 复杂度     │ 简单                    │ 复杂                │");
        System.out.println("│ 灵活性     │ 有限                    │ 高度灵活            │");
        System.out.println("│ 适用场景   │ 静态权限                │ 动态、细粒度权限    │");
        System.out.println("│ 维护成本   │ 低                      │ 高                  │");
        System.out.println("└────────────┴─────────────────────────┴─────────────────────┘");
    }

    // ==================== 4. 代码实现 ====================

    private static void implementation() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. 权限设计实现\n");

        System.out.println("数据库设计：");
        System.out.println("```sql");
        System.out.println("-- 用户表");
        System.out.println("CREATE TABLE users (");
        System.out.println("    id BIGINT PRIMARY KEY,");
        System.out.println("    username VARCHAR(50),");
        System.out.println("    password VARCHAR(255)");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 角色表");
        System.out.println("CREATE TABLE roles (");
        System.out.println("    id BIGINT PRIMARY KEY,");
        System.out.println("    name VARCHAR(50),");
        System.out.println("    parent_id BIGINT  -- 角色继承");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 权限表");
        System.out.println("CREATE TABLE permissions (");
        System.out.println("    id BIGINT PRIMARY KEY,");
        System.out.println("    name VARCHAR(100),  -- 如 'user:read'");
        System.out.println("    resource VARCHAR(50),");
        System.out.println("    action VARCHAR(50)");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 用户-角色关联");
        System.out.println("CREATE TABLE user_roles (");
        System.out.println("    user_id BIGINT,");
        System.out.println("    role_id BIGINT");
        System.out.println(");");
        System.out.println();
        System.out.println("-- 角色-权限关联");
        System.out.println("CREATE TABLE role_permissions (");
        System.out.println("    role_id BIGINT,");
        System.out.println("    permission_id BIGINT");
        System.out.println(");");
        System.out.println("```");
        System.out.println();

        System.out.println("Spring Security 配置：");
        System.out.println("```java");
        System.out.println("@PreAuthorize(\"hasRole('ADMIN')\")");
        System.out.println("public void adminOnlyMethod() { }");
        System.out.println();
        System.out.println("@PreAuthorize(\"hasPermission(#id, 'user', 'read')\")");
        System.out.println("public User getUser(Long id) { }");
        System.out.println();
        System.out.println("// 自定义权限评估器");
        System.out.println("@Component");
        System.out.println("public class CustomPermissionEvaluator implements PermissionEvaluator {");
        System.out.println("    @Override");
        System.out.println("    public boolean hasPermission(Authentication auth,");
        System.out.println("            Object targetId, Object permission) {");
        System.out.println("        // 权限检查逻辑");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    // ==================== 5. 最佳实践 ====================

    private static void bestPractices() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 权限设计最佳实践\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 设计原则                                                  │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 最小权限原则                                              │");
        System.out.println("│    只授予完成工作所需的最小权限                              │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 职责分离                                                  │");
        System.out.println("│    关键操作需要多人配合（如：申请-审批）                     │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 默认拒绝                                                  │");
        System.out.println("│    未明确授权的操作默认禁止                                  │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 权限可审计                                                │");
        System.out.println("│    记录所有权限变更和敏感操作                                │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("权限命名规范：");
        System.out.println("  格式: {resource}:{action}");
        System.out.println("  示例:");
        System.out.println("    • user:read    - 读取用户");
        System.out.println("    • user:write   - 创建/修改用户");
        System.out.println("    • user:delete  - 删除用户");
        System.out.println("    • order:*      - 订单所有权限");
        System.out.println();

        System.out.println("💡 推荐架构：");
        System.out.println("  • 小型系统: RBAC0 足够");
        System.out.println("  • 中型系统: RBAC1（角色继承）");
        System.out.println("  • 大型/合规系统: RBAC2/3 + ABAC 混合");
    }
}
