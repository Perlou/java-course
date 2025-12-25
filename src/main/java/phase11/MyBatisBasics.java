package phase11;

/**
 * Phase 11 - Lesson 4: MyBatis 基础
 * 
 * 🎯 学习目标:
 * 1. 理解 MyBatis 核心概念
 * 2. 掌握 Mapper 接口和 XML 配置
 * 3. 学会结果映射和参数传递
 */
public class MyBatisBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 4: MyBatis 基础");
        System.out.println("=".repeat(60));

        // 1. MyBatis 简介
        System.out.println("\n【1. MyBatis 简介】");
        System.out.println("""
                MyBatis = 半自动 ORM 框架

                对比:
                ┌──────────────┬──────────────────────────────────────┐
                │   框架       │              特点                    │
                ├──────────────┼──────────────────────────────────────┤
                │ JDBC         │ 全手动，代码量大                     │
                │ MyBatis      │ 半自动，SQL 可控，灵活               │
                │ JPA/Hibernate│ 全自动，ORM 映射，简单 CRUD          │
                └──────────────┴──────────────────────────────────────┘

                MyBatis 优势:
                ✅ 灵活控制 SQL，性能优化方便
                ✅ 支持复杂 SQL 和存储过程
                ✅ 与既有数据库良好兼容
                ✅ 学习成本低

                核心组件:
                ┌─────────────────────────────────────────────────────────┐
                │  SqlSessionFactory ──创建──▶ SqlSession               │
                │                                 │                       │
                │                                 ▼                       │
                │  Mapper 接口 ◀────调用──── getMapper()                 │
                │       │                                                 │
                │       ▼                                                 │
                │  Mapper XML ──执行──▶ 数据库                           │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. Spring Boot 集成
        System.out.println("=".repeat(60));
        System.out.println("【2. Spring Boot 集成】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                    <version>3.0.3</version>
                </dependency>
                <dependency>
                    <groupId>com.mysql</groupId>
                    <artifactId>mysql-connector-j</artifactId>
                </dependency>

                2. 配置数据源

                spring:
                  datasource:
                    url: jdbc:mysql://localhost:3306/demo
                    username: root
                    password: 123456
                    driver-class-name: com.mysql.cj.jdbc.Driver

                mybatis:
                  mapper-locations: classpath:mapper/*.xml
                  type-aliases-package: com.example.entity
                  configuration:
                    map-underscore-to-camel-case: true  # 驼峰映射
                    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

                3. 启动类添加扫描

                @SpringBootApplication
                @MapperScan("com.example.mapper")  // 扫描 Mapper 接口
                public class Application {
                    public static void main(String[] args) {
                        SpringApplication.run(Application.class, args);
                    }
                }
                """);

        // 3. Mapper 接口
        System.out.println("=".repeat(60));
        System.out.println("【3. Mapper 接口与注解】");
        System.out.println("""
                方式 1: 注解方式 (简单 SQL)

                @Mapper
                public interface UserMapper {

                    @Select("SELECT * FROM users WHERE id = #{id}")
                    User findById(Long id);

                    @Select("SELECT * FROM users")
                    List<User> findAll();

                    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
                    @Options(useGeneratedKeys = true, keyProperty = "id")
                    int insert(User user);

                    @Update("UPDATE users SET name=#{name} WHERE id=#{id}")
                    int update(User user);

                    @Delete("DELETE FROM users WHERE id = #{id}")
                    int deleteById(Long id);
                }

                方式 2: XML 方式 (复杂 SQL - 推荐)

                // Java 接口
                @Mapper
                public interface UserMapper {
                    User findById(Long id);
                    List<User> findByCondition(UserQuery query);
                    int insert(User user);
                }

                // XML 映射 (resources/mapper/UserMapper.xml)
                见下一节
                """);

        // 4. XML 映射
        System.out.println("=".repeat(60));
        System.out.println("【4. XML 映射文件】");
        System.out.println("""
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

                <mapper namespace="com.example.mapper.UserMapper">

                    <!-- 结果映射 -->
                    <resultMap id="userResultMap" type="User">
                        <id property="id" column="id"/>
                        <result property="username" column="user_name"/>
                        <result property="email" column="email"/>
                        <result property="createdAt" column="created_at"/>
                    </resultMap>

                    <!-- 查询 -->
                    <select id="findById" resultMap="userResultMap">
                        SELECT * FROM users WHERE id = #{id}
                    </select>

                    <select id="findAll" resultType="User">
                        SELECT * FROM users
                    </select>

                    <!-- 插入 -->
                    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
                        INSERT INTO users (user_name, email, created_at)
                        VALUES (#{username}, #{email}, NOW())
                    </insert>

                    <!-- 更新 -->
                    <update id="update">
                        UPDATE users
                        SET user_name = #{username}, email = #{email}
                        WHERE id = #{id}
                    </update>

                    <!-- 删除 -->
                    <delete id="deleteById">
                        DELETE FROM users WHERE id = #{id}
                    </delete>

                </mapper>
                """);

        // 5. 参数传递
        System.out.println("=".repeat(60));
        System.out.println("【5. 参数传递方式】");
        System.out.println("""
                1. 单个参数
                User findById(Long id);
                → #{id} 或 #{任意名称}

                2. 多个参数 (使用 @Param)
                List<User> findByNameAndAge(@Param("name") String name,
                                            @Param("age") Integer age);
                → #{name}, #{age}

                3. 对象参数
                List<User> findByCondition(UserQuery query);
                → #{query.name}, #{query.age} 或直接 #{name}, #{age}

                4. Map 参数
                List<User> findByMap(Map<String, Object> params);
                → #{key}

                #{} vs ${}:
                ┌─────────────────────────────────────────────────────────┐
                │  #{}  预编译参数，防止 SQL 注入 (推荐)                  │
                │  ${}  字符串替换，有 SQL 注入风险                       │
                │                                                         │
                │  SELECT * FROM users WHERE id = #{id}                  │
                │  → SELECT * FROM users WHERE id = ?                    │
                │                                                         │
                │  SELECT * FROM ${tableName} ORDER BY ${column}         │
                │  → SELECT * FROM users ORDER BY create_time            │
                │  ⚠️ 动态表名、列名才用 ${}                              │
                └─────────────────────────────────────────────────────────┘
                """);

        // 6. 结果映射
        System.out.println("=".repeat(60));
        System.out.println("【6. 结果映射】");
        System.out.println("""
                1. 自动映射

                <!-- 列名和属性名一致，自动映射 -->
                <select id="findById" resultType="User">
                    SELECT id, username, email FROM users
                </select>

                2. 驼峰映射

                mybatis:
                  configuration:
                    map-underscore-to-camel-case: true

                user_name → userName  (自动转换)

                3. resultMap 手动映射

                <resultMap id="userMap" type="User">
                    <id property="id" column="user_id"/>
                    <result property="username" column="user_name"/>
                </resultMap>

                4. 关联映射 (一对一)

                <resultMap id="orderWithUser" type="Order">
                    <id property="id" column="id"/>
                    <result property="orderNo" column="order_no"/>
                    <association property="user" javaType="User">
                        <id property="id" column="user_id"/>
                        <result property="username" column="user_name"/>
                    </association>
                </resultMap>

                5. 集合映射 (一对多)

                <resultMap id="userWithOrders" type="User">
                    <id property="id" column="id"/>
                    <result property="username" column="username"/>
                    <collection property="orders" ofType="Order">
                        <id property="id" column="order_id"/>
                        <result property="orderNo" column="order_no"/>
                    </collection>
                </resultMap>
                """);

        // 7. 常用技巧
        System.out.println("=".repeat(60));
        System.out.println("【7. 常用技巧】");
        System.out.println("""
                1. 获取自增主键

                <insert id="insert" useGeneratedKeys="true" keyProperty="id">
                    INSERT INTO users (name) VALUES (#{name})
                </insert>

                User user = new User();
                user.setName("张三");
                userMapper.insert(user);
                System.out.println(user.getId());  // 获取生成的 id

                2. 批量操作

                <insert id="batchInsert">
                    INSERT INTO users (name, email) VALUES
                    <foreach collection="list" item="user" separator=",">
                        (#{user.name}, #{user.email})
                    </foreach>
                </insert>

                <!-- 调用 -->
                int count = userMapper.batchInsert(userList);

                3. SQL 片段复用

                <sql id="baseColumns">
                    id, username, email, created_at
                </sql>

                <select id="findAll" resultType="User">
                    SELECT <include refid="baseColumns"/> FROM users
                </select>

                4. 使用别名

                mybatis:
                  type-aliases-package: com.example.entity

                <!-- 配置后可以直接用 User，不需要全限定名 -->
                <select id="findById" resultType="User">
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 MyBatis 让你完全控制 SQL");
        System.out.println("💡 推荐使用 XML 方式编写复杂 SQL");
        System.out.println("💡 注意使用 #{} 防止 SQL 注入");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 核心组件:
 * - SqlSessionFactory
 * - Mapper 接口
 * - XML 映射文件
 * 
 * 2. 配置集成:
 * - @MapperScan
 * - mapper-locations
 * - 驼峰映射
 * 
 * 3. 参数传递:
 * - #{} 预编译
 * - @Param 多参数
 * 
 * 4. 结果映射:
 * - resultType
 * - resultMap
 * - association/collection
 */
