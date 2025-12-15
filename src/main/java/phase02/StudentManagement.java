package phase02;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Phase 2 - 实战项目: 学生管理系统
 * 
 * 🎯 项目目标:
 * 1. 综合运用 Phase 2 所学的 OOP 知识
 * 2. 实践类设计、继承、多态、接口等概念
 * 3. 实现一个简单的 CRUD 系统
 * 
 * 📚 涉及知识点:
 * - 类与对象
 * - 封装、继承、多态
 * - 抽象类与接口
 * - 枚举
 * - 集合操作
 */
public class StudentManagement {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║       🎓 学生管理系统 v1.0                  ║");
        System.out.println("║       Phase 2 综合实战项目                  ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        // 添加示例数据
        initSampleData();

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listAll();
                case "2" -> addStudent();
                case "3" -> searchStudent();
                case "4" -> updateStudent();
                case "5" -> deleteStudent();
                case "6" -> showStatistics();
                case "7" -> filterByGrade();
                case "0" -> {
                    running = false;
                    System.out.println("感谢使用，再见！👋");
                }
                default -> System.out.println("❌ 无效选项，请重新选择");
            }
            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("┌────────────────────────────────────────────┐");
        System.out.println("│                  主菜单                    │");
        System.out.println("├────────────────────────────────────────────┤");
        System.out.println("│  1. 查看所有学生                           │");
        System.out.println("│  2. 添加学生                               │");
        System.out.println("│  3. 搜索学生                               │");
        System.out.println("│  4. 更新学生信息                           │");
        System.out.println("│  5. 删除学生                               │");
        System.out.println("│  6. 统计信息                               │");
        System.out.println("│  7. 按年级筛选                             │");
        System.out.println("│  0. 退出                                   │");
        System.out.println("└────────────────────────────────────────────┘");
        System.out.print("请选择: ");
    }

    private static void initSampleData() {
        studentService.add(new UndergraduateStudent("S001", "张三", 20, Gender.MALE, "计算机科学", Grade.SOPHOMORE));
        studentService.add(new UndergraduateStudent("S002", "李四", 21, Gender.FEMALE, "软件工程", Grade.JUNIOR));
        studentService.add(new GraduateStudent("S003", "王五", 24, Gender.MALE, "人工智能", "李教授", ResearchArea.AI));
        studentService
                .add(new GraduateStudent("S004", "赵六", 25, Gender.FEMALE, "数据科学", "张教授", ResearchArea.DATA_SCIENCE));
        studentService.add(new UndergraduateStudent("S005", "钱七", 19, Gender.MALE, "信息安全", Grade.FRESHMAN));
    }

    private static void listAll() {
        System.out.println("\n📋 学生列表:");
        System.out.println("─".repeat(70));
        List<Student3> students = studentService.getAll();
        if (students.isEmpty()) {
            System.out.println("暂无学生信息");
        } else {
            for (Student3 s : students) {
                s.displayInfo();
            }
        }
        System.out.println("─".repeat(70));
        System.out.println("共 " + students.size() + " 名学生");
    }

    private static void addStudent() {
        System.out.println("\n➕ 添加学生");
        System.out.print("学号: ");
        String id = scanner.nextLine().trim();

        if (studentService.findById(id) != null) {
            System.out.println("❌ 学号已存在！");
            return;
        }

        System.out.print("姓名: ");
        String name = scanner.nextLine().trim();

        System.out.print("年龄: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("性别 (M/F): ");
        Gender gender = scanner.nextLine().trim().equalsIgnoreCase("M") ? Gender.MALE : Gender.FEMALE;

        System.out.print("专业: ");
        String major = scanner.nextLine().trim();

        System.out.print("学生类型 (1-本科生, 2-研究生): ");
        String type = scanner.nextLine().trim();

        Student3 student;
        if (type.equals("1")) {
            System.out.print("年级 (1-大一, 2-大二, 3-大三, 4-大四): ");
            int gradeNum = Integer.parseInt(scanner.nextLine().trim());
            Grade grade = Grade.values()[gradeNum - 1];
            student = new UndergraduateStudent(id, name, age, gender, major, grade);
        } else {
            System.out.print("导师: ");
            String advisor = scanner.nextLine().trim();
            System.out.print("研究方向 (1-AI, 2-数据科学, 3-网络安全, 4-其他): ");
            int areaNum = Integer.parseInt(scanner.nextLine().trim());
            ResearchArea area = ResearchArea.values()[areaNum - 1];
            student = new GraduateStudent(id, name, age, gender, major, advisor, area);
        }

        studentService.add(student);
        System.out.println("✅ 学生添加成功！");
    }

    private static void searchStudent() {
        System.out.println("\n🔍 搜索学生");
        System.out.print("输入学号或姓名: ");
        String keyword = scanner.nextLine().trim();

        List<Student3> results = studentService.search(keyword);
        if (results.isEmpty()) {
            System.out.println("未找到匹配的学生");
        } else {
            System.out.println("找到 " + results.size() + " 个结果:");
            for (Student3 s : results) {
                s.displayInfo();
            }
        }
    }

    private static void updateStudent() {
        System.out.println("\n✏️ 更新学生信息");
        System.out.print("输入学号: ");
        String id = scanner.nextLine().trim();

        Student3 student = studentService.findById(id);
        if (student == null) {
            System.out.println("❌ 未找到该学生！");
            return;
        }

        System.out.println("当前信息:");
        student.displayInfo();

        System.out.print("新姓名 (留空保持不变): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            student.setName(newName);
        }

        System.out.print("新年龄 (留空保持不变): ");
        String newAge = scanner.nextLine().trim();
        if (!newAge.isEmpty()) {
            student.setAge(Integer.parseInt(newAge));
        }

        System.out.println("✅ 信息更新成功！");
    }

    private static void deleteStudent() {
        System.out.println("\n🗑️ 删除学生");
        System.out.print("输入学号: ");
        String id = scanner.nextLine().trim();

        if (studentService.delete(id)) {
            System.out.println("✅ 删除成功！");
        } else {
            System.out.println("❌ 未找到该学生！");
        }
    }

    private static void showStatistics() {
        System.out.println("\n📊 统计信息");
        System.out.println("─".repeat(40));

        Statistics stats = studentService.getStatistics();
        System.out.println("总人数: " + stats.total());
        System.out.println("本科生: " + stats.undergraduates());
        System.out.println("研究生: " + stats.graduates());
        System.out.printf("平均年龄: %.1f%n", stats.averageAge());
        System.out.println("男生: " + stats.maleCount());
        System.out.println("女生: " + stats.femaleCount());
    }

    private static void filterByGrade() {
        System.out.println("\n🎓 按年级筛选 (仅本科生)");
        System.out.print("年级 (1-大一, 2-大二, 3-大三, 4-大四): ");
        int gradeNum = Integer.parseInt(scanner.nextLine().trim());

        Grade grade = Grade.values()[gradeNum - 1];
        List<Student3> filtered = studentService.filterByGrade(grade);

        if (filtered.isEmpty()) {
            System.out.println("没有 " + grade.getDisplayName() + " 的学生");
        } else {
            System.out.println(grade.getDisplayName() + " 学生:");
            for (Student3 s : filtered) {
                s.displayInfo();
            }
        }
    }
}

// ==================== 枚举定义 ====================

enum Gender {
    MALE("男"), FEMALE("女");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

enum Grade {
    FRESHMAN("大一"), SOPHOMORE("大二"), JUNIOR("大三"), SENIOR("大四");

    private final String displayName;

    Grade(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

enum ResearchArea {
    AI("人工智能"), DATA_SCIENCE("数据科学"), CYBER_SECURITY("网络安全"), OTHER("其他");

    private final String displayName;

    ResearchArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

// ==================== 抽象类和接口 ====================

interface Displayable {
    void displayInfo();
}

abstract class Student3 implements Displayable {
    protected String id;
    protected String name;
    protected int age;
    protected Gender gender;
    protected String major;

    public Student3(String id, String name, int age, Gender gender, String major) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.major = major;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public String getMajor() {
        return major;
    }

    // 抽象方法
    abstract String getStudentType();
}

// ==================== 具体类 ====================

class UndergraduateStudent extends Student3 {
    private Grade grade;

    public UndergraduateStudent(String id, String name, int age, Gender gender, String major, Grade grade) {
        super(id, name, age, gender, major);
        this.grade = grade;
    }

    public Grade getGrade() {
        return grade;
    }

    @Override
    String getStudentType() {
        return "本科生";
    }

    @Override
    public void displayInfo() {
        System.out.printf("  [%s] %s | %s | %d岁 | %s | %s | %s%n",
                id, name, gender.getDisplayName(), age, major, getStudentType(), grade.getDisplayName());
    }
}

class GraduateStudent extends Student3 {
    private String advisor;
    private ResearchArea researchArea;

    public GraduateStudent(String id, String name, int age, Gender gender, String major,
            String advisor, ResearchArea researchArea) {
        super(id, name, age, gender, major);
        this.advisor = advisor;
        this.researchArea = researchArea;
    }

    public String getAdvisor() {
        return advisor;
    }

    public ResearchArea getResearchArea() {
        return researchArea;
    }

    @Override
    String getStudentType() {
        return "研究生";
    }

    @Override
    public void displayInfo() {
        System.out.printf("  [%s] %s | %s | %d岁 | %s | %s | 导师:%s | 方向:%s%n",
                id, name, gender.getDisplayName(), age, major, getStudentType(),
                advisor, researchArea.getDisplayName());
    }
}

// ==================== Record 定义 ====================

record Statistics(int total, int undergraduates, int graduates,
        double averageAge, int maleCount, int femaleCount) {
}

// ==================== 服务类 ====================

class StudentService {
    private final List<Student3> students = new ArrayList<>();

    public void add(Student3 student) {
        students.add(student);
    }

    public List<Student3> getAll() {
        return new ArrayList<>(students);
    }

    public Student3 findById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Student3> search(String keyword) {
        return students.stream()
                .filter(s -> s.getId().contains(keyword) || s.getName().contains(keyword))
                .toList();
    }

    public boolean delete(String id) {
        return students.removeIf(s -> s.getId().equals(id));
    }

    public List<Student3> filterByGrade(Grade grade) {
        return students.stream()
                .filter(s -> s instanceof UndergraduateStudent)
                .map(s -> (UndergraduateStudent) s)
                .filter(s -> s.getGrade() == grade)
                .map(s -> (Student3) s)
                .toList();
    }

    public Statistics getStatistics() {
        int total = students.size();
        int undergraduates = (int) students.stream()
                .filter(s -> s instanceof UndergraduateStudent).count();
        int graduates = total - undergraduates;
        double avgAge = students.stream().mapToInt(Student3::getAge).average().orElse(0);
        int males = (int) students.stream()
                .filter(s -> s.getGender() == Gender.MALE).count();
        int females = total - males;

        return new Statistics(total, undergraduates, graduates, avgAge, males, females);
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 Phase 2 的所有知识点:
 * - 类与对象: Student3, StudentService 等
 * - 封装: private 字段 + getter/setter
 * - 继承: UndergraduateStudent, GraduateStudent 继承 Student3
 * - 多态: 统一处理不同类型的学生
 * - 抽象类: Student3 定义通用行为
 * - 接口: Displayable 定义显示能力
 * - 枚举: Gender, Grade, ResearchArea
 * - Record: Statistics 统计数据
 * 
 * 🎯 扩展任务:
 * 1. 添加成绩管理功能
 * 2. 实现数据持久化 (文件存储)
 * 3. 添加课程管理和选课功能
 * 4. 实现更复杂的查询条件
 */
