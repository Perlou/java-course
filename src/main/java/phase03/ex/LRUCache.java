package phase03.ex;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        LRUCache<String, Integer> cache = new LRUCache<>(3);

        System.out.println("=== LRU Cache 演示 ===");

        cache.put("A", 1);
        System.out.println("put(A,1): " + cache); // {A=1}

        cache.put("B", 2);
        System.out.println("put(B,2): " + cache); // {A=1, B=2}

        cache.put("C", 3);
        System.out.println("put(C,3): " + cache); // {A=1, B=2, C=3}

        cache.get("A"); // 访问 A，移到末尾
        System.out.println("get(A):   " + cache); // {B=2, C=3, A=1}

        cache.put("D", 4); // 容量满，淘汰 B
        System.out.println("put(D,4): " + cache); // {C=3, A=1, D=4}

        cache.get("C"); // 访问 C
        System.out.println("get(C):   " + cache); // {A=1, D=4, C=3}

        cache.put("E", 5); // 淘汰 A
        System.out.println("put(E,5): " + cache); // {D=4, C=3, E=5}
    }
}
