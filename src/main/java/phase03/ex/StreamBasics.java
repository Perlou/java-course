package phase03.ex;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamBasics {
    public static void main(String[] args) {
        List<String> list = List.of("a", "b", "c");
        Stream<String> stream1 = list.stream();

        String[] arr = { "x", "y", "z" };
        Stream<String> stream2 = Arrays.stream(arr);

        Stream<Integer> stream3 = Stream.of(1, 2, 3);

        Stream<Integer> infinite = Stream.iterate(0, n -> n + 2);
        // infinite.limit(5).forEach(n -> n);

        List<Integer> numbers = List.of(5, 2, 8, 1, 9, 3, 7, 4, 6);

        numbers.stream()
                .filter(n -> n % 2 == 0);

        numbers.stream()
                .map(n -> n * n);

        numbers.stream()
                .skip(2)
                .limit(3);

        List<Employee> employees = List.of(
                new Employee("张三", "开发", 15000),
                new Employee("李四", "开发", 18000),
                new Employee("王五", "测试", 12000),
                new Employee("赵六", "开发", 20000),
                new Employee("钱七", "测试", 14000));

        Optional<Employee> topDev = employees.stream()
                .filter(e -> e.dept().equals("开发"))
                .max(Comparator.comparing(Employee::salary));

        Map<String, Double> avgByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::dept,
                        Collectors.averagingDouble(Employee::salary)));

        double totalSalary = employees.stream()
                .mapToDouble(Employee::salary)
                .sum();
    }
}

record Employee(String name, String dept, double salary) {
}
