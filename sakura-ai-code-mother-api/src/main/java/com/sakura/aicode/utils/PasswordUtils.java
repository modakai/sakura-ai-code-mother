package com.sakura.aicode.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 密码加密与校验工具类，使用JDK自带的SHA-256算法
 */
public class PasswordUtils {

    private static final String SALT = "+Hj7SAtm+AnBWFdxsOdXmg==";

    private static final MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 使用盐值加密密码
    public static String encryptPassword(String password) {
        // 先将盐值加入摘要
        messageDigest.update(Base64.getDecoder().decode(SALT));

        // 再将密码加入摘要并计算哈希值
        byte[] hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        // 将哈希值转为Base64字符串便于存储
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    // 校验密码是否正确
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        // 对输入的密码使用相同的盐值进行加密
        String encryptedInput = encryptPassword(inputPassword);
        // 比较加密后的结果与存储的密码是否一致
        return encryptedInput.equals(storedPassword);
    }
}
    