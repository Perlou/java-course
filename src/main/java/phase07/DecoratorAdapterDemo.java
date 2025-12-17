package phase07;

/**
 * Phase 7 - Lesson 6: 装饰器模式与适配器模式
 * 
 * 🎯 学习目标:
 * 1. 理解装饰器模式的作用
 * 2. 理解适配器模式的作用
 * 3. 区分装饰器和代理模式
 */
public class DecoratorAdapterDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 6: 装饰器模式与适配器模式");
        System.out.println("=".repeat(60));

        // ==================== 装饰器模式 ====================

        System.out.println("\n【装饰器模式 (Decorator)】");
        System.out.println("""
                意图: 动态地给对象添加额外的职责

                特点:
                - 比继承更灵活
                - 可以叠加多个装饰器
                - 遵循开闭原则

                结构:
                Coffee coffee = new SimpleCoffee();          // 原始对象
                coffee = new MilkDecorator(coffee);          // 加牛奶
                coffee = new SugarDecorator(coffee);         // 加糖
                // coffee 现在是 加糖(加牛奶(简单咖啡))
                """);

        // 演示装饰器
        System.out.println("\n咖啡装饰器示例:");

        Coffee simpleCoffee = new SimpleCoffee();
        System.out.println(simpleCoffee.getDescription() + " - $" + simpleCoffee.getCost());

        Coffee milkCoffee = new MilkDecorator(simpleCoffee);
        System.out.println(milkCoffee.getDescription() + " - $" + milkCoffee.getCost());

        Coffee fancyCoffee = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println(fancyCoffee.getDescription() + " - $" + fancyCoffee.getCost());

        // Java IO 中的装饰器
        System.out.println("\nJava IO 中的装饰器模式:");
        System.out.println("""
                new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream("file.txt"),
                        "UTF-8"
                    )
                )

                InputStream           ← 抽象组件
                FileInputStream       ← 具体组件
                FilterInputStream     ← 装饰器基类
                BufferedInputStream   ← 具体装饰器
                DataInputStream       ← 具体装饰器
                """);

        // ==================== 适配器模式 ====================

        System.out.println("=".repeat(60));
        System.out.println("\n【适配器模式 (Adapter)】");
        System.out.println("""
                意图: 将一个类的接口转换成客户期望的另一个接口

                类型:
                - 类适配器: 通过继承实现
                - 对象适配器: 通过组合实现 (推荐)

                应用场景:
                - 新旧系统对接
                - 第三方库集成
                - 接口不兼容问题
                """);

        // 演示适配器
        System.out.println("\n充电器适配器示例:");

        // 旧的充电器 (110V)
        OldCharger oldCharger = new OldCharger();

        // 新设备需要 USB 接口
        USBDevice mobilePhone = new MobilePhone();

        // 使用适配器
        USB adapter = new ChargerAdapter(oldCharger);
        mobilePhone.charge(adapter);

        // 另一个示例：媒体播放器
        System.out.println("\n媒体播放器适配器:");

        MediaPlayer player = new AudioPlayer();
        player.play("mp3", "song.mp3");
        player.play("mp4", "video.mp4"); // 需要适配器
        player.play("vlc", "movie.vlc"); // 需要适配器

        // 装饰器 vs 适配器
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【装饰器 vs 适配器 vs 代理】");
        System.out.println("""
                ┌────────────┬─────────────────────┬───────────────────┐
                │   模式     │      目的           │      关系         │
                ├────────────┼─────────────────────┼───────────────────┤
                │  装饰器    │ 增强功能            │ 实现相同接口      │
                │  适配器    │ 转换接口            │ 接口不同          │
                │  代理      │ 控制访问            │ 实现相同接口      │
                └────────────┴─────────────────────┴───────────────────┘

                实际案例:
                - 装饰器: BufferedInputStream 增强 InputStream
                - 适配器: InputStreamReader 将 InputStream 适配为 Reader
                - 代理: Spring AOP 代理原有方法
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 装饰器: 增强现有功能，接口不变");
        System.out.println("💡 适配器: 转换接口，使不兼容的类能协作");
        System.out.println("💡 Java IO 是装饰器模式的经典应用");
        System.out.println("=".repeat(60));
    }
}

// ==================== 装饰器模式实现 ====================

// 抽象组件
interface Coffee {
    double getCost();

    String getDescription();
}

// 具体组件
class SimpleCoffee implements Coffee {
    @Override
    public double getCost() {
        return 10.0;
    }

    @Override
    public String getDescription() {
        return "简单咖啡";
    }
}

// 装饰器基类
abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public double getCost() {
        return coffee.getCost();
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
}

// 具体装饰器
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 2.0;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + 牛奶";
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 1.0;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + 糖";
    }
}

// ==================== 适配器模式实现 ====================

// 旧接口
class OldCharger {
    public void charge110V() {
        System.out.println("  110V 充电中...");
    }
}

// 新接口
interface USB {
    void chargeUSB();
}

interface USBDevice {
    void charge(USB usb);
}

// 适配器
class ChargerAdapter implements USB {
    private final OldCharger oldCharger;

    public ChargerAdapter(OldCharger oldCharger) {
        this.oldCharger = oldCharger;
    }

    @Override
    public void chargeUSB() {
        System.out.println("  适配器: 将 USB 转换为 110V");
        oldCharger.charge110V();
    }
}

// 使用适配器的设备
class MobilePhone implements USBDevice {
    @Override
    public void charge(USB usb) {
        System.out.println("手机通过 USB 充电:");
        usb.chargeUSB();
    }
}

// ==================== 媒体播放器适配器示例 ====================

interface MediaPlayer {
    void play(String type, String fileName);
}

interface AdvancedMediaPlayer {
    void playVlc(String fileName);

    void playMp4(String fileName);
}

class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("  播放 VLC: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("  播放 MP4: " + fileName);
    }
}

class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(String type) {
        if (type.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        } else if (type.equalsIgnoreCase("mp4")) {
            advancedPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String type, String fileName) {
        if (type.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        } else if (type.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        }
    }
}

class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String type, String fileName) {
        if (type.equalsIgnoreCase("mp3")) {
            System.out.println("  播放 MP3: " + fileName);
        } else if (type.equalsIgnoreCase("vlc") || type.equalsIgnoreCase("mp4")) {
            MediaAdapter adapter = new MediaAdapter(type);
            adapter.play(type, fileName);
        } else {
            System.out.println("  不支持的格式: " + type);
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 装饰器模式:
 * - 动态添加功能
 * - 可叠加多个装饰器
 * - Java IO 是典型应用
 * 
 * 适配器模式:
 * - 接口转换
 * - 解决不兼容问题
 * - 对象适配器更灵活
 * 
 * 🏃 练习:
 * 1. 用装饰器实现日志增强
 * 2. 为老 API 设计适配器
 * 3. 分析 Arrays.asList() 的适配器特性
 */
