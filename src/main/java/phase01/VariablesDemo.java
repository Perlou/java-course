package phase01;

/**
 * Phase 1 - Lesson 2: 变量与数据类型
 * 
 * 🎯 学习目标:
 * 1. 掌握 Java 的 8 种基本数据类型
 * 2. 理解变量的声明和初始化
 * 3. 了解基本类型和引用类型的区别
 * 4. 掌握类型转换规则
 */
public class VariablesDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 2: 变量与数据类型 ===\n");

        // ==================== 1. 整型 (4种) ====================
        System.out.println("【1. 整型】");

        // byte: 1字节，范围 -128 ~ 127
        byte b = 100;
        System.out.println("byte b = " + b + " (范围: -128 ~ 127)");

        // short: 2字节，范围 -32768 ~ 32767
        short s = 30000;
        System.out.println("short s = " + s + " (范围: -32768 ~ 32767)");

        // int: 4字节，范围约 ±21亿
        int i = 2_000_000_000; // 可以用下划线分隔数字，增加可读性
        System.out.println("int i = " + i + " (最常用的整数类型)");

        // long: 8字节，范围很大
        long l = 9_000_000_000_000_000_000L; // 注意要加 L 后缀
        System.out.println("long l = " + l + " (需要 L 后缀)");

        // ==================== 2. 浮点型 (2种) ====================
        System.out.println("\n【2. 浮点型】");

        // float: 4字节，约6-7位有效数字
        float f = 3.14159f; // 需要加 f 后缀
        System.out.println("float f = " + f + " (需要 f 后缀)");

        // double: 8字节，约15-16位有效数字
        double d = 3.141592653589793;
        System.out.println("double d = " + d + " (默认浮点类型)");

        // 浮点数的精度问题
        System.out.println("\n⚠️ 浮点数精度问题演示:");
        System.out.println("0.1 + 0.2 = " + (0.1 + 0.2)); // 不是 0.3！
        System.out.println("建议: 金融计算使用 BigDecimal");

        // ==================== 3. 字符型 ====================
        System.out.println("\n【3. 字符型】");

        // char: 2字节，存储单个字符
        char c1 = 'A';
        char c2 = '中';
        char c3 = 65; // 使用 ASCII/Unicode 码
        char c4 = '\u0041'; // 使用 Unicode 转义

        System.out.println("char c1 = '" + c1 + "' (字母)");
        System.out.println("char c2 = '" + c2 + "' (中文)");
        System.out.println("char c3 = '" + c3 + "' (ASCII 码 65)");
        System.out.println("char c4 = '" + c4 + "' (Unicode \\u0041)");
        System.out.println("'A' 的数值: " + (int) c1);

        // ==================== 4. 布尔型 ====================
        System.out.println("\n【4. 布尔型】");

        // boolean: 只有 true 和 false
        boolean isJavaFun = true;
        boolean isHard = false;

        System.out.println("isJavaFun = " + isJavaFun);
        System.out.println("isHard = " + isHard);
        System.out.println("逻辑运算: isJavaFun && !isHard = " + (isJavaFun && !isHard));

        // ==================== 5. 类型转换 ====================
        System.out.println("\n【5. 类型转换】");

        // 自动类型转换（小 → 大）
        System.out.println("自动类型转换 (隐式):");
        int intValue = 100;
        long longValue = intValue; // int → long
        float floatValue = longValue; // long → float
        double doubleValue = floatValue; // float → double
        System.out.printf("  int(%d) → long(%d) → float(%.1f) → double(%.1f)%n",
                intValue, longValue, floatValue, doubleValue);

        // 强制类型转换（大 → 小）
        System.out.println("强制类型转换 (显式):");
        double pi = 3.14159;
        int piInt = (int) pi; // 截断小数部分
        System.out.println("  (int) 3.14159 = " + piInt);

        // 注意溢出问题
        int bigInt = 130;
        byte smallByte = (byte) bigInt; // 溢出！
        System.out.println("  (byte) 130 = " + smallByte + " (溢出!)");

        // ==================== 6. 包装类 ====================
        System.out.println("\n【6. 包装类】");

        // 每个基本类型都有对应的包装类
        Integer intObj = 100; // 自动装箱
        int intPrimitive = intObj; // 自动拆箱

        System.out.println("Integer 最大值: " + Integer.MAX_VALUE);
        System.out.println("Integer 最小值: " + Integer.MIN_VALUE);
        System.out.println("字符串转整数: Integer.parseInt(\"123\") = " + Integer.parseInt("123"));
        System.out.println("整数转字符串: Integer.toString(456) = " + Integer.toString(456));

        // 包装类对照表
        System.out.println("\n包装类对照:");
        System.out.println("  byte    → Byte");
        System.out.println("  short   → Short");
        System.out.println("  int     → Integer");
        System.out.println("  long    → Long");
        System.out.println("  float   → Float");
        System.out.println("  double  → Double");
        System.out.println("  char    → Character");
        System.out.println("  boolean → Boolean");

        // ==================== 7. var 关键字 (Java 10+) ====================
        System.out.println("\n【7. var 关键字 (Java 10+)】");

        var message = "Hello"; // 编译器推断为 String
        var number = 42; // 编译器推断为 int
        var decimal = 3.14; // 编译器推断为 double

        System.out.println("var message = \"Hello\" → 类型: " + ((Object) message).getClass().getSimpleName());
        System.out.println("var number = 42 → 类型: int");
        System.out.println("var decimal = 3.14 → 类型: double");
        System.out.println("注意: var 只能用于局部变量，不能用于字段或参数");

        // ==================== 8. 常量 ====================
        System.out.println("\n【8. 常量】");

        final double PI = 3.14159;
        final int MAX_SIZE = 100;
        // PI = 3.14; // 编译错误：不能修改 final 变量

        System.out.println("final double PI = " + PI);
        System.out.println("final int MAX_SIZE = " + MAX_SIZE);
        System.out.println("常量命名规范: 全大写，下划线分隔");

        System.out.println("\n✅ Phase 1 - Lesson 2 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 8种基本数据类型:
 * 整型: byte(1), short(2), int(4), long(8)
 * 浮点: float(4), double(8)
 * 字符: char(2)
 * 布尔: boolean
 * 
 * 2. 类型转换:
 * - 自动转换: byte → short → int → long → float → double
 * - 强制转换: 大类型 → 小类型，可能丢失精度或溢出
 * 
 * 3. 默认值 (类成员变量):
 * - 数值类型: 0
 * - boolean: false
 * - 引用类型: null
 * - 局部变量必须初始化！
 * 
 * 🏃 练习:
 * 1. 尝试不同类型的极限值运算，观察溢出现象
 * 2. 使用 BigDecimal 解决 0.1 + 0.2 的精度问题
 * 3. 探索 Character 类的静态方法
 */
