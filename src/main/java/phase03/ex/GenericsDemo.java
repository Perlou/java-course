package phase03.ex;

public class GenericsDemo {
    public static void main(String[] args) {
        Box<String> stringBox = new Box<>("hello");

        Pair<String, Integer> pair = new Pair<>("Age", 25);

        NumberBox<Integer> numBox = new NumberBox<>(100);
    }
}

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

record User(String name, int age) {
}
