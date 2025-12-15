package phase01;

/**
 * Phase 1 - Lesson 3: 运算符详解
 * 
 * 🎯 学习目标:
 * 1. 掌握算术、关系、逻辑运算符
 * 2. 理解位运算符的使用
 * 3. 掌握赋值和三元运算符
 * 4. 了解运算符优先级
 */
public class OperatorsDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 3: 运算符详解 ===\n");

        // ==================== 1. 算术运算符 ====================
        System.out.println("【1. 算术运算符】");

        int a = 17, b = 5;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b = " + (a + b)); // 加法
        System.out.println("a - b = " + (a - b)); // 减法
        System.out.println("a * b = " + (a * b)); // 乘法
        System.out.println("a / b = " + (a / b)); // 整数除法（取整）
        System.out.println("a % b = " + (a % b)); // 取余（模运算）

        // 浮点数除法
        System.out.println("17.0 / 5 = " + (17.0 / 5)); // 浮点除法

        // 自增自减
        System.out.println("\n自增自减运算符:");
        int x = 10;
        System.out.println("x = " + x);
        System.out.println("x++ = " + (x++)); // 先使用，后加1
        System.out.println("x 现在 = " + x);
        System.out.println("++x = " + (++x)); // 先加1，后使用
        System.out.println("x 现在 = " + x);

        // ==================== 2. 关系运算符 ====================
        System.out.println("\n【2. 关系运算符】");

        int m = 10, n = 20;
        System.out.println("m = " + m + ", n = " + n);
        System.out.println("m == n: " + (m == n)); // 等于
        System.out.println("m != n: " + (m != n)); // 不等于
        System.out.println("m > n: " + (m > n)); // 大于
        System.out.println("m < n: " + (m < n)); // 小于
        System.out.println("m >= n: " + (m >= n)); // 大于等于
        System.out.println("m <= n: " + (m <= n)); // 小于等于

        // ==================== 3. 逻辑运算符 ====================
        System.out.println("\n【3. 逻辑运算符】");

        boolean p = true, q = false;
        System.out.println("p = " + p + ", q = " + q);
        System.out.println("p && q (逻辑与): " + (p && q));
        System.out.println("p || q (逻辑或): " + (p || q));
        System.out.println("!p (逻辑非): " + (!p));

        // 短路运算
        System.out.println("\n短路运算演示:");
        int num = 5;
        // && 短路: 如果左边为false，右边不执行
        boolean result1 = (num > 10) && (++num > 0);
        System.out.println("(num > 10) && (++num > 0): num = " + num + " (右边未执行，num未变)");

        // || 短路: 如果左边为true，右边不执行
        boolean result2 = (num < 10) || (++num > 0);
        System.out.println("(num < 10) || (++num > 0): num = " + num + " (右边未执行，num未变)");

        // ==================== 4. 位运算符 ====================
        System.out.println("\n【4. 位运算符】");

        int bit1 = 0b1010; // 二进制 10
        int bit2 = 0b1100; // 二进制 12

        System.out.println("bit1 = " + bit1 + " (二进制: " + Integer.toBinaryString(bit1) + ")");
        System.out.println("bit2 = " + bit2 + " (二进制: " + Integer.toBinaryString(bit2) + ")");

        System.out.println("bit1 & bit2 (按位与): " + (bit1 & bit2) +
                " (二进制: " + Integer.toBinaryString(bit1 & bit2) + ")");
        System.out.println("bit1 | bit2 (按位或): " + (bit1 | bit2) +
                " (二进制: " + Integer.toBinaryString(bit1 | bit2) + ")");
        System.out.println("bit1 ^ bit2 (按位异或): " + (bit1 ^ bit2) +
                " (二进制: " + Integer.toBinaryString(bit1 ^ bit2) + ")");
        System.out.println("~bit1 (按位取反): " + (~bit1));

        // 移位运算
        System.out.println("\n移位运算:");
        int shift = 8; // 二进制: 1000
        System.out.println(shift + " << 2 (左移): " + (shift << 2) + " (相当于 * 4)");
        System.out.println(shift + " >> 2 (右移): " + (shift >> 2) + " (相当于 / 4)");
        System.out.println("-8 >> 2 (算术右移): " + (-8 >> 2) + " (保留符号位)");
        System.out.println("-8 >>> 2 (逻辑右移): " + (-8 >>> 2) + " (高位补0)");

        // 位运算的实际应用
        System.out.println("\n位运算实际应用:");
        // 快速判断奇偶
        System.out.println("7 & 1 = " + (7 & 1) + " (奇数)");
        System.out.println("8 & 1 = " + (8 & 1) + " (偶数)");
        // 快速乘除2的幂
        System.out.println("5 << 3 = " + (5 << 3) + " (5 * 8)");
        System.out.println("40 >> 3 = " + (40 >> 3) + " (40 / 8)");

        // ==================== 5. 赋值运算符 ====================
        System.out.println("\n【5. 赋值运算符】");

        int v = 10;
        System.out.println("v = " + v);
        v += 5; // 等价于 v = v + 5
        System.out.println("v += 5: " + v);
        v -= 3; // 等价于 v = v - 3
        System.out.println("v -= 3: " + v);
        v *= 2; // 等价于 v = v * 2
        System.out.println("v *= 2: " + v);
        v /= 4; // 等价于 v = v / 4
        System.out.println("v /= 4: " + v);
        v %= 3; // 等价于 v = v % 3
        System.out.println("v %= 3: " + v);

        // 复合赋值的类型转换优势
        System.out.println("\n复合赋值的类型转换:");
        byte by = 10;
        // by = by + 5; // 编译错误！int 不能直接赋给 byte
        by += 5; // 正确！复合赋值自动类型转换
        System.out.println("byte by += 5: " + by);

        // ==================== 6. 三元运算符 ====================
        System.out.println("\n【6. 三元运算符】");

        int age = 20;
        String status = (age >= 18) ? "成年人" : "未成年人";
        System.out.println("age = " + age + ", 状态: " + status);

        // 嵌套三元运算符（不推荐，可读性差）
        int score = 85;
        String grade = (score >= 90) ? "优秀" : (score >= 80) ? "良好" : (score >= 60) ? "及格" : "不及格";
        System.out.println("score = " + score + ", 等级: " + grade);

        // ==================== 7. instanceof 运算符 ====================
        System.out.println("\n【7. instanceof 运算符】");

        String str = "Hello";
        Object obj = str;

        System.out.println("str instanceof String: " + (str instanceof String));
        System.out.println("obj instanceof String: " + (obj instanceof String));
        System.out.println("obj instanceof Object: " + (obj instanceof Object));

        // Java 16+ 模式匹配
        if (obj instanceof String s) {
            System.out.println("模式匹配: s.length() = " + s.length());
        }

        // ==================== 8. 运算符优先级 ====================
        System.out.println("\n【8. 运算符优先级】");
        System.out.println("优先级从高到低 (简化版):");
        System.out.println("1. () [] .                    - 括号、数组索引、成员访问");
        System.out.println("2. ++ -- ! ~                  - 一元运算符");
        System.out.println("3. * / %                      - 乘除取余");
        System.out.println("4. + -                        - 加减");
        System.out.println("5. << >> >>>                  - 移位");
        System.out.println("6. < <= > >= instanceof       - 关系");
        System.out.println("7. == !=                      - 相等性");
        System.out.println("8. &                          - 按位与");
        System.out.println("9. ^                          - 按位异或");
        System.out.println("10. |                         - 按位或");
        System.out.println("11. &&                        - 逻辑与");
        System.out.println("12. ||                        - 逻辑或");
        System.out.println("13. ?:                        - 三元");
        System.out.println("14. = += -= ...               - 赋值");

        System.out.println("\n建议: 使用括号明确优先级，提高可读性");

        System.out.println("\n✅ Phase 1 - Lesson 3 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 算术运算符: + - * / % ++ --
 * 2. 关系运算符: == != > < >= <=
 * 3. 逻辑运算符: && || ! (短路特性)
 * 4. 位运算符: & | ^ ~ << >> >>>
 * 5. 赋值运算符: = += -= *= /= %= 等
 * 6. 三元运算符: ? :
 * 7. instanceof: 类型检查
 * 
 * 🏃 练习:
 * 1. 使用位运算实现两个数交换（不用临时变量）
 * 2. 实现一个判断闰年的表达式
 * 3. 使用三元运算符实现 Math.max() 功能
 */
