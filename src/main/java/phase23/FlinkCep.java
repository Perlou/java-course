package phase23;

/**
 * Flink CEP - 复杂事件处理
 * 
 * CEP (Complex Event Processing) 用于从事件流中检测复杂的事件模式。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkCep {

    /**
     * ========================================
     * 第一部分：CEP 概述
     * ========================================
     */
    public static void explainCepConcept() {
        System.out.println("=== CEP 概述 ===");
        System.out.println();

        System.out.println("【什么是 CEP】");
        System.out.println("  Complex Event Processing 复杂事件处理");
        System.out.println("  从事件流中识别符合特定模式的事件序列");
        System.out.println();

        System.out.println("【典型应用场景】");
        System.out.println("  • 金融: 欺诈检测、异常交易");
        System.out.println("  • 网络: 入侵检测、DDoS 攻击");
        System.out.println("  • IoT: 设备故障预警");
        System.out.println("  • 电商: 用户行为分析");
        System.out.println("  • 物流: 异常配送检测");
        System.out.println();

        System.out.println("【示例场景】");
        System.out.println("  检测\"同一用户 5 分钟内连续登录失败 3 次\"");
        System.out.println("```");
        System.out.println("  用户A: [失败] → [失败] → [失败]  ← 触发告警!");
        System.out.println("              └─── 5分钟内 ───┘");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：CEP 基本用法
     * ========================================
     */
    public static void explainBasicUsage() {
        System.out.println("=== CEP 基本用法 ===");
        System.out.println();

        System.out.println("【CEP 三步骤】");
        System.out.println("  1. 定义 Pattern (模式)");
        System.out.println("  2. 应用 Pattern 到流");
        System.out.println("  3. 处理匹配结果");
        System.out.println();

        System.out.println("【完整示例 - 连续登录失败检测】");
        System.out.println("```java");
        System.out.println("// 输入事件流");
        System.out.println("DataStream<LoginEvent> loginStream = ...;");
        System.out.println();
        System.out.println("// 1. 定义 Pattern");
        System.out.println("Pattern<LoginEvent, ?> pattern = Pattern");
        System.out.println("    .<LoginEvent>begin(\"first\")");
        System.out.println("    .where(SimpleCondition.of(e -> e.getStatus().equals(\"fail\")))");
        System.out.println("    .next(\"second\")");
        System.out.println("    .where(SimpleCondition.of(e -> e.getStatus().equals(\"fail\")))");
        System.out.println("    .next(\"third\")");
        System.out.println("    .where(SimpleCondition.of(e -> e.getStatus().equals(\"fail\")))");
        System.out.println("    .within(Time.minutes(5));  // 时间约束");
        System.out.println();
        System.out.println("// 2. 应用 Pattern");
        System.out.println("PatternStream<LoginEvent> patternStream = CEP.pattern(");
        System.out.println("    loginStream.keyBy(LoginEvent::getUserId),");
        System.out.println("    pattern);");
        System.out.println();
        System.out.println("// 3. 处理匹配结果");
        System.out.println("DataStream<Alert> alerts = patternStream.process(");
        System.out.println("    new PatternProcessFunction<LoginEvent, Alert>() {");
        System.out.println("        @Override");
        System.out.println("        public void processMatch(Map<String, List<LoginEvent>> match,");
        System.out.println("                Context ctx, Collector<Alert> out) {");
        System.out.println("            ");
        System.out.println("            LoginEvent first = match.get(\"first\").get(0);");
        System.out.println("            LoginEvent third = match.get(\"third\").get(0);");
        System.out.println("            ");
        System.out.println("            out.collect(new Alert(");
        System.out.println("                first.getUserId(),");
        System.out.println("                \"连续登录失败3次\",");
        System.out.println("                first.getTimestamp(),");
        System.out.println("                third.getTimestamp()));");
        System.out.println("        }");
        System.out.println("    });");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Pattern 类型
     * ========================================
     */
    public static void explainPatternTypes() {
        System.out.println("=== Pattern 类型 ===");
        System.out.println();

        System.out.println("【单个模式 (Individual Pattern)】");
        System.out.println("  定义对单个事件的匹配条件");
        System.out.println("```java");
        System.out.println("// 匹配失败的登录事件");
        System.out.println("Pattern.<LoginEvent>begin(\"failed\")");
        System.out.println("    .where(SimpleCondition.of(e -> \"fail\".equals(e.getStatus())));");
        System.out.println("```");
        System.out.println();

        System.out.println("【组合模式 (Combining Pattern)】");
        System.out.println("  多个单个模式的组合");
        System.out.println();

        System.out.println("• next - 严格紧邻（中间无其他匹配事件）");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).next(\"B\").where(...);");
        System.out.println("// 必须: A 紧接着 B");
        System.out.println("```");
        System.out.println();

        System.out.println("• followedBy - 宽松近邻（中间可有其他事件）");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).followedBy(\"B\").where(...);");
        System.out.println("// A 之后出现 B，中间可有其他事件");
        System.out.println("```");
        System.out.println();

        System.out.println("• followedByAny - 非确定性宽松近邻");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).followedByAny(\"B\").where(...);");
        System.out.println("// 每个 A 后面的每个 B 都匹配");
        System.out.println("```");
        System.out.println();

        System.out.println("• notNext / notFollowedBy - 否定模式");
        System.out.println("```java");
        System.out.println("// A 之后不能紧跟 B");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).notNext(\"B\");");
        System.out.println();
        System.out.println("// A 之后不能出现 B（到超时或匹配结束）");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).notFollowedBy(\"B\").within(Time.minutes(5));");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：量词
     * ========================================
     */
    public static void explainQuantifiers() {
        System.out.println("=== 量词 (Quantifiers) ===");
        System.out.println();

        System.out.println("【循环模式】");
        System.out.println();

        System.out.println("• times(n) - 恰好 n 次");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).times(3);");
        System.out.println("// 恰好匹配 3 个连续 A");
        System.out.println("```");
        System.out.println();

        System.out.println("• times(n, m) - n 到 m 次");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).times(2, 4);");
        System.out.println("// 匹配 2-4 个连续 A");
        System.out.println("```");
        System.out.println();

        System.out.println("• oneOrMore - 一次或多次");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).oneOrMore();");
        System.out.println("// 匹配 1 个或多个 A");
        System.out.println("```");
        System.out.println();

        System.out.println("• timesOrMore(n) - 至少 n 次");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).timesOrMore(3);");
        System.out.println("// 匹配至少 3 个 A");
        System.out.println("```");
        System.out.println();

        System.out.println("• optional - 可选");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).optional();");
        System.out.println("// A 可有可无");
        System.out.println("```");
        System.out.println();

        System.out.println("• greedy - 贪婪匹配");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"A\").where(...).oneOrMore().greedy();");
        System.out.println("// 尽可能多地匹配 A");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：条件
     * ========================================
     */
    public static void explainConditions() {
        System.out.println("=== 条件 (Conditions) ===");
        System.out.println();

        System.out.println("【SimpleCondition - 简单条件】");
        System.out.println("  只依赖当前事件");
        System.out.println("```java");
        System.out.println("pattern.where(SimpleCondition.of(event -> event.getAmount() > 1000));");
        System.out.println("```");
        System.out.println();

        System.out.println("【IterativeCondition - 迭代条件】");
        System.out.println("  可以访问已匹配的事件");
        System.out.println("```java");
        System.out.println("pattern.where(new IterativeCondition<Event>() {");
        System.out.println("    @Override");
        System.out.println("    public boolean filter(Event current, Context<Event> ctx) {");
        System.out.println("        // 访问已匹配的事件");
        System.out.println("        List<Event> previous = ctx.getEventsForPattern(\"first\");");
        System.out.println("        double sum = previous.stream().mapToDouble(Event::getAmount).sum();");
        System.out.println("        return current.getAmount() + sum > 5000;");
        System.out.println("    }");
        System.out.println("});");
        System.out.println("```");
        System.out.println();

        System.out.println("【组合条件】");
        System.out.println("```java");
        System.out.println("// AND");
        System.out.println("pattern.where(...).where(...);  // 默认 AND");
        System.out.println();
        System.out.println("// OR");
        System.out.println("pattern.where(...).or(...);");
        System.out.println();
        System.out.println("// subtype - 类型过滤");
        System.out.println("pattern.subtype(HighValueOrder.class).where(...);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：超时处理
     * ========================================
     */
    public static void explainTimeout() {
        System.out.println("=== 超时处理 ===");
        System.out.println();

        System.out.println("【within - 时间约束】");
        System.out.println("```java");
        System.out.println("Pattern.<Event>begin(\"start\").where(...)");
        System.out.println("    .followedBy(\"end\").where(...)");
        System.out.println("    .within(Time.minutes(10));  // 10分钟内完成匹配");
        System.out.println("```");
        System.out.println();

        System.out.println("【处理超时 - 侧输出】");
        System.out.println("```java");
        System.out.println("OutputTag<Event> timeoutTag = new OutputTag<>(\"timeout\") {};");
        System.out.println();
        System.out.println("SingleOutputStreamOperator<Alert> result = patternStream.process(");
        System.out.println("    new PatternProcessFunction<Event, Alert>() {");
        System.out.println("        @Override");
        System.out.println("        public void processMatch(Map<String, List<Event>> match,");
        System.out.println("                Context ctx, Collector<Alert> out) {");
        System.out.println("            // 正常匹配处理");
        System.out.println("            out.collect(new Alert(\"完整匹配\", match));");
        System.out.println("        }");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void processTimedOutMatch(Map<String, List<Event>> match,");
        System.out.println("                Context ctx) {");
        System.out.println("            // 超时处理");
        System.out.println("            Event start = match.get(\"start\").get(0);");
        System.out.println("            ctx.output(timeoutTag, start);");
        System.out.println("        }");
        System.out.println("    });");
        System.out.println();
        System.out.println("// 获取超时流");
        System.out.println("DataStream<Event> timeoutStream = result.getSideOutput(timeoutTag);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第七部分：实战案例
     * ========================================
     */
    public static void explainUseCases() {
        System.out.println("=== 实战案例 ===");
        System.out.println();

        System.out.println("【案例1: 信用卡盗刷检测】");
        System.out.println("  规则: 同一用户 10 分钟内在不同城市消费");
        System.out.println("```java");
        System.out.println("Pattern<Transaction, ?> pattern = Pattern");
        System.out.println("    .<Transaction>begin(\"first\")");
        System.out.println("    .followedBy(\"second\")");
        System.out.println("    .where(new IterativeCondition<Transaction>() {");
        System.out.println("        @Override");
        System.out.println("        public boolean filter(Transaction current, Context<Transaction> ctx) {");
        System.out.println("            Transaction first = ctx.getEventsForPattern(\"first\").get(0);");
        System.out.println("            return !first.getCity().equals(current.getCity());");
        System.out.println("        }");
        System.out.println("    })");
        System.out.println("    .within(Time.minutes(10));");
        System.out.println("```");
        System.out.println();

        System.out.println("【案例2: 订单超时检测】");
        System.out.println("  规则: 下单后 30 分钟内未支付");
        System.out.println("```java");
        System.out.println("Pattern<OrderEvent, ?> pattern = Pattern");
        System.out.println("    .<OrderEvent>begin(\"create\")");
        System.out.println("    .where(SimpleCondition.of(e -> e.getType().equals(\"CREATE\")))");
        System.out.println("    .notFollowedBy(\"pay\")");
        System.out.println("    .where(SimpleCondition.of(e -> e.getType().equals(\"PAY\")))");
        System.out.println("    .within(Time.minutes(30));");
        System.out.println("```");
        System.out.println();

        System.out.println("【案例3: 价格异常波动】");
        System.out.println("  规则: 连续 3 次价格变化超过 10%");
        System.out.println("```java");
        System.out.println("Pattern<PriceEvent, ?> pattern = Pattern");
        System.out.println("    .<PriceEvent>begin(\"first\")");
        System.out.println("    .next(\"second\")");
        System.out.println("    .where(new IterativeCondition<PriceEvent>() {");
        System.out.println("        @Override");
        System.out.println("        public boolean filter(PriceEvent current, Context<PriceEvent> ctx) {");
        System.out.println("            PriceEvent prev = ctx.getEventsForPattern(\"first\").get(0);");
        System.out
                .println("            return Math.abs(current.getPrice() - prev.getPrice()) / prev.getPrice() > 0.1;");
        System.out.println("        }");
        System.out.println("    })");
        System.out.println("    .next(\"third\")");
        System.out.println("    .where(...)  // 类似条件");
        System.out.println("    .within(Time.minutes(5));");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink CEP                             ║");
        System.out.println("║          复杂事件处理                                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainCepConcept();
        System.out.println();

        explainBasicUsage();
        System.out.println();

        explainPatternTypes();
        System.out.println();

        explainQuantifiers();
        System.out.println();

        explainConditions();
        System.out.println();

        explainTimeout();
        System.out.println();

        explainUseCases();
    }
}
