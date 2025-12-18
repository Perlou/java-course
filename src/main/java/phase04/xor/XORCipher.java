package phase04.xor;

/**
 * XORCipher.java - XOR 加密核心算法
 */
public class XORCipher {

    private final byte[] key;

    public XORCipher(String password) {
        // 将密码转换为字节数组
        this.key = password.getBytes();
        if (key.length == 0) {
            throw new IllegalArgumentException("密钥不能为空!");
        }
    }

    public XORCipher(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("密钥不能为空!");
        }
        this.key = key.clone();
    }

    /**
     * XOR 加密/解密（对称操作）
     * 
     * 原理：data XOR key = encrypted
     * encrypted XOR key = data
     */
    public byte[] process(byte[] data) {
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            // 循环使用密钥字节
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }

        return result;
    }

    /**
     * 处理单个字节
     */
    public byte processByte(byte data, int position) {
        return (byte) (data ^ key[position % key.length]);
    }
}