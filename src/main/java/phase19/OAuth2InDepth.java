package phase19;

/**
 * Phase 19: OAuth2 深入解析
 * 
 * 本课程涵盖：
 * 1. OAuth2 核心概念
 * 2. 四种授权模式
 * 3. PKCE 扩展
 * 4. Token 管理最佳实践
 */
public class OAuth2InDepth {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          🔐 Phase 19: OAuth2 深入解析                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        oauth2Concepts();
        authorizationCodeFlow();
        otherGrantTypes();
        pkceExtension();
        tokenManagement();
    }

    // ==================== 1. OAuth2 核心概念 ====================

    private static void oauth2Concepts() {
        System.out.println("\n📌 1. OAuth2 核心概念\n");

        System.out.println("OAuth2 是一个授权框架，允许第三方应用访问用户资源：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     OAuth2 角色                              │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   ┌──────────────┐              ┌──────────────┐           │");
        System.out.println("│   │ Resource     │              │ Authorization│           │");
        System.out.println("│   │ Owner (用户) │              │ Server       │           │");
        System.out.println("│   └──────────────┘              └──────────────┘           │");
        System.out.println("│         │                              │                    │");
        System.out.println("│         │ 授权同意                     │ 颁发令牌           │");
        System.out.println("│         ↓                              ↓                    │");
        System.out.println("│   ┌──────────────┐              ┌──────────────┐           │");
        System.out.println("│   │ Client       │  ──请求资源─→ │ Resource     │           │");
        System.out.println("│   │ (第三方应用) │              │ Server       │           │");
        System.out.println("│   └──────────────┘              └──────────────┘           │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("四个角色：");
        System.out.println("  • Resource Owner - 用户（资源拥有者）");
        System.out.println("  • Client         - 第三方应用（需要访问资源）");
        System.out.println("  • Authorization Server - 授权服务器（颁发令牌）");
        System.out.println("  • Resource Server - 资源服务器（存储受保护资源）");
        System.out.println();

        System.out.println("核心令牌：");
        System.out.println("  • Access Token  - 访问令牌（短期有效，用于 API 调用）");
        System.out.println("  • Refresh Token - 刷新令牌（长期有效，用于获取新 Access Token）");
    }

    // ==================== 2. 授权码模式 ====================

    private static void authorizationCodeFlow() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. 授权码模式 (Authorization Code)\n");

        System.out.println("最安全的授权模式，适用于有后端的 Web 应用：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                   授权码流程                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   用户    Client(浏览器)    Client(后端)    Auth Server     │");
        System.out.println("│    │           │                │               │           │");
        System.out.println("│    │──登录────→│                │               │           │");
        System.out.println("│    │           │──重定向到授权页面──────────────→│           │");
        System.out.println("│    │←─────────────────────────授权页面──────────│           │");
        System.out.println("│    │──────────────────同意授权──────────────────→│           │");
        System.out.println("│    │           │←────重定向 + code──────────────│           │");
        System.out.println("│    │           │───code───→│                    │           │");
        System.out.println("│    │           │            │──code+secret─────→│           │");
        System.out.println("│    │           │            │←─access_token────│           │");
        System.out.println("│    │           │←──登录成功─│                    │           │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("请求示例：");
        System.out.println();
        System.out.println("Step 1: 重定向到授权页面");
        System.out.println("```");
        System.out.println("GET /oauth/authorize");
        System.out.println("    ?response_type=code");
        System.out.println("    &client_id=my-app");
        System.out.println("    &redirect_uri=https://myapp.com/callback");
        System.out.println("    &scope=read write");
        System.out.println("    &state=abc123  (防 CSRF)");
        System.out.println("```");
        System.out.println();
        System.out.println("Step 2: 用授权码换取 Token");
        System.out.println("```");
        System.out.println("POST /oauth/token");
        System.out.println("Content-Type: application/x-www-form-urlencoded");
        System.out.println();
        System.out.println("grant_type=authorization_code");
        System.out.println("&code=AUTHORIZATION_CODE");
        System.out.println("&redirect_uri=https://myapp.com/callback");
        System.out.println("&client_id=my-app");
        System.out.println("&client_secret=my-secret");
        System.out.println("```");
    }

    // ==================== 3. 其他授权模式 ====================

    private static void otherGrantTypes() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 其他授权模式\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 客户端凭证模式 (Client Credentials)                         │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 场景: 服务间调用（无用户参与）                               │");
        System.out.println("│                                                              │");
        System.out.println("│ POST /oauth/token                                            │");
        System.out.println("│ grant_type=client_credentials                                │");
        System.out.println("│ &client_id=service-a                                         │");
        System.out.println("│ &client_secret=secret                                        │");
        System.out.println("│ &scope=internal                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 刷新令牌模式 (Refresh Token)                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 场景: Access Token 过期后，用 Refresh Token 获取新令牌       │");
        System.out.println("│                                                              │");
        System.out.println("│ POST /oauth/token                                            │");
        System.out.println("│ grant_type=refresh_token                                     │");
        System.out.println("│ &refresh_token=REFRESH_TOKEN                                 │");
        System.out.println("│ &client_id=my-app                                            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ⚠️  已废弃的模式                                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 密码模式 (Password Grant)                                 │");
        System.out.println("│    - 用户直接把密码给第三方应用 → 不安全                     │");
        System.out.println("│    - 仅用于高度信任的第一方应用                              │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 隐式模式 (Implicit Grant)                                 │");
        System.out.println("│    - Token 直接暴露在 URL 中 → 不安全                        │");
        System.out.println("│    - 请改用 PKCE 模式                                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 4. PKCE 扩展 ====================

    private static void pkceExtension() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. PKCE 扩展 (Proof Key for Code Exchange)\n");

        System.out.println("PKCE 解决公共客户端（如 SPA、移动 App）的安全问题：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     PKCE 工作原理                            │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│  1. 客户端生成随机 code_verifier                             │");
        System.out.println("│     code_verifier = \"dBjftJeZ4CVP...\" (43-128 字符)        │");
        System.out.println("│                                                              │");
        System.out.println("│  2. 计算 code_challenge                                      │");
        System.out.println("│     code_challenge = BASE64URL(SHA256(code_verifier))        │");
        System.out.println("│                                                              │");
        System.out.println("│  3. 授权请求带上 code_challenge                              │");
        System.out.println("│     /authorize?...&code_challenge=xxx&code_challenge_method=S256│");
        System.out.println("│                                                              │");
        System.out.println("│  4. 换取 Token 时带上 code_verifier                          │");
        System.out.println("│     授权服务器验证: SHA256(code_verifier) == code_challenge  │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("Java 实现示例：");
        System.out.println("```java");
        System.out.println("// 生成 code_verifier");
        System.out.println("SecureRandom random = new SecureRandom();");
        System.out.println("byte[] bytes = new byte[32];");
        System.out.println("random.nextBytes(bytes);");
        System.out.println("String codeVerifier = Base64.getUrlEncoder()");
        System.out.println("    .withoutPadding().encodeToString(bytes);");
        System.out.println();
        System.out.println("// 计算 code_challenge");
        System.out.println("MessageDigest md = MessageDigest.getInstance(\"SHA-256\");");
        System.out.println("byte[] digest = md.digest(codeVerifier.getBytes(UTF_8));");
        System.out.println("String codeChallenge = Base64.getUrlEncoder()");
        System.out.println("    .withoutPadding().encodeToString(digest);");
        System.out.println("```");
    }

    // ==================== 5. Token 管理 ====================

    private static void tokenManagement() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. Token 管理最佳实践\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ Token 类型对比                                               │");
        System.out.println("├────────────┬─────────────────┬─────────────────────────────┤");
        System.out.println("│ 类型       │ 有效期          │ 用途                         │");
        System.out.println("├────────────┼─────────────────┼─────────────────────────────┤");
        System.out.println("│ Access     │ 15分钟 - 1小时  │ API 调用                     │");
        System.out.println("│ Refresh    │ 7天 - 30天      │ 获取新 Access Token          │");
        System.out.println("│ ID Token   │ 1小时           │ 用户身份信息（OIDC）         │");
        System.out.println("└────────────┴─────────────────┴─────────────────────────────┘");
        System.out.println();

        System.out.println("安全存储策略：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 客户端类型        │ 存储方式                                │");
        System.out.println("├───────────────────┼─────────────────────────────────────────┤");
        System.out.println("│ 后端应用          │ 内存/Redis（加密存储）                  │");
        System.out.println("│ 浏览器 SPA        │ HttpOnly Cookie（避免 localStorage）    │");
        System.out.println("│ 移动 App          │ Secure Keychain / Keystore              │");
        System.out.println("└───────────────────┴─────────────────────────────────────────┘");
        System.out.println();

        System.out.println("💡 最佳实践：");
        System.out.println("  1. Access Token 短期有效（15分钟）");
        System.out.println("  2. Refresh Token 轮换（每次使用后更新）");
        System.out.println("  3. 使用 HTTPS 传输");
        System.out.println("  4. 实现 Token 撤销机制");
        System.out.println("  5. 避免在 URL 中传递 Token");
    }
}
