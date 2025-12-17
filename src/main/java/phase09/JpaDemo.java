package phase09;

/**
 * Phase 9 - Lesson 4: Spring Data JPA
 * 
 * 🎯 学习目标:
 * 1. 理解 JPA 和 Hibernate
 * 2. 掌握 Spring Data JPA 使用
 * 3. 学会编写 Repository
 */
public class JpaDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 4: Spring Data JPA");
        System.out.println("=".repeat(60));

        // 1. JPA 简介
        System.out.println("\n【1. JPA 简介】");
        System.out.println("""
                JPA = Java Persistence API

                ┌─────────────────────────────────────────────────────────┐
                │ JPA 是规范，Hibernate 是实现                            │
                │                                                         │
                │  应用代码                                               │
                │     ↓                                                   │
                │  Spring Data JPA (简化层)                              │
                │     ↓                                                   │
                │  JPA (规范接口)                                         │
                │     ↓                                                   │
                │  Hibernate (实现)                                       │
                │     ↓                                                   │
                │  JDBC                                                   │
                │     ↓                                                   │
                │  数据库                                                 │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 实体定义
        System.out.println("=".repeat(60));
        System.out.println("【2. 实体定义 @Entity】");
        System.out.println("""
                @Entity
                @Table(name = "users")
                public class User {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    @Column(nullable = false, length = 50)
                    private String username;

                    @Column(unique = true)
                    private String email;

                    @Enumerated(EnumType.STRING)
                    private UserStatus status;

                    @CreatedDate
                    private LocalDateTime createdAt;

                    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
                    private List<Order> orders;

                    // getters/setters
                }

                常用注解:
                ┌──────────────────────┬────────────────────────────────┐
                │      注解            │           说明                 │
                ├──────────────────────┼────────────────────────────────┤
                │ @Entity              │ 标识实体类                     │
                │ @Table               │ 指定表名                       │
                │ @Id                  │ 主键                           │
                │ @GeneratedValue      │ 主键生成策略                   │
                │ @Column              │ 列属性                         │
                │ @Enumerated          │ 枚举映射                       │
                │ @Temporal            │ 日期类型                       │
                │ @Transient           │ 不持久化                       │
                └──────────────────────┴────────────────────────────────┘
                """);

        // 3. Repository 接口
        System.out.println("=".repeat(60));
        System.out.println("【3. Repository 接口】");
        System.out.println("""
                Repository 继承体系:

                Repository (标记接口)
                    ↓
                CrudRepository (CRUD)
                    ↓
                PagingAndSortingRepository (分页排序)
                    ↓
                JpaRepository (JPA 特有方法)

                示例:

                public interface UserRepository extends JpaRepository<User, Long> {
                    // 继承的方法:
                    // save(entity)
                    // findById(id)
                    // findAll()
                    // deleteById(id)
                    // count()
                    // existsById(id)
                }
                """);

        // 4. 查询方法
        System.out.println("=".repeat(60));
        System.out.println("【4. 查询方法】");
        System.out.println("""
                方式 1: 方法名派生查询

                public interface UserRepository extends JpaRepository<User, Long> {

                    // SELECT * FROM users WHERE username = ?
                    User findByUsername(String username);

                    // SELECT * FROM users WHERE email = ? AND status = ?
                    List<User> findByEmailAndStatus(String email, UserStatus status);

                    // SELECT * FROM users WHERE age > ?
                    List<User> findByAgeGreaterThan(int age);

                    // SELECT * FROM users WHERE username LIKE ?
                    List<User> findByUsernameLike(String pattern);

                    // SELECT * FROM users ORDER BY createdAt DESC LIMIT 1
                    User findFirstByOrderByCreatedAtDesc();

                    // SELECT COUNT(*) FROM users WHERE status = ?
                    long countByStatus(UserStatus status);

                    // DELETE FROM users WHERE status = ?
                    void deleteByStatus(UserStatus status);
                }

                关键字:
                ┌──────────────────┬────────────────────────────────┐
                │     关键字        │          SQL                   │
                ├──────────────────┼────────────────────────────────┤
                │ And              │ AND                            │
                │ Or               │ OR                             │
                │ Between          │ BETWEEN ? AND ?                │
                │ LessThan         │ < ?                            │
                │ GreaterThan      │ > ?                            │
                │ Like             │ LIKE ?                         │
                │ In               │ IN (?)                         │
                │ OrderBy          │ ORDER BY                       │
                │ Not              │ NOT                            │
                │ IsNull           │ IS NULL                        │
                └──────────────────┴────────────────────────────────┘
                """);

        // 5. JPQL 和原生 SQL
        System.out.println("=".repeat(60));
        System.out.println("【5. @Query 注解】");
        System.out.println("""
                JPQL 查询:

                @Query("SELECT u FROM User u WHERE u.status = :status")
                List<User> findActiveUsers(@Param("status") UserStatus status);

                @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
                List<User> findByEmailDomain(@Param("domain") String domain);

                原生 SQL:

                @Query(value = "SELECT * FROM users WHERE created_at > ?1",
                       nativeQuery = true)
                List<User> findRecentUsers(LocalDateTime date);

                更新/删除:

                @Modifying
                @Transactional
                @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
                int updateStatus(@Param("id") Long id, @Param("status") UserStatus status);
                """);

        // 6. 关联关系
        System.out.println("=".repeat(60));
        System.out.println("【6. 关联关系】");
        System.out.println("""
                一对多 (OneToMany):

                @Entity
                public class User {
                    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
                    private List<Order> orders;
                }

                @Entity
                public class Order {
                    @ManyToOne(fetch = FetchType.LAZY)
                    @JoinColumn(name = "user_id")
                    private User user;
                }

                多对多 (ManyToMany):

                @Entity
                public class User {
                    @ManyToMany
                    @JoinTable(
                        name = "user_roles",
                        joinColumns = @JoinColumn(name = "user_id"),
                        inverseJoinColumns = @JoinColumn(name = "role_id")
                    )
                    private Set<Role> roles;
                }

                抓取策略:
                - EAGER: 立即加载 (小心 N+1 问题)
                - LAZY: 延迟加载 (推荐)
                """);

        // 7. 分页和排序
        System.out.println("=".repeat(60));
        System.out.println("【7. 分页和排序】");
        System.out.println("""
                Repository 方法:

                Page<User> findByStatus(UserStatus status, Pageable pageable);

                使用:

                Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
                Page<User> page = userRepository.findByStatus(UserStatus.ACTIVE, pageable);

                page.getContent();      // 当前页数据
                page.getTotalPages();   // 总页数
                page.getTotalElements();// 总记录数
                page.getNumber();       // 当前页码 (0开始)
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Spring Data JPA 大大简化了数据访问层代码");
        System.out.println("💡 方法名派生查询很方便，复杂查询用 @Query");
        System.out.println("💡 注意 N+1 问题，合理使用 fetch 策略");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 实体定义:
 * - @Entity, @Table, @Id
 * - @Column, @GeneratedValue
 * 
 * 2. Repository:
 * - JpaRepository<Entity, ID>
 * - 方法名派生查询
 * 
 * 3. @Query:
 * - JPQL 查询
 * - 原生 SQL
 * - @Modifying 更新
 * 
 * 4. 关联关系:
 * - @OneToMany, @ManyToOne
 * - @ManyToMany
 * - LAZY vs EAGER
 */
