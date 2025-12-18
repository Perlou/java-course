package phase04.ex;

import java.io.File;
import java.io.IOException;

public class FileIODemo {

    public static void main(String[] args) {
        File file1 = new File("test.txt");
        File file2 = new File("/Users/example/documents", "report.pdf");
        File file3 = new File(new File("/Users/example"), "config.json");

        File currentDir = new File(".");
        System.out.println("当前目录: " + currentDir.getAbsolutePath());

        try {
            System.out.println("规范路径: " + currentDir.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用项目中的实际文件演示
        File srcDir = new File("src/main/java/phase04");
        if (srcDir.exists()) {
            System.out.println("\n源码目录信息:");
            System.out.println("  存在: " + srcDir.exists());
            System.out.println("  是目录: " + srcDir.isDirectory());
            System.out.println("  是文件: " + srcDir.isFile());
            System.out.println("  可读: " + srcDir.canRead());
            System.out.println("  可写: " + srcDir.canWrite());
            System.out.println("  绝对路径: " + srcDir.getAbsolutePath());
        }

        // 3. 文件和目录操作
        System.out.println("\n【3. 文件和目录操作】");

        File testDir = new File("target/test-files");
        File testFile = new File(testDir, "sample.txt");

        try {
            // 创建目录 (包括父目录)
            if (testDir.mkdirs()) {
                System.out.println("创建目录: " + testDir.getPath());
            }

            // 创建文件
            if (testFile.createNewFile()) {
                System.out.println("创建文件: " + testFile.getPath());
            }

            // 文件信息
            System.out.println("文件大小: " + testFile.length() + " 字节");
            System.out.println("最后修改: " + new java.util.Date(testFile.lastModified()));

            // 重命名
            File renamedFile = new File(testDir, "renamed.txt");
            if (testFile.renameTo(renamedFile)) {
                System.out.println("重命名为: " + renamedFile.getName());
            }

            // 删除文件
            if (renamedFile.delete()) {
                System.out.println("删除文件: " + renamedFile.getName());
            }

        } catch (IOException e) {
            System.err.println("IO 错误: " + e.getMessage());
        }

        // 4. 目录遍历
        System.out.println("\n【4. 目录遍历】");
        File phase04Dir = new File("src/main/java/phase04");
        if (phase04Dir.exists() && phase04Dir.isDirectory()) {
            String[] fileNames = phase04Dir.list();
            if (fileNames != null) {

            }
        }
    }
}
