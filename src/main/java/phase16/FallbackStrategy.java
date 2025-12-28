package phase16;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 服务降级策略 (Fallback Strategy)
 * 
 * 当服务不可用时，提供备选方案，保证核心功能可用
 * 
 * 降级类型：
 * 1. 功能降级：关闭非核心功能
 * 2. 数据降级：返回缓存/静态数据
 * 3. 体验降级：降低服务质量
 * 4. 异步降级：同步改异步
 */
public class FallbackStrategy {

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("          Phase 16: 服务降级策略 (Fallback)          ");
        System.out.println("====================================================");

        System.out.println("\n[1] 降级类型概览\n");
        showDegradationTypes();

        System.out.println("\n[2] 缓存降级策略\n");
        demonstrateCacheFallback();

        System.out.println("\n[3] 静态降级策略\n");
        demonstrateStaticFallback();

        System.out.println("\n[4] 多级降级链\n");
        demonstrateFallbackChain();
    }

    // ==================== 1. 降级类型 ====================

    private static void showDegradationTypes() {
        System.out.println("+------------+--------------------------------------------+");
        System.out.println("|  降级类型   |                   示例                     |");
        System.out.println("+------------+--------------------------------------------+");
        System.out.println("|  功能降级   | 秒杀时关闭猜你喜欢、商品评价等非核心功能     |");
        System.out.println("+------------+--------------------------------------------+");
        System.out.println("|  数据降级   | 库存查询失败时返回[有货]，让订单继续        |");
        System.out.println("+------------+--------------------------------------------+");
        System.out.println("|  体验降级   | 图片从高清改为缩略图，减少带宽消耗          |");
        System.out.println("+------------+--------------------------------------------+");
        System.out.println("|  异步降级   | 短信验证码排队发送，先返回[发送中]          |");
        System.out.println("+------------+--------------------------------------------+");
    }

    // ==================== 2. 缓存降级 ====================

    static class CacheFallbackService {
        private final Map<String, String> cache = new ConcurrentHashMap<>();
        private volatile boolean remoteAvailable = true;

        public CacheFallbackService() {
            cache.put("product:1001", "iPhone 15 Pro - 8999");
            cache.put("product:1002", "MacBook Pro - 14999");
            cache.put("product:1003", "AirPods Pro - 1899");
        }

        public String getProduct(String productId) {
            if (remoteAvailable) {
                try {
                    String data = callRemoteService(productId);
                    cache.put(productId, data);
                    return "[远程] " + data;
                } catch (Exception e) {
                    System.out.println("    [!] 远程服务异常，降级到缓存");
                }
            }

            String cached = cache.get(productId);
            if (cached != null) {
                return "[缓存] " + cached;
            }
            return "[默认] 商品信息暂不可用";
        }

        private String callRemoteService(String productId) {
            if (!remoteAvailable)
                throw new RuntimeException("Service Down");
            return "Product " + productId + " - Real-time Data";
        }

        public void setRemoteAvailable(boolean available) {
            this.remoteAvailable = available;
        }
    }

    private static void demonstrateCacheFallback() {
        CacheFallbackService service = new CacheFallbackService();

        System.out.println("场景: 商品详情查询服务");
        System.out.println();

        System.out.println("1. 远程服务正常:");
        System.out.println("   " + service.getProduct("product:1001"));

        System.out.println("\n2. 远程服务异常:");
        service.setRemoteAvailable(false);
        System.out.println("   " + service.getProduct("product:1001"));
        System.out.println("   " + service.getProduct("product:1002"));
        System.out.println("   " + service.getProduct("product:9999"));
    }

    // ==================== 3. 静态降级 ====================

    private static void demonstrateStaticFallback() {
        System.out.println("场景: 推荐系统降级");
        System.out.println();

        Supplier<String[]> recommendService = () -> {
            throw new RuntimeException("推荐服务超时");
        };

        String[] hotItems = { "热销商品1", "热销商品2", "热销商品3" };

        String[] result;
        try {
            result = recommendService.get();
            System.out.println("个性化推荐: " + String.join(", ", result));
        } catch (Exception e) {
            System.out.println("[!] 推荐服务异常，使用静态热销榜单");
            result = hotItems;
            System.out.println("静态推荐: " + String.join(", ", result));
        }
    }

    // ==================== 4. 多级降级链 ====================

    static class FallbackChain<T> {
        private final Supplier<T>[] suppliers;
        private final String[] names;

        @SafeVarargs
        public FallbackChain(String[] names, Supplier<T>... suppliers) {
            this.names = names;
            this.suppliers = suppliers;
        }

        public T execute() {
            for (int i = 0; i < suppliers.length; i++) {
                try {
                    T result = suppliers[i].get();
                    System.out.println("  [OK] " + names[i] + " - 成功");
                    return result;
                } catch (Exception e) {
                    System.out.println("  [X] " + names[i] + " - 失败: " + e.getMessage());
                }
            }
            throw new RuntimeException("所有降级策略都失败了");
        }
    }

    private static void demonstrateFallbackChain() {
        System.out.println("场景: 用户信息查询的多级降级");
        System.out.println();

        String[] names = { "主数据库", "从数据库", "Redis缓存", "本地缓存", "默认值" };

        @SuppressWarnings("unchecked")
        FallbackChain<String> chain = new FallbackChain<>(names,
                () -> {
                    throw new RuntimeException("主库连接超时");
                },
                () -> {
                    throw new RuntimeException("从库连接超时");
                },
                () -> {
                    throw new RuntimeException("Redis不可用");
                },
                () -> "User{id=1001, name='缓存用户', level=VIP}",
                () -> "User{id=0, name='游客', level=GUEST}");

        System.out.println("降级链执行过程:");
        String result = chain.execute();
        System.out.println("\n最终结果: " + result);
        System.out.println();
        System.out.println("降级原则:");
        System.out.println("   1. 核心功能优先保障");
        System.out.println("   2. 降级策略要提前准备");
        System.out.println("   3. 降级数据要保持合理");
        System.out.println("   4. 降级状态要可监控");
    }
}
