package phase03.ex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArrayListDemo {
    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>();
        list1.add("Apple");
        list1.add("Banana");

        List<String> list2 = new ArrayList<>(100);

        List<String> list3 = new ArrayList<>(list1);

        List<String> list4 = Arrays.asList("A", "B", "C");

        List<String> list5 = List.of("X", "Y", "Z");

        List<String> list6 = new ArrayList<>(List.of("1", "2", "3"));
        list6.add("4");

        List<String> fruits = new ArrayList<>();

        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add(1, "Grape");

        fruits.get(0);
        fruits.size();
        fruits.isEmpty();

        fruits.set(0, "Mango");

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        for (int i = 0; i < numbers.size(); i++) {

        }

        for (Integer n : numbers) {

        }

        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            it.next();
        }

        numbers.forEach(null);

        numbers.stream().forEach(null);

        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D", "E"));

        list.addAll(List.of("F", "G"));

        list.removeAll(List.of("A", "C", "E"));

        List<Integer> nums = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3));

        Collections.sort(nums);

        Collections.sort(nums, Collections.reverseOrder());
    }
}
