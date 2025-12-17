package phase03.ex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class HashSetDemo {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();

        set.add("Apple");

        List<Integer> listWithDups = Arrays.asList(1, 2, 3, 2, 1, 4, 5, 4, 3);

        Set<Integer> unique = new HashSet<>(listWithDups);

        Set<Integer> orderedUnique = new LinkedHashSet<>();

        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.add(5);
        treeSet.add(2);
        treeSet.add(8);
        treeSet.add(1);
        treeSet.add(9);

        treeSet.last();
        treeSet.first();
        treeSet.higher(5);
        treeSet.lower(5);
        treeSet.floor(6);

        treeSet.subSet(2, 8);
        treeSet.headSet(5);
        treeSet.tailSet(5);
    }
}
