package phase01;

/**
 * Phase 1 - Lesson 10: 方法定义与调用
 * 
 * 🎯 学习目标:
 * 1. 掌握方法的定义和调用
 * 2. 理解参数传递机制
 * 3. 掌握方法重载
 * 4. 理解递归的概念和应用
 */
public class MethodDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 10: 方法定义与调用 ===\n");

        // ==================== 1. 无参无返回值方法 ====================
        System.out.println("【1. 无参无返回值方法】");
        sayHello();
        printSeparator();

        // ==================== 2. 有参无返回值方法 ====================
        System.out.println("【2. 有参无返回值方法】");
        greet("张三");
        greet("李四");
        printSeparator();

        // ==================== 3. 有返回值方法 ====================
        System.out.println("【3. 有返回值方法】");
        int sum = add(10, 20);
        System.out.println("add(10, 20) = " + sum);

        double average = getAverage(80, 90, 85);
        System.out.println("getAverage(80, 90, 85) = " + average);
        printSeparator();

        // ==================== 4. 方法重载 ====================
        System.out.println("【4. 方法重载 (Overloading)】");
        System.out.println("add(1, 2) = " + add(1, 2));
        System.out.println("add(1, 2, 3) = " + add(1, 2, 3));
        System.out.println("add(1.5, 2.5) = " + add(1.5, 2.5));
        System.out.println("add(\"Hello\", \"World\") = " + add("Hello", "World"));
        printSeparator();

        // ==================== 5. 参数传递 ====================
        System.out.println("【5. 参数传递】");

        // 基本类型: 值传递
        System.out.println("基本类型 (值传递):");
        int num = 100;
        System.out.println("  调用前: num = " + num);
        modifyInt(num);
        System.out.println("  调用后: num = " + num + " (未改变)");

        // 引用类型: 引用传递
        System.out.println("引用类型 (引用传递):");
        int[] arr = { 1, 2, 3 };
        System.out.println("  调用前: arr = [" + arr[0] + ", " + arr[1] + ", " + arr[2] + "]");
        modifyArray(arr);
        System.out.println("  调用后: arr = [" + arr[0] + ", " + arr[1] + ", " + arr[2] + "] (已改变)");
        printSeparator();

        // ==================== 6. 方法返回多个值 ====================
        System.out.println("【6. 返回多个值】");

        // 使用数组
        int[] minMax = findMinMax(new int[] { 3, 1, 4, 1, 5, 9, 2, 6 });
        System.out.println("最小值: " + minMax[0] + ", 最大值: " + minMax[1]);

        // 使用 Record (Java 16+)
        Result result = getStats(new int[] { 10, 20, 30, 40, 50 });
        System.out.println("统计: min=" + result.min + ", max=" + result.max + ", avg=" + result.average);
        printSeparator();

        // ==================== 7. 方法的作用域 ====================
        System.out.println("【7. 变量作用域】");

        // 局部变量
        int localVar = 10;
        {
            int blockVar = 20; // 块级作用域
            System.out.println("块内: localVar=" + localVar + ", blockVar=" + blockVar);
        }
        // System.out.println(blockVar); // 编译错误: 块外无法访问
        System.out.println("块外: localVar=" + localVar);

        // 静态变量
        System.out.println("静态变量 counter = " + counter);
        incrementCounter();
        incrementCounter();
        System.out.println("调用 incrementCounter() 两次后: " + counter);

        System.out.println("\n✅ Phase 1 - Lesson 10 完成！");
    }

    // ========== 静态变量 ==========
    static int counter = 0;

    // ========== 方法定义 ==========

    // 无参无返回值
    static void sayHello() {
        System.out.println("Hello, Java!");
    }

    // 打印分隔线
    static void printSeparator() {
        System.out.println("-".repeat(40));
    }

    // 有参无返回值
    static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    // 有返回值
    static int add(int a, int b) {
        return a + b;
    }

    // 多参数
    static double getAverage(int... scores) {
        if (scores.length == 0)
            return 0;
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.length;
    }

    // ========== 方法重载 ==========

    // 重载: 三个 int 参数
    static int add(int a, int b, int c) {
        return a + b + c;
    }

    // 重载: 两个 double 参数
    static double add(double a, double b) {
        return a + b;
    }

    // 重载: 两个 String 参数
    static String add(String a, String b) {
        return a + " " + b;
    }

    // ========== 参数传递演示 ==========

    static void modifyInt(int value) {
        value = 999; // 不会影响原变量
        System.out.println("  方法内: value = " + value);
    }

    static void modifyArray(int[] arr) {
        arr[0] = 999; // 会影响原数组
    }

    // ========== 返回多个值 ==========

    static int[] findMinMax(int[] arr) {
        int min = arr[0], max = arr[0];
        for (int num : arr) {
            if (num < min)
                min = num;
            if (num > max)
                max = num;
        }
        return new int[] { min, max };
    }

    // 使用 Record 返回多个值
    record Result(int min, int max, double average) {
    }

    static Result getStats(int[] arr) {
        int min = arr[0], max = arr[0], sum = 0;
        for (int num : arr) {
            if (num < min)
                min = num;
            if (num > max)
                max = num;
            sum += num;
        }
        return new Result(min, max, (double) sum / arr.length);
    }

    // ========== 静态方法 ==========

    static void incrementCounter() {
        counter++;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 方法定义: 修饰符 返回类型 方法名(参数列表) { 方法体 }
 * 2. 方法重载: 同名方法，参数列表不同
 * 3. 参数传递: 基本类型值传递，引用类型引用传递
 * 4. 返回多个值: 使用数组、集合或 Record
 * 5. 作用域: 局部变量、块级变量、类级变量
 * 
 * 🏃 练习:
 * 1. 实现一个判断素数的方法
 * 2. 实现一个计算最大公约数的方法
 * 3. 实现一个字符串反转的方法
 */
