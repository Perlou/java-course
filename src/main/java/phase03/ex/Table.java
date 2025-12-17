package phase03.ex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 内存表实现
 * 
 * @param <T> 实体类型
 */
public class Table<T extends Entity> {

    private final String tableName;
    private final Map<Long, T> data; // 主数据存储
    private final Map<String, Map<Object, Set<Long>>> indexes; // 二级索引
    private final Map<String, Function<T, Object>> indexExtractors; // 索引字段提取器
    private final AtomicLong idGenerator;

    public Table(String tableName) {
        this.tableName = tableName;
        this.data = new ConcurrentHashMap<>();
        this.indexes = new ConcurrentHashMap<>();
        this.indexExtractors = new ConcurrentHashMap<>();
        this.idGenerator = new AtomicLong(1);
    }

    // ==================== 索引管理 ====================

    /**
     * 创建索引
     */
    public void createIndex(String indexName, Function<T, Object> extractor) {
        indexes.put(indexName, new ConcurrentHashMap<>());
        indexExtractors.put(indexName, extractor);

        // 为现有数据建立索引
        data.forEach((id, entity) -> addToIndex(indexName, entity));
        System.out.println("索引 [" + indexName + "] 创建成功");
    }

    private void addToIndex(String indexName, T entity) {
        Function<T, Object> extractor = indexExtractors.get(indexName);
        if (extractor != null) {
            Object key = extractor.apply(entity);
            indexes.get(indexName)
                    .computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                    .add(entity.getId());
        }
    }

    private void removeFromIndex(String indexName, T entity) {
        Function<T, Object> extractor = indexExtractors.get(indexName);
        if (extractor != null) {
            Object key = extractor.apply(entity);
            Map<Object, Set<Long>> index = indexes.get(indexName);
            if (index != null && index.containsKey(key)) {
                index.get(key).remove(entity.getId());
            }
        }
    }

    // ==================== CRUD 操作 ====================

    /**
     * 插入记录
     */
    public T insert(T entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        data.put(id, entity);

        // 更新所有索引
        indexExtractors.keySet().forEach(indexName -> addToIndex(indexName, entity));

        return entity;
    }

    /**
     * 批量插入
     */
    public List<T> insertBatch(List<T> entities) {
        return entities.stream()
                .map(this::insert)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 查询
     */
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * 查询所有
     */
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    /**
     * 条件查询
     */
    public List<T> findWhere(Predicate<T> condition) {
        return data.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    /**
     * 通过索引查询
     */
    public List<T> findByIndex(String indexName, Object value) {
        Map<Object, Set<Long>> index = indexes.get(indexName);
        if (index == null) {
            throw new RuntimeException("索引不存在: " + indexName);
        }

        Set<Long> ids = index.getOrDefault(value, Collections.emptySet());
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 更新记录
     */
    public boolean update(T entity) {
        if (entity.getId() == null || !data.containsKey(entity.getId())) {
            return false;
        }

        T oldEntity = data.get(entity.getId());

        // 更新索引
        indexExtractors.keySet().forEach(indexName -> {
            removeFromIndex(indexName, oldEntity);
            addToIndex(indexName, entity);
        });

        data.put(entity.getId(), entity);
        return true;
    }

    /**
     * 删除记录
     */
    public boolean delete(Long id) {
        T entity = data.get(id);
        if (entity == null) {
            return false;
        }

        // 从索引中移除
        indexExtractors.keySet().forEach(indexName -> removeFromIndex(indexName, entity));

        data.remove(id);
        return true;
    }

    /**
     * 条件删除
     */
    public int deleteWhere(Predicate<T> condition) {
        List<Long> toDelete = data.values().stream()
                .filter(condition)
                .map(Entity::getId)
                .collect(Collectors.toList());

        toDelete.forEach(this::delete);
        return toDelete.size();
    }

    /**
     * 记录数量
     */
    public int count() {
        return data.size();
    }

    /**
     * 条件计数
     */
    public long countWhere(Predicate<T> condition) {
        return data.values().stream().filter(condition).count();
    }

    /**
     * 清空表
     */
    public void truncate() {
        data.clear();
        indexes.values().forEach(Map::clear);
        idGenerator.set(1);
    }

    // ==================== 高级查询 ====================

    /**
     * 分页查询
     */
    public List<T> findPage(int page, int size) {
        return data.values().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * 排序查询
     */
    public List<T> findAllSorted(Comparator<T> comparator) {
        return data.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 聚合 - 求最大值
     */
    public <R extends Comparable<R>> Optional<T> max(Function<T, R> extractor) {
        return data.values().stream()
                .max(Comparator.comparing(extractor));
    }

    /**
     * 聚合 - 求最小值
     */
    public <R extends Comparable<R>> Optional<T> min(Function<T, R> extractor) {
        return data.values().stream()
                .min(Comparator.comparing(extractor));
    }

    /**
     * 分组统计
     */
    public <K> Map<K, List<T>> groupBy(Function<T, K> classifier) {
        return data.values().stream()
                .collect(Collectors.groupingBy(classifier));
    }

    public String getTableName() {
        return tableName;
    }
}