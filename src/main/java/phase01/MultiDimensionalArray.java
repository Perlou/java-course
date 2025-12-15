package phase01;

import java.util.Arrays;

/**
 * Phase 1 - Lesson 9: 多维数组
 * 
 * 🎯 学习目标:
 * 1. 掌握二维数组的创建和使用
 * 2. 理解多维数组的内存结构
 * 3. 掌握常见的二维数组操作
 */
public class MultiDimensionalArray {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 9: 多维数组 ===\n");

        // ==================== 1. 二维数组的创建 ====================
        System.out.println("【1. 二维数组的创建】");

        // 方式1: 直接初始化
        int[][] matrix1 = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };
        System.out.println("静态初始化 3x3 矩阵:");
        printMatrix(matrix1);

        // 方式2: 先创建后赋值
        int[][] matrix2 = new int[2][3]; // 2行3列
        matrix2[0][0] = 1;
        matrix2[0][1] = 2;
        matrix2[1][2] = 5;
        System.out.println("动态创建 2x3 矩阵:");
        printMatrix(matrix2);

        // 方式3: 不规则数组（锯齿数组）
        int[][] jagged = new int[3][];
        jagged[0] = new int[] { 1, 2 };
        jagged[1] = new int[] { 3, 4, 5 };
        jagged[2] = new int[] { 6 };
        System.out.println("锯齿数组:");
        for (int[] row : jagged) {
            System.out.println("  " + Arrays.toString(row));
        }

        // ==================== 2. 二维数组的遍历 ====================
        System.out.println("\n【2. 二维数组的遍历】");

        int[][] data = {
                { 10, 20, 30 },
                { 40, 50, 60 }
        };

        // 普通 for 循环
        System.out.println("普通 for 循环:");
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }

        // 增强 for 循环
        System.out.println("for-each 循环:");
        for (int[] row : data) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        // ==================== 3. 二维数组的常见操作 ====================
        System.out.println("\n【3. 常见操作】");

        int[][] matrix = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };

        // 获取行数和列数
        int rows = matrix.length;
        int cols = matrix[0].length;
        System.out.println("行数: " + rows + ", 列数: " + cols);

        // 行求和
        System.out.println("各行之和:");
        for (int i = 0; i < rows; i++) {
            int sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j];
            }
            System.out.println("  第 " + i + " 行: " + sum);
        }

        // 列求和
        System.out.println("各列之和:");
        for (int j = 0; j < cols; j++) {
            int sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += matrix[i][j];
            }
            System.out.println("  第 " + j + " 列: " + sum);
        }

        // 对角线求和
        int diagSum = 0;
        for (int i = 0; i < rows; i++) {
            diagSum += matrix[i][i];
        }
        System.out.println("主对角线之和: " + diagSum);

        // ==================== 4. 矩阵转置 ====================
        System.out.println("\n【4. 矩阵转置】");

        int[][] original = {
                { 1, 2, 3 },
                { 4, 5, 6 }
        };
        System.out.println("原矩阵 (2x3):");
        printMatrix(original);

        int[][] transposed = transpose(original);
        System.out.println("转置后 (3x2):");
        printMatrix(transposed);

        // ==================== 5. 矩阵相加 ====================
        System.out.println("\n【5. 矩阵相加】");

        int[][] a = { { 1, 2 }, { 3, 4 } };
        int[][] b = { { 5, 6 }, { 7, 8 } };
        int[][] sum = addMatrices(a, b);

        System.out.println("矩阵 A:");
        printMatrix(a);
        System.out.println("矩阵 B:");
        printMatrix(b);
        System.out.println("A + B:");
        printMatrix(sum);

        // ==================== 6. 矩阵相乘 ====================
        System.out.println("\n【6. 矩阵相乘】");

        int[][] m1 = { { 1, 2 }, { 3, 4 } };
        int[][] m2 = { { 5, 6 }, { 7, 8 } };
        int[][] product = multiplyMatrices(m1, m2);

        System.out.println("矩阵 M1:");
        printMatrix(m1);
        System.out.println("矩阵 M2:");
        printMatrix(m2);
        System.out.println("M1 × M2:");
        printMatrix(product);

        // ==================== 7. 实际应用示例 ====================
        System.out.println("\n【7. 实际应用：学生成绩表】");

        String[] students = { "Alice", "Bob", "Charlie" };
        String[] subjects = { "数学", "英语", "物理" };
        int[][] scores = {
                { 85, 90, 78 }, // Alice
                { 92, 88, 95 }, // Bob
                { 76, 83, 80 } // Charlie
        };

        // 打印成绩表
        System.out.print("学生\\科目\t");
        for (String subject : subjects) {
            System.out.print(subject + "\t");
        }
        System.out.println("平均");

        for (int i = 0; i < students.length; i++) {
            System.out.print(students[i] + "\t\t");
            int total = 0;
            for (int j = 0; j < subjects.length; j++) {
                System.out.print(scores[i][j] + "\t");
                total += scores[i][j];
            }
            System.out.printf("%.1f%n", (double) total / subjects.length);
        }

        System.out.println("\n✅ Phase 1 - Lesson 9 完成！");
    }

    // 打印矩阵
    static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println("  " + Arrays.toString(row));
        }
    }

    // 矩阵转置
    static int[][] transpose(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] result = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    // 矩阵相加
    static int[][] addMatrices(int[][] a, int[][] b) {
        int rows = a.length;
        int cols = a[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    // 矩阵相乘
    static int[][] multiplyMatrices(int[][] a, int[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;
        int[][] result = new int[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 二维数组本质是数组的数组
 * 2. matrix.length = 行数
 * 3. matrix[i].length = 第 i 行的列数
 * 4. 锯齿数组: 每行长度可以不同
 * 5. 常见操作: 遍历、转置、相加、相乘
 * 
 * 🏃 练习:
 * 1. 实现矩阵的 90 度旋转
 * 2. 实现杨辉三角
 * 3. 实现螺旋矩阵遍历
 * 4. 实现矩阵的行列式计算
 */
