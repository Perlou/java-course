package phase21;

/**
 * 链式 MapReduce
 * 
 * 链式 MapReduce 允许在一个 Job 中串联多个 Mapper 或 Reducer，
 * 减少中间结果写入 HDFS 的开销。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class ChainMapReduce {

    public static void main(String[] args) {
        System.out.println("=== 链式 MapReduce ===\n");

        chainConcept();
        chainTypes();
        chainExample();
        multiJobExample();
        bestPractices();
    }

    private static void chainConcept() {
        System.out.println("【链式 MapReduce 概念】\n");

        System.out.println("传统方式（多个 Job）：");
        System.out.println("  Job1: Map -> Reduce -> HDFS");
        System.out.println("  Job2: Map -> Reduce -> HDFS");
        System.out.println("  问题: 中间结果写入 HDFS，IO 开销大");
        System.out.println();

        System.out.println("链式方式（单个 Job）：");
        System.out.println("  ┌─────────────────────────────────────────────┐");
        System.out.println("  │              ChainMapper                    │");
        System.out.println("  │  Input -> [M1 -> M2 -> M3] -> Shuffle       │");
        System.out.println("  │              ChainReducer                   │");
        System.out.println("  │        -> [R1 -> M4 -> M5] -> Output        │");
        System.out.println("  └─────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void chainTypes() {
        System.out.println("【链式类型】\n");

        System.out.println("1. ChainMapper");
        System.out.println("   - 在 Map 阶段串联多个 Mapper");
        System.out.println("   - 前一个 Mapper 的输出作为后一个的输入");
        System.out.println("   - 用于预处理、过滤、转换");
        System.out.println();

        System.out.println("2. ChainReducer");
        System.out.println("   - 在 Reduce 阶段先执行一个 Reducer");
        System.out.println("   - 然后串联多个 Mapper 进行后处理");
        System.out.println("   - 用于聚合后的二次处理");
        System.out.println();
    }

    private static void chainExample() {
        System.out.println("【代码示例】\n");

        System.out.println("=== 配置 ChainMapper ===");
        System.out.println("// 添加第一个 Mapper (数据清洗)");
        System.out.println("ChainMapper.addMapper(job, CleanMapper.class,");
        System.out.println("    LongWritable.class, Text.class,    // 输入类型");
        System.out.println("    Text.class, Text.class,            // 输出类型");
        System.out.println("    new Configuration(false));");
        System.out.println();
        System.out.println("// 添加第二个 Mapper (过滤)");
        System.out.println("ChainMapper.addMapper(job, FilterMapper.class,");
        System.out.println("    Text.class, Text.class,");
        System.out.println("    Text.class, IntWritable.class,");
        System.out.println("    new Configuration(false));");
        System.out.println();

        System.out.println("=== 配置 ChainReducer ===");
        System.out.println("// 设置主 Reducer");
        System.out.println("ChainReducer.setReducer(job, SumReducer.class,");
        System.out.println("    Text.class, IntWritable.class,     // 输入类型");
        System.out.println("    Text.class, IntWritable.class,     // 输出类型");
        System.out.println("    new Configuration(false));");
        System.out.println();
        System.out.println("// 添加后处理 Mapper");
        System.out.println("ChainReducer.addMapper(job, FormatMapper.class,");
        System.out.println("    Text.class, IntWritable.class,");
        System.out.println("    Text.class, Text.class,");
        System.out.println("    new Configuration(false));");
        System.out.println();
    }

    private static void multiJobExample() {
        System.out.println("【多 Job 串联】\n");

        System.out.println("当链式无法满足需求时，使用多个 Job：");
        System.out.println();
        System.out.println("// Job1 配置");
        System.out.println("Job job1 = Job.getInstance(conf, \"job1\");");
        System.out.println("FileInputFormat.addInputPath(job1, inputPath);");
        System.out.println("FileOutputFormat.setOutputPath(job1, tempPath);");
        System.out.println("job1.waitForCompletion(true);");
        System.out.println();
        System.out.println("// Job2 使用 Job1 的输出");
        System.out.println("Job job2 = Job.getInstance(conf, \"job2\");");
        System.out.println("FileInputFormat.addInputPath(job2, tempPath);");
        System.out.println("FileOutputFormat.setOutputPath(job2, outputPath);");
        System.out.println("job2.waitForCompletion(true);");
        System.out.println();

        System.out.println("使用 JobControl 管理依赖：");
        System.out.println("ControlledJob cJob1 = new ControlledJob(job1.getConfiguration());");
        System.out.println("ControlledJob cJob2 = new ControlledJob(job2.getConfiguration());");
        System.out.println("cJob2.addDependingJob(cJob1);  // job2 依赖 job1");
        System.out.println("JobControl jobControl = new JobControl(\"chain\");");
        System.out.println("jobControl.addJob(cJob1);");
        System.out.println("jobControl.addJob(cJob2);");
        System.out.println("new Thread(jobControl).start();");
        System.out.println();
    }

    private static void bestPractices() {
        System.out.println("【最佳实践】\n");
        System.out.println("1. 链式适用于轻量级处理，避免复杂计算");
        System.out.println("2. 注意类型匹配：前一个输出类型 = 后一个输入类型");
        System.out.println("3. 大计算量任务使用多 Job 方式");
        System.out.println("4. 考虑使用 Spark 代替复杂的 MR 链");
    }
}
