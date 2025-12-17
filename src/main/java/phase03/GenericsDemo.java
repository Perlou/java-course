package phase03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 3 - Lesson 8: 泛型
 * 
 * 🎯 学习目标:
 * 1. 理解泛型的概念和作用
 * 2. 掌握泛型类、接口、方法
 * 3. 了解类型擦除和通配符
 * 4. 学会泛型的边界限定
 */
public class GenericsDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 8: 泛型 ===\n");

        // ==================== 1. 为什么需要泛型 ====================
        System.out.println("【1. 为什么需要泛型】");

        // 没有泛型: 需要强制类型转换，容易出错
        List rawList = new ArrayList();
        rawList.add("Hello");
        rawList.add(123); // 可以添加任何类型
        // String s = (String) rawList.get(1); // 运行时 ClassCastException

        // 使用泛型: 编译时类型检查，无需强转
        List<String> safeList = new ArrayList<>();
        safeList.add("Hello");
        // safeList.add(123); // 编译错误！
        String s = safeList.get(0); // 无需强转

        System.out.println("泛型优点: 类型安全、消除强转、代码复用");

        // ==================== 2. 泛型类 ====================
        System.out.println("\n【2. 泛型类】");

        Box<String> stringBox = new Box<>("Hello");
        System.out.println("String Box: " + stringBox.get());

        Box<Integer> intBox = new Box<>(42);
        System.out.println("Integer Box: " + intBox.get());

        // 多类型参数
        Pair<String, Integer> pair = new Pair<>("Age", 25);
        System.out.println("Pair: " + pair.getKey() + " = " + pair.getValue());

        // ==================== 3. 泛型方法 ====================
        System.out.println("\n【3. 泛型方法】");

        // 类型推断
        String[] strArr = { "A", "B", "C" };
        List<String> strList = arrayToList(strArr);
        System.out.println("arrayToList: " + strList);

        // 泛型方法打印任意数组
        Integer[] intArr = { 1, 2, 3 };
        printArray(strArr);
        printArray(intArr);

        // ==================== 4. 有界类型参数 ====================
        System.out.println("\n【4. 有界类型参数】");

        // upper bound: <T extends Number>
        System.out.println("sum(1, 2, 3): " + sum(1, 2, 3));
        System.out.println("sum(1.5, 2.5): " + sum(1.5, 2.5));

        // 多重边界
        NumberBox<Integer> numBox = new NumberBox<>(100);
        System.out.println("NumberBox: " + numBox.doubleValue());

        // ==================== 5. 通配符 ====================
        System.out.println("\n【5. 通配符 (?)】");

        List<Integer> integers = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.1, 2.2, 3.3);
        List<String> strings = List.of("A", "B", "C");

        // 无界通配符: ?
        System.out.print("printList (任意类型): ");
        printList(integers);
        printList(strings);

        // 上界通配符: ? extends Number
        System.out.println("sumList (Integer): " + sumList(integers));
        System.out.println("sumList (Double): " + sumList(doubles));
        // sumList(strings); // 编译错误！

        // 下界通配符: ? super Integer
        List<Number> numbers = new ArrayList<>();
        addNumbers(numbers);
        System.out.println("addNumbers后: " + numbers);

        // ==================== 6. PECS 原则 ====================
        System.out.println("\n【6. PECS 原则】");
        System.out.println("Producer Extends, Consumer Super");
        System.out.println("  - 如果只读取，使用 <? extends T>");
        System.out.println("  - 如果只写入，使用 <? super T>");
        System.out.println("  - 如果读写都有，使用 <T>");

        // 示例: Collections.copy
        List<Number> dest = new ArrayList<>(List.of(0, 0, 0));
        List<Integer> src = List.of(1, 2, 3);
        Collections.copy(dest, src); // copy(List<? super T>, List<? extends T>)
        System.out.println("copy后: " + dest);

        // ==================== 7. 类型擦除 ====================
        System.out.println("\n【7. 类型擦除】");
        System.out.println("泛型只在编译时检查，运行时会被擦除");

        List<String> strL = new ArrayList<>();
        List<Integer> intL = new ArrayList<>();
        System.out.println("List<String>.class == List<Integer>.class: " +
                (strL.getClass() == intL.getClass())); // true!

        System.out.println("运行时类型: " + strL.getClass().getName());

        // ==================== 8. 泛型限制 ====================
        System.out.println("\n【8. 泛型限制】");
        System.out.println("不能做的事:");
        System.out.println("  ✗ new T() - 不能实例化类型参数");
        System.out.println("  ✗ new T[] - 不能创建泛型数组");
        System.out.println("  ✗ T.class - 不能获取类型参数的 Class");
        System.out.println("  ✗ static T field - 不能有泛型静态字段");
        System.out.println("  ✗ instanceof T - 不能使用 instanceof");

        // ==================== 9. 实际应用 ====================
        System.out.println("\n【9. 实际应用】");

        // 泛型缓存
        Cache<String, User> userCache = new Cache<>();
        userCache.put("u1", new User("张三", 25));
        userCache.put("u2", new User("李四", 30));
        System.out.println("缓存获取: " + userCache.get("u1"));

        // 泛型结果类
        Result<User> success = Result.success(new User("王五", 28));
        Result<User> failure = Result.failure("用户不存在");
        System.out.println("成功结果: " + success);
        System.out.println("失败结果: " + failure);

        System.out.println("\n✅ Phase 3 - Lesson 8 完成！");
    }

    // ==================== 泛型方法 ====================

    static <T> List<T> arrayToList(T[] arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

    static <T> void printArray(T[] arr) {
        System.out.println("printArray: " + Arrays.toString(arr));
    }

    // 有界类型参数
    @SafeVarargs
    static <T extends Number> double sum(T... numbers) {
        double result = 0;
        for (T num : numbers) {
            result += num.doubleValue();
        }
        return result;
    }

    // 通配符方法
    static void printList(List<?> list) {
        for (Object obj : list) {
            System.out.print(obj + " ");
        }
        System.out.println();
    }

    static double sumList(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        return sum;
    }

    static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
}

// ==================== 泛型类 ====================

class Box<T> {
    private T content;

    public Box(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }

    public void set(T content) {
        this.content = content;
    }
}

class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}

class NumberBox<T extends Number> {
    private T number;

    public NumberBox(T number) {
        this.number = number;
    }

    public double doubleValue() {
        return number.doubleValue();
    }
}

// ==================== 泛型缓存 ====================

class Cache<K, V> {
    private Map<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }
}

// ==================== 泛型结果类 ====================

class Result<T> {
    private final boolean success;
    private final T data;
    private final String error;

    private Result(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> failure(String error) {
        return new Result<>(false, null, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return success
                ? "Result{success, data=" + data + "}"
                : "Result{failure, error='" + error + "'}";
    }
}

record User(String name, int age) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. 泛型提供编译时类型检查
 * 2. 泛型类: class Box<T>
 * 3. 泛型方法: <T> T method(T param)
 * 4. 有界类型: <T extends Number>
 * 5. 通配符: ?, ? extends T, ? super T
 * 6. PECS: Producer Extends, Consumer Super
 * 7. 类型擦除: 运行时泛型信息丢失
 * 
 * 🏃 练习:
 * 1. 实现一个泛型栈 GenericStack<T>
 * 2. 实现一个通用的比较方法
 * 3. 创建一个泛型的 Tuple 类
 */
