package phase01;

/**
 * Phase 1 - Lesson 4: 条件语句
 * 
 * 🎯 学习目标:
 * 1. 掌握 if-else 条件语句
 * 2. 掌握 switch 语句（包括 Java 17 新特性）
 * 3. 理解条件语句的嵌套和组合
 */
public class ConditionDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 4: 条件语句 ===\n");

        // ==================== 1. if 语句 ====================
        System.out.println("【1. if 语句】");

        int score = 85;

        // 简单 if
        if (score >= 60) {
            System.out.println("score = " + score + ": 及格了！");
        }

        // if-else
        System.out.println("\nif-else 示例:");
        int age = 20;
        if (age >= 18) {
            System.out.println("age = " + age + ": 成年人");
        } else {
            System.out.println("age = " + age + ": 未成年人");
        }

        // if-else if-else
        System.out.println("\nif-else if-else 示例:");
        int grade = 78;
        String level;
        if (grade >= 90) {
            level = "优秀";
        } else if (grade >= 80) {
            level = "良好";
        } else if (grade >= 60) {
            level = "及格";
        } else {
            level = "不及格";
        }
        System.out.println("grade = " + grade + ", level = " + level);

        // ==================== 2. 嵌套 if ====================
        System.out.println("\n【2. 嵌套 if】");

        int num = 15;
        if (num > 0) {
            if (num % 2 == 0) {
                System.out.println(num + " 是正偶数");
            } else {
                System.out.println(num + " 是正奇数");
            }
        } else if (num < 0) {
            System.out.println(num + " 是负数");
        } else {
            System.out.println(num + " 是零");
        }

        // ==================== 3. 条件表达式组合 ====================
        System.out.println("\n【3. 条件表达式组合】");

        int year = 2024;
        int month = 2;

        // 判断闰年
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        System.out.println(year + " 年是闰年: " + isLeapYear);

        // 判断月份天数
        int days;
        if (month == 2) {
            days = isLeapYear ? 29 : 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            days = 30;
        } else {
            days = 31;
        }
        System.out.println(year + " 年 " + month + " 月有 " + days + " 天");

        // ==================== 4. 传统 switch 语句 ====================
        System.out.println("\n【4. 传统 switch 语句】");

        int dayOfWeek = 3;
        String dayName;

        switch (dayOfWeek) {
            case 1:
                dayName = "星期一";
                break;
            case 2:
                dayName = "星期二";
                break;
            case 3:
                dayName = "星期三";
                break;
            case 4:
                dayName = "星期四";
                break;
            case 5:
                dayName = "星期五";
                break;
            case 6:
                dayName = "星期六";
                break;
            case 7:
                dayName = "星期日";
                break;
            default:
                dayName = "无效";
        }
        System.out.println("dayOfWeek = " + dayOfWeek + " → " + dayName);

        // fall-through 现象（忘记 break）
        System.out.println("\nfall-through 示例:");
        int season = 1;
        System.out.print("season = " + season + " → ");
        switch (season) {
            case 1:
            case 2:
            case 3:
                System.out.println("春季");
                break;
            case 4:
            case 5:
            case 6:
                System.out.println("夏季");
                break;
            case 7:
            case 8:
            case 9:
                System.out.println("秋季");
                break;
            case 10:
            case 11:
            case 12:
                System.out.println("冬季");
                break;
            default:
                System.out.println("无效月份");
        }

        // ==================== 5. switch 表达式 (Java 14+) ====================
        System.out.println("\n【5. switch 表达式 (Java 14+)】");

        // 箭头语法，不需要 break
        int day = 5;
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "工作日";
            case 6, 7 -> "周末";
            default -> "无效";
        };
        System.out.println("day = " + day + " → " + type);

        // yield 关键字返回值
        String description = switch (day) {
            case 1 -> "星期一，新的一周开始";
            case 5 -> {
                String msg = "星期五，马上周末了";
                yield msg + "！"; // 使用 yield 返回值
            }
            case 6, 7 -> "周末，好好休息";
            default -> "普通的一天";
        };
        System.out.println("description: " + description);

        // ==================== 6. switch 支持的类型 ====================
        System.out.println("\n【6. switch 支持的类型】");
        System.out.println("支持的类型:");
        System.out.println("- byte, short, int, char");
        System.out.println("- Byte, Short, Integer, Character");
        System.out.println("- String (Java 7+)");
        System.out.println("- enum (枚举)");

        // String switch 示例
        String command = "start";
        switch (command) {
            case "start" -> System.out.println("启动程序...");
            case "stop" -> System.out.println("停止程序...");
            case "restart" -> System.out.println("重启程序...");
            default -> System.out.println("未知命令: " + command);
        }

        // ==================== 7. 模式匹配 (Java 16+ instanceof) ====================
        System.out.println("\n【7. 模式匹配 instanceof (Java 16+)】");

        Object obj = "Hello World";

        // 使用 instanceof 模式匹配 (Java 16+) 代替 switch 模式匹配 (Java 21+)
        // 这样可以兼容更多 Java 版本
        String result;
        if (obj == null) {
            result = "null 值";
        } else if (obj instanceof Integer i) {
            result = "整数: " + i;
        } else if (obj instanceof String s && s.length() > 5) {
            result = "长字符串: " + s;
        } else if (obj instanceof String s) {
            result = "短字符串: " + s;
        } else {
            result = "其他类型: " + obj.getClass().getSimpleName();
        }
        System.out.println("模式匹配结果: " + result);

        // ==================== 8. 最佳实践 ====================
        System.out.println("\n【8. 最佳实践】");
        System.out.println("1. 优先使用 switch 表达式（Java 14+）");
        System.out.println("2. 避免过深的嵌套（超过3层考虑重构）");
        System.out.println("3. 使用卫语句（Guard Clause）简化逻辑");
        System.out.println("4. 条件判断顺序：先处理特殊情况，再处理一般情况");

        // 卫语句示例
        System.out.println("\n卫语句示例:");
        printGrade(85);
        printGrade(-1);
        printGrade(101);

        System.out.println("\n✅ Phase 1 - Lesson 4 完成！");
    }

    // 卫语句示例方法
    static void printGrade(int score) {
        // 先处理无效情况
        if (score < 0 || score > 100) {
            System.out.println("  无效分数: " + score);
            return; // 提前返回
        }

        // 正常逻辑
        String level = switch (score / 10) {
            case 10, 9 -> "优秀";
            case 8 -> "良好";
            case 7, 6 -> "及格";
            default -> "不及格";
        };
        System.out.println("  分数 " + score + " → " + level);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. if-else: 最灵活的条件语句
 * 2. switch: 适合多分支等值判断
 * 3. 传统 switch: 需要 break，容易 fall-through
 * 4. switch 表达式: 箭头语法，不需要 break，更安全
 * 5. 模式匹配: Java 21+ 的强大特性
 * 
 * 🏃 练习:
 * 1. 实现一个成绩等级计算器
 * 2. 实现一个简单的计算器（加减乘除）
 * 3. 实现一个月份天数查询器
 */
