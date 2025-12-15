package phase01;

import java.util.Scanner;

/**
 * Phase 1 - 实战项目: 控制台计算器
 * 
 * 🎯 项目目标:
 * 1. 综合运用 Phase 1 所学知识
 * 2. 实现基本的四则运算
 * 3. 支持连续计算和历史记录
 * 
 * 📚 涉及知识点:
 * - 变量和数据类型
 * - 运算符
 * - 控制流程 (if/switch)
 * - 循环
 * - 方法定义
 * - 数组
 */
public class Calculator {

    // 存储历史记录
    private static String[] history = new String[100];
    private static int historyCount = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     🧮 Java 控制台计算器 v1.0          ║");
        System.out.println("║     Phase 1 实战项目                   ║");
        System.out.println("╚════════════════════════════════════════╝");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHelp();

        while (running) {
            System.out.print("\n请输入表达式 (或命令): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            // 处理命令
            switch (input.toLowerCase()) {
                case "help", "h", "?" -> printHelp();
                case "history", "hist" -> printHistory();
                case "clear", "c" -> clearHistory();
                case "quit", "exit", "q" -> {
                    running = false;
                    System.out.println("感谢使用，再见！👋");
                }
                default -> processExpression(input);
            }
        }

        scanner.close();
    }

    /**
     * 处理表达式
     */
    private static void processExpression(String input) {
        try {
            // 简单表达式解析: "数字 运算符 数字"
            String[] parts = parseExpression(input);

            if (parts == null) {
                // 尝试作为单一数学函数处理
                if (input.startsWith("sqrt(") || input.startsWith("pow(") ||
                        input.startsWith("sin(") || input.startsWith("cos(") ||
                        input.startsWith("fact(")) {
                    double result = evaluateFunction(input);
                    String record = input + " = " + formatResult(result);
                    addHistory(record);
                    System.out.println("= " + formatResult(result));
                } else {
                    System.out.println("❌ 无法解析表达式: " + input);
                    System.out.println("   格式: 数字 运算符 数字, 例如: 10 + 5");
                }
                return;
            }

            double num1 = Double.parseDouble(parts[0]);
            String operator = parts[1];
            double num2 = Double.parseDouble(parts[2]);

            double result = calculate(num1, operator, num2);

            if (Double.isNaN(result)) {
                System.out.println("❌ 计算错误");
                return;
            }

            String record = formatExpression(num1, operator, num2, result);
            addHistory(record);
            System.out.println("= " + formatResult(result));

        } catch (NumberFormatException e) {
            System.out.println("❌ 数字格式错误: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 错误: " + e.getMessage());
        }
    }

    /**
     * 解析表达式
     */
    private static String[] parseExpression(String input) {
        String[] operators = { "+", "-", "*", "/", "%", "^" };

        for (String op : operators) {
            int index = -1;

            // 跳过开头的负号
            if (op.equals("-") && input.startsWith("-")) {
                index = input.indexOf(op, 1);
            } else {
                index = input.indexOf(op);
            }

            if (index > 0) {
                String num1 = input.substring(0, index).trim();
                String num2 = input.substring(index + 1).trim();

                if (!num1.isEmpty() && !num2.isEmpty()) {
                    return new String[] { num1, op, num2 };
                }
            }
        }

        return null;
    }

    /**
     * 执行计算
     */
    private static double calculate(double num1, String operator, double num2) {
        return switch (operator) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            case "/" -> {
                if (num2 == 0) {
                    System.out.println("❌ 错误: 除数不能为零");
                    yield Double.NaN;
                }
                yield num1 / num2;
            }
            case "%" -> {
                if (num2 == 0) {
                    System.out.println("❌ 错误: 除数不能为零");
                    yield Double.NaN;
                }
                yield num1 % num2;
            }
            case "^" -> Math.pow(num1, num2);
            default -> {
                System.out.println("❌ 未知运算符: " + operator);
                yield Double.NaN;
            }
        };
    }

    /**
     * 计算数学函数
     */
    private static double evaluateFunction(String input) {
        if (input.startsWith("sqrt(") && input.endsWith(")")) {
            double num = Double.parseDouble(input.substring(5, input.length() - 1));
            return Math.sqrt(num);
        }
        if (input.startsWith("pow(") && input.endsWith(")")) {
            String[] args = input.substring(4, input.length() - 1).split(",");
            return Math.pow(Double.parseDouble(args[0].trim()),
                    Double.parseDouble(args[1].trim()));
        }
        if (input.startsWith("sin(") && input.endsWith(")")) {
            double num = Double.parseDouble(input.substring(4, input.length() - 1));
            return Math.sin(Math.toRadians(num));
        }
        if (input.startsWith("cos(") && input.endsWith(")")) {
            double num = Double.parseDouble(input.substring(4, input.length() - 1));
            return Math.cos(Math.toRadians(num));
        }
        if (input.startsWith("fact(") && input.endsWith(")")) {
            int num = Integer.parseInt(input.substring(5, input.length() - 1));
            return factorial(num);
        }
        throw new IllegalArgumentException("未知函数");
    }

    /**
     * 阶乘
     */
    private static long factorial(int n) {
        if (n <= 1)
            return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * 格式化结果
     */
    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        }
        return String.format("%.6f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * 格式化表达式
     */
    private static String formatExpression(double num1, String op, double num2, double result) {
        return formatResult(num1) + " " + op + " " + formatResult(num2) + " = " + formatResult(result);
    }

    /**
     * 添加历史记录
     */
    private static void addHistory(String record) {
        if (historyCount < history.length) {
            history[historyCount++] = record;
        } else {
            // 移除最旧的记录
            System.arraycopy(history, 1, history, 0, history.length - 1);
            history[history.length - 1] = record;
        }
    }

    /**
     * 打印历史记录
     */
    private static void printHistory() {
        if (historyCount == 0) {
            System.out.println("📜 暂无历史记录");
            return;
        }
        System.out.println("📜 计算历史:");
        for (int i = 0; i < historyCount; i++) {
            System.out.println("  " + (i + 1) + ". " + history[i]);
        }
    }

    /**
     * 清空历史记录
     */
    private static void clearHistory() {
        historyCount = 0;
        System.out.println("🗑️ 历史记录已清空");
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("""

                📖 使用说明:
                ─────────────────────────────────────
                基本运算:
                  • 加法: 10 + 5
                  • 减法: 10 - 5
                  • 乘法: 10 * 5
                  • 除法: 10 / 5
                  • 取余: 10 % 3
                  • 乘方: 2 ^ 10

                数学函数:
                  • 平方根: sqrt(16)
                  • 幂运算: pow(2, 10)
                  • 正弦:   sin(30)
                  • 余弦:   cos(60)
                  • 阶乘:   fact(5)

                命令:
                  • help/h/?     - 显示帮助
                  • history/hist - 显示历史
                  • clear/c      - 清空历史
                  • quit/exit/q  - 退出程序
                ─────────────────────────────────────
                """);
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 Phase 1 的所有知识点:
 * - 变量: 存储数字、运算符、结果
 * - 数据类型: int, double, String, boolean
 * - 运算符: 算术运算、逻辑运算
 * - 条件语句: switch 表达式处理命令和运算符
 * - 循环: while 循环实现交互式界面
 * - 方法: 模块化设计，职责分离
 * - 数组: 存储历史记录
 * - 递归: 阶乘计算
 * 
 * 🎯 扩展任务:
 * 1. 支持括号运算: (1 + 2) * 3
 * 2. 支持变量存储: x = 10, x + 5
 * 3. 支持更多数学函数: log, tan, abs
 * 4. 添加单元测试
 */
