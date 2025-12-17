package phase09;

/**
 * Phase 9 - Lesson 6: Spring Security 与 JWT
 * 
 * 🎯 学习目标:
 * 1. 理解认证与授权
 * 2. 掌握 Spring Security 基础
 * 3. 了解 JWT 认证
 */
public class SecurityDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 6: Spring Security 与 JWT");
        System.out.println("=".repeat(60));

        // 1. 认证与授权
        System.out.println("\n【1. 认证与授权】");
        System.out.println("""
                认证 (Authentication): 你是谁?
                - 用户名密码
                - Token
                - OAuth2

                授权 (Authorization): 你能做什么?
                - 角色权限
                - 资源访问控制

                ┌─────────────────────────────────────────────────────────┐
                │                  请求流程                               │
                │                                                         │
                │  请求 → [认证过滤器] → [授权过滤器] → Controller        │
                │            ↓                ↓                           │
                │        验证身份          检查权限                       │
                │            ↓                ↓                           │
                │        401 未认证       403 无权限                      │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. Spring Security 基础
        System.out.println("=".repeat(60));
        System.out.println("【2. Spring Security 基础】");
        System.out.println("""
                添加依赖:
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-security</artifactId>
                </dependency>

                默认行为:
                - 所有请求需要认证
                - 生成随机密码 (启动日志)
                - 默认用户: user
                - 表单登录页面

                安全配置:

                @Configuration
                @EnableWebSecurity
                public class SecurityConfig {

                    @Bean
                    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                        http
                            .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/public/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                            )
                            .formLogin(form -> form
                                .loginPage("/login")
                                .permitAll()
                            )
                            .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                            );

                        return http.build();
                    }

                    @Bean
                    public PasswordEncoder passwordEncoder() {
                        return new BCryptPasswordEncoder();
                    }
                }
                """);

        // 3. 用户认证
        System.out.println("=".repeat(60));
        System.out.println("【3. 用户认证】");
        System.out.println("""
                方式 1: 内存用户

                @Bean
                public UserDetailsService userDetailsService() {
                    UserDetails user = User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("password"))
                        .roles("USER")
                        .build();

                    UserDetails admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .roles("ADMIN")
                        .build();

                    return new InMemoryUserDetailsManager(user, admin);
                }

                方式 2: 数据库用户 (推荐)

                @Service
                public class CustomUserDetailsService implements UserDetailsService {

                    @Autowired
                    private UserRepository userRepository;

                    @Override
                    public UserDetails loadUserByUsername(String username) {
                        User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                        return org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRoles().toArray(new String[0]))
                            .build();
                    }
                }
                """);

        // 4. 方法级安全
        System.out.println("=".repeat(60));
        System.out.println("【4. 方法级安全】");
        System.out.println("""
                启用注解:
                @EnableMethodSecurity

                使用:

                @Service
                public class UserService {

                    @PreAuthorize("hasRole('ADMIN')")
                    public void deleteUser(Long id) { }

                    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
                    public User getUser(Long userId) { }

                    @PostAuthorize("returnObject.owner == authentication.name")
                    public Document getDocument(Long id) { }

                    @PreFilter("filterObject.status == 'ACTIVE'")
                    public void processList(List<Item> items) { }
                }

                常用注解:
                - @PreAuthorize: 方法执行前检查
                - @PostAuthorize: 方法执行后检查
                - @Secured: 简单角色检查
                """);

        // 5. JWT 认证
        System.out.println("=".repeat(60));
        System.out.println("【5. JWT 认证】");
        System.out.println("""
                JWT = JSON Web Token

                结构:
                ┌─────────────────────────────────────────────────────────┐
                │  Header.Payload.Signature                              │
                │                                                         │
                │  Header (头部):                                        │
                │  {                                                      │
                │    "alg": "HS256",                                     │
                │    "typ": "JWT"                                        │
                │  }                                                      │
                │                                                         │
                │  Payload (载荷):                                       │
                │  {                                                      │
                │    "sub": "1234567890",                                │
                │    "name": "John",                                     │
                │    "iat": 1516239022,                                  │
                │    "exp": 1516325422                                   │
                │  }                                                      │
                │                                                         │
                │  Signature (签名):                                     │
                │  HMACSHA256(base64(header) + "." + base64(payload),    │
                │             secret)                                    │
                └─────────────────────────────────────────────────────────┘

                认证流程:

                1. 用户登录
                   POST /login {username, password}
                          ↓
                2. 服务端验证，返回 JWT
                   {token: "eyJhbGciOi..."}
                          ↓
                3. 客户端保存 Token
                   localStorage / Cookie
                          ↓
                4. 后续请求携带 Token
                   Authorization: Bearer eyJhbGciOi...
                          ↓
                5. 服务端验证 Token
                   解析 → 验证签名 → 检查过期 → 获取用户信息
                """);

        // 6. JWT 实现示例
        System.out.println("=".repeat(60));
        System.out.println("【6. JWT 实现】");
        System.out.println("""
                JwtUtil:

                @Component
                public class JwtUtil {
                    @Value("${jwt.secret}")
                    private String secret;

                    @Value("${jwt.expiration}")
                    private long expiration;

                    public String generateToken(UserDetails user) {
                        return Jwts.builder()
                            .setSubject(user.getUsername())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + expiration))
                            .signWith(SignatureAlgorithm.HS256, secret)
                            .compact();
                    }

                    public String extractUsername(String token) {
                        return extractClaim(token, Claims::getSubject);
                    }

                    public boolean validateToken(String token, UserDetails user) {
                        String username = extractUsername(token);
                        return username.equals(user.getUsername()) && !isExpired(token);
                    }
                }

                JwtAuthenticationFilter:

                public class JwtAuthenticationFilter extends OncePerRequestFilter {

                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                            HttpServletResponse response, FilterChain chain) {

                        String authHeader = request.getHeader("Authorization");

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            String username = jwtUtil.extractUsername(token);

                            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                UserDetails user = userDetailsService.loadUserByUsername(username);

                                if (jwtUtil.validateToken(token, user)) {
                                    UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                }
                            }
                        }

                        chain.doFilter(request, response);
                    }
                }
                """);

        // 7. OAuth2 简介
        System.out.println("=".repeat(60));
        System.out.println("【7. OAuth2 简介】");
        System.out.println("""
                OAuth2 角色:
                - Resource Owner: 用户
                - Client: 第三方应用
                - Authorization Server: 认证服务器
                - Resource Server: 资源服务器

                常见授权模式:
                - Authorization Code: 授权码 (推荐)
                - Implicit: 隐式
                - Password: 密码
                - Client Credentials: 客户端凭证

                Spring Security OAuth2:

                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-oauth2-client</artifactId>
                </dependency>

                配置:
                spring:
                  security:
                    oauth2:
                      client:
                        registration:
                          github:
                            client-id: xxx
                            client-secret: xxx
                          google:
                            client-id: xxx
                            client-secret: xxx
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 认证 (谁) vs 授权 (能做什么)");
        System.out.println("💡 JWT 适合无状态 API 认证");
        System.out.println("💡 OAuth2 适合第三方登录");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Spring Security:
 * - 认证与授权
 * - SecurityFilterChain 配置
 * - UserDetailsService
 * 
 * 2. JWT:
 * - Header.Payload.Signature
 * - 无状态认证
 * - 过滤器实现
 * 
 * 3. OAuth2:
 * - 授权码模式
 * - 第三方登录
 */
