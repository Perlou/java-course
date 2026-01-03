package phase22;

/**
 * Spark 自定义 UDF 函数
 * 
 * UDF (User Defined Function) 允许用户扩展 SparkSQL 的内置函数，
 * 实现自定义的数据处理逻辑。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkUdf {

    public static void main(String[] args) {
        System.out.println("=== Spark 自定义 UDF 函数 ===\n");

        udfOverview();
        simpleUdf();
        complexUdf();
        udafExample();
        udfBestPractices();
    }

    private static void udfOverview() {
        System.out.println("【UDF 概述】\n");

        System.out.println("什么是 UDF？");
        System.out.println("  • User Defined Function 用户自定义函数");
        System.out.println("  • 扩展 SparkSQL 的内置函数");
        System.out.println("  • 可以在 SQL 和 DataFrame API 中使用");
        System.out.println();

        System.out.println("UDF 类型：");
        System.out.println("  • UDF：普通标量函数，一行输入一行输出");
        System.out.println("  • UDAF：聚合函数，多行输入一行输出");
        System.out.println("  • UDTF：表生成函数，一行输入多行输出");
        System.out.println();

        System.out.println("注意事项：");
        System.out.println("  • UDF 是黑盒，Catalyst 无法优化");
        System.out.println("  • 尽量使用内置函数，性能更好");
        System.out.println("  • 需要序列化，避免使用不可序列化的对象");
        System.out.println();
    }

    private static void simpleUdf() {
        System.out.println("【简单 UDF 示例】\n");

        System.out.println("=== 方式一：使用 Lambda 注册 ===");
        System.out.println();
        System.out.println("// 注册 UDF：字符串转大写");
        System.out.println("spark.udf().register(\"toUpper\",");
        System.out.println("    (String s) -> s != null ? s.toUpperCase() : null,");
        System.out.println("    DataTypes.StringType);");
        System.out.println();
        System.out.println("// 在 SQL 中使用");
        System.out.println("spark.sql(\"SELECT toUpper(name) FROM users\");");
        System.out.println();

        System.out.println("=== 方式二：使用 UDF 接口 ===");
        System.out.println();
        System.out.println("import org.apache.spark.sql.api.java.UDF1;");
        System.out.println();
        System.out.println("// 定义 UDF 类");
        System.out.println("public class ToUpperUDF implements UDF1<String, String> {");
        System.out.println("    @Override");
        System.out.println("    public String call(String s) {");
        System.out.println("        return s != null ? s.toUpperCase() : null;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("// 注册");
        System.out.println("spark.udf().register(\"toUpper\", new ToUpperUDF(), DataTypes.StringType);");
        System.out.println();

        System.out.println("=== 方式三：在 DataFrame API 中使用 ===");
        System.out.println();
        System.out.println("import static org.apache.spark.sql.functions.*;");
        System.out.println();
        System.out.println("// 创建 UDF");
        System.out.println("UserDefinedFunction toUpperUdf = udf(");
        System.out.println("    (String s) -> s != null ? s.toUpperCase() : null,");
        System.out.println("    DataTypes.StringType");
        System.out.println(");");
        System.out.println();
        System.out.println("// 在 DataFrame 中使用");
        System.out.println("df.select(toUpperUdf.apply(col(\"name\")).alias(\"upper_name\"));");
        System.out.println();
    }

    private static void complexUdf() {
        System.out.println("【复杂 UDF 示例】\n");

        System.out.println("=== 多参数 UDF ===");
        System.out.println();
        System.out.println("// UDF2：两个参数");
        System.out.println("spark.udf().register(\"concat3\",");
        System.out.println("    (UDF3<String, String, String, String>) (a, b, c) -> a + b + c,");
        System.out.println("    DataTypes.StringType);");
        System.out.println();

        System.out.println("=== 返回复杂类型 ===");
        System.out.println();
        System.out.println("// 返回数组");
        System.out.println("spark.udf().register(\"splitWords\",");
        System.out.println("    (String s) -> s != null ? Arrays.asList(s.split(\" \")) : null,");
        System.out.println("    DataTypes.createArrayType(DataTypes.StringType));");
        System.out.println();
        System.out.println("// 返回 Map");
        System.out.println("DataType mapType = DataTypes.createMapType(");
        System.out.println("    DataTypes.StringType, DataTypes.IntegerType);");
        System.out.println("spark.udf().register(\"wordCount\",");
        System.out.println("    (String s) -> {");
        System.out.println("        Map<String, Integer> counts = new HashMap<>();");
        System.out.println("        for (String word : s.split(\" \")) {");
        System.out.println("            counts.merge(word, 1, Integer::sum);");
        System.out.println("        }");
        System.out.println("        return counts;");
        System.out.println("    }, mapType);");
        System.out.println();

        System.out.println("=== 带状态的 UDF ===");
        System.out.println();
        System.out.println("// 使用 Broadcast 变量");
        System.out.println("Broadcast<Map<String, String>> dictBroadcast = ");
        System.out.println("    spark.sparkContext().broadcast(dict);");
        System.out.println();
        System.out.println("spark.udf().register(\"translate\",");
        System.out.println("    (String word) -> dictBroadcast.value().getOrDefault(word, word),");
        System.out.println("    DataTypes.StringType);");
        System.out.println();
    }

    private static void udafExample() {
        System.out.println("【UDAF 聚合函数示例】\n");

        System.out.println("Spark 3.0+ 使用 Aggregator API：");
        System.out.println();
        System.out.println("import org.apache.spark.sql.expressions.Aggregator;");
        System.out.println();
        System.out.println("// 自定义平均值 UDAF");
        System.out.println("public class MyAverage extends Aggregator<Long, Average, Double> {");
        System.out.println("    ");
        System.out.println("    // 中间状态类");
        System.out.println("    public static class Average implements Serializable {");
        System.out.println("        long sum = 0;");
        System.out.println("        long count = 0;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 初始值");
        System.out.println("    @Override");
        System.out.println("    public Average zero() {");
        System.out.println("        return new Average();");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 分区内聚合");
        System.out.println("    @Override");
        System.out.println("    public Average reduce(Average buffer, Long data) {");
        System.out.println("        buffer.sum += data;");
        System.out.println("        buffer.count++;");
        System.out.println("        return buffer;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 分区间合并");
        System.out.println("    @Override");
        System.out.println("    public Average merge(Average b1, Average b2) {");
        System.out.println("        b1.sum += b2.sum;");
        System.out.println("        b1.count += b2.count;");
        System.out.println("        return b1;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 最终结果");
        System.out.println("    @Override");
        System.out.println("    public Double finish(Average reduction) {");
        System.out.println("        return (double) reduction.sum / reduction.count;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // Encoder");
        System.out.println("    @Override");
        System.out.println("    public Encoder<Average> bufferEncoder() {");
        System.out.println("        return Encoders.bean(Average.class);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Encoder<Double> outputEncoder() {");
        System.out.println("        return Encoders.DOUBLE();");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("// 注册 UDAF");
        System.out.println("spark.udf().register(\"myAvg\", functions.udaf(new MyAverage(), Encoders.LONG()));");
        System.out.println();
        System.out.println("// 使用");
        System.out.println("spark.sql(\"SELECT dept, myAvg(salary) FROM employees GROUP BY dept\");");
        System.out.println();
    }

    private static void udfBestPractices() {
        System.out.println("【UDF 最佳实践】\n");

        System.out.println("1. 优先使用内置函数");
        System.out.println("   内置函数经过 Catalyst 优化，性能更好");
        System.out.println();

        System.out.println("2. 避免在 UDF 中创建大对象");
        System.out.println("   每行数据都会调用 UDF，创建对象开销大");
        System.out.println();

        System.out.println("3. 使用 Broadcast 共享大数据");
        System.out.println("   避免每个 Task 都序列化大数据");
        System.out.println();

        System.out.println("4. 处理 null 值");
        System.out.println("   显式检查和处理 null，避免 NPE");
        System.out.println();

        System.out.println("5. 保持 UDF 简单");
        System.out.println("   复杂逻辑拆分成多个简单 UDF");
        System.out.println();

        System.out.println("6. 考虑使用 Scala 或 Python UDF");
        System.out.println("   如果需要更好的性能，考虑 Pandas UDF");
    }
}
