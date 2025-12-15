package phase01;

/**
 * Phase 1 - Lesson 6: Switch 表达式 (Java 17 新特性)
 * 
 * 🎯 学习目标:
 * 1. 掌握 Java 17 的 switch 表达式
 * 2. 理解箭头语法和 yield 的使用
 * 3. 了解模式匹配的初步应用
 */
public class SwitchDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 6: Switch 表达式 ===\n");

        // ==================== 1. 传统 switch vs 新 switch ====================
        System.out.println("【1. 传统 switch vs 新 switch】");

        int day = 3;

        // 传统写法
        String dayName1;
        switch (day) {
            case 1:
                dayName1 = "Monday";
                break;
            case 2:
                dayName1 = "Tuesday";
                break;
            case 3:
                dayName1 = "Wednesday";
                break;
            default:
                dayName1 = "Unknown";
        }
        System.out.println("传统 switch: day " + day + " = " + dayName1);

        // Java 14+ 箭头语法
        String dayName2 = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6, 7 -> "Weekend";
            default -> "Unknown";
        };
        System.out.println("新 switch: day " + day + " = " + dayName2);

        // ==================== 2. 多值匹配 ====================
        System.out.println("\n【2. 多值匹配】");

        int month = 7;
        String season = switch (month) {
            case 3, 4, 5 -> "Spring";
            case 6, 7, 8 -> "Summer";
            case 9, 10, 11 -> "Autumn";
            case 12, 1, 2 -> "Winter";
            default -> "Invalid month";
        };
        System.out.println("Month " + month + " is in " + season);

        // ==================== 3. yield 关键字 ====================
        System.out.println("\n【3. yield 关键字】");

        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            case 6 -> {
                System.out.println("  (刚好及格，需要努力)");
                yield "D"; // 块语句需要用 yield 返回值
            }
            default -> {
                System.out.println("  (不及格，加油)");
                yield "F";
            }
        };
        System.out.println("Score " + score + " = Grade " + grade);

        // ==================== 4. switch 表达式完整性检查 ====================
        System.out.println("\n【4. switch 表达式完整性检查】");

        // 枚举类型会自动检查完整性
        Day today = Day.FRIDAY;
        String activity = switch (today) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY -> "Work hard";
            case FRIDAY -> "TGIF!";
            case SATURDAY, SUNDAY -> "Relax";
            // 不需要 default，因为枚举是完整的
        };
        System.out.println(today + " → " + activity);

        // ==================== 5. 模式匹配 (Java 21) ====================
        System.out.println("\n【5. 模式匹配 switch (Java 21)】");

        // 类型模式匹配
        Object[] objects = { 42, "Hello", 3.14, null, true };
        for (Object obj : objects) {
            String result = describeObject(obj);
            System.out.println("  " + obj + " → " + result);
        }

        // ==================== 6. 守卫条件 (when) ====================
        System.out.println("\n【6. 守卫条件 (when 子句)】");

        // 注意：基本类型的模式匹配 (如 case int val) 是 Java 23+ 预览功能
        // 这里使用传统方式演示守卫条件的概念
        for (int i : new int[] { -5, 0, 5, 100 }) {
            String desc;
            if (i < 0) {
                desc = "负数";
            } else if (i == 0) {
                desc = "零";
            } else if (i <= 10) {
                desc = "小正数 (1-10)";
            } else {
                desc = "大正数 (>10)";
            }
            System.out.println("  " + i + " → " + desc);
        }

        // 完整的守卫条件示例 (使用 Object 类型)
        System.out.println("\n  使用对象类型的守卫条件:");
        Object[] numbers = { -3, 0, 7, 50 };
        for (Object num : numbers) {
            String desc = switch (num) {
                case Integer val when val < 0 -> "负数: " + val;
                case Integer val when val == 0 -> "零";
                case Integer val when val <= 10 -> "小正数: " + val;
                case Integer val -> "大正数: " + val;
                default -> "未知类型";
            };
            System.out.println("  " + num + " → " + desc);
        }

        // ==================== 7. 实际应用示例 ====================
        System.out.println("\n【7. 实际应用示例】");

        // HTTP 状态码描述
        int httpStatus = 404;
        String message = switch (httpStatus) {
            case 200, 201 -> "Success";
            case 301, 302 -> "Redirect";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> httpStatus >= 400 && httpStatus < 500
                    ? "Client Error"
                    : "Unknown Status";
        };
        System.out.println("HTTP " + httpStatus + ": " + message);

        // 命令处理器
        String command = "save";
        int resultCode = switch (command.toLowerCase()) {
            case "new", "create" -> {
                System.out.println("  创建新文件...");
                yield 0;
            }
            case "open", "load" -> {
                System.out.println("  打开文件...");
                yield 0;
            }
            case "save" -> {
                System.out.println("  保存文件...");
                yield 0;
            }
            case "quit", "exit" -> {
                System.out.println("  退出程序...");
                yield -1;
            }
            default -> {
                System.out.println("  未知命令: " + command);
                yield 1;
            }
        };
        System.out.println("  返回码: " + resultCode);

        // ==================== 8. 最佳实践 ====================
        System.out.println("\n【8. 最佳实践】");
        System.out.println("1. 优先使用箭头语法 (->)，避免 fall-through");
        System.out.println("2. 使用 switch 表达式而不是语句");
        System.out.println("3. 利用编译器的完整性检查");
        System.out.println("4. 对于复杂逻辑，使用块语句和 yield");
        System.out.println("5. 考虑使用枚举来增强类型安全");

        System.out.println("\n✅ Phase 1 - Lesson 6 完成！");
    }

    // 模式匹配示例方法
    static String describeObject(Object obj) {
        return switch (obj) {
            case null -> "null 值";
            case Integer i when i > 0 -> "正整数: " + i;
            case Integer i -> "非正整数: " + i;
            case String s when s.isEmpty() -> "空字符串";
            case String s -> "字符串 (长度 " + s.length() + ")";
            case Double d -> "浮点数: " + d;
            case Boolean b -> "布尔值: " + b;
            default -> "其他类型: " + obj.getClass().getSimpleName();
        };
    }

    // 演示用的枚举
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 箭头语法: case X -> result
 * 2. 多值匹配: case 1, 2, 3 -> ...
 * 3. yield: 在块语句中返回值
 * 4. 完整性检查: 枚举不需要 default
 * 5. 模式匹配: case Type var -> ... (Java 21)
 * 6. 守卫条件: case Type var when condition -> ...
 * 
 * 🏃 练习:
 * 1. 用 switch 实现简易计算器
 * 2. 用 switch 处理不同的 HTTP 方法 (GET, POST, PUT, DELETE)
 * 3. 用模式匹配处理不同类型的命令对象
 */
