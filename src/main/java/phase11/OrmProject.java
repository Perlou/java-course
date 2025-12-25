package phase11;

/**
 * Phase 11 - Lesson 10: 订单管理系统实战
 * 
 * 🎯 学习目标:
 * 1. 综合应用 MySQL 和 ORM 知识
 * 2. 设计订单管理系统数据模型
 * 3. 使用 MyBatis-Plus 实现业务功能
 */
public class OrmProject {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 11 - 实战项目: 订单管理系统");
        System.out.println("=".repeat(60));

        // 1. 项目概述
        System.out.println("\n【1. 项目概述】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                  订单管理系统                            │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  功能模块:                                              │
                │  ├── 用户管理: 注册、登录、个人信息                     │
                │  ├── 商品管理: 商品分类、商品列表、库存                 │
                │  ├── 订单管理: 下单、支付、订单查询、取消                │
                │  └── 报表统计: 销售报表、热销商品                       │
                │                                                         │
                │  技术栈:                                                │
                │  ├── Spring Boot 3.x                                   │
                │  ├── MyBatis-Plus                                       │
                │  ├── MySQL 8.0                                          │
                │  └── Redis (缓存)                                       │
                │                                                         │
                │  学习重点:                                              │
                │  ├── 数据库设计                                        │
                │  ├── 事务处理                                          │
                │  ├── 并发控制 (库存扣减)                                │
                │  └── SQL 优化                                          │
                │                                                         │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 数据库设计
        System.out.println("=".repeat(60));
        System.out.println("【2. 数据库设计】");
        System.out.println("""
                ER 关系图:

                ┌────────────┐      ┌────────────┐      ┌────────────┐
                │   users    │      │   orders   │      │ order_items│
                ├────────────┤      ├────────────┤      ├────────────┤
                │ id (PK)    │◀─────│ user_id(FK)│      │ id (PK)    │
                │ username   │      │ id (PK)    │◀─────│ order_id   │
                │ password   │      │ order_no   │      │ product_id │
                │ phone      │      │ total      │      │ quantity   │
                │ status     │      │ status     │      │ price      │
                └────────────┘      │ created_at │      └────────────┘
                                    └────────────┘           │
                                                             │
                ┌────────────┐      ┌────────────┐           │
                │ categories │      │  products  │◀──────────┘
                ├────────────┤      ├────────────┤
                │ id (PK)    │◀─────│ category_id│
                │ name       │      │ id (PK)    │
                │ parent_id  │      │ name       │
                └────────────┘      │ price      │
                                    │ stock      │
                                    │ status     │
                                    └────────────┘

                SQL 建表语句:

                -- 用户表
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    phone VARCHAR(20),
                    status TINYINT DEFAULT 1,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_username (username),
                    INDEX idx_phone (phone)
                );

                -- 商品分类表
                CREATE TABLE categories (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(50) NOT NULL,
                    parent_id BIGINT DEFAULT 0,
                    sort_order INT DEFAULT 0,
                    INDEX idx_parent (parent_id)
                );

                -- 商品表
                CREATE TABLE products (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    category_id BIGINT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    description TEXT,
                    price DECIMAL(10, 2) NOT NULL,
                    stock INT NOT NULL DEFAULT 0,
                    status TINYINT DEFAULT 1,
                    version INT DEFAULT 0,  -- 乐观锁
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_category (category_id),
                    INDEX idx_status_price (status, price)
                );

                -- 订单表
                CREATE TABLE orders (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_no VARCHAR(30) NOT NULL UNIQUE,
                    user_id BIGINT NOT NULL,
                    total_amount DECIMAL(10, 2) NOT NULL,
                    status TINYINT DEFAULT 0,  -- 0待支付 1已支付 2已发货 3已完成 4已取消
                    payment_time DATETIME,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_user (user_id),
                    INDEX idx_status (status),
                    INDEX idx_created (created_at)
                );

                -- 订单明细表
                CREATE TABLE order_items (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT NOT NULL,
                    product_id BIGINT NOT NULL,
                    product_name VARCHAR(100) NOT NULL,
                    unit_price DECIMAL(10, 2) NOT NULL,
                    quantity INT NOT NULL,
                    INDEX idx_order (order_id)
                );
                """);

        // 3. 实体设计
        System.out.println("=".repeat(60));
        System.out.println("【3. 实体类设计】");
        System.out.println("""
                基础实体类:

                @Data
                @MappedSuperclass
                public abstract class BaseEntity {
                    @Id
                    @TableId(type = IdType.AUTO)
                    private Long id;

                    @TableField(fill = FieldFill.INSERT)
                    private LocalDateTime createdAt;

                    @TableField(fill = FieldFill.INSERT_UPDATE)
                    private LocalDateTime updatedAt;
                }

                商品实体:

                @Data
                @TableName("products")
                @EqualsAndHashCode(callSuper = true)
                public class Product extends BaseEntity {

                    private Long categoryId;
                    private String name;
                    private String description;
                    private BigDecimal price;
                    private Integer stock;
                    private Integer status;

                    @Version  // 乐观锁
                    private Integer version;
                }

                订单实体:

                @Data
                @TableName("orders")
                public class Order {

                    @TableId(type = IdType.AUTO)
                    private Long id;

                    private String orderNo;
                    private Long userId;
                    private BigDecimal totalAmount;
                    private Integer status;
                    private LocalDateTime paymentTime;
                    private LocalDateTime createdAt;

                    @TableField(exist = false)
                    private List<OrderItem> items;
                }

                订单明细:

                @Data
                @TableName("order_items")
                public class OrderItem {

                    @TableId(type = IdType.AUTO)
                    private Long id;

                    private Long orderId;
                    private Long productId;
                    private String productName;
                    private BigDecimal unitPrice;
                    private Integer quantity;
                }
                """);

        // 4. 库存扣减
        System.out.println("=".repeat(60));
        System.out.println("【4. 核心功能: 库存扣减】");
        System.out.println("""
                ⚠️ 高并发库存扣减是关键技术点

                方式 1: 悲观锁 (FOR UPDATE)

                @Transactional
                public void deductStock(Long productId, int quantity) {
                    // 加锁查询
                    Product product = productMapper.selectByIdForUpdate(productId);
                    if (product.getStock() < quantity) {
                        throw new BusinessException("库存不足");
                    }
                    // 扣减库存
                    product.setStock(product.getStock() - quantity);
                    productMapper.updateById(product);
                }

                // Mapper
                @Select("SELECT * FROM products WHERE id = #{id} FOR UPDATE")
                Product selectByIdForUpdate(Long id);

                方式 2: 乐观锁 (版本号)

                @Transactional
                public void deductStock(Long productId, int quantity) {
                    Product product = productMapper.selectById(productId);
                    if (product.getStock() < quantity) {
                        throw new BusinessException("库存不足");
                    }
                    // 使用 MyBatis-Plus 乐观锁
                    product.setStock(product.getStock() - quantity);
                    int rows = productMapper.updateById(product);
                    if (rows == 0) {
                        throw new BusinessException("库存扣减失败，请重试");
                    }
                }

                方式 3: 原子操作 (推荐)

                @Update("UPDATE products SET stock = stock - #{quantity} " +
                        "WHERE id = #{productId} AND stock >= #{quantity}")
                int deductStock(@Param("productId") Long productId,
                                @Param("quantity") int quantity);

                @Transactional
                public void deductStock(Long productId, int quantity) {
                    int rows = productMapper.deductStock(productId, quantity);
                    if (rows == 0) {
                        throw new BusinessException("库存不足");
                    }
                }
                """);

        // 5. 订单服务
        System.out.println("=".repeat(60));
        System.out.println("【5. 订单服务实现】");
        System.out.println("""
                @Service
                @RequiredArgsConstructor
                public class OrderService {

                    private final OrderMapper orderMapper;
                    private final OrderItemMapper orderItemMapper;
                    private final ProductMapper productMapper;

                    @Transactional(rollbackFor = Exception.class)
                    public Order createOrder(Long userId, List<OrderItemDTO> items) {
                        // 1. 生成订单号
                        String orderNo = generateOrderNo();

                        // 2. 计算总金额 & 扣减库存
                        BigDecimal totalAmount = BigDecimal.ZERO;
                        List<OrderItem> orderItems = new ArrayList<>();

                        for (OrderItemDTO dto : items) {
                            Product product = productMapper.selectById(dto.getProductId());
                            if (product == null || product.getStatus() != 1) {
                                throw new BusinessException("商品不存在或已下架");
                            }

                            // 扣减库存 (原子操作)
                            int rows = productMapper.deductStock(
                                dto.getProductId(), dto.getQuantity()
                            );
                            if (rows == 0) {
                                throw new BusinessException("商品 " + product.getName() + " 库存不足");
                            }

                            // 计算金额
                            BigDecimal amount = product.getPrice()
                                .multiply(BigDecimal.valueOf(dto.getQuantity()));
                            totalAmount = totalAmount.add(amount);

                            // 构建订单明细
                            OrderItem item = new OrderItem();
                            item.setProductId(product.getId());
                            item.setProductName(product.getName());
                            item.setUnitPrice(product.getPrice());
                            item.setQuantity(dto.getQuantity());
                            orderItems.add(item);
                        }

                        // 3. 保存订单
                        Order order = new Order();
                        order.setOrderNo(orderNo);
                        order.setUserId(userId);
                        order.setTotalAmount(totalAmount);
                        order.setStatus(0);  // 待支付
                        orderMapper.insert(order);

                        // 4. 保存订单明细
                        for (OrderItem item : orderItems) {
                            item.setOrderId(order.getId());
                            orderItemMapper.insert(item);
                        }

                        return order;
                    }

                    private String generateOrderNo() {
                        // 年月日时分秒 + 6位随机数
                        return LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                        ) + String.format("%06d", new Random().nextInt(1000000));
                    }
                }
                """);

        // 6. 复杂查询
        System.out.println("=".repeat(60));
        System.out.println("【6. 复杂查询实现】");
        System.out.println("""
                1. 订单列表查询 (带商品信息)

                // OrderMapper.xml
                <resultMap id="orderWithItems" type="Order">
                    <id property="id" column="id"/>
                    <result property="orderNo" column="order_no"/>
                    <result property="totalAmount" column="total_amount"/>
                    <result property="status" column="status"/>
                    <result property="createdAt" column="created_at"/>
                    <collection property="items" ofType="OrderItem">
                        <id property="id" column="item_id"/>
                        <result property="productName" column="product_name"/>
                        <result property="unitPrice" column="unit_price"/>
                        <result property="quantity" column="quantity"/>
                    </collection>
                </resultMap>

                <select id="findOrdersWithItems" resultMap="orderWithItems">
                    SELECT o.*, oi.id as item_id, oi.product_name,
                           oi.unit_price, oi.quantity
                    FROM orders o
                    LEFT JOIN order_items oi ON o.id = oi.order_id
                    WHERE o.user_id = #{userId}
                    ORDER BY o.created_at DESC
                </select>

                2. 销售报表 (按日统计)

                <select id="dailySalesReport" resultType="SalesReport">
                    SELECT DATE(created_at) as date,
                           COUNT(*) as order_count,
                           SUM(total_amount) as total_sales
                    FROM orders
                    WHERE status IN (1, 2, 3)
                      AND created_at >= #{startDate}
                      AND created_at < #{endDate}
                    GROUP BY DATE(created_at)
                    ORDER BY date
                </select>

                3. 热销商品 TOP 10

                <select id="topProducts" resultType="ProductRank">
                    SELECT p.id, p.name, p.price,
                           SUM(oi.quantity) as total_sold
                    FROM order_items oi
                    JOIN products p ON oi.product_id = p.id
                    JOIN orders o ON oi.order_id = o.id
                    WHERE o.status IN (1, 2, 3)
                      AND o.created_at >= #{startDate}
                    GROUP BY p.id
                    ORDER BY total_sold DESC
                    LIMIT 10
                </select>
                """);

        // 7. 项目结构
        System.out.println("=".repeat(60));
        System.out.println("【7. 项目结构】");
        System.out.println("""
                order-management/
                ├── pom.xml
                ├── src/main/java/com/example/order/
                │   ├── OrderApplication.java
                │   ├── config/
                │   │   ├── MyBatisPlusConfig.java     # 分页、乐观锁插件
                │   │   └── WebConfig.java
                │   ├── entity/
                │   │   ├── BaseEntity.java
                │   │   ├── User.java
                │   │   ├── Product.java
                │   │   ├── Order.java
                │   │   └── OrderItem.java
                │   ├── mapper/
                │   │   ├── UserMapper.java
                │   │   ├── ProductMapper.java
                │   │   ├── OrderMapper.java
                │   │   └── OrderItemMapper.java
                │   ├── service/
                │   │   ├── UserService.java
                │   │   ├── ProductService.java
                │   │   └── OrderService.java
                │   ├── controller/
                │   │   ├── UserController.java
                │   │   ├── ProductController.java
                │   │   └── OrderController.java
                │   ├── dto/
                │   │   ├── OrderCreateDTO.java
                │   │   └── OrderQueryDTO.java
                │   └── exception/
                │       ├── BusinessException.java
                │       └── GlobalExceptionHandler.java
                ├── src/main/resources/
                │   ├── application.yml
                │   ├── mapper/
                │   │   ├── UserMapper.xml
                │   │   ├── ProductMapper.xml
                │   │   └── OrderMapper.xml
                │   └── db/
                │       └── schema.sql
                └── src/test/java/
                    └── ...
                """);

        // 8. 开发建议
        System.out.println("=".repeat(60));
        System.out.println("【8. 开发建议】");
        System.out.println("""
                1. 开发顺序
                ┌─────┬───────────────────────────────────────────────────┐
                │ Day │ 任务                                              │
                ├─────┼───────────────────────────────────────────────────┤
                │  1  │ 项目初始化，数据库设计，建表                      │
                │  2  │ 实体类、Mapper、基础 CRUD                         │
                │  3  │ 用户模块 (注册、登录)                             │
                │  4  │ 商品模块 (列表、详情、分页)                       │
                │  5  │ 订单模块 (创建订单、库存扣减)                     │
                │  6  │ 订单模块 (订单查询、取消)                         │
                │  7  │ 报表统计、SQL 优化                                │
                └─────┴───────────────────────────────────────────────────┘

                2. 注意事项
                ✅ 库存扣减一定要用事务
                ✅ 高并发场景用乐观锁或原子操作
                ✅ 复杂查询先用 EXPLAIN 分析
                ✅ 分页查询注意深分页问题
                ✅ 敏感数据 (密码) 加密存储

                3. 扩展方向
                - 接入 Redis 缓存热点数据
                - 订单超时取消 (定时任务/延迟队列)
                - 分布式锁 (Redis/Redisson)
                - 分库分表 (订单量大时)
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 这是一个综合性实战项目");
        System.out.println("💡 重点掌握: 事务、库存扣减、复杂查询");
        System.out.println("💡 建议按开发顺序逐步实现");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 项目知识点:
 * 
 * 1. 数据库设计:
 * - ER 图设计
 * - 索引设计
 * 
 * 2. MyBatis-Plus:
 * - 基础 CRUD
 * - 乐观锁
 * - 自动填充
 * 
 * 3. 事务处理:
 * - @Transactional
 * - 库存扣减
 * 
 * 4. 复杂查询:
 * - resultMap
 * - 关联查询
 * - 统计报表
 */
