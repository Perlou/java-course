package phase01;

/**
 * Phase 1 - Lesson 1: Hello World
 * 
 * 🎯 学习目标:
 * 1. 理解 Java 程序的基本结构
 * 2. 掌握 main 方法的作用
 * 3. 学会使用 System.out 输出
 * 
 * 📝 核心概念:
 * - Java 程序从 main 方法开始执行
 * - 类名必须与文件名一致
 * - Java 是强类型、面向对象的语言
 */
public class HelloWorld {
    
    /**
     * main 方法是 Java 程序的入口点
     * - public: 访问修饰符，表示可以从任何地方访问
     * - static: 静态方法，不需要创建对象就可以调用
     * - void: 返回类型，表示不返回任何值
     * - String[] args: 命令行参数
     */
    public static void main(String[] args) {
        // ==================== 1. 基础输出 ====================
        System.out.println("Hello, World!");
        System.out.println("欢迎来到 Java 世界！");
        
        // println vs print 的区别
        System.out.print("这是 print，");    // 不换行
        System.out.print("继续在同一行");
        System.out.println();                 // 手动换行
        System.out.println("这是 println，会自动换行");
        
        // ==================== 2. 格式化输出 ====================
        System.out.println("\n=== 格式化输出 ===");
        String name = "张三";
        int age = 25;
        double salary = 15000.50;
        
        // 使用 printf 进行格式化输出
        System.out.printf("姓名: %s, 年龄: %d, 薪资: %.2f%n", name, age, salary);
        
        // 常用格式化符号:
        // %s - 字符串
        // %d - 整数
        // %f - 浮点数
        // %n - 换行（跨平台）
        // %% - 输出 % 符号
        
        // ==================== 3. 字符串拼接 ====================
        System.out.println("\n=== 字符串拼接 ===");
        
        // 方式1: 使用 + 运算符
        System.out.println("姓名: " + name + ", 年龄: " + age);
        
        // 方式2: 使用 String.format()
        String info = String.format("姓名: %s, 年龄: %d", name, age);
        System.out.println(info);
        
        // 方式3: Java 15+ 文本块 (Text Blocks)
        String textBlock = """
            这是一个文本块
            可以跨越多行
            保持格式不变
            """;
        System.out.println(textBlock);
        
        // ==================== 4. 转义字符 ====================
        System.out.println("=== 转义字符 ===");
        System.out.println("换行符:\\n 效果如下:");
        System.out.println("第一行\n第二行");
        System.out.println("制表符:\\t 效果: A\tB\tC");
        System.out.println("引号: \"双引号\" 和 \'单引号\'");
        System.out.println("反斜杠: \\\\");
        
        // ==================== 5. 命令行参数 ====================
        System.out.println("\n=== 命令行参数 ===");
        if (args.length > 0) {
            System.out.println("收到 " + args.length + " 个命令行参数:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("args[" + i + "] = " + args[i]);
            }
        } else {
            System.out.println("没有收到命令行参数");
            System.out.println("尝试运行: java phase01.HelloWorld 参数1 参数2");
        }
        
        // ==================== 6. 程序信息 ====================
        System.out.println("\n=== Java 环境信息 ===");
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.println("操作系统: " + System.getProperty("os.name"));
        System.out.println("用户名: " + System.getProperty("user.name"));
        
        System.out.println("\n✅ Phase 1 - Lesson 1 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Java 程序结构:
 *    - package: 包声明（可选）
 *    - import: 导入语句（可选）
 *    - class: 类定义
 *    - main: 程序入口
 * 
 * 2. 命名规范:
 *    - 类名: 大驼峰 (HelloWorld)
 *    - 方法名/变量名: 小驼峰 (myVariable)
 *    - 常量: 全大写 (MAX_VALUE)
 *    - 包名: 全小写 (com.example)
 * 
 * 3. 运行方式:
 *    mvn compile
 *    mvn exec:java -Dexec.mainClass="phase01.HelloWorld"
 *    
 *    或者:
 *    cd src/main/java
 *    javac phase01/HelloWorld.java
 *    java phase01.HelloWorld
 * 
 * 🏃 练习:
 * 1. 修改程序，输出你自己的信息
 * 2. 尝试使用命令行参数传入你的名字
 * 3. 探索更多 System.getProperty() 可以获取的信息
 */
