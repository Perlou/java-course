package phase07;

/**
 * Phase 7 - Lesson 8: 模板方法与责任链模式
 * 
 * 🎯 学习目标:
 * 1. 理解模板方法模式
 * 2. 理解责任链模式
 * 3. 学会在实际场景中应用
 */
public class TemplateChainDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 8: 模板方法与责任链模式");
        System.out.println("=".repeat(60));

        // ==================== 模板方法模式 ====================

        System.out.println("\n【模板方法模式 (Template Method)】");
        System.out.println("""
                意图: 定义算法骨架，将某些步骤延迟到子类

                结构:
                - 抽象类定义模板方法 (算法骨架)
                - 具体步骤由子类实现
                - 钩子方法 (Hook) 提供可选扩展点

                应用场景:
                - 固定流程，细节不同
                - 框架设计
                - 代码复用
                """);

        // 演示模板方法 - 饮料制作
        System.out.println("饮料制作模板:");

        Beverage coffee = new CoffeeBeverage();
        coffee.prepare();

        System.out.println();

        Beverage tea = new TeaBeverage();
        tea.prepare();

        // 带钩子方法
        System.out.println("\n带钩子方法的模板:");

        DataProcessor csvProcessor = new CsvProcessor();
        csvProcessor.process("data.csv");

        System.out.println();

        DataProcessor jsonProcessor = new JsonProcessor();
        jsonProcessor.process("data.json");

        // Java 中的模板方法
        System.out.println("\nJava 中的模板方法:");
        System.out.println("""
                1. HttpServlet
                   doGet(), doPost() 是需要子类实现的方法
                   service() 方法是模板

                2. InputStream
                   read(byte[]) 调用 read() 抽象方法

                3. AbstractList
                   add(), get() 待实现
                   addAll() 等是模板方法
                """);

        // ==================== 责任链模式 ====================

        System.out.println("=".repeat(60));
        System.out.println("\n【责任链模式 (Chain of Responsibility)】");
        System.out.println("""
                意图: 将请求沿着处理者链传递，直到有处理者处理它

                结构:
                Handler1 → Handler2 → Handler3 → ...

                特点:
                - 解耦请求发送者和接收者
                - 可动态组合处理链
                - 请求可能不被处理

                应用场景:
                - 审批流程
                - 过滤器/拦截器
                - 日志级别处理
                """);

        // 演示责任链 - 审批流程
        System.out.println("请假审批流程:");

        // 构建责任链
        Approver manager = new Manager();
        Approver director = new Director();
        Approver ceo = new CEO();

        manager.setNext(director);
        director.setNext(ceo);

        // 处理请求
        manager.approve(new LeaveRequest("张三", 1));
        manager.approve(new LeaveRequest("李四", 5));
        manager.approve(new LeaveRequest("王五", 15));
        manager.approve(new LeaveRequest("赵六", 60));

        // 责任链 - 过滤器
        System.out.println("\n请求过滤器链:");

        Filter authFilter = new AuthFilter();
        Filter logFilter = new LogFilter();
        Filter validationFilter = new ValidationFilter();

        authFilter.setNext(logFilter);
        logFilter.setNext(validationFilter);

        Request request1 = new Request("/api/users", "token123");
        authFilter.doFilter(request1);

        System.out.println();

        Request request2 = new Request("/api/admin", null); // 无 token
        authFilter.doFilter(request2);

        // Java 中的责任链
        System.out.println("\nJava 中的责任链:");
        System.out.println("""
                1. Servlet Filter
                   FilterChain.doFilter()

                2. Spring Interceptor
                   HandlerInterceptor 链

                3. Java 异常处理
                   try-catch 链

                4. java.util.logging
                   Logger → Handler 链
                """);

        // 模式对比
        System.out.println("=".repeat(60));
        System.out.println("【模板方法 vs 策略模式】");
        System.out.println("""
                ┌────────────┬─────────────────────┬─────────────────────┐
                │   方面     │    模板方法          │     策略模式         │
                ├────────────┼─────────────────────┼─────────────────────┤
                │  实现方式  │ 继承                │ 组合                 │
                │  变化部分  │ 某些步骤            │ 整个算法             │
                │  控制权    │ 父类控制流程        │ 客户端控制策略       │
                │  扩展方式  │ 重写方法            │ 实现接口             │
                └────────────┴─────────────────────┴─────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 模板方法: 定义骨架，子类实现细节");
        System.out.println("💡 责任链: 请求沿链传递，找到合适处理者");
        System.out.println("💡 两者都是解决不同变化点的行为型模式");
        System.out.println("=".repeat(60));
    }
}

// ==================== 模板方法模式 - 饮料 ====================

abstract class Beverage {
    // 模板方法，定义算法骨架
    public final void prepare() {
        boilWater();
        brew(); // 抽象方法，子类实现
        pourInCup();
        if (customerWantsCondiments()) { // 钩子方法
            addCondiments(); // 抽象方法
        }
    }

    private void boilWater() {
        System.out.println("  1. 煮水");
    }

    protected abstract void brew();

    private void pourInCup() {
        System.out.println("  3. 倒入杯中");
    }

    protected abstract void addCondiments();

    // 钩子方法，子类可选择重写
    protected boolean customerWantsCondiments() {
        return true;
    }
}

class CoffeeBeverage extends Beverage {
    @Override
    protected void brew() {
        System.out.println("  2. 冲泡咖啡");
    }

    @Override
    protected void addCondiments() {
        System.out.println("  4. 加糖和牛奶");
    }
}

class TeaBeverage extends Beverage {
    @Override
    protected void brew() {
        System.out.println("  2. 浸泡茶叶");
    }

    @Override
    protected void addCondiments() {
        System.out.println("  4. 加柠檬");
    }

    @Override
    protected boolean customerWantsCondiments() {
        return false; // 茶不加配料
    }
}

// ==================== 模板方法模式 - 数据处理 ====================

abstract class DataProcessor {
    public final void process(String filename) {
        System.out.println("处理文件: " + filename);
        String data = readData(filename);
        if (shouldValidate()) { // 钩子
            validate(data);
        }
        processData(data);
        saveResult();
    }

    protected abstract String readData(String filename);

    protected abstract void processData(String data);

    protected void validate(String data) {
        System.out.println("  验证数据...");
    }

    protected void saveResult() {
        System.out.println("  保存结果");
    }

    // 钩子方法
    protected boolean shouldValidate() {
        return true;
    }
}

class CsvProcessor extends DataProcessor {
    @Override
    protected String readData(String filename) {
        System.out.println("  读取 CSV 文件");
        return "csv-data";
    }

    @Override
    protected void processData(String data) {
        System.out.println("  解析 CSV 数据");
    }
}

class JsonProcessor extends DataProcessor {
    @Override
    protected String readData(String filename) {
        System.out.println("  读取 JSON 文件");
        return "json-data";
    }

    @Override
    protected void processData(String data) {
        System.out.println("  解析 JSON 数据");
    }

    @Override
    protected boolean shouldValidate() {
        return false; // JSON 不需要额外验证
    }
}

// ==================== 责任链模式 - 审批 ====================

class LeaveRequest {
    private final String name;
    private final int days;

    public LeaveRequest(String name, int days) {
        this.name = name;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public int getDays() {
        return days;
    }
}

abstract class Approver {
    protected Approver next;

    public void setNext(Approver next) {
        this.next = next;
    }

    public abstract void approve(LeaveRequest request);
}

class Manager extends Approver {
    @Override
    public void approve(LeaveRequest request) {
        if (request.getDays() <= 3) {
            System.out.println("经理批准 " + request.getName() + " 的 " + request.getDays() + " 天假期");
        } else if (next != null) {
            next.approve(request);
        }
    }
}

class Director extends Approver {
    @Override
    public void approve(LeaveRequest request) {
        if (request.getDays() <= 10) {
            System.out.println("总监批准 " + request.getName() + " 的 " + request.getDays() + " 天假期");
        } else if (next != null) {
            next.approve(request);
        }
    }
}

class CEO extends Approver {
    @Override
    public void approve(LeaveRequest request) {
        if (request.getDays() <= 30) {
            System.out.println("CEO 批准 " + request.getName() + " 的 " + request.getDays() + " 天假期");
        } else {
            System.out.println("拒绝 " + request.getName() + " 的 " + request.getDays() + " 天假期请求");
        }
    }
}

// ==================== 责任链模式 - 过滤器 ====================

class Request {
    private final String path;
    private final String token;

    public Request(String path, String token) {
        this.path = path;
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public String getToken() {
        return token;
    }
}

abstract class Filter {
    protected Filter next;

    public void setNext(Filter next) {
        this.next = next;
    }

    public abstract void doFilter(Request request);

    protected void passToNext(Request request) {
        if (next != null) {
            next.doFilter(request);
        } else {
            System.out.println("  [最终] 请求处理完成");
        }
    }
}

class AuthFilter extends Filter {
    @Override
    public void doFilter(Request request) {
        System.out.println("  [认证过滤器] 检查 token");
        if (request.getToken() == null || request.getToken().isEmpty()) {
            System.out.println("  [认证过滤器] ❌ 未授权，拒绝请求");
            return;
        }
        System.out.println("  [认证过滤器] ✅ 通过");
        passToNext(request);
    }
}

class LogFilter extends Filter {
    @Override
    public void doFilter(Request request) {
        System.out.println("  [日志过滤器] 记录请求: " + request.getPath());
        passToNext(request);
    }
}

class ValidationFilter extends Filter {
    @Override
    public void doFilter(Request request) {
        System.out.println("  [验证过滤器] 验证请求参数");
        passToNext(request);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 模板方法模式:
 * - 定义算法骨架
 * - 子类实现具体步骤
 * - 钩子方法提供扩展点
 * 
 * 责任链模式:
 * - 请求沿链传递
 * - 解耦发送者和处理者
 * - 可动态组合链
 * 
 * 🏃 练习:
 * 1. 用模板方法实现数据导出功能
 * 2. 实现一个日志级别处理的责任链
 * 3. 分析 Servlet Filter 的责任链实现
 */
