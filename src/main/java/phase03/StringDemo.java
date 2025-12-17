package phase03;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Phase 3 - Lesson 1: String 与 StringBuilder
 * 
 * 🎯 学习目标:
 * 1. 深入理解 String 的不可变性
 * 2. 掌握常用字符串操作方法
 * 3. 学会使用 StringBuilder 和 StringBuffer
 * 4. 理解字符串池的工作原理
 */
public class StringDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 1: String 与 StringBuilder ===\n");

        // ==================== 1. String 的不可变性 ====================
        System.out.println("【1. String 的不可变性】");
        System.out.println("String 对象一旦创建，内容就不能修改");

        String s1 = "Hello";
        String s2 = s1;
        s1 = s1 + " World"; // 创建了新对象

        System.out.println("s1 = " + s1); // Hello World
        System.out.println("s2 = " + s2); // Hello (原对象未变)
        System.out.println("s1 == s2: " + (s1 == s2)); // false

        // ==================== 2. 字符串池 (String Pool) ====================
        System.out.println("\n【2. 字符串池 (String Pool)】");

        String a = "Java";
        String b = "Java";
        String c = new String("Java");
        String d = c.intern(); // 返回池中的引用

        System.out.println("a == b: " + (a == b)); // true (同一池中对象)
        System.out.println("a == c: " + (a == c)); // false (c在堆中)
        System.out.println("a == d: " + (a == d)); // true (intern返回池中对象)
        System.out.println("a.equals(c): " + a.equals(c)); // true (内容相同)

        // ==================== 3. 常用字符串方法 ====================
        System.out.println("\n【3. 常用字符串方法】");

        String str = "  Hello, Java World!  ";

        // 长度和访问
        System.out.println("原字符串: \"" + str + "\"");
        System.out.println("length(): " + str.length());
        System.out.println("charAt(2): '" + str.charAt(2) + "'");
        System.out.println("isEmpty(): " + str.isEmpty());
        System.out.println("isBlank(): " + str.isBlank()); // Java 11+

        // 查找
        System.out.println("\n查找方法:");
        System.out.println("indexOf('o'): " + str.indexOf('o'));
        System.out.println("lastIndexOf('o'): " + str.lastIndexOf('o'));
        System.out.println("contains(\"Java\"): " + str.contains("Java"));
        System.out.println("startsWith(\"  Hello\"): " + str.startsWith("  Hello"));
        System.out.println("endsWith(\"!  \"): " + str.endsWith("!  "));

        // 截取和替换
        System.out.println("\n截取和替换:");
        System.out.println("substring(8, 12): \"" + str.substring(8, 12) + "\"");
        System.out.println("replace('o', '0'): \"" + str.replace('o', '0') + "\"");
        System.out.println("replaceAll(\"\\\\s+\", \"-\"): \"" + str.replaceAll("\\s+", "-") + "\"");

        // 大小写和修剪
        System.out.println("\n大小写和修剪:");
        System.out.println("toUpperCase(): \"" + str.toUpperCase() + "\"");
        System.out.println("toLowerCase(): \"" + str.toLowerCase() + "\"");
        System.out.println("trim(): \"" + str.trim() + "\"");
        System.out.println("strip(): \"" + str.strip() + "\""); // Java 11+

        // ==================== 4. 字符串拆分和连接 ====================
        System.out.println("\n【4. 字符串拆分和连接】");

        // split
        String csv = "apple,banana,orange,grape";
        String[] fruits = csv.split(",");
        System.out.println("split(\",\"): " + Arrays.toString(fruits));

        // join
        String joined = String.join(" | ", fruits);
        System.out.println("join(\" | \"): " + joined);

        // StringJoiner
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        joiner.add("A").add("B").add("C");
        System.out.println("StringJoiner: " + joiner);

        // ==================== 5. StringBuilder ====================
        System.out.println("\n【5. StringBuilder】");
        System.out.println("StringBuilder 是可变的字符串，用于频繁修改");

        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        System.out.println("append: " + sb);

        sb.insert(6, "Java ");
        System.out.println("insert: " + sb);

        sb.delete(6, 11);
        System.out.println("delete: " + sb);

        sb.replace(6, 11, "Everyone");
        System.out.println("replace: " + sb);

        sb.reverse();
        System.out.println("reverse: " + sb);

        // ==================== 6. 性能对比 ====================
        System.out.println("\n【6. 性能对比】");

        int iterations = 10000;

        // String 拼接 (慢)
        long start = System.currentTimeMillis();
        String result = "";
        for (int i = 0; i < iterations; i++) {
            result += "a";
        }
        long stringTime = System.currentTimeMillis() - start;

        // StringBuilder (快)
        start = System.currentTimeMillis();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb2.append("a");
        }
        long builderTime = System.currentTimeMillis() - start;

        System.out.println("String 拼接 " + iterations + " 次: " + stringTime + "ms");
        System.out.println("StringBuilder " + iterations + " 次: " + builderTime + "ms");
        System.out.println("StringBuilder 快约 " + (stringTime / Math.max(1, builderTime)) + " 倍");

        // ==================== 7. 格式化字符串 ====================
        System.out.println("\n【7. 格式化字符串】");

        String name = "张三";
        int age = 25;
        double salary = 15000.5678;

        // String.format
        String formatted = String.format("姓名: %s, 年龄: %d, 薪资: %.2f", name, age, salary);
        System.out.println("String.format: " + formatted);

        // formatted (Java 15+)
        String formatted2 = "姓名: %s, 年龄: %d".formatted(name, age);
        System.out.println("formatted: " + formatted2);

        // 常用格式符
        System.out.println("\n常用格式符:");
        System.out.printf("  %%s - 字符串: %s%n", "hello");
        System.out.printf("  %%d - 整数: %d%n", 42);
        System.out.printf("  %%f - 浮点数: %f%n", 3.14159);
        System.out.printf("  %%.2f - 保留2位: %.2f%n", 3.14159);
        System.out.printf("  %%05d - 补零: %05d%n", 42);
        System.out.printf("  %%-10s - 左对齐: |%-10s|%n", "hi");

        // ==================== 8. 文本块 (Java 15+) ====================
        System.out.println("\n【8. 文本块 (Java 15+)】");

        String json = """
                {
                    "name": "张三",
                    "age": 25,
                    "skills": ["Java", "Python"]
                }
                """;
        System.out.println("JSON 文本块:\n" + json);

        String sql = """
                SELECT id, name, email
                FROM users
                WHERE status = 'active'
                ORDER BY created_at DESC
                """;
        System.out.println("SQL 文本块:\n" + sql);

        // ==================== 9. 正则表达式基础 ====================
        System.out.println("【9. 正则表达式基础】");

        String email = "test@example.com";
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.\\w+$";
        System.out.println(email + " 是有效邮箱: " + email.matches(emailPattern));

        String phone = "138-0013-8000";
        String cleaned = phone.replaceAll("[^0-9]", "");
        System.out.println("清理电话号码: " + phone + " -> " + cleaned);

        // Pattern 预编译 (高性能)
        Pattern pattern = Pattern.compile("\\d+");
        String text = "abc123def456ghi789";
        var matcher = pattern.matcher(text);
        System.out.print("提取数字: ");
        while (matcher.find()) {
            System.out.print(matcher.group() + " ");
        }
        System.out.println();

        System.out.println("\n✅ Phase 3 - Lesson 1 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. String 是不可变的，每次修改都创建新对象
 * 2. 字符串池优化：相同字面量共享同一对象
 * 3. StringBuilder: 可变，非线程安全，高性能
 * 4. StringBuffer: 可变，线程安全，稍慢
 * 5. 常用方法: length, charAt, indexOf, substring, split, join
 * 6. 格式化: String.format, printf, formatted
 * 7. 文本块: 多行字符串更清晰 (Java 15+)
 * 
 * 🏃 练习:
 * 1. 实现一个字符串反转方法
 * 2. 编写邮箱验证正则表达式
 * 3. 使用 StringBuilder 构建 HTML 表格
 */
