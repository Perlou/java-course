# Phase 11: 数据库与 ORM - 学习总结

> **完成状态**: ✅ 已完成  
> **学习时间**: 2025 年 12 月

---

## 🎯 学习目标达成

| 目标                         | 状态 |
| ---------------------------- | ---- |
| 理解 MySQL 索引原理 (B+ 树)  | ✅   |
| 掌握事务隔离级别和锁机制     | ✅   |
| 熟练使用 MyBatis 和动态 SQL  | ✅   |
| 掌握 MyBatis-Plus 快速开发   | ✅   |
| 深入理解 JPA Repository 模式 | ✅   |
| 了解分库分表方案             | ✅   |
| 能够进行 SQL 性能优化        | ✅   |
| 完成订单管理系统项目         | ✅   |

---

## 📚 核心知识点

### 1. MySQL 索引 ([MysqlIndexDemo.java](./MysqlIndexDemo.java))

```
B+ 树索引结构:
┌─────────────────────────────────────────────────────────┐
│                    [15 | 28]                            │ ← 非叶子节点
│                   /    |    \                           │   只存索引
│            [5|10]   [18|23]  [30|35]                    │
│           / | \     / | \    / | \                      │
│         [数据] ↔ [数据] ↔ [数据] ↔ [数据]               │ ← 叶子节点
│                  双向链表连接                            │   存数据
└─────────────────────────────────────────────────────────┘
```

**关键概念:**

- 聚簇索引 (主键) vs 非聚簇索引 (普通)
- 回表查询 vs 覆盖索引
- 最左前缀原则
- 索引失效场景

---

### 2. 事务隔离 ([TransactionIsolation.java](./TransactionIsolation.java))

| 隔离级别            | 脏读 | 不可重复读 | 幻读 |
| ------------------- | ---- | ---------- | ---- |
| READ UNCOMMITTED    | ❌   | ❌         | ❌   |
| READ COMMITTED      | ✅   | ❌         | ❌   |
| **REPEATABLE READ** | ✅   | ✅         | ❌\* |
| SERIALIZABLE        | ✅   | ✅         | ✅   |

\*MySQL InnoDB 通过间隙锁 + MVCC 可避免大部分幻读

**MVCC 核心:** 版本链 + Read View 实现读写不阻塞

---

### 3. 锁机制 ([LockMechanism.java](./LockMechanism.java))

```
悲观锁 (FOR UPDATE):
SELECT * FROM products WHERE id = 1 FOR UPDATE;
-- 锁定行，其他事务等待

乐观锁 (Version):
UPDATE products SET stock=99, version=version+1
WHERE id=1 AND version=1;
-- 版本不匹配则失败
```

**锁类型:** 行锁、表锁、间隙锁、临键锁

---

### 4. MyBatis ([MyBatisBasics.java](./MyBatisBasics.java) / [DynamicSqlDemo.java](./DynamicSqlDemo.java))

**动态 SQL 标签:**

| 标签        | 用途            |
| ----------- | --------------- |
| `<if>`      | 条件判断        |
| `<where>`   | 智能 WHERE      |
| `<set>`     | 智能 SET        |
| `<foreach>` | 循环 (IN、批量) |
| `<choose>`  | 多选一          |
| `<sql>`     | 片段复用        |

```xml
<select id="findByCondition">
    SELECT * FROM users
    <where>
        <if test="name != null">AND name = #{name}</if>
        <if test="status != null">AND status = #{status}</if>
    </where>
</select>
```

---

### 5. MyBatis-Plus ([MyBatisPlusDemo.java](./MyBatisPlusDemo.java))

**BaseMapper 提供开箱即用的 CRUD:**

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 无需任何代码，自动拥有 CRUD 方法
}
```

**LambdaQueryWrapper (推荐):**

```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1)
       .like(User::getUsername, "张")
       .orderByDesc(User::getCreatedAt);

List<User> users = userMapper.selectList(wrapper);
```

**自动功能:** 自动填充、乐观锁、逻辑删除、分页

---

### 6. JPA 进阶 ([JpaAdvanced.java](./JpaAdvanced.java))

**方法名派生查询:**

```java
// 自动生成 SQL
User findByUsername(String username);
List<User> findByAgeGreaterThanAndStatus(Integer age, Integer status);
```

**@Query 自定义:**

```java
@Query("SELECT u FROM User u WHERE u.status = :status")
Page<User> findByStatus(@Param("status") Integer status, Pageable pageable);
```

**审计功能:** `@CreatedDate`, `@LastModifiedDate`

---

### 7. 分库分表 ([ShardingDemo.java](./ShardingDemo.java))

```
水平分片示例:
┌─────────┐    ┌─────────┐
│   ds0   │    │   ds1   │
├─────────┤    ├─────────┤
│ order_0 │    │ order_0 │
│ order_1 │    │ order_1 │
└─────────┘    └─────────┘

分片策略: user_id % 2 → ds0/ds1
         order_id % 2 → order_0/order_1
```

**ShardingSphere 核心功能:** 数据分片、读写分离、数据加密

---

### 8. SQL 优化 ([SqlOptimization.java](./SqlOptimization.java))

**EXPLAIN 关键字段:**

| 字段  | 说明                                 |
| ----- | ------------------------------------ |
| type  | 访问类型 (const > ref > range > ALL) |
| key   | 实际使用的索引                       |
| rows  | 预估扫描行数                         |
| Extra | Using index (覆盖索引) ✅            |
|       | Using filesort ⚠️                    |
|       | Using temporary ⚠️                   |

**优化技巧:**

- 避免 SELECT \*
- 使用覆盖索引
- 避免索引列使用函数
- 分页优化 (游标分页)
- 批量操作

---

## 🎯 实战项目: 订单管理系统

[OrmProject.java](./OrmProject.java) 提供了完整的实战指南：

### 核心功能

| 模块     | 技术点               |
| -------- | -------------------- |
| 用户管理 | 注册、登录、密码加密 |
| 商品管理 | 分类、分页、缓存     |
| 订单管理 | 库存扣减、事务、并发 |
| 报表统计 | 复杂查询、SQL 优化   |

### 库存扣减 (核心)

```java
// 原子操作 (推荐)
@Update("UPDATE products SET stock = stock - #{quantity} " +
        "WHERE id = #{id} AND stock >= #{quantity}")
int deductStock(@Param("id") Long id, @Param("quantity") int quantity);
```

---

## 🔑 关键收获

1. **B+ 树** 是 MySQL InnoDB 的核心索引结构
2. **MVCC** 实现了读写不阻塞，提高并发
3. **MyBatis-Plus** 大幅提高开发效率
4. **分库分表** 解决单库单表性能瓶颈
5. **EXPLAIN** 是 SQL 优化的第一步
6. **库存扣减** 要用原子操作或悲观锁

---

## 🛠 环境准备

```bash
# 启动 MySQL
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=demo \
  mysql:8.0

# 访问数据库
mysql -h localhost -P 3306 -u root -p123456
```

---

## 📈 进阶方向

- [ ] 学习 Redis 缓存
- [ ] 了解分布式事务 (Seata)
- [ ] 进入 Phase 12: 数据库进阶

---

> 📝 Phase 11 完成！接下来进入 [Phase 12: 数据库进阶](../phase12/README.md)
