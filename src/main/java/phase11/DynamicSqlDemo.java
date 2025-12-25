package phase11;

/**
 * Phase 11 - Lesson 5: MyBatis 动态 SQL
 * 
 * 🎯 学习目标:
 * 1. 掌握动态 SQL 标签
 * 2. 学会构建复杂查询
 * 3. 了解动态 SQL 最佳实践
 */
public class DynamicSqlDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 5: MyBatis 动态 SQL");
        System.out.println("=".repeat(60));

        // 1. 动态 SQL 概述
        System.out.println("\n【1. 动态 SQL 概述】");
        System.out.println("""
                动态 SQL = 根据条件动态生成 SQL 语句

                场景: 多条件组合查询
                ┌─────────────────────────────────────────────────────────┐
                │  搜索条件:                                              │
                │  - 用户名 (可选)                                        │
                │  - 年龄范围 (可选)                                      │
                │  - 状态 (可选)                                          │
                │                                                         │
                │  需要根据传入条件动态拼接 SQL                           │
                └─────────────────────────────────────────────────────────┘

                传统方式 (Java 拼接):
                String sql = "SELECT * FROM users WHERE 1=1";
                if (name != null) sql += " AND name = '" + name + "'";
                ❌ 繁琐，容易 SQL 注入

                MyBatis 动态 SQL:
                <select id="findByCondition">
                    SELECT * FROM users
                    <where>
                        <if test="name != null">name = #{name}</if>
                    </where>
                </select>
                ✅ 简洁，安全
                """);

        // 2. if 标签
        System.out.println("=".repeat(60));
        System.out.println("【2. <if> 条件判断】");
        System.out.println("""
                基本用法:

                <select id="findByCondition" resultType="User">
                    SELECT * FROM users
                    WHERE 1 = 1
                    <if test="name != null and name != ''">
                        AND name = #{name}
                    </if>
                    <if test="age != null">
                        AND age = #{age}
                    </if>
                    <if test="status != null">
                        AND status = #{status}
                    </if>
                </select>

                test 表达式:
                ┌────────────────────────────────────────────────────────┐
                │  test="name != null"           非空判断               │
                │  test="name != null and name != ''"  非空非空串       │
                │  test="age > 18"               数值比较               │
                │  test="list != null and list.size() > 0"  集合非空   │
                │  test="type == 'ADMIN'"        字符串比较             │
                └────────────────────────────────────────────────────────┘

                ⚠️ 字符串比较用单引号:
                test="type == 'ADMIN'"  ✅
                test='type == "ADMIN"'  ✅
                """);

        // 3. where 和 set 标签
        System.out.println("=".repeat(60));
        System.out.println("【3. <where> 和 <set> 标签】");
        System.out.println("""
                <where> 智能处理 WHERE 和 AND/OR:

                <select id="findByCondition" resultType="User">
                    SELECT * FROM users
                    <where>
                        <if test="name != null">
                            AND name = #{name}
                        </if>
                        <if test="age != null">
                            AND age = #{age}
                        </if>
                    </where>
                </select>

                自动处理:
                - 无条件: SELECT * FROM users
                - 有条件: SELECT * FROM users WHERE name = ? AND age = ?
                - 自动去掉开头的 AND/OR

                <set> 智能处理 SET 和逗号:

                <update id="updateSelective">
                    UPDATE users
                    <set>
                        <if test="name != null">
                            name = #{name},
                        </if>
                        <if test="email != null">
                            email = #{email},
                        </if>
                        <if test="age != null">
                            age = #{age},
                        </if>
                    </set>
                    WHERE id = #{id}
                </update>

                自动去掉末尾逗号:
                UPDATE users SET name = ?, email = ? WHERE id = ?
                """);

        // 4. choose-when-otherwise
        System.out.println("=".repeat(60));
        System.out.println("【4. <choose> 多选一】");
        System.out.println("""
                类似 Java 的 switch-case:

                <select id="findUsers" resultType="User">
                    SELECT * FROM users
                    <where>
                        <choose>
                            <when test="id != null">
                                id = #{id}
                            </when>
                            <when test="username != null">
                                username = #{username}
                            </when>
                            <when test="email != null">
                                email = #{email}
                            </when>
                            <otherwise>
                                status = 'ACTIVE'
                            </otherwise>
                        </choose>
                    </where>
                </select>

                执行逻辑:
                1. 如果 id 不为空，按 id 查询
                2. 否则如果 username 不为空，按 username 查询
                3. 否则如果 email 不为空，按 email 查询
                4. 否则查询 ACTIVE 状态的用户
                """);

        // 5. foreach 循环
        System.out.println("=".repeat(60));
        System.out.println("【5. <foreach> 循环】");
        System.out.println("""
                1. IN 查询

                <select id="findByIds" resultType="User">
                    SELECT * FROM users
                    WHERE id IN
                    <foreach collection="ids" item="id"
                             open="(" separator="," close=")">
                        #{id}
                    </foreach>
                </select>

                生成: WHERE id IN (1, 2, 3, 4, 5)

                2. 批量插入

                <insert id="batchInsert">
                    INSERT INTO users (name, email) VALUES
                    <foreach collection="list" item="user" separator=",">
                        (#{user.name}, #{user.email})
                    </foreach>
                </insert>

                生成: INSERT INTO users (name, email) VALUES
                      ('张三', 'a@test.com'), ('李四', 'b@test.com')

                3. 批量更新

                <update id="batchUpdate">
                    <foreach collection="list" item="user" separator=";">
                        UPDATE users
                        SET name = #{user.name}
                        WHERE id = #{user.id}
                    </foreach>
                </update>

                ⚠️ 需要开启 allowMultiQueries=true

                foreach 属性:
                ┌─────────────┬────────────────────────────────────┐
                │    属性     │             说明                   │
                ├─────────────┼────────────────────────────────────┤
                │ collection  │ 集合名 (list, array, 或 @Param)    │
                │ item        │ 元素变量名                         │
                │ index       │ 索引变量名                         │
                │ open        │ 开始字符                           │
                │ close       │ 结束字符                           │
                │ separator   │ 分隔符                             │
                └─────────────┴────────────────────────────────────┘
                """);

        // 6. trim 标签
        System.out.println("=".repeat(60));
        System.out.println("【6. <trim> 自定义处理】");
        System.out.println("""
                <trim> 可以实现 <where> 和 <set> 的功能:

                等价于 <where>:
                <trim prefix="WHERE" prefixOverrides="AND |OR ">
                    <if test="name != null">AND name = #{name}</if>
                    <if test="age != null">AND age = #{age}</if>
                </trim>

                等价于 <set>:
                <trim prefix="SET" suffixOverrides=",">
                    <if test="name != null">name = #{name},</if>
                    <if test="age != null">age = #{age},</if>
                </trim>

                属性:
                ┌─────────────────┬────────────────────────────────────┐
                │      属性       │             说明                   │
                ├─────────────────┼────────────────────────────────────┤
                │ prefix          │ 添加前缀                           │
                │ suffix          │ 添加后缀                           │
                │ prefixOverrides │ 去除开头匹配的字符                 │
                │ suffixOverrides │ 去除结尾匹配的字符                 │
                └─────────────────┴────────────────────────────────────┘
                """);

        // 7. SQL 片段
        System.out.println("=".repeat(60));
        System.out.println("【7. <sql> 片段复用】");
        System.out.println("""
                定义 SQL 片段:

                <sql id="baseColumns">
                    id, username, email, phone, status, created_at
                </sql>

                <sql id="queryCondition">
                    <where>
                        <if test="name != null">AND username LIKE CONCAT('%',#{name},'%')</if>
                        <if test="status != null">AND status = #{status}</if>
                    </where>
                </sql>

                引用片段:

                <select id="findAll" resultType="User">
                    SELECT <include refid="baseColumns"/>
                    FROM users
                </select>

                <select id="findByCondition" resultType="User">
                    SELECT <include refid="baseColumns"/>
                    FROM users
                    <include refid="queryCondition"/>
                </select>

                <select id="countByCondition" resultType="int">
                    SELECT COUNT(*) FROM users
                    <include refid="queryCondition"/>
                </select>
                """);

        // 8. 实战示例
        System.out.println("=".repeat(60));
        System.out.println("【8. 实战示例: 通用分页查询】");
        System.out.println("""
                <!-- 定义通用查询条件 -->
                <sql id="queryCondition">
                    <where>
                        <if test="query.username != null and query.username != ''">
                            AND username LIKE CONCAT('%', #{query.username}, '%')
                        </if>
                        <if test="query.status != null">
                            AND status = #{query.status}
                        </if>
                        <if test="query.startTime != null">
                            AND created_at >= #{query.startTime}
                        </if>
                        <if test="query.endTime != null">
                            AND created_at <= #{query.endTime}
                        </if>
                    </where>
                </sql>

                <!-- 分页查询 -->
                <select id="findPage" resultType="User">
                    SELECT * FROM users
                    <include refid="queryCondition"/>
                    ORDER BY
                    <choose>
                        <when test="query.orderBy != null">
                            ${query.orderBy} ${query.orderDir}
                        </when>
                        <otherwise>
                            created_at DESC
                        </otherwise>
                    </choose>
                    LIMIT #{query.offset}, #{query.pageSize}
                </select>

                <!-- 统计总数 -->
                <select id="countTotal" resultType="long">
                    SELECT COUNT(*) FROM users
                    <include refid="queryCondition"/>
                </select>
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 <where> 和 <set> 智能处理前后缀");
        System.out.println("💡 <foreach> 用于 IN 查询和批量操作");
        System.out.println("💡 <sql> 提取公共片段，减少重复");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 条件判断:
 * - <if> 条件
 * - <choose>/<when>/<otherwise>
 * 
 * 2. 智能标签:
 * - <where> 处理 WHERE 和 AND
 * - <set> 处理 SET 和逗号
 * - <trim> 自定义处理
 * 
 * 3. 循环:
 * - <foreach> IN 查询、批量操作
 * 
 * 4. 复用:
 * - <sql> + <include>
 */
