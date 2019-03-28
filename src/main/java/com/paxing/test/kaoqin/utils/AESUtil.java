package com.paxing.test.kaoqin.utils;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.ResourceBundle;

/**
 * aes对称加密
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/27
 */
public class AESUtil {
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 默认加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 默认字符集
     */
    private static final String DEFAULT_CHARSET = "utf-8";

    private static String privateKey;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("application");
        privateKey = bundle.getString("cryptKey");
        if (StringUtils.isBlank(privateKey)) {
            throw new SecurityException("秘钥不能为空");
        }
    }

    public static String decrypt(String data) {
        return crypt(data, Cipher.DECRYPT_MODE, null);
    }

    public static String decrypt(String data, String privateKey) {
        return crypt(data, Cipher.DECRYPT_MODE, privateKey);
    }

    public static String encrypt(String data) {
        return crypt(data, Cipher.ENCRYPT_MODE, null);
    }

    public static String encrypt(String data, String privateKey) {
        return crypt(data, Cipher.ENCRYPT_MODE, privateKey);
    }

    private static Key generateKey(String key) throws UnsupportedEncodingException {
        byte[] keyBytes = key.getBytes(DEFAULT_CHARSET);
        byte[] rsKeyBytes = new byte[16];
        byte[] plusBytes = {97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112};
        for (int i = 0; i < 16; i++) {
            if (keyBytes.length > i) {
                rsKeyBytes[i] = keyBytes[i];
            } else {
                rsKeyBytes[i] = plusBytes[16 - i - 1];
            }
        }
        return new SecretKeySpec(rsKeyBytes, KEY_ALGORITHM);
    }

    private static String crypt(String data, int mode, String pKey) {
        try {
            Key key = generateKey(StringUtils.isBlank(pKey) ? privateKey : pKey);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(mode, key);

            if (mode == Cipher.ENCRYPT_MODE) {
                byte[] encryptData = cipher.doFinal(data.getBytes(DEFAULT_CHARSET));
                return byte2Hex(encryptData);
            } else if (mode == Cipher.DECRYPT_MODE) {
                byte[] encryptedDataBytes = hex2Byte(data);
                byte[] decryptData = cipher.doFinal(encryptedDataBytes);
                return new String(decryptData, DEFAULT_CHARSET);
            } else {
                throw new SecurityException("不支持的模式");
            }
        } catch (Exception e) {
            throw new SecurityException("加密失败");
        }
    }

    private static String byte2Hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte buf : data) {
            String s = Integer.toHexString(buf & 0xFF);
            if (s.length() == 1) {
                s = '0' + s;
            }
            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] hex2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length(); i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
