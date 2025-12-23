package phase05.ex;

public class SynchronizedDemo {
    private int count = 0;
    private static int staticCount = 0;

    public static void main(String[] args) throws InterruptedException {
        class BlockCounter {
            private int count = 0;
            private final Object lock = new Object();

            public void increment() {
                synchronized (lock) {
                    count++;
                }
            }

            public int getCount() {
                synchronized (lock) {
                    return count;
                }
            }
        }

        BlockCounter block = new BlockCounter();
        Thread[] blockThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            blockThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    block.increment();
                }
            });
            blockThreads[i].start();
        }

        for (Thread t : blockThreads) {
            t.join();
        }
        System.out.println("synchronized 代码块结果: " + block.getCount());

        // 4. 对象锁 vs 类锁
        System.out.println("\n【4. 对象锁 vs 类锁】");
        System.out.println("""
                对象锁 (实例锁):
                - synchronized(this) 或 synchronized 实例方法
                - 锁住的是当前对象实例
                - 不同实例之间互不影响

                类锁:
                - synchronized(Class.class) 或 synchronized static 方法
                - 锁住的是类的 Class 对象
                - 所有实例共享同一把锁
                """);

        SynchronizedDemo demo = new SynchronizedDemo();

        Thread objThread1 = new Thread(() -> {
            demo.incrementInstance();
        });

        Thread objThread2 = new Thread(() -> {
            demo.incrementInstance();
        });

        Object lockA = new Object();
        Object lockB = new Object();

        Thread deadThread1 = new Thread(() -> {
            synchronized (lockA) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                synchronized (lockB) {

                }
            }
        }, "DeadThread1");

        // Thread deadThread2 = new Thread(() -> {
        // synchronized (lockB) {
        // try
        // }
        // }, "DeadThread2");
    }

    // 实例方法锁 - 锁 this
    public synchronized void incrementInstance() {
        count++;
        System.out.println("实例方法, count = " + count);
    }

    // 静态方法锁 - 锁 Class 对象
    public static synchronized void incrementStatic() {
        staticCount++;
        System.out.println("静态方法, staticCount = " + staticCount);
    }
}
