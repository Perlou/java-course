package phase03.ex;

import java.util.Deque;
import java.util.LinkedList;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();

        list.add("B");
        list.add("D");
        list.addFirst("A");
        list.addLast("E");
        list.add(2, "C");

        Deque<String> deque = new LinkedList<>();

        deque.offer("First");
        deque.offer("Second");
        deque.offer("Third");
    }
}
