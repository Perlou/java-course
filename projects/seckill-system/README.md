# 🔥 秒杀系统 - Phase 12 实战项目

> 综合运用 Redis + RabbitMQ + 分布式锁 实现高并发秒杀系统

## 📋 项目概述

本项目是 Phase 12 的实战项目，演示如何使用 Redis、RabbitMQ 和分布式锁解决高并发场景下的库存扣减问题。

### 技术栈

- **Spring Boot 3.2** - 应用框架
- **Redis** - 库存缓存、Lua 脚本原子操作
- **RabbitMQ** - 异步下单、削峰填谷
- **MyBatis-Plus** - 数据访问
- **MySQL** - 数据持久化
- **Redisson** - 分布式锁 (备用)

## 🏗️ 项目结构

```
seckill-system/
├── src/main/java/com/example/seckill/
│   ├── SeckillApplication.java     # 启动类
│   ├── controller/                  # 接口层
│   │   └── SeckillController.java
│   ├── service/                     # 服务层
│   │   ├── SeckillService.java      # 秒杀核心服务
│   │   └── StockInitializer.java    # 库存预热
│   ├── mq/                          # 消息队列
│   │   ├── SeckillMessage.java
│   │   ├── SeckillMessageProducer.java
│   │   └── SeckillMessageConsumer.java
│   ├── mapper/                      # 数据访问
│   ├── entity/                      # 实体类
│   ├── config/                      # 配置类
│   └── common/                      # 通用类
├── src/main/resources/
│   ├── application.yml              # 应用配置
│   ├── lua/seckill.lua              # Lua 脚本
│   ├── db/migration/                # 数据库迁移
│   └── static/index.html            # 前端页面
└── docker-compose.yml               # Docker 环境
```

## 🚀 快速开始

### 1. 启动基础设施

```bash
cd /Users/perlou/Desktop/personal/java-course/projects/seckill-system

# 启动 MySQL + Redis + RabbitMQ
docker-compose up -d

# 等待服务启动完成
docker-compose ps
```

### 2. 启动应用

```bash
# 方式一：Maven 启动
mvn spring-boot:run

# 方式二：IDEA 启动
# 运行 SeckillApplication.java
```

### 3. 访问系统

- **前端页面**: http://localhost:8080
- **Swagger 文档**: http://localhost:8080/swagger-ui.html
- **RabbitMQ 管理**: http://localhost:15673 (admin/admin123)

## 📡 API 接口

| 方法 | 路径                                     | 描述              |
| ---- | ---------------------------------------- | ----------------- |
| GET  | `/api/seckill/goods`                     | 获取秒杀商品列表  |
| GET  | `/api/seckill/goods/{id}`                | 获取商品详情      |
| POST | `/api/seckill/do?userId=1&goodsId=1`     | 执行秒杀          |
| GET  | `/api/seckill/result?userId=1&goodsId=1` | 查询秒杀结果      |
| POST | `/api/seckill/reset/{id}`                | 重置秒杀 (测试用) |

## 🔧 核心实现

### 1. Lua 脚本原子扣减

```lua
-- 检查库存
local stock = tonumber(redis.call('GET', KEYS[1]))
if stock <= 0 then return 0 end

-- 检查是否已购买
local bought = redis.call('SISMEMBER', KEYS[2], ARGV[1])
if bought == 1 then return -1 end

-- 扣减库存 + 记录购买
redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])
return 1
```

### 2. 异步下单流程

```
用户请求 -> Redis Lua 扣库存 -> 发送 MQ 消息 -> 返回"排队中"
                                    |
                                    v
                         消费者异步创建订单 -> 更新结果到 Redis
                                    |
                                    v
                              用户轮询结果
```

## 🧪 测试

### 并发测试

使用 JMeter 或 curl 进行并发测试：

```bash
# 模拟多用户秒杀
for i in {1..10}; do
  curl -X POST "http://localhost:8080/api/seckill/do?userId=$i&goodsId=1" &
done
wait

# 查看库存
curl http://localhost:8080/api/seckill/goods/1
```

### 重置测试

```bash
# 重置商品1的秒杀状态
curl -X POST http://localhost:8080/api/seckill/reset/1
```

## 📚 学习要点

1. **Redis Lua 脚本** - 保证库存扣减的原子性
2. **消息队列削峰** - 异步处理订单，降低数据库压力
3. **本地内存标记** - 减少 Redis 访问，提升性能
4. **乐观锁** - 数据库层面防止超卖
5. **幂等性** - 防止重复消费

## 🔗 相关文档

- [Phase 12 - Redis 缓存](../../src/main/java/phase12/README.md)
- [DistributedLock.java](../../src/main/java/phase12/DistributedLock.java)
- [SeckillProject.java (教学版)](../../src/main/java/phase12/SeckillProject.java)
