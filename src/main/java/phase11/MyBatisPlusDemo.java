package phase11;

/**
 * Phase 11 - Lesson 6: MyBatis-Plus
 * 
 * 🎯 学习目标:
 * 1. 掌握 MyBatis-Plus 快速开发
 * 2. 学会使用 CRUD 接口和 Wrapper
 * 3. 了解代码生成器和分页插件
 */
public class MyBatisPlusDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 6: MyBatis-Plus");
        System.out.println("=".repeat(60));

        // 1. MyBatis-Plus 简介
        System.out.println("\n【1. MyBatis-Plus 简介】");
        System.out.println("""
                MyBatis-Plus = MyBatis 增强工具

                ┌─────────────────────────────────────────────────────────┐
                │                   MyBatis-Plus                          │
                │                                                         │
                │  特性:                                                  │
                │  ✅ 无侵入: 只做增强不做改变                           │
                │  ✅ 通用 CRUD: 无需编写 XML                            │
                │  ✅ 条件构造器: 链式调用，动态 SQL                     │
                │  ✅ 代码生成器: 一键生成代码                           │
                │  ✅ 分页插件: 物理分页                                 │
                │  ✅ 乐观锁: @Version                                   │
                │  ✅ 逻辑删除: @TableLogic                              │
                │  ✅ 自动填充: 创建/更新时间                            │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                依赖:
                <dependency>
                    <groupId>com.baomidou</groupId>
                    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                    <version>3.5.5</version>
                </dependency>
                """);

        // 2. 实体类配置
        System.out.println("=".repeat(60));
        System.out.println("【2. 实体类配置】");
        System.out.println("""
                @Data
                @TableName("users")  // 表名
                public class User {

                    @TableId(type = IdType.AUTO)  // 主键自增
                    private Long id;

                    @TableField("user_name")  // 列名映射
                    private String username;

                    private String email;

                    @TableField(fill = FieldFill.INSERT)  // 插入时填充
                    private LocalDateTime createdAt;

                    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入/更新时填充
                    private LocalDateTime updatedAt;

                    @Version  // 乐观锁版本号
                    private Integer version;

                    @TableLogic  // 逻辑删除
                    private Integer deleted;

                    @TableField(exist = false)  // 非数据库字段
                    private String statusText;
                }

                常用注解:
                ┌──────────────────┬────────────────────────────────────┐
                │      注解        │             说明                   │
                ├──────────────────┼────────────────────────────────────┤
                │ @TableName       │ 表名                               │
                │ @TableId         │ 主键                               │
                │ @TableField      │ 字段配置                           │
                │ @Version         │ 乐观锁                             │
                │ @TableLogic      │ 逻辑删除                           │
                └──────────────────┴────────────────────────────────────┘
                """);

        // 3. Mapper 接口
        System.out.println("=".repeat(60));
        System.out.println("【3. BaseMapper 通用 CRUD】");
        System.out.println("""
                @Mapper
                public interface UserMapper extends BaseMapper<User> {
                    // 继承 BaseMapper，自动拥有 CRUD 方法
                    // 无需编写任何代码！
                }

                内置方法:
                ┌─────────────────────────┬──────────────────────────────┐
                │         方法             │           说明              │
                ├─────────────────────────┼──────────────────────────────┤
                │ insert(entity)          │ 插入                        │
                │ deleteById(id)          │ 根据 ID 删除                │
                │ deleteBatchIds(ids)     │ 批量删除                    │
                │ delete(wrapper)         │ 条件删除                    │
                │ updateById(entity)      │ 根据 ID 更新                │
                │ update(entity, wrapper) │ 条件更新                    │
                │ selectById(id)          │ 根据 ID 查询                │
                │ selectBatchIds(ids)     │ 批量查询                    │
                │ selectOne(wrapper)      │ 查询单条                    │
                │ selectList(wrapper)     │ 查询列表                    │
                │ selectCount(wrapper)    │ 统计数量                    │
                │ selectPage(page,wrapper)│ 分页查询                    │
                └─────────────────────────┴──────────────────────────────┘

                使用示例:
                @Service
                @RequiredArgsConstructor
                public class UserService {
                    private final UserMapper userMapper;

                    public User findById(Long id) {
                        return userMapper.selectById(id);
                    }

                    public void save(User user) {
                        userMapper.insert(user);
                    }
                }
                """);

        // 4. 条件构造器
        System.out.println("=".repeat(60));
        System.out.println("【4. 条件构造器 Wrapper】");
        System.out.println("""
                QueryWrapper - 查询条件:

                // 基本查询
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("status", 1)           // status = 1
                       .like("username", "张")    // username LIKE '%张%'
                       .between("age", 18, 30)    // age BETWEEN 18 AND 30
                       .orderByDesc("created_at"); // ORDER BY created_at DESC

                List<User> users = userMapper.selectList(wrapper);

                LambdaQueryWrapper - Lambda 方式 (推荐):

                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getStatus, 1)
                       .like(User::getUsername, "张")
                       .between(User::getAge, 18, 30)
                       .orderByDesc(User::getCreatedAt);

                List<User> users = userMapper.selectList(wrapper);

                条件方法:
                ┌─────────────────────────┬──────────────────────────────┐
                │         方法             │           SQL               │
                ├─────────────────────────┼──────────────────────────────┤
                │ eq(col, val)            │ col = val                   │
                │ ne(col, val)            │ col <> val                  │
                │ gt / ge / lt / le       │ > / >= / < / <=             │
                │ like(col, val)          │ LIKE '%val%'                │
                │ likeLeft / likeRight    │ LIKE '%val' / 'val%'        │
                │ in(col, vals)           │ IN (...)                    │
                │ between(col, v1, v2)    │ BETWEEN v1 AND v2           │
                │ isNull / isNotNull      │ IS NULL / IS NOT NULL       │
                │ orderByAsc / Desc       │ ORDER BY ... ASC/DESC       │
                │ select(cols)            │ SELECT cols                 │
                └─────────────────────────┴──────────────────────────────┘

                条件拼接 (动态条件):
                wrapper.eq(status != null, User::getStatus, status)
                       .like(name != null, User::getUsername, name);
                // 第一个参数为 true 时才添加条件
                """);

        // 5. 分页查询
        System.out.println("=".repeat(60));
        System.out.println("【5. 分页插件】");
        System.out.println("""
                1. 配置分页插件

                @Configuration
                public class MyBatisPlusConfig {

                    @Bean
                    public MybatisPlusInterceptor mybatisPlusInterceptor() {
                        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
                        interceptor.addInnerInterceptor(
                            new PaginationInnerInterceptor(DbType.MYSQL)
                        );
                        return interceptor;
                    }
                }

                2. 使用分页

                // 创建分页对象 (页码从1开始)
                Page<User> page = new Page<>(1, 10);  // 第1页,每页10条

                // 条件
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getStatus, 1);

                // 执行分页查询
                Page<User> result = userMapper.selectPage(page, wrapper);

                // 获取结果
                long total = result.getTotal();       // 总记录数
                long pages = result.getPages();       // 总页数
                List<User> records = result.getRecords();  // 当前页数据

                3. 自定义 SQL 分页

                // Mapper 接口
                Page<UserVO> selectUserPage(Page<UserVO> page, @Param("query") UserQuery query);

                // XML
                <select id="selectUserPage" resultType="UserVO">
                    SELECT u.*, d.name as deptName
                    FROM users u LEFT JOIN departments d ON u.dept_id = d.id
                    <where>
                        <if test="query.name != null">AND u.username LIKE '%${query.name}%'</if>
                    </where>
                </select>
                """);

        // 6. 自动填充
        System.out.println("=".repeat(60));
        System.out.println("【6. 自动填充】");
        System.out.println("""
                1. 实体类配置

                @TableField(fill = FieldFill.INSERT)
                private LocalDateTime createdAt;

                @TableField(fill = FieldFill.INSERT_UPDATE)
                private LocalDateTime updatedAt;

                2. 实现填充处理器

                @Component
                public class MyMetaObjectHandler implements MetaObjectHandler {

                    @Override
                    public void insertFill(MetaObject metaObject) {
                        this.strictInsertFill(metaObject, "createdAt",
                            LocalDateTime.class, LocalDateTime.now());
                        this.strictInsertFill(metaObject, "updatedAt",
                            LocalDateTime.class, LocalDateTime.now());
                    }

                    @Override
                    public void updateFill(MetaObject metaObject) {
                        this.strictUpdateFill(metaObject, "updatedAt",
                            LocalDateTime.class, LocalDateTime.now());
                    }
                }

                填充时机:
                - FieldFill.INSERT: 插入时填充
                - FieldFill.UPDATE: 更新时填充
                - FieldFill.INSERT_UPDATE: 插入和更新时都填充
                """);

        // 7. 逻辑删除和乐观锁
        System.out.println("=".repeat(60));
        System.out.println("【7. 逻辑删除和乐观锁】");
        System.out.println("""
                逻辑删除:

                1. 配置
                mybatis-plus:
                  global-config:
                    db-config:
                      logic-delete-field: deleted
                      logic-delete-value: 1
                      logic-not-delete-value: 0

                2. 实体类
                @TableLogic
                private Integer deleted;

                3. 效果
                userMapper.deleteById(1);
                // 实际执行: UPDATE users SET deleted = 1 WHERE id = 1

                userMapper.selectById(1);
                // 实际执行: SELECT * FROM users WHERE id = 1 AND deleted = 0

                乐观锁:

                1. 配置插件
                @Bean
                public MybatisPlusInterceptor interceptor() {
                    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
                    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
                    return interceptor;
                }

                2. 实体类
                @Version
                private Integer version;

                3. 效果
                User user = userMapper.selectById(1);  // version = 1
                user.setName("新名字");
                userMapper.updateById(user);
                // UPDATE users SET name='新名字', version=2 WHERE id=1 AND version=1
                """);

        // 8. IService 服务层
        System.out.println("=".repeat(60));
        System.out.println("【8. IService 服务层接口】");
        System.out.println("""
                接口定义:
                public interface UserService extends IService<User> {
                    // 可以添加自定义方法
                }

                实现类:
                @Service
                public class UserServiceImpl extends ServiceImpl<UserMapper, User>
                        implements UserService {
                    // ServiceImpl 已经实现了 IService 的所有方法
                }

                IService 常用方法:
                ┌─────────────────────────┬──────────────────────────────┐
                │         方法             │           说明              │
                ├─────────────────────────┼──────────────────────────────┤
                │ save(entity)            │ 插入                        │
                │ saveBatch(list)         │ 批量插入                    │
                │ removeById(id)          │ 删除                        │
                │ updateById(entity)      │ 更新                        │
                │ getById(id)             │ 查询                        │
                │ list()                  │ 查询全部                    │
                │ list(wrapper)           │ 条件查询                    │
                │ page(page, wrapper)     │ 分页查询                    │
                │ count()                 │ 总数                        │
                │ saveOrUpdate(entity)    │ 存在则更新，否则插入        │
                └─────────────────────────┴──────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 MyBatis-Plus 大大提高开发效率");
        System.out.println("💡 推荐使用 LambdaQueryWrapper");
        System.out.println("💡 善用自动填充、逻辑删除、乐观锁");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. BaseMapper:
 * - 通用 CRUD
 * - 无需编写 XML
 * 
 * 2. Wrapper:
 * - QueryWrapper
 * - LambdaQueryWrapper (推荐)
 * 
 * 3. 插件:
 * - 分页插件
 * - 乐观锁插件
 * 
 * 4. 自动化:
 * - 自动填充
 * - 逻辑删除
 */
