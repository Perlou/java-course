package phase22;

/**
 * SparkSQL 基础 - DataFrame/Dataset API
 * 
 * SparkSQL 是 Spark 的结构化数据处理模块，
 * 提供 DataFrame 和 Dataset 两种高级 API。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkSqlBasics {

    public static void main(String[] args) {
        System.out.println("=== SparkSQL 基础 ===\n");

        sparkSqlOverview();
        dataFrameApi();
        datasetApi();
        sqlQueries();
        dataSourceApi();
        codeExamples();
    }

    private static void sparkSqlOverview() {
        System.out.println("【SparkSQL 概述】\n");

        System.out.println("SparkSQL 特性：");
        System.out.println("  • 统一的数据访问接口（JSON, Parquet, Hive, JDBC）");
        System.out.println("  • 支持 SQL 查询和 DataFrame/Dataset API");
        System.out.println("  • Catalyst 优化器自动优化查询");
        System.out.println("  • Tungsten 执行引擎提升性能");
        System.out.println();

        System.out.println("核心抽象：");
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.println("  │                    Dataset<T>                   │");
        System.out.println("  │         (强类型，编译时类型检查)                 │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │               DataFrame = Dataset<Row>          │");
        System.out.println("  │         (弱类型，运行时类型检查)                 │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │                       RDD                       │");
        System.out.println("  │              (底层分布式数据集)                  │");
        System.out.println("  └─────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("SparkSession 入口：");
        System.out.println("  SparkSession spark = SparkSession.builder()");
        System.out.println("      .appName(\"MyApp\")");
        System.out.println("      .master(\"local[*]\")");
        System.out.println("      .enableHiveSupport()  // 可选");
        System.out.println("      .getOrCreate();");
        System.out.println();
    }

    private static void dataFrameApi() {
        System.out.println("【DataFrame API】\n");

        System.out.println("创建 DataFrame：");
        System.out.println("  // 1. 从文件读取");
        System.out.println("  Dataset<Row> df = spark.read().json(\"data.json\");");
        System.out.println("  Dataset<Row> df = spark.read().parquet(\"data.parquet\");");
        System.out.println("  Dataset<Row> df = spark.read().csv(\"data.csv\");");
        System.out.println();
        System.out.println("  // 2. 从 RDD 转换");
        System.out.println("  Dataset<Row> df = spark.createDataFrame(rdd, schema);");
        System.out.println();
        System.out.println("  // 3. 从集合创建");
        System.out.println("  List<Row> data = Arrays.asList(");
        System.out.println("      RowFactory.create(1, \"Alice\"),");
        System.out.println("      RowFactory.create(2, \"Bob\")");
        System.out.println("  );");
        System.out.println("  Dataset<Row> df = spark.createDataFrame(data, schema);");
        System.out.println();

        System.out.println("常用 DataFrame 操作：");
        System.out.println("  df.show()             // 显示前 20 行");
        System.out.println("  df.printSchema()      // 打印 Schema");
        System.out.println("  df.columns()          // 获取列名");
        System.out.println("  df.count()            // 行数");
        System.out.println("  df.describe()         // 统计信息");
        System.out.println();

        System.out.println("查询操作：");
        System.out.println("  df.select(\"name\", \"age\")              // 选择列");
        System.out.println("  df.select(col(\"age\").plus(1))        // 列计算");
        System.out.println("  df.filter(col(\"age\").gt(18))         // 过滤");
        System.out.println("  df.where(\"age > 18\")                  // SQL 条件");
        System.out.println("  df.groupBy(\"city\").count()           // 分组统计");
        System.out.println("  df.orderBy(col(\"age\").desc())        // 排序");
        System.out.println("  df.limit(10)                          // 限制行数");
        System.out.println("  df.distinct()                         // 去重");
        System.out.println();

        System.out.println("连接操作：");
        System.out.println("  df1.join(df2, col(\"id\").equalTo(col(\"uid\")), \"inner\")");
        System.out.println("  // 连接类型: inner, left, right, full, cross, semi, anti");
        System.out.println();
    }

    private static void datasetApi() {
        System.out.println("【Dataset API】\n");

        System.out.println("Dataset<T> 是强类型的数据集：");
        System.out.println("  • 编译时类型检查");
        System.out.println("  • 适合面向对象编程");
        System.out.println("  • 需要定义 JavaBean 或 case class");
        System.out.println();

        System.out.println("定义 JavaBean：");
        System.out.println("  public class Person implements Serializable {");
        System.out.println("      private String name;");
        System.out.println("      private int age;");
        System.out.println("      // getters and setters");
        System.out.println("  }");
        System.out.println();

        System.out.println("创建 Dataset：");
        System.out.println("  Encoder<Person> encoder = Encoders.bean(Person.class);");
        System.out.println("  Dataset<Person> ds = spark.createDataset(list, encoder);");
        System.out.println();
        System.out.println("  // 从 DataFrame 转换");
        System.out.println("  Dataset<Person> ds = df.as(encoder);");
        System.out.println();

        System.out.println("Dataset 操作：");
        System.out.println("  ds.map(p -> p.getName(), Encoders.STRING())");
        System.out.println("  ds.filter(p -> p.getAge() > 18)");
        System.out.println("  ds.groupByKey(p -> p.getCity(), Encoders.STRING())");
        System.out.println();

        System.out.println("DataFrame vs Dataset：");
        System.out.println("  ┌────────────┬─────────────────┬─────────────────┐");
        System.out.println("  │   特性      │    DataFrame    │    Dataset      │");
        System.out.println("  ├────────────┼─────────────────┼─────────────────┤");
        System.out.println("  │  类型检查   │     运行时      │     编译时      │");
        System.out.println("  │  性能       │     高          │     略低        │");
        System.out.println("  │  语法       │   SQL-like      │   函数式        │");
        System.out.println("  │  适用场景   │   数据分析      │   类型安全      │");
        System.out.println("  └────────────┴─────────────────┴─────────────────┘");
        System.out.println();
    }

    private static void sqlQueries() {
        System.out.println("【SQL 查询】\n");

        System.out.println("注册临时视图：");
        System.out.println("  df.createOrReplaceTempView(\"users\");  // 会话级别");
        System.out.println("  df.createOrReplaceGlobalTempView(\"users\");  // 全局");
        System.out.println();

        System.out.println("执行 SQL 查询：");
        System.out.println("  Dataset<Row> result = spark.sql(");
        System.out.println("      \"SELECT name, age FROM users WHERE age > 18\"");
        System.out.println("  );");
        System.out.println();

        System.out.println("复杂 SQL 示例：");
        System.out.println("  spark.sql(\"\"\"");
        System.out.println("      SELECT city, AVG(age) as avg_age, COUNT(*) as cnt");
        System.out.println("      FROM users");
        System.out.println("      WHERE age IS NOT NULL");
        System.out.println("      GROUP BY city");
        System.out.println("      HAVING COUNT(*) > 10");
        System.out.println("      ORDER BY avg_age DESC");
        System.out.println("      LIMIT 10");
        System.out.println("  \"\"\");");
        System.out.println();

        System.out.println("窗口函数：");
        System.out.println("  spark.sql(\"\"\"");
        System.out.println("      SELECT name, salary,");
        System.out.println("             ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) as rank,");
        System.out.println("             SUM(salary) OVER (PARTITION BY dept) as dept_total");
        System.out.println("      FROM employees");
        System.out.println("  \"\"\");");
        System.out.println();
    }

    private static void dataSourceApi() {
        System.out.println("【数据源 API】\n");

        System.out.println("=== 读取数据 ===");
        System.out.println();

        System.out.println("JSON:");
        System.out.println("  spark.read().json(\"path/to/data.json\")");
        System.out.println();

        System.out.println("Parquet (推荐)：");
        System.out.println("  spark.read().parquet(\"path/to/data.parquet\")");
        System.out.println();

        System.out.println("CSV:");
        System.out.println("  spark.read()");
        System.out.println("      .option(\"header\", \"true\")");
        System.out.println("      .option(\"inferSchema\", \"true\")");
        System.out.println("      .csv(\"path/to/data.csv\")");
        System.out.println();

        System.out.println("JDBC:");
        System.out.println("  spark.read()");
        System.out.println("      .format(\"jdbc\")");
        System.out.println("      .option(\"url\", \"jdbc:mysql://host:3306/db\")");
        System.out.println("      .option(\"dbtable\", \"users\")");
        System.out.println("      .option(\"user\", \"root\")");
        System.out.println("      .option(\"password\", \"xxx\")");
        System.out.println("      .load()");
        System.out.println();

        System.out.println("=== 写入数据 ===");
        System.out.println();

        System.out.println("df.write()");
        System.out.println("    .mode(SaveMode.Overwrite)  // Overwrite/Append/Ignore/ErrorIfExists");
        System.out.println("    .partitionBy(\"date\")       // 分区列");
        System.out.println("    .option(\"compression\", \"snappy\")");
        System.out.println("    .parquet(\"output/path\")");
        System.out.println();
    }

    private static void codeExamples() {
        System.out.println("【完整代码示例】\n");

        System.out.println("// 用户行为分析示例");
        System.out.println("SparkSession spark = SparkSession.builder()");
        System.out.println("    .appName(\"UserAnalysis\")");
        System.out.println("    .getOrCreate();");
        System.out.println();
        System.out.println("// 读取用户日志");
        System.out.println("Dataset<Row> logs = spark.read().json(\"hdfs:///logs/*.json\");");
        System.out.println();
        System.out.println("// 分析 PV/UV");
        System.out.println("Dataset<Row> pvuv = logs");
        System.out.println("    .groupBy(\"page\")");
        System.out.println("    .agg(");
        System.out.println("        count(\"*\").alias(\"pv\"),");
        System.out.println("        countDistinct(\"userId\").alias(\"uv\")");
        System.out.println("    )");
        System.out.println("    .orderBy(col(\"pv\").desc());");
        System.out.println();
        System.out.println("// 保存结果");
        System.out.println("pvuv.write()");
        System.out.println("    .mode(SaveMode.Overwrite)");
        System.out.println("    .parquet(\"hdfs:///output/pvuv\");");
    }
}
