package phase23;

/**
 * Flink 状态编程
 * 
 * 状态是 Flink 的一等公民，用于存储跨事件的中间计算结果。
 * Flink 提供了丰富的状态类型和强大的状态管理机制。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkState {

    /**
     * ========================================
     * 第一部分：状态概述
     * ========================================
     */
    public static void explainStateConcept() {
        System.out.println("=== 状态概述 ===");
        System.out.println();

        System.out.println("【什么是状态】");
        System.out.println("  状态是算子中存储的跨事件的中间数据");
        System.out.println("  例如：计数器、累加器、历史数据缓存等");
        System.out.println();

        System.out.println("【有状态 vs 无状态】");
        System.out.println("  无状态算子: 每条数据独立处理，如 map、filter");
        System.out.println("  有状态算子: 需要历史数据，如 sum、reduce、窗口聚合");
        System.out.println();

        System.out.println("【Flink 状态特点】");
        System.out.println("  • 容错: 自动快照恢复");
        System.out.println("  • 高效: 本地访问，无网络开销");
        System.out.println("  • 可扩展: 支持大规模状态");
        System.out.println("  • 一致性: 精确一次语义");
    }

    /**
     * ========================================
     * 第二部分：状态分类
     * ========================================
     */
    public static void explainStateTypes() {
        System.out.println("=== 状态分类 ===");
        System.out.println();

        System.out.println("【按范围分类】");
        System.out.println();

        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│          Keyed State (键控状态)                           │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  • 绑定到 keyBy 后的 Key                                  │");
        System.out.println("│  • 每个 Key 有独立的状态                                  │");
        System.out.println("│  • 只能在 KeyedStream 上使用                              │");
        System.out.println("│  • 常用类型: ValueState, ListState, MapState等            │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│          Operator State (算子状态)                        │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  • 绑定到算子的并行实例                                   │");
        System.out.println("│  • 同一并行度的所有数据共享                              │");
        System.out.println("│  • 常用于 Source/Sink                                     │");
        System.out.println("│  • 类型: ListState, BroadcastState                        │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("【示意图】");
        System.out.println("```");
        System.out.println("Keyed State:");
        System.out.println("  keyBy(user) → [user1: state1, user2: state2, user3: state3]");
        System.out.println("  每个 key 有独立状态");
        System.out.println();
        System.out.println("Operator State:");
        System.out.println("  Source(0): state0");
        System.out.println("  Source(1): state1");
        System.out.println("  每个并行实例有独立状态");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Keyed State 类型
     * ========================================
     */
    public static void explainKeyedStateTypes() {
        System.out.println("=== Keyed State 类型 ===");
        System.out.println();

        System.out.println("【1. ValueState - 单值状态】");
        System.out.println("  存储单个值，最常用");
        System.out.println("```java");
        System.out.println("public class CountFunction extends KeyedProcessFunction<String, Event, Long> {");
        System.out.println("    private ValueState<Long> countState;");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void open(OpenContext ctx) {");
        System.out.println("        // 定义状态描述符");
        System.out.println("        ValueStateDescriptor<Long> descriptor = ");
        System.out.println("            new ValueStateDescriptor<>(\"count\", Long.class);");
        System.out.println("        countState = getRuntimeContext().getState(descriptor);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void processElement(Event event, Context ctx, Collector<Long> out) {");
        System.out.println("        Long count = countState.value();  // 读取");
        System.out.println("        if (count == null) count = 0L;");
        System.out.println("        count++;");
        System.out.println("        countState.update(count);         // 更新");
        System.out.println("        out.collect(count);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【2. ListState - 列表状态】");
        System.out.println("  存储列表，可追加元素");
        System.out.println("```java");
        System.out.println("private ListState<Event> eventListState;");
        System.out.println();
        System.out.println("@Override");
        System.out.println("public void open(OpenContext ctx) {");
        System.out.println("    ListStateDescriptor<Event> descriptor = ");
        System.out.println("        new ListStateDescriptor<>(\"events\", Event.class);");
        System.out.println("    eventListState = getRuntimeContext().getListState(descriptor);");
        System.out.println("}");
        System.out.println();
        System.out.println("// 使用");
        System.out.println("eventListState.add(event);           // 添加单个");
        System.out.println("eventListState.addAll(events);       // 添加多个");
        System.out.println("Iterable<Event> all = eventListState.get();  // 获取全部");
        System.out.println("eventListState.update(newList);      // 替换");
        System.out.println("eventListState.clear();              // 清空");
        System.out.println("```");
        System.out.println();

        System.out.println("【3. MapState - Map 状态】");
        System.out.println("  存储 Key-Value 映射");
        System.out.println("```java");
        System.out.println("private MapState<String, Integer> userScoreState;");
        System.out.println();
        System.out.println("@Override");
        System.out.println("public void open(OpenContext ctx) {");
        System.out.println("    MapStateDescriptor<String, Integer> descriptor = ");
        System.out.println("        new MapStateDescriptor<>(\"userScore\", String.class, Integer.class);");
        System.out.println("    userScoreState = getRuntimeContext().getMapState(descriptor);");
        System.out.println("}");
        System.out.println();
        System.out.println("// 使用");
        System.out.println("userScoreState.put(\"user1\", 100);   // 添加/更新");
        System.out.println("Integer score = userScoreState.get(\"user1\");  // 获取");
        System.out.println("boolean exists = userScoreState.contains(\"user1\");  // 检查");
        System.out.println("userScoreState.remove(\"user1\");     // 删除");
        System.out.println("Iterable<Map.Entry<String, Integer>> entries = userScoreState.entries();");
        System.out.println("```");
        System.out.println();

        System.out.println("【4. ReducingState / AggregatingState - 聚合状态】");
        System.out.println("  自动聚合，添加元素时执行聚合函数");
        System.out.println("```java");
        System.out.println("private ReducingState<Long> sumState;");
        System.out.println();
        System.out.println("@Override");
        System.out.println("public void open(OpenContext ctx) {");
        System.out.println("    ReducingStateDescriptor<Long> descriptor = ");
        System.out.println("        new ReducingStateDescriptor<>(\"sum\", Long::sum, Long.class);");
        System.out.println("    sumState = getRuntimeContext().getReducingState(descriptor);");
        System.out.println("}");
        System.out.println();
        System.out.println("// 使用");
        System.out.println("sumState.add(10L);  // 自动累加");
        System.out.println("Long sum = sumState.get();  // 获取当前和");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Operator State
     * ========================================
     */
    public static void explainOperatorState() {
        System.out.println("=== Operator State ===");
        System.out.println();

        System.out.println("【Operator State 使用场景】");
        System.out.println("  • Kafka Source: 存储 partition offset");
        System.out.println("  • 自定义 Source/Sink: 存储元数据");
        System.out.println();

        System.out.println("【实现 CheckpointedFunction】");
        System.out.println("```java");
        System.out.println("public class BufferingSink implements SinkFunction<Event>, ");
        System.out.println("                                       CheckpointedFunction {");
        System.out.println("    ");
        System.out.println("    private List<Event> buffer = new ArrayList<>();");
        System.out.println("    private ListState<Event> checkpointedState;");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void snapshotState(FunctionSnapshotContext context) {");
        System.out.println("        // Checkpoint 时保存状态");
        System.out.println("        checkpointedState.clear();");
        System.out.println("        checkpointedState.addAll(buffer);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void initializeState(FunctionInitializationContext context) {");
        System.out.println("        // 初始化/恢复状态");
        System.out.println("        ListStateDescriptor<Event> descriptor = ");
        System.out.println("            new ListStateDescriptor<>(\"buffer\", Event.class);");
        System.out.println("        checkpointedState = context.getOperatorStateStore()");
        System.out.println("            .getListState(descriptor);");
        System.out.println("        ");
        System.out.println("        // 从恢复的状态填充 buffer");
        System.out.println("        if (context.isRestored()) {");
        System.out.println("            for (Event event : checkpointedState.get()) {");
        System.out.println("                buffer.add(event);");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void invoke(Event event, Context context) {");
        System.out.println("        buffer.add(event);");
        System.out.println("        if (buffer.size() >= 100) {");
        System.out.println("            flush();");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【状态重分布策略】");
        System.out.println("  并行度变化时，Operator State 需要重分布");
        System.out.println("  • Even-split: 均匀分配给新并行实例");
        System.out.println("  • Union: 每个新实例获得全部状态");
    }

    /**
     * ========================================
     * 第五部分：状态 TTL
     * ========================================
     */
    public static void explainStateTTL() {
        System.out.println("=== 状态 TTL (生存时间) ===");
        System.out.println();

        System.out.println("【为什么需要 TTL】");
        System.out.println("  • 防止状态无限增长");
        System.out.println("  • 自动清理过期数据");
        System.out.println("  • 符合 GDPR 等数据合规要求");
        System.out.println();

        System.out.println("【TTL 配置】");
        System.out.println("```java");
        System.out.println("StateTtlConfig ttlConfig = StateTtlConfig");
        System.out.println("    .newBuilder(Time.hours(1))  // TTL = 1小时");
        System.out.println("    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)");
        System.out.println("    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("ValueStateDescriptor<String> descriptor = ");
        System.out.println("    new ValueStateDescriptor<>(\"myState\", String.class);");
        System.out.println("descriptor.enableTimeToLive(ttlConfig);  // 启用 TTL");
        System.out.println("```");
        System.out.println();

        System.out.println("【updateType - TTL 刷新时机】");
        System.out.println("  • OnCreateAndWrite: 创建和写入时刷新（默认）");
        System.out.println("  • OnReadAndWrite: 读和写都刷新");
        System.out.println();

        System.out.println("【stateVisibility - 过期数据可见性】");
        System.out.println("  • NeverReturnExpired: 不返回过期数据（默认）");
        System.out.println("  • ReturnExpiredIfNotCleanedUp: 可能返回未清理的过期数据");
        System.out.println();

        System.out.println("【清理策略】");
        System.out.println("```java");
        System.out.println("StateTtlConfig ttlConfig = StateTtlConfig");
        System.out.println("    .newBuilder(Time.hours(1))");
        System.out.println("    .cleanupFullSnapshot()        // Checkpoint 时清理");
        System.out.println("    .cleanupIncrementally(10, true) // 增量清理");
        System.out.println("    .cleanupInRocksdbCompactFilter(1000) // RocksDB 压缩时清理");
        System.out.println("    .build();");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：Broadcast State
     * ========================================
     */
    public static void explainBroadcastState() {
        System.out.println("=== Broadcast State (广播状态) ===");
        System.out.println();

        System.out.println("【使用场景】");
        System.out.println("  • 规则引擎：动态更新规则");
        System.out.println("  • 配置分发：实时配置变更");
        System.out.println("  • 维表 Join：小表广播");
        System.out.println();

        System.out.println("【实现示例 - 动态规则匹配】");
        System.out.println("```java");
        System.out.println("// 1. 定义广播流");
        System.out.println("MapStateDescriptor<String, Rule> ruleStateDescriptor =");
        System.out.println("    new MapStateDescriptor<>(\"rules\", String.class, Rule.class);");
        System.out.println();
        System.out.println("BroadcastStream<Rule> ruleBroadcast = ruleStream");
        System.out.println("    .broadcast(ruleStateDescriptor);");
        System.out.println();
        System.out.println("// 2. 连接主流和广播流");
        System.out.println("eventStream.connect(ruleBroadcast)");
        System.out.println("    .process(new BroadcastProcessFunction<Event, Rule, Alert>() {");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void processBroadcastElement(Rule rule, Context ctx,");
        System.out.println("                Collector<Alert> out) {");
        System.out.println("            // 更新规则状态");
        System.out.println("            ctx.getBroadcastState(ruleStateDescriptor)");
        System.out.println("               .put(rule.getId(), rule);");
        System.out.println("        }");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void processElement(Event event,");
        System.out.println("                ReadOnlyContext ctx, Collector<Alert> out) {");
        System.out.println("            // 只读访问规则状态");
        System.out.println("            ReadOnlyBroadcastState<String, Rule> state = ");
        System.out.println("                ctx.getBroadcastState(ruleStateDescriptor);");
        System.out.println("            ");
        System.out.println("            for (Map.Entry<String, Rule> entry : state.immutableEntries()) {");
        System.out.println("                if (entry.getValue().matches(event)) {");
        System.out.println("                    out.collect(new Alert(event, entry.getValue()));");
        System.out.println("                }");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    });");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink 状态编程                        ║");
        System.out.println("║          Flink 的核心能力                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainStateConcept();
        System.out.println();

        explainStateTypes();
        System.out.println();

        explainKeyedStateTypes();
        System.out.println();

        explainOperatorState();
        System.out.println();

        explainStateTTL();
        System.out.println();

        explainBroadcastState();
    }
}
