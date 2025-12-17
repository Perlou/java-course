package phase04;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 4 - Lesson 6: 对象序列化
 * 
 * 🎯 学习目标:
 * 1. 理解对象序列化的概念和用途
 * 2. 掌握 Serializable 接口的使用
 * 3. 理解 transient 关键字
 * 4. 了解序列化的安全性和版本控制
 */
public class SerializationDemo {

    private static final String TEST_DIR = "target/serialization-test";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 4 - Lesson 6: 对象序列化");
        System.out.println("=".repeat(60));

        new File(TEST_DIR).mkdirs();

        // 1. 序列化概念
        System.out.println("\n【1. 序列化概念】");
        System.out.println("""
                序列化 (Serialization):
                - 将对象转换为字节序列的过程
                - 用于持久化存储或网络传输

                反序列化 (Deserialization):
                - 将字节序列还原为对象的过程

                用途:
                - 保存对象状态到文件
                - 通过网络发送对象
                - 深拷贝对象
                - 缓存对象 (如 Redis)
                """);

        // 2. 基本序列化
        System.out.println("【2. 基本序列化】");

        Person person = new Person("张三", 25, "secret_password");
        person.setEmail("zhangsan@example.com");
        person.addSkill("Java");
        person.addSkill("Python");

        String personFile = TEST_DIR + "/person.ser";

        // 序列化
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(personFile))) {
            oos.writeObject(person);
            System.out.println("序列化: " + person);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 反序列化
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(personFile))) {
            Person restored = (Person) ois.readObject();
            System.out.println("反序列化: " + restored);
            System.out.println("  password (transient): " + restored.getPassword());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 3. transient 关键字
        System.out.println("\n【3. transient 关键字】");
        System.out.println("""
                transient 修饰的字段不会被序列化:
                - 敏感信息 (密码、密钥)
                - 可重新计算的字段
                - 不需要持久化的临时数据

                反序列化后 transient 字段为默认值:
                - 引用类型: null
                - 基本类型: 0, false 等
                """);

        // 4. 序列化多个对象
        System.out.println("【4. 序列化多个对象】");

        String multiFile = TEST_DIR + "/multi.ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(multiFile))) {

            // 可以写入多个对象和基本类型
            oos.writeInt(42);
            oos.writeUTF("Hello");
            oos.writeObject(new Person("李四", 30, "pwd"));
            oos.writeObject(new Person("王五", 28, "pwd"));
            oos.writeObject(List.of("A", "B", "C"));

            System.out.println("写入多个对象完成");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(multiFile))) {

            // 读取顺序必须与写入顺序一致!
            int num = ois.readInt();
            String str = ois.readUTF();
            Person p1 = (Person) ois.readObject();
            Person p2 = (Person) ois.readObject();
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) ois.readObject();

            System.out.println("读取: " + num);
            System.out.println("读取: " + str);
            System.out.println("读取: " + p1);
            System.out.println("读取: " + p2);
            System.out.println("读取: " + list);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 5. 序列化集合
        System.out.println("\n【5. 序列化集合】");

        String listFile = TEST_DIR + "/people.ser";
        List<Person> people = new ArrayList<>();
        people.add(new Person("A", 20, "p"));
        people.add(new Person("B", 21, "p"));
        people.add(new Person("C", 22, "p"));

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(listFile))) {
            oos.writeObject(people);
            System.out.println("序列化 List: " + people.size() + " 个对象");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(listFile))) {
            @SuppressWarnings("unchecked")
            List<Person> restored = (List<Person>) ois.readObject();
            System.out.println("反序列化 List:");
            restored.forEach(p -> System.out.println("  " + p));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 6. serialVersionUID
        System.out.println("\n【6. serialVersionUID (版本控制)】");
        System.out.println("""
                serialVersionUID 用于版本兼容性检查:

                private static final long serialVersionUID = 1L;

                - 如果不指定，JVM 会自动生成
                - 类结构变化后自动生成的值也会变化
                - 导致旧数据无法反序列化

                最佳实践:
                - 显式声明 serialVersionUID
                - 兼容性变更保持不变
                - 不兼容变更时更新版本号
                """);

        // 7. 深拷贝
        System.out.println("【7. 使用序列化实现深拷贝】");

        Person original = new Person("原对象", 25, "pwd");
        original.addSkill("Java");

        try {
            Person copy = deepCopy(original);
            copy.setName("拷贝对象");
            copy.addSkill("Go");

            System.out.println("原对象: " + original);
            System.out.println("拷贝对象: " + copy);
            System.out.println("是否不同对象: " + (original != copy));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 8. 自定义序列化
        System.out.println("\n【8. 自定义序列化】");

        CustomSerializable custom = new CustomSerializable("Test", 100);
        String customFile = TEST_DIR + "/custom.ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(customFile))) {
            oos.writeObject(custom);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(customFile))) {
            CustomSerializable restored = (CustomSerializable) ois.readObject();
            System.out.println("自定义序列化恢复: " + restored);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 9. 序列化注意事项
        System.out.println("\n【9. 序列化注意事项】");
        System.out.println("""
                ⚠️ 安全风险:
                - 反序列化不可信数据可能导致安全漏洞
                - 可能执行任意代码
                - Java 9+ 引入了 ObjectInputFilter

                💡 最佳实践:
                1. 不要序列化敏感数据
                2. 显式声明 serialVersionUID
                3. 考虑使用 JSON/XML 替代
                4. 验证反序列化的数据来源
                5. 使用 ObjectInputFilter 过滤

                🔄 替代方案:
                - JSON: Jackson, Gson
                - Protocol Buffers
                - Kryo (高性能)
                - 数据库
                """);

        // 10. 使用 ObjectInputFilter (Java 9+)
        System.out.println("【10. ObjectInputFilter (安全过滤)】");

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(personFile))) {

            // 设置过滤器
            ois.setObjectInputFilter(filterInfo -> {
                Class<?> clazz = filterInfo.serialClass();
                if (clazz != null) {
                    System.out.println("  过滤检查: " + clazz.getName());
                    // 只允许特定类
                    if (clazz.getName().startsWith("phase04.")
                            || clazz.getName().startsWith("java.util.")) {
                        return ObjectInputFilter.Status.ALLOWED;
                    }
                    return ObjectInputFilter.Status.REJECTED;
                }
                return ObjectInputFilter.Status.UNDECIDED;
            });

            Person p = (Person) ois.readObject();
            System.out.println("通过过滤器: " + p.getName());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 序列化用于持久化对象状态");
        System.out.println("💡 transient 修饰敏感字段");
        System.out.println("💡 显式声明 serialVersionUID");
        System.out.println("💡 对于新项目考虑使用 JSON 替代");
        System.out.println("=".repeat(60));
    }

    /**
     * 使用序列化实现深拷贝
     */
    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T deepCopy(T obj)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        }
    }
}

/**
 * 可序列化的 Person 类
 */
class Person implements Serializable {

    // 版本控制
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private transient String password; // 不序列化
    private String email;
    private List<String> skills = new ArrayList<>();

    public Person(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public List<String> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d, email='%s', skills=%s}",
                name, age, email, skills);
    }
}

/**
 * 自定义序列化的类
 */
class CustomSerializable implements Serializable {

    private static final long serialVersionUID = 1L;

    private String data;
    private transient int cachedValue; // 可重新计算的缓存

    public CustomSerializable(String data, int cachedValue) {
        this.data = data;
        this.cachedValue = cachedValue;
    }

    /**
     * 自定义序列化
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        System.out.println("  自定义 writeObject 被调用");
        oos.defaultWriteObject(); // 写入非 transient 字段
        // 可以写入额外数据
        oos.writeInt(cachedValue * 2); // 例如：加密或转换
    }

    /**
     * 自定义反序列化
     */
    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        System.out.println("  自定义 readObject 被调用");
        ois.defaultReadObject(); // 读取非 transient 字段
        // 读取额外数据并恢复
        this.cachedValue = ois.readInt() / 2;
    }

    @Override
    public String toString() {
        return String.format("CustomSerializable{data='%s', cachedValue=%d}",
                data, cachedValue);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 基本序列化:
 * - 实现 Serializable 接口
 * - ObjectOutputStream.writeObject()
 * - ObjectInputStream.readObject()
 * 
 * 2. transient 关键字:
 * - 修饰的字段不参与序列化
 * - 反序列化后为默认值
 * 
 * 3. serialVersionUID:
 * - 用于版本兼容性检查
 * - 建议显式声明
 * 
 * 4. 自定义序列化:
 * - writeObject(ObjectOutputStream)
 * - readObject(ObjectInputStream)
 * - readObjectNoData()
 * - writeReplace() / readResolve()
 * 
 * 5. 安全考虑:
 * - 不要反序列化不可信数据
 * - 使用 ObjectInputFilter
 * - 考虑使用 JSON 替代
 * 
 * 🏃 练习:
 * 1. 实现一个简单的对象存储库 (保存/加载多个对象)
 * 2. 使用序列化实现程序配置的保存和恢复
 * 3. 比较 Java 序列化和 JSON 序列化的性能
 */
