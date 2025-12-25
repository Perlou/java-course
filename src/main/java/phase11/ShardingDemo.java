package phase11;

/**
 * Phase 11 - Lesson 8: 分库分表
 * 
 * 🎯 学习目标:
 * 1. 理解分库分表的概念和场景
 * 2. 了解 ShardingSphere 的使用
 * 3. 掌握分片策略和配置
 */
public class ShardingDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - Lesson 8: 分库分表");
        System.out.println("=".repeat(60));

        // 1. 为什么需要分库分表
        System.out.println("\n【1. 为什么需要分库分表】");
        System.out.println("""
                单库单表的性能瓶颈:

                ┌─────────────────────────────────────────────────────────┐
                │  数据量          单表性能                               │
                ├─────────────────────────────────────────────────────────┤
                │  < 500万         性能良好                               │
                │  500万 - 1000万  开始变慢，需要优化                     │
                │  > 1000万        明显变慢，考虑分表                     │
                │  > 5000万        必须分库分表                           │
                └─────────────────────────────────────────────────────────┘

                问题:
                1. 单表数据量过大 → 查询慢、索引效率低
                2. 单库压力过大 → 连接数不够、CPU 瓶颈
                3. 存储空间有限 → 单机硬盘上限

                解决方案:
                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  垂直拆分: 按业务拆分                                   │
                │  ├── 用户库: user, user_profile                        │
                │  ├── 订单库: order, order_item                         │
                │  └── 商品库: product, category                         │
                │                                                         │
                │  水平拆分: 按数据拆分                                   │
                │  ├── order_0: id % 4 = 0                               │
                │  ├── order_1: id % 4 = 1                               │
                │  ├── order_2: id % 4 = 2                               │
                │  └── order_3: id % 4 = 3                               │
                │                                                         │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 分库分表方案
        System.out.println("=".repeat(60));
        System.out.println("【2. 分库分表方案对比】");
        System.out.println("""
                ┌──────────────────┬────────────────────────────────────┐
                │       类型        │             适用场景               │
                ├──────────────────┼────────────────────────────────────┤
                │ 垂直分库         │ 业务解耦，不同业务独立库           │
                │ 垂直分表         │ 大字段拆分，冷热数据分离           │
                │ 水平分库         │ 多个库存储同类数据                 │
                │ 水平分表         │ 单库多表存储同类数据               │
                │ 水平分库分表     │ 既分库又分表，最大灵活性           │
                └──────────────────┴────────────────────────────────────┘

                常见中间件:
                ┌──────────────────┬────────────────────────────────────┐
                │       方案        │             特点                   │
                ├──────────────────┼────────────────────────────────────┤
                │ ShardingSphere   │ Apache 顶级项目，功能全面          │
                │ MyCat            │ 数据库代理，独立部署               │
                │ TDDL             │ 阿里巴巴，需二次开发               │
                │ Vitess           │ YouTube 开发，适合 MySQL           │
                └──────────────────┴────────────────────────────────────┘

                本课程使用 ShardingSphere-JDBC
                """);

        // 3. ShardingSphere 简介
        System.out.println("=".repeat(60));
        System.out.println("【3. ShardingSphere 简介】");
        System.out.println("""
                Apache ShardingSphere 生态:

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  ShardingSphere-JDBC                                    │
                │  ├── 作为 JAR 包嵌入应用                               │
                │  ├── 无需额外部署                                      │
                │  └── 适合 Java 应用                                    │
                │                                                         │
                │  ShardingSphere-Proxy                                   │
                │  ├── 独立部署的数据库代理                              │
                │  ├── 对应用透明                                        │
                │  └── 适合多语言、遗留系统                              │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                核心功能:
                ✅ 数据分片: 分库分表
                ✅ 读写分离: 主从复制
                ✅ 数据加密: 敏感数据脱敏
                ✅ 影子库: 压测隔离

                添加依赖:
                <dependency>
                    <groupId>org.apache.shardingsphere</groupId>
                    <artifactId>shardingsphere-jdbc-core</artifactId>
                    <version>5.4.0</version>
                </dependency>
                """);

        // 4. 分片规则配置
        System.out.println("=".repeat(60));
        System.out.println("【4. 分片规则配置】");
        System.out.println("""
                YAML 配置 (application.yml):

                spring:
                  shardingsphere:
                    datasource:
                      names: ds0, ds1  # 数据源名称

                      ds0:
                        type: com.zaxxer.hikari.HikariDataSource
                        driver-class-name: com.mysql.cj.jdbc.Driver
                        jdbc-url: jdbc:mysql://localhost:3306/db0
                        username: root
                        password: 123456

                      ds1:
                        type: com.zaxxer.hikari.HikariDataSource
                        driver-class-name: com.mysql.cj.jdbc.Driver
                        jdbc-url: jdbc:mysql://localhost:3306/db1
                        username: root
                        password: 123456

                    rules:
                      sharding:
                        tables:
                          # 订单表分片配置
                          t_order:
                            actual-data-nodes: ds$->{0..1}.t_order_$->{0..1}
                            # 分库策略
                            database-strategy:
                              standard:
                                sharding-column: user_id
                                sharding-algorithm-name: db-inline
                            # 分表策略
                            table-strategy:
                              standard:
                                sharding-column: order_id
                                sharding-algorithm-name: table-inline
                            # 主键生成策略
                            key-generate-strategy:
                              column: order_id
                              key-generator-name: snowflake

                        # 分片算法
                        sharding-algorithms:
                          db-inline:
                            type: INLINE
                            props:
                              algorithm-expression: ds$->{user_id % 2}
                          table-inline:
                            type: INLINE
                            props:
                              algorithm-expression: t_order_$->{order_id % 2}

                        # 主键生成器
                        key-generators:
                          snowflake:
                            type: SNOWFLAKE
                """);

        // 5. 分片策略
        System.out.println("=".repeat(60));
        System.out.println("【5. 分片策略详解】");
        System.out.println("""
                1. 取模分片 (INLINE)

                algorithm-expression: t_order_$->{order_id % 4}
                // order_id=1 → t_order_1
                // order_id=2 → t_order_2
                // order_id=5 → t_order_1

                2. 范围分片 (BOUNDARY_RANGE)

                sharding-ranges: 100000,200000,300000
                // id < 100000 → table_0
                // 100000 <= id < 200000 → table_1
                // 200000 <= id < 300000 → table_2

                3. 时间分片 (自定义)

                // 按月分表: t_order_202401, t_order_202402
                public class DateShardingAlgorithm implements StandardShardingAlgorithm {
                    @Override
                    public String doSharding(Collection<String> tables, PreciseShardingValue value) {
                        LocalDate date = (LocalDate) value.getValue();
                        String suffix = date.format(DateTimeFormatter.ofPattern("yyyyMM"));
                        return "t_order_" + suffix;
                    }
                }

                4. 哈希分片 (HASH_MOD)

                type: HASH_MOD
                props:
                  sharding-count: 4
                // 对 sharding-column 取哈希后取模

                分片键选择:
                ┌─────────────────────────────────────────────────────────┐
                │  好的分片键:                                            │
                │  ✅ 查询条件中经常出现                                  │
                │  ✅ 分布均匀，避免数据倾斜                              │
                │  ✅ 不经常变化                                          │
                │                                                         │
                │  常见分片键: user_id, order_id, create_time            │
                └─────────────────────────────────────────────────────────┘
                """);

        // 6. 跨分片查询
        System.out.println("=".repeat(60));
        System.out.println("【6. 跨分片查询问题】");
        System.out.println("""
                问题 1: 跨分片 JOIN

                -- 跨库 JOIN 无法直接执行
                SELECT o.*, u.username
                FROM t_order o
                JOIN t_user u ON o.user_id = u.id

                解决方案:
                1. 广播表: 小表全量同步到所有分片
                2. 绑定表: 相同分片键的表放在同一分片
                3. 应用层关联: 分两次查询再拼接

                rules:
                  sharding:
                    # 广播表
                    broadcast-tables:
                      - t_dict
                      - t_config

                    # 绑定表 (相同分片规则)
                    binding-tables:
                      - t_order, t_order_item

                问题 2: 分布式主键

                解决方案:
                - UUID: 无序，不适合做主键
                - 雪花ID: 有序，推荐
                - 数据库序列: 需要额外表

                key-generate-strategy:
                  column: order_id
                  key-generator-name: snowflake

                问题 3: 跨分片分页

                -- 查询所有分片，排序合并
                SELECT * FROM t_order ORDER BY create_time LIMIT 10, 10

                优化:
                1. 使用游标分页代替 OFFSET
                2. 带分片键查询
                3. 业务层限制查询范围
                """);

        // 7. 读写分离
        System.out.println("=".repeat(60));
        System.out.println("【7. 读写分离】");
        System.out.println("""
                主从架构:

                ┌─────────┐     复制     ┌─────────┐
                │  Master │ ──────────▶ │  Slave  │
                │  (写)   │              │  (读)   │
                └─────────┘              └─────────┘

                配置:
                spring:
                  shardingsphere:
                    datasource:
                      names: master, slave1, slave2

                      master:
                        jdbc-url: jdbc:mysql://localhost:3306/db_master

                      slave1:
                        jdbc-url: jdbc:mysql://localhost:3307/db_slave1

                      slave2:
                        jdbc-url: jdbc:mysql://localhost:3308/db_slave2

                    rules:
                      readwrite-splitting:
                        data-sources:
                          ds:
                            write-data-source-name: master
                            read-data-source-names:
                              - slave1
                              - slave2
                            load-balancer-name: round-robin

                        load-balancers:
                          round-robin:
                            type: ROUND_ROBIN

                强制走主库:
                // 在需要读取最新数据时
                HintManager hintManager = HintManager.getInstance();
                hintManager.setWriteRouteOnly();
                try {
                    // 读操作也会路由到主库
                    userRepository.findById(id);
                } finally {
                    hintManager.close();
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 单表超过 1000 万考虑分表");
        System.out.println("💡 选择合适的分片键很重要");
        System.out.println("💡 ShardingSphere 提供开箱即用的解决方案");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 分库分表:
 * - 垂直拆分 (按业务)
 * - 水平拆分 (按数据)
 * 
 * 2. ShardingSphere:
 * - JDBC 嵌入式
 * - Proxy 代理模式
 * 
 * 3. 分片策略:
 * - 取模、范围、时间、哈希
 * - 分片键选择
 * 
 * 4. 注意事项:
 * - 跨分片 JOIN
 * - 分布式主键
 * - 跨分片分页
 */
