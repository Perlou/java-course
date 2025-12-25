package phase11;

/**
 * Phase 11 - Lesson 7: JPA 进阶
 * 
 * 🎯 学习目标:
 * 1. 掌握 Spring Data JPA 高级用法
 * 2. 学会自定义查询和审计功能
 * 3. 了解 JPA 性能优化
 */
public class JpaAdvanced {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 7: JPA 进阶");
        System.out.println("=".repeat(60));

        // 1. JPA Repository 层次
        System.out.println("\n【1. Repository 接口层次】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                 Repository 继承体系                      │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  Repository (标记接口)                                  │
                │      │                                                  │
                │      └── CrudRepository                                │
                │              │  save, findById, delete, count...       │
                │              │                                          │
                │              └── PagingAndSortingRepository            │
                │                      │  findAll(Pageable), findAll(Sort)│
                │                      │                                  │
                │                      └── JpaRepository                 │
                │                              flush, saveAndFlush       │
                │                              deleteInBatch, ...        │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                public interface UserRepository extends JpaRepository<User, Long> {
                    // 继承即可获得所有 CRUD 方法
                }
                """);

        // 2. 方法名查询
        System.out.println("=".repeat(60));
        System.out.println("【2. 方法名派生查询】");
        System.out.println("""
                Spring Data JPA 可以根据方法名自动生成 SQL:

                public interface UserRepository extends JpaRepository<User, Long> {

                    // 按单个条件查询
                    User findByUsername(String username);
                    User findByEmail(String email);

                    // 多条件
                    User findByUsernameAndEmail(String username, String email);
                    List<User> findByStatusOrRole(Integer status, String role);

                    // 模糊查询
                    List<User> findByUsernameLike(String pattern);
                    List<User> findByUsernameContaining(String keyword);
                    List<User> findByUsernameStartingWith(String prefix);

                    // 范围查询
                    List<User> findByAgeBetween(Integer min, Integer max);
                    List<User> findByAgeGreaterThan(Integer age);
                    List<User> findByCreatedAtAfter(LocalDateTime time);

                    // 集合查询
                    List<User> findByIdIn(List<Long> ids);

                    // NULL 判断
                    List<User> findByEmailIsNull();
                    List<User> findByEmailIsNotNull();

                    // 排序和限制
                    List<User> findTop10ByOrderByCreatedAtDesc();
                    User findFirstByOrderByIdDesc();

                    // 统计
                    long countByStatus(Integer status);
                    boolean existsByUsername(String username);

                    // 删除
                    void deleteByStatus(Integer status);
                }

                关键字对照:
                ┌─────────────────┬────────────────────────────────────┐
                │     关键字      │           SQL 等价                 │
                ├─────────────────┼────────────────────────────────────┤
                │ And             │ AND                                │
                │ Or              │ OR                                 │
                │ Between         │ BETWEEN ... AND ...                │
                │ LessThan        │ <                                  │
                │ GreaterThan     │ >                                  │
                │ Like            │ LIKE                               │
                │ Containing      │ LIKE %...%                         │
                │ In              │ IN (...)                           │
                │ OrderBy         │ ORDER BY                           │
                │ Not             │ NOT                                │
                └─────────────────┴────────────────────────────────────┘
                """);

        // 3. @Query 自定义查询
        System.out.println("=".repeat(60));
        System.out.println("【3. @Query 自定义查询】");
        System.out.println("""
                JPQL 查询 (面向对象):

                @Query("SELECT u FROM User u WHERE u.status = :status")
                List<User> findByStatusJpql(@Param("status") Integer status);

                @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
                List<User> searchByUsername(@Param("keyword") String keyword);

                @Query("SELECT u FROM User u WHERE u.age > ?1 AND u.status = ?2")
                List<User> findByAgeAndStatus(Integer age, Integer status);

                原生 SQL 查询:

                @Query(value = "SELECT * FROM users WHERE status = :status",
                       nativeQuery = true)
                List<User> findByStatusNative(@Param("status") Integer status);

                @Query(value = "SELECT * FROM users ORDER BY created_at DESC LIMIT :limit",
                       nativeQuery = true)
                List<User> findRecentUsers(@Param("limit") int limit);

                投影查询 (只返回部分字段):

                // 定义投影接口
                public interface UserSummary {
                    Long getId();
                    String getUsername();
                    String getEmail();
                }

                @Query("SELECT u.id as id, u.username as username, u.email as email " +
                       "FROM User u WHERE u.status = 1")
                List<UserSummary> findAllSummary();

                更新和删除:

                @Modifying
                @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
                int updateStatus(@Param("id") Long id, @Param("status") Integer status);

                @Modifying
                @Query("DELETE FROM User u WHERE u.status = 0")
                int deleteInactiveUsers();
                """);

        // 4. 分页和排序
        System.out.println("=".repeat(60));
        System.out.println("【4. 分页和排序】");
        System.out.println("""
                基础分页:

                // Controller
                @GetMapping("/users")
                public Page<User> getUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                    Pageable pageable = PageRequest.of(page, size);
                    return userRepository.findAll(pageable);
                }

                带排序的分页:

                Pageable pageable = PageRequest.of(
                    0, 10,
                    Sort.by(Sort.Direction.DESC, "createdAt")
                );

                // 多字段排序
                Pageable pageable = PageRequest.of(0, 10,
                    Sort.by("status").ascending()
                        .and(Sort.by("createdAt").descending())
                );

                自定义分页查询:

                @Query("SELECT u FROM User u WHERE u.status = :status")
                Page<User> findByStatus(@Param("status") Integer status, Pageable pageable);

                // 调用
                Page<User> page = userRepository.findByStatus(1, PageRequest.of(0, 10));

                Page 对象:
                ┌─────────────────────────┬──────────────────────────────┐
                │         方法             │           说明              │
                ├─────────────────────────┼──────────────────────────────┤
                │ getContent()            │ 当前页数据                  │
                │ getTotalElements()      │ 总记录数                    │
                │ getTotalPages()         │ 总页数                      │
                │ getNumber()             │ 当前页码 (0开始)            │
                │ getSize()               │ 每页大小                    │
                │ hasNext() / hasPrevious()│ 是否有上/下页              │
                └─────────────────────────┴──────────────────────────────┘
                """);

        // 5. JPA 审计
        System.out.println("=".repeat(60));
        System.out.println("【5. 审计功能 (Auditing)】");
        System.out.println("""
                1. 启用审计

                @Configuration
                @EnableJpaAuditing
                public class JpaConfig {
                }

                2. 使用审计注解

                @Entity
                @EntityListeners(AuditingEntityListener.class)
                public class User {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    private String username;

                    @CreatedDate
                    private LocalDateTime createdAt;

                    @LastModifiedDate
                    private LocalDateTime updatedAt;

                    @CreatedBy
                    private String createdBy;

                    @LastModifiedBy
                    private String updatedBy;
                }

                3. 配置审计者 (可选)

                @Component
                public class AuditorAwareImpl implements AuditorAware<String> {

                    @Override
                    public Optional<String> getCurrentAuditor() {
                        // 从 SecurityContext 获取当前用户
                        return Optional.of(
                            SecurityContextHolder.getContext()
                                .getAuthentication().getName()
                        );
                    }
                }

                4. 抽取基类

                @MappedSuperclass
                @EntityListeners(AuditingEntityListener.class)
                public abstract class BaseEntity {

                    @CreatedDate
                    private LocalDateTime createdAt;

                    @LastModifiedDate
                    private LocalDateTime updatedAt;
                }

                @Entity
                public class User extends BaseEntity {
                    // ...
                }
                """);

        // 6. 关联关系
        System.out.println("=".repeat(60));
        System.out.println("【6. 关联关系处理】");
        System.out.println("""
                一对多:

                @Entity
                public class Department {
                    @Id
                    private Long id;

                    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
                    private List<User> users;
                }

                @Entity
                public class User {
                    @Id
                    private Long id;

                    @ManyToOne(fetch = FetchType.LAZY)
                    @JoinColumn(name = "department_id")
                    private Department department;
                }

                多对多:

                @Entity
                public class User {
                    @Id
                    private Long id;

                    @ManyToMany
                    @JoinTable(
                        name = "user_roles",
                        joinColumns = @JoinColumn(name = "user_id"),
                        inverseJoinColumns = @JoinColumn(name = "role_id")
                    )
                    private Set<Role> roles;
                }

                ⚠️ N+1 问题:

                // 问题: 查询用户时会触发 N 次部门查询
                List<User> users = userRepository.findAll();
                users.forEach(u -> System.out.println(u.getDepartment().getName()));

                // 解决: 使用 JOIN FETCH
                @Query("SELECT u FROM User u JOIN FETCH u.department")
                List<User> findAllWithDepartment();
                """);

        // 7. 性能优化
        System.out.println("=".repeat(60));
        System.out.println("【7. JPA 性能优化】");
        System.out.println("""
                1. 使用懒加载

                @ManyToOne(fetch = FetchType.LAZY)  // 懒加载
                private Department department;

                // 避免 EAGER 带来的额外查询

                2. 使用投影

                // 只查询需要的字段
                @Query("SELECT new com.example.dto.UserDTO(u.id, u.username) FROM User u")
                List<UserDTO> findAllDto();

                3. 批量操作

                @Modifying
                @Query("DELETE FROM User u WHERE u.id IN :ids")
                void deleteByIds(@Param("ids") List<Long> ids);

                // 比循环 deleteById 效率高

                4. 配置批量大小

                spring:
                  jpa:
                    properties:
                      hibernate:
                        jdbc:
                          batch_size: 50
                        order_inserts: true
                        order_updates: true

                5. 使用只读事务

                @Transactional(readOnly = true)
                public List<User> findAll() {
                    return userRepository.findAll();
                }

                6. 二级缓存

                spring:
                  jpa:
                    properties:
                      hibernate:
                        cache:
                          use_second_level_cache: true
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 方法名查询简化简单查询");
        System.out.println("💡 @Query 用于复杂 SQL");
        System.out.println("💡 注意懒加载和 N+1 问题");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Repository 接口:
 * - JpaRepository
 * - 方法名派生查询
 * 
 * 2. @Query:
 * - JPQL
 * - 原生 SQL
 * - 投影查询
 * 
 * 3. 分页排序:
 * - Pageable
 * - Sort
 * - Page
 * 
 * 4. 审计:
 * - @CreatedDate
 * - @LastModifiedDate
 * 
 * 5. 优化:
 * - 懒加载
 * - 批量操作
 * - JOIN FETCH
 */
