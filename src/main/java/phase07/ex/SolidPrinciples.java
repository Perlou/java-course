package phase07.ex;

public class SolidPrinciples {
    public static void main(String[] args) {

    }
}

// ==================== 开闭原则演示 ====================

interface PricingStrategy {
    double calculate(double price);
}

class NormalPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(double price) {
        return price;
    }
}

class VipPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(double price) {
        return price * 0.9;
    }
}

// ==================== 依赖倒置演示 ====================

interface Database {
    void save(String data);
}

class MySQLDatabaseImpl implements Database {
    @Override
    public void save(String data) {
        System.out.println("MySQL 保存: " + data);
    }
}

class MongoDatabaseImpl implements Database {
    @Override
    public void save(String data) {
        System.out.println("MongoDB 保存: " + data);
    }
}

class UserRepositoryDemo {
    private final Database db;

    public UserRepositoryDemo(Database db) {
        this.db = db;
    }

    public void saveUser(String username) {
        db.save("User: " + username);
    }
}
