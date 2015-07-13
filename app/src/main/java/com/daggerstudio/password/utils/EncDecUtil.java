/**
 * Created by alex on 15/5/24.
 */
package com.daggerstudio.password.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncDecUtil {
    /**
     * 对给定输入的字符加密,依赖AesCbcWithIntegrity.java
     *
     * @param plainText 明文
     * @param password 密码
     * @return decryptResult, or if exception eccounterd, null is returned
     */
    public static byte[] encrypt(String plainText, String password) {
        try {
            byte[] salt = AesCbcWithIntegrity.generateSalt();
            AesCbcWithIntegrity.SecretKeys sKeys = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
            AesCbcWithIntegrity.CipherTextIvMac ctim = AesCbcWithIntegrity.encrypt(plainText, sKeys);
            ctim.setSalt(salt);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ctim);
            byte[] result = baos.toByteArray();
            return result;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对给定的字节流进行解密,依赖AesCbcWithIntegrity.java
     * @param content 加密内容
     * @param password 密码
     * @return decryptResult, or if exception eccounterd, null is returned
     */
    public static String decrypt(byte[] content, String password) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(content);
            ObjectInputStream ois = new ObjectInputStream(bais);
            AesCbcWithIntegrity.CipherTextIvMac ctim = (AesCbcWithIntegrity.CipherTextIvMac)ois.readObject();
            String result = AesCbcWithIntegrity.decryptString(ctim, AesCbcWithIntegrity.generateKeyFromPassword(password, ctim.getSalt()));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据给定的字符串text进行SHA1加密得到字节流
     * @param text
     * @return 加密结果
     */
    public static byte[] SHA1(String text){
        if(null == text){
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(text.getBytes());
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
