package phase04.xor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * EncryptorGUI.java - 图形界面
 */
public class EncryptorGUI extends JFrame {

    private JTextField filePathField;
    private JPasswordField passwordField;
    private JTextField outputField;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton encryptBtn, decryptBtn, browseBtn;

    public EncryptorGUI() {
        setTitle("🔐 XOR 文件加密工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // === 输入面板 ===
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("文件设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 文件路径
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("选择文件:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        filePathField = new JTextField();
        inputPanel.add(filePathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        browseBtn = new JButton("浏览...");
        browseBtn.addActionListener(e -> browseFile());
        inputPanel.add(browseBtn, gbc);

        // 密钥
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("加密密钥:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        passwordField = new JPasswordField();
        inputPanel.add(passwordField, gbc);

        // 输出路径
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("输出路径:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        outputField = new JTextField();
        outputField.setToolTipText("留空则自动生成");
        inputPanel.add(outputField, gbc);

        // === 按钮面板 ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        encryptBtn = new JButton("🔒 加密文件");
        encryptBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        encryptBtn.setPreferredSize(new Dimension(150, 40));
        encryptBtn.addActionListener(e -> processFile(true));

        decryptBtn = new JButton("🔓 解密文件");
        decryptBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        decryptBtn.setPreferredSize(new Dimension(150, 40));
        decryptBtn.addActionListener(e -> processFile(false));

        buttonPanel.add(encryptBtn);
        buttonPanel.add(decryptBtn);

        // === 进度条 ===
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 25));

        // === 日志区域 ===
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("处理日志"));
        scrollPane.setPreferredSize(new Dimension(0, 150));

        // 组装界面
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(progressBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        log("程序已启动，请选择文件并输入密钥...");
    }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择文件");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void processFile(boolean isEncrypt) {
        String filePath = filePathField.getText().trim();
        String password = new String(passwordField.getPassword());
        String outputPath = outputField.getText().trim();

        // 验证输入
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择文件!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!new File(filePath).exists()) {
            JOptionPane.showMessageDialog(this, "文件不存在!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密钥!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 生成输出路径
        if (outputPath.isEmpty()) {
            outputPath = isEncrypt ? filePath + ".encrypted"
                    : (filePath.endsWith(".encrypted") ? filePath.substring(0, filePath.length() - 10)
                            : filePath + ".decrypted");
        }

        final String finalOutputPath = outputPath;
        String operation = isEncrypt ? "加密" : "解密";

        // 禁用按钮
        setButtonsEnabled(false);
        progressBar.setValue(0);
        log("开始" + operation + ": " + filePath);

        // 在后台线程处理
        final String finalFilePath = filePath;
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                FileEncryptor encryptor = new FileEncryptor(password);

                encryptor.setProgressListener(new FileEncryptor.ProgressListener() {
                    @Override
                    public void onProgress(long bytesProcessed, long totalBytes, int percentage) {
                        publish(percentage);
                    }

                    @Override
                    public void onComplete(String message) {
                        SwingUtilities.invokeLater(() -> log("✅ " + message));
                    }

                    @Override
                    public void onError(String error) {
                        SwingUtilities.invokeLater(() -> log("❌ " + error));
                    }
                });

                encryptor.processFile(finalFilePath, finalOutputPath);
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    get();
                    progressBar.setValue(100);
                    log(operation + "完成! 输出: " + finalOutputPath);
                    JOptionPane.showMessageDialog(EncryptorGUI.this,
                            operation + "成功!\n输出文件: " + finalOutputPath,
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    log("❌ 错误: " + e.getMessage());
                    JOptionPane.showMessageDialog(EncryptorGUI.this,
                            operation + "失败: " + e.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
                setButtonsEnabled(true);
            }
        }.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        encryptBtn.setEnabled(enabled);
        decryptBtn.setEnabled(enabled);
        browseBtn.setEnabled(enabled);
    }

    private void log(String message) {
        logArea.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new EncryptorGUI().setVisible(true);
        });
    }
}
