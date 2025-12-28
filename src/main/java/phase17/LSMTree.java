package phase17;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * LSM-Tree 存储引擎原理演示
 * 
 * LSM-Tree (Log-Structured Merge Tree) 是一种写优化的数据结构
 * 广泛用于: RocksDB, LevelDB, Cassandra, HBase, TiKV
 */
public class LSMTree {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("           Phase 17: LSM-Tree 存储引擎原理                   ");
        System.out.println("============================================================");

        System.out.println("\n[1] LSM-Tree 结构概览\n");
        showLSMStructure();

        System.out.println("\n[2] 写入流程\n");
        demonstrateWrite();

        System.out.println("\n[3] 读取流程\n");
        demonstrateRead();

        System.out.println("\n[4] Compaction 策略\n");
        explainCompaction();
    }

    private static void showLSMStructure() {
        System.out.println("LSM-Tree 分层结构:");
        System.out.println();
        System.out.println("  写入 --+");
        System.out.println("         |");
        System.out.println("         v");
        System.out.println("  +---------------+");
        System.out.println("  |   MemTable    |  <- 内存 (红黑树/跳表)");
        System.out.println("  +-------+-------+");
        System.out.println("          | 刷盘");
        System.out.println("          v");
        System.out.println("  +---------------+");
        System.out.println("  |  Immutable    |  <- 只读，等待刷盘");
        System.out.println("  |   MemTable    |");
        System.out.println("  +-------+-------+");
        System.out.println("          |");
        System.out.println("  ========|===================== 磁盘 =======");
        System.out.println("          v");
        System.out.println("  Level 0: [SST][SST][SST]    可能重叠");
        System.out.println("  Level 1: [  SST  ][  SST  ] 有序不重叠");
        System.out.println("  Level 2: [    SST    ][    SST    ]");
        System.out.println("  Level 3: [        SST        ]");
        System.out.println();
        System.out.println("SST = Sorted String Table (有序键值表)");
    }

    // ==================== 简化的 LSM 模拟 ====================

    static class SimpleLSM {
        // 内存表
        private TreeMap<String, String> memTable = new TreeMap<>();
        // 不可变内存表 (等待刷盘)
        private TreeMap<String, String> immutableMemTable = null;
        // 磁盘上的 SSTable (简化为内存中的 List)
        private List<TreeMap<String, String>> level0 = new ArrayList<>();

        private static final int MEMTABLE_THRESHOLD = 3;

        public void put(String key, String value) {
            System.out.println("  PUT " + key + " = " + value);
            memTable.put(key, value);

            // 检查是否需要刷盘
            if (memTable.size() >= MEMTABLE_THRESHOLD) {
                flush();
            }
        }

        public String get(String key) {
            // 1. 先查 MemTable
            if (memTable.containsKey(key)) {
                return memTable.get(key) + " (from MemTable)";
            }

            // 2. 查 Immutable MemTable
            if (immutableMemTable != null && immutableMemTable.containsKey(key)) {
                return immutableMemTable.get(key) + " (from Immutable)";
            }

            // 3. 查 Level 0 SSTables (从新到旧)
            for (int i = level0.size() - 1; i >= 0; i--) {
                TreeMap<String, String> sst = level0.get(i);
                if (sst.containsKey(key)) {
                    return sst.get(key) + " (from SST-" + i + ")";
                }
            }

            return null;
        }

        private void flush() {
            System.out.println("  [!] MemTable 已满，执行刷盘...");
            immutableMemTable = memTable;
            memTable = new TreeMap<>();

            // 模拟异步刷盘
            level0.add(immutableMemTable);
            immutableMemTable = null;
            System.out.println("  [OK] 生成 SSTable-" + (level0.size() - 1));
        }

        public void printState() {
            System.out.println("  MemTable: " + memTable);
            System.out.println("  Level 0 SSTables: " + level0.size() + " 个");
        }
    }

    private static void demonstrateWrite() {
        System.out.println("写入流程演示:");
        System.out.println();
        System.out.println("步骤: 1.写WAL -> 2.写MemTable -> 3.刷盘为SSTable");
        System.out.println();

        SimpleLSM lsm = new SimpleLSM();

        lsm.put("user:1", "Alice");
        lsm.put("user:2", "Bob");
        lsm.put("user:3", "Charlie"); // 触发刷盘
        lsm.put("user:4", "David");
        lsm.put("user:5", "Eve");

        System.out.println();
        lsm.printState();

        System.out.println();
        System.out.println("为什么写入快?");
        System.out.println("  - 顺序写入 (append-only)");
        System.out.println("  - 先写内存，批量刷盘");
        System.out.println("  - WAL 保证持久性");
    }

    private static void demonstrateRead() {
        System.out.println("读取流程演示:");
        System.out.println();

        SimpleLSM lsm = new SimpleLSM();
        lsm.put("a", "1");
        lsm.put("b", "2");
        lsm.put("c", "3"); // 刷盘
        lsm.put("a", "100"); // 更新 a
        lsm.put("d", "4");

        System.out.println();
        System.out.println("查询结果:");
        System.out.println("  GET a = " + lsm.get("a")); // 从 MemTable
        System.out.println("  GET b = " + lsm.get("b")); // 从 SSTable
        System.out.println("  GET x = " + lsm.get("x")); // 不存在

        System.out.println();
        System.out.println("优化读取的方式:");
        System.out.println("  1. 布隆过滤器 - 快速判断 key 不存在");
        System.out.println("  2. 索引块 - 二分查找定位 key");
        System.out.println("  3. 缓存 - Block Cache 缓存热数据");
    }

    private static void explainCompaction() {
        System.out.println("Compaction (压缩合并):");
        System.out.println();
        System.out.println("为什么需要 Compaction?");
        System.out.println("  - 同一个 key 可能存在于多个 SSTable");
        System.out.println("  - 删除的 key 还占用空间 (墓碑标记)");
        System.out.println("  - 读取需要查多个 SSTable");
        System.out.println();
        System.out.println("Compaction 过程:");
        System.out.println("  1. 选择需要合并的 SSTable");
        System.out.println("  2. 多路归并排序");
        System.out.println("  3. 保留最新版本，删除旧版本");
        System.out.println("  4. 生成新的 SSTable");
        System.out.println("  5. 删除旧 SSTable");
        System.out.println();
        System.out.println("两种 Compaction 策略:");
        System.out.println();
        System.out.println("1. Size-Tiered (大小分层)");
        System.out.println("   - 相似大小的 SSTable 合并");
        System.out.println("   - 写放大低，空间放大高");
        System.out.println("   - 适合写多读少");
        System.out.println();
        System.out.println("2. Leveled (分层)");
        System.out.println("   - 每层大小固定倍数");
        System.out.println("   - 写放大高，读放大低");
        System.out.println("   - 适合读多写少");
    }
}
