# Flyway 完全指南：从零开始掌握数据库迁移

---

## 📚 目录

1. [Flyway 简介](#1-flyway-简介)
2. [为什么需要 Flyway](#2-为什么需要-flyway)
3. [核心概念](#3-核心概念)
4. [工作原理](#4-工作原理)
5. [安装与配置](#5-安装与配置)
6. [使用方法](#6-使用方法)
7. [命令详解](#7-命令详解)
8. [Spring Boot 集成](#8-spring-boot-集成)
9. [最佳实践](#9-最佳实践)
10. [常见问题](#10-常见问题)

---

## 1. Flyway 简介

### 1.1 什么是 Flyway？

Flyway 是一个开源的**数据库迁移工具**，它帮助开发团队管理和追踪数据库的版本变化。

```
┌─────────────────────────────────────────────────────────────┐
│                        Flyway                                │
│                                                              │
│   "Version control for your database"                       │
│   (数据库的版本控制工具)                                      │
│                                                              │
│   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐    │
│   │  V1.0   │ → │  V1.1   │ → │  V1.2   │ → │  V2.0   │    │
│   │ 初始化   │   │ 添加表  │   │ 修改列  │   │ 新功能  │    │
│   └─────────┘   └─────────┘   └─────────┘   └─────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 支持的数据库

| 数据库          | 支持情况 |
| --------------- | -------- |
| MySQL / MariaDB | ✅       |
| PostgreSQL      | ✅       |
| Oracle          | ✅       |
| SQL Server      | ✅       |
| SQLite          | ✅       |
| H2              | ✅       |
| 更多...         | ✅       |

---

## 2. 为什么需要 Flyway

### 2.1 没有 Flyway 时的痛点

```
❌ 传统方式的问题：

开发环境数据库  →  测试环境数据库  →  生产环境数据库
     ↓                   ↓                   ↓
  手动执行SQL         手动执行SQL         手动执行SQL
     ↓                   ↓                   ↓
  容易遗漏            版本不一致          难以追踪变更
```

**常见问题：**

- 🔴 多人开发时数据库结构冲突
- 🔴 不知道当前数据库是什么版本
- 🔴 部署时漏执行 SQL 脚本
- 🔴 无法回滚到之前的版本
- 🔴 不同环境数据库结构不一致

### 2.2 使用 Flyway 后

```
✅ Flyway 解决方案：

SQL脚本 (Git版本控制)
        ↓
    ┌───────┐
    │Flyway │  ← 自动检测并执行未运行的脚本
    └───────┘
        ↓
┌───────────────────────────────────────┐
│  开发环境  │  测试环境  │  生产环境   │  ← 保持一致！
└───────────────────────────────────────┘
```

---

## 3. 核心概念

### 3.1 迁移脚本 (Migration)

Flyway 使用 SQL 脚本来管理数据库变更，这些脚本称为"迁移"。

#### 迁移类型

| 类型           | 前缀 | 说明               | 可重复执行 |
| -------------- | ---- | ------------------ | ---------- |
| **版本化迁移** | V    | 只执行一次         | ❌         |
| **可重复迁移** | R    | 每次内容变化都执行 | ✅         |
| **撤销迁移**   | U    | 回滚脚本（付费版） | -          |

### 3.2 命名规范

```
V{版本号}__{描述}.sql

┌─────────────────────────────────────────────────────┐
│                                                     │
│    V 2 _ 1 __ Create_user_table .sql               │
│    │ │   │  │          │         │                 │
│    │ │   │  │          │         └── 文件扩展名    │
│    │ │   │  │          └── 描述（用下划线分隔）    │
│    │ │   │  └── 双下划线分隔符（必须）             │
│    │ │   └── 次版本号                              │
│    │ └── 主版本号                                  │
│    └── 前缀（V=版本化, R=可重复）                  │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 合法的命名示例

```
V1__Initial_schema.sql
V1.1__Add_user_table.sql
V1.1.1__Add_email_column.sql
V2__Create_order_table.sql
V20231225.1__Holiday_update.sql
R__Create_views.sql
```

### 3.3 历史记录表 (flyway_schema_history)

Flyway 会自动创建一张表来记录执行历史：

```sql
-- flyway_schema_history 表结构
+----------------+-------------+--------------------------------------+
| installed_rank | version     | description                          |
+----------------+-------------+--------------------------------------+
| 1              | 1           | Initial schema                       |
| 2              | 1.1         | Add user table                       |
| 3              | 2           | Create order table                   |
+----------------+-------------+--------------------------------------+
| script                          | checksum    | success | installed_on |
+----------------+-------------+--------------------------------------+
| V1__Initial_schema.sql          | -123456789  | true    | 2024-01-01   |
| V1.1__Add_user_table.sql        | -987654321  | true    | 2024-01-02   |
| V2__Create_order_table.sql      | -456789123  | true    | 2024-01-03   |
+----------------+-------------+--------------------------------------+
```

---

## 4. 工作原理

### 4.1 执行流程

```
┌────────────────────────────────────────────────────────────┐
│                    Flyway 执行流程                          │
└────────────────────────────────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  1. 连接目标数据库       │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  2. 检查历史记录表       │
              │  (flyway_schema_history)│
              └─────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              │ 表存在？                   │
              └─────────────┬─────────────┘
                   │                │
                   ▼ 否             ▼ 是
        ┌─────────────────┐  ┌─────────────────┐
        │ 创建历史记录表   │  │ 读取已执行版本   │
        └─────────────────┘  └─────────────────┘
                   │                │
                   └────────┬───────┘
                            ▼
              ┌─────────────────────────┐
              │ 3. 扫描迁移脚本目录      │
              │    (db/migration)       │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ 4. 比较并找出待执行脚本  │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ 5. 按版本顺序执行脚本    │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ 6. 记录执行结果到历史表  │
              └─────────────────────────┘
```

### 4.2 版本比较示例

```
磁盘上的迁移脚本：          数据库历史表中：
┌──────────────────┐       ┌──────────────────┐
│ V1__Init.sql     │  ←→   │ V1 ✅ 已执行      │
│ V1.1__Users.sql  │  ←→   │ V1.1 ✅ 已执行    │
│ V2__Orders.sql   │  ←→   │ (无记录)         │  ← 待执行！
│ V3__Products.sql │  ←→   │ (无记录)         │  ← 待执行！
└──────────────────┘       └──────────────────┘

结论：Flyway 将按顺序执行 V2 和 V3
```

---

## 5. 安装与配置

### 5.1 安装方式

#### 方式一：命令行工具

```bash
# macOS
brew install flyway

# Windows (使用 Chocolatey)
choco install flyway

# Linux (下载解压)
wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.22.0/flyway-commandline-9.22.0-linux-x64.tar.gz | tar xvz
sudo ln -s `pwd`/flyway-9.22.0/flyway /usr/local/bin
```

#### 方式二：Maven 依赖

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.0</version>
</dependency>

<!-- MySQL 需要额外添加 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>9.22.0</version>
</dependency>
```

#### 方式三：Gradle 依赖

```groovy
// build.gradle
implementation 'org.flywaydb:flyway-core:9.22.0'
implementation 'org.flywaydb:flyway-mysql:9.22.0'  // MySQL
```

### 5.2 配置文件

创建 `flyway.conf` 配置文件：

```properties
# 数据库连接配置
flyway.url=jdbc:mysql://localhost:3306/mydb
flyway.user=root
flyway.password=123456

# 迁移脚本位置
flyway.locations=filesystem:sql/migrations,classpath:db/migration

# Schema 设置
flyway.schemas=mydb
flyway.defaultSchema=mydb

# 表名设置
flyway.table=flyway_schema_history

# 编码设置
flyway.encoding=UTF-8

# 是否允许混合迁移
flyway.mixed=false

# 基线版本（用于已有数据库）
flyway.baselineVersion=1
flyway.baselineOnMigrate=false
```

---

## 6. 使用方法

### 6.1 项目结构

```
my-project/
├── src/
│   └── main/
│       └── resources/
│           └── db/
│               └── migration/           ← 迁移脚本目录
│                   ├── V1__Initial_schema.sql
│                   ├── V1.1__Create_user_table.sql
│                   ├── V1.2__Create_order_table.sql
│                   ├── V2__Add_indexes.sql
│                   └── R__Create_views.sql
├── pom.xml
└── flyway.conf                          ← 配置文件（可选）
```

### 6.2 编写迁移脚本

#### V1\_\_Initial_schema.sql

```sql
-- 初始化数据库 schema
CREATE DATABASE IF NOT EXISTS myapp;
USE myapp;

-- 创建基础配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### V1.1\_\_Create_user_table.sql

```sql
-- 创建用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '1:正常 0:禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### V1.2\_\_Create_order_table.sql

```sql
-- 创建订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 添加订单明细表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### V2\_\_Add_user_columns.sql

```sql
-- 为用户表添加新字段
ALTER TABLE users
    ADD COLUMN phone VARCHAR(20) AFTER email,
    ADD COLUMN avatar_url VARCHAR(500) AFTER phone,
    ADD COLUMN last_login_at TIMESTAMP NULL;

-- 添加索引
CREATE INDEX idx_phone ON users(phone);
```

#### R\_\_Create_views.sql (可重复迁移)

```sql
-- 可重复迁移：每次内容变化都会重新执行
-- 适合视图、存储过程等

DROP VIEW IF EXISTS v_user_orders;

CREATE VIEW v_user_orders AS
SELECT
    u.id AS user_id,
    u.username,
    COUNT(o.id) AS order_count,
    COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.username;
```

### 6.3 命令行使用

```bash
# 执行迁移
flyway migrate

# 查看状态
flyway info

# 验证迁移
flyway validate

# 清空数据库（危险！）
flyway clean

# 修复历史记录
flyway repair

# 设置基线
flyway baseline
```

---

## 7. 命令详解

### 7.1 命令总览

```
┌─────────────────────────────────────────────────────────────────┐
│                      Flyway Commands                             │
├───────────┬─────────────────────────────────────────────────────┤
│  migrate  │ 执行所有待处理的迁移脚本                             │
├───────────┼─────────────────────────────────────────────────────┤
│  info     │ 显示所有迁移的详细信息和状态                         │
├───────────┼─────────────────────────────────────────────────────┤
│  validate │ 验证已应用的迁移是否被修改                           │
├───────────┼─────────────────────────────────────────────────────┤
│  clean    │ 删除配置的 schema 中的所有对象 ⚠️                    │
├───────────┼─────────────────────────────────────────────────────┤
│  repair   │ 修复 schema 历史表                                   │
├───────────┼─────────────────────────────────────────────────────┤
│  baseline │ 为已存在的数据库设置基线版本                         │
├───────────┼─────────────────────────────────────────────────────┤
│  undo     │ 撤销最近的迁移（Teams 版本）                         │
└───────────┴─────────────────────────────────────────────────────┘
```

### 7.2 migrate 命令

```bash
# 基本用法
flyway migrate

# 指定配置
flyway -url=jdbc:mysql://localhost:3306/mydb \
       -user=root \
       -password=123456 \
       migrate

# 使用配置文件
flyway -configFiles=conf/flyway.conf migrate
```

**执行结果示例：**

```
Flyway Community Edition 9.22.0

Database: jdbc:mysql://localhost:3306/mydb (MySQL 8.0)
Successfully validated 4 migrations (execution time 00:00.028s)
Current version of schema `mydb`: 1.1
Migrating schema `mydb` to version "1.2 - Create order table"
Migrating schema `mydb` to version "2 - Add user columns"
Successfully applied 2 migrations to schema `mydb` (execution time 00:00.156s)
```

### 7.3 info 命令

```bash
flyway info
```

**输出示例：**

```
+-----------+---------+---------------------+----------+---------------------+----------+
| Category  | Version | Description         | Type     | Installed On        | State    |
+-----------+---------+---------------------+----------+---------------------+----------+
| Versioned | 1       | Initial schema      | SQL      | 2024-01-01 10:00:00 | Success  |
| Versioned | 1.1     | Create user table   | SQL      | 2024-01-02 11:00:00 | Success  |
| Versioned | 1.2     | Create order table  | SQL      | 2024-01-03 12:00:00 | Success  |
| Versioned | 2       | Add user columns    | SQL      |                     | Pending  |
| Repeatable|         | Create views        | SQL      |                     | Pending  |
+-----------+---------+---------------------+----------+---------------------+----------+
```

### 7.4 validate 命令

```bash
flyway validate
```

**用途：**

- 检查迁移脚本是否被修改
- 验证校验和是否匹配
- 确保迁移顺序正确

### 7.5 baseline 命令

**场景：** 将 Flyway 应用到已有数据库

```bash
# 设置基线版本为 1
flyway -baselineVersion=1 -baselineDescription="Initial baseline" baseline
```

```
应用前：                          应用后：
┌────────────────────────┐      ┌────────────────────────┐
│ 已有数据库（无历史表）  │  →   │ 已有数据库              │
│                        │      │ + flyway_schema_history │
│ - users 表             │      │ - V1 Baseline ✅        │
│ - orders 表            │      │                        │
└────────────────────────┘      └────────────────────────┘

之后执行 migrate 将从 V1.1 开始
```

---

## 8. Spring Boot 集成

### 8.1 添加依赖

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>

    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- MySQL 8.x 需要此依赖 -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 8.2 配置文件

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

  flyway:
    # 启用 Flyway
    enabled: true

    # 迁移脚本位置
    locations: classpath:db/migration

    # 编码
    encoding: UTF-8

    # 历史表名
    table: flyway_schema_history

    # 当历史表不存在时是否自动创建
    baseline-on-migrate: true
    baseline-version: 0

    # 是否允许乱序执行
    out-of-order: false

    # 验证迁移脚本
    validate-on-migrate: true

    # 是否清理（生产环境务必设为 false）
    clean-disabled: true
```

### 8.3 Java 配置（可选）

```java
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .table("flyway_schema_history")
                .encoding("UTF-8")
                .outOfOrder(false)
                .validateOnMigrate(true)
                .load();
    }
}
```

### 8.4 项目结构

```
src/
└── main/
    ├── java/
    │   └── com/example/
    │       ├── Application.java
    │       └── config/
    │           └── FlywayConfig.java
    └── resources/
        ├── application.yml
        └── db/
            └── migration/
                ├── V1__Create_schema.sql
                ├── V1.1__Create_users_table.sql
                ├── V1.2__Create_orders_table.sql
                └── R__Create_views.sql
```

### 8.5 多环境配置

```yaml
# application-dev.yml (开发环境)
spring:
  flyway:
    enabled: true
    clean-disabled: false # 开发环境可以清理

---
# application-prod.yml (生产环境)
spring:
  flyway:
    enabled: true
    clean-disabled: true # 生产环境禁止清理！
    validate-on-migrate: true
```

### 8.6 启动日志

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

INFO  --- [main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 9.22.0
INFO  --- [main] o.f.c.i.database.base.DatabaseType       : Database: jdbc:mysql://localhost:3306/mydb (MySQL 8.0)
INFO  --- [main] o.f.core.internal.command.DbValidate     : Successfully validated 5 migrations
INFO  --- [main] o.f.core.internal.command.DbMigrate      : Current version of schema `mydb`: 1.1
INFO  --- [main] o.f.core.internal.command.DbMigrate      : Migrating schema `mydb` to version "1.2"
INFO  --- [main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration
```

---

## 9. 最佳实践

### 9.1 命名规范

```
推荐的命名格式：

V{YYYYMMDD}.{序号}__{描述}.sql

示例：
V20240101.1__Initial_schema.sql
V20240101.2__Create_user_table.sql
V20240115.1__Add_user_phone_column.sql
V20240120.1__Create_order_table.sql
```

### 9.2 脚本编写规范

```sql
-- ✅ 好的实践
-- ========================================
-- Migration: V1.1__Create_user_table.sql
-- Author: zhangsan
-- Date: 2024-01-15
-- Description: 创建用户表
-- ========================================

-- 每个语句一个操作
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 添加注释说明
-- 创建索引以提高查询性能
CREATE INDEX idx_username ON users(username);


-- ❌ 不好的实践
-- 不要在一个迁移中做太多事情
-- 不要使用 IF EXISTS（不利于版本控制）
-- 不要修改已执行的迁移脚本
```

### 9.3 版本控制策略

```
┌─────────────────────────────────────────────────────────────┐
│                    Git + Flyway 工作流                       │
└─────────────────────────────────────────────────────────────┘

feature/add-products 分支:
│
├── V20240120.1__Create_products_table.sql
│
│    合并到 develop
│         │
│         ▼
develop 分支:
├── V20240101.1__Initial.sql
├── V20240110.1__Users.sql
├── V20240120.1__Create_products_table.sql  ← 新增
│
│    发布到 main
│         │
│         ▼
main 分支 (生产):
├── V20240101.1__Initial.sql ✅ 已执行
├── V20240110.1__Users.sql   ✅ 已执行
├── V20240120.1__Create_products_table.sql  ← 待执行
```

### 9.4 团队协作规范

```markdown
## 团队 Flyway 使用规范

### 1. 创建迁移脚本前

- [ ] 从 develop 分支拉取最新代码
- [ ] 确认当前最大版本号
- [ ] 使用统一的命名格式

### 2. 编写迁移脚本

- [ ] 脚本头部添加注释
- [ ] 一个脚本只做一件事
- [ ] 编写可重复执行的 DDL（尽量）
- [ ] 本地测试通过

### 3. 提交前检查

- [ ] 运行 `flyway validate`
- [ ] 运行 `flyway info` 确认状态
- [ ] Code Review

### 4. 禁止行为

- ❌ 修改已经执行的迁移脚本
- ❌ 删除已经执行的迁移脚本
- ❌ 在生产环境使用 clean 命令
```

### 9.5 回滚策略

```sql
-- 由于社区版不支持 undo，推荐使用补偿脚本

-- V1.5__Add_phone_column.sql (原迁移)
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

-- V1.6__Remove_phone_column.sql (补偿迁移)
ALTER TABLE users DROP COLUMN phone;
```

### 9.6 大型数据迁移

```sql
-- V2.1__Migrate_user_data.sql
-- 大数据量迁移时分批处理

-- 创建临时表
CREATE TABLE users_new LIKE users;

-- 分批迁移数据
INSERT INTO users_new
SELECT * FROM users WHERE id BETWEEN 1 AND 10000;

INSERT INTO users_new
SELECT * FROM users WHERE id BETWEEN 10001 AND 20000;

-- ... 更多批次

-- 切换表
RENAME TABLE users TO users_old, users_new TO users;

-- 验证后删除旧表
-- DROP TABLE users_old;
```

---

## 10. 常见问题

### 10.1 迁移脚本被修改

```
问题：Validate failed: Migration checksum mismatch

原因：已执行的脚本内容被修改

解决方案：
1. 恢复脚本原始内容（推荐）
2. 使用 repair 命令修复校验和（谨慎使用）
   flyway repair
```

### 10.2 版本冲突

```
问题：多人开发时使用了相同的版本号

解决方案：
1. 使用日期作为版本号前缀
   V20240115.1__xxx.sql

2. 合并前执行 flyway info 检查

3. 发生冲突时重命名版本号
```

### 10.3 生产环境迁移失败

```
问题：迁移执行一半失败了

解决方案：
1. 检查 flyway_schema_history 表中的 success 字段
2. 手动修复失败的部分
3. 使用 flyway repair 修复历史记录
4. 重新执行迁移

预防措施：
1. 先在测试环境验证
2. 备份数据库
3. 使用事务（如果数据库支持 DDL 事务）
```

### 10.4 已有数据库如何接入

```bash
# 1. 导出当前数据库结构作为 V1
mysqldump --no-data mydb > V1__Initial_baseline.sql

# 2. 设置基线
flyway -baselineVersion=1 baseline

# 3. 之后的变更从 V1.1 开始
# V1.1__Add_new_feature.sql
```

### 10.5 如何跳过某个迁移

```yaml
# application.yml
spring:
  flyway:
    # 忽略丢失的迁移
    ignore-missing-migrations: true

    # 忽略未来的迁移
    ignore-future-migrations: true
```

### 10.6 性能优化

```yaml
spring:
  flyway:
    # 批量执行
    batch: true

    # 使用流式读取大文件
    stream: true

    # 并行执行（付费版）
    # cherry-pick: ...
```

---

## 📋 附录：常用配置速查表

| 配置项                     | 说明            | 默认值                 |
| -------------------------- | --------------- | ---------------------- |
| `flyway.url`               | 数据库连接 URL  | -                      |
| `flyway.user`              | 数据库用户名    | -                      |
| `flyway.password`          | 数据库密码      | -                      |
| `flyway.locations`         | 迁移脚本位置    | classpath:db/migration |
| `flyway.table`             | 历史记录表名    | flyway_schema_history  |
| `flyway.schemas`           | 要管理的 Schema | 默认 Schema            |
| `flyway.encoding`          | 脚本编码        | UTF-8                  |
| `flyway.baselineVersion`   | 基线版本        | 1                      |
| `flyway.baselineOnMigrate` | 自动基线        | false                  |
| `flyway.validateOnMigrate` | 迁移时验证      | true                   |
| `flyway.outOfOrder`        | 允许乱序        | false                  |
| `flyway.cleanDisabled`     | 禁用 clean      | false                  |
| `flyway.mixed`             | 允许混合迁移    | false                  |

---

## 📚 参考资源

- 官方文档：https://flywaydb.org/documentation/
- GitHub：https://github.com/flyway/flyway
- Spring Boot 集成：https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway

---

> 📝 **文档版本**: 1.0  
> **更新日期**: 2024-01  
> **适用版本**: Flyway 9.x
