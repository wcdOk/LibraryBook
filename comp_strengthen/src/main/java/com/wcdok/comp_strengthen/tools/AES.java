package com.wcdok.comp_strengthen.tools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/22/21 2:32 PM
 * @desc:对称加密算法(微信小程序加密传输就是用这个加密算法的)。对称加密算法也就是加密和解密用相同的密钥
 *
 * AES算法是一种分组密码算法，有三种不同的密钥长度规模，分别是128比特、192比特和256比特。
 * 题主说的“16位”应该是指的128比特，也就是16字节。
 * 这几个密钥长度是由AES算法设计本身决定的。
 * 如果题主是想问，为什么现在以AES为代表的非轻量级分组密码算法通常会采用128比特的密钥长度，
 * 那这其实是个很好的问题。我试着从这个角度回答一下。
 * 简单地说，128的密钥长度是目前能对安全性和性能的一种比较理想的折中选择。
 * (1)128比特安全强度目前在经典计算模型下是安全的。
 * (2)128比特是32和64的整数倍，便于现有计算架构进行计算。
 * (3)理想状态下，密钥长度越长，算法安全强度约高，但是密钥长度越长，算法规模也就越大，可能影响算法性能，也增加了算法设计和算法分析的难度。
 * (4)从当前技术发展情况来看，128是此前比较理想的选择。但是现在随着量子计算技术的发展，普遍认为量子攻击下分组密码算法的安全性会折半，所以以后AES256版本的应用可能会增加。
 *
 * https://blog.csdn.net/u010603798/article/details/98586594
 */
public class AES {
    //16字节
    private static final String DEFAULT_PWD = "wcdOKwcdOK3wcdOK";
    private static Cipher decryptCipher;

    //加密方式
    public static String KEY_ALGORITHM = "AES";
    //数据填充方式
    private static final String algorithmStr = "AES/ECB/PKCS5Padding";
    private static volatile AES instance;

    private AES() {
        init(DEFAULT_PWD);
    }

    public static AES getInstance() {
        if (instance == null) {
            synchronized (AES.class) {
                if (instance == null) {
                    instance = new AES();
                }
            }
        }
        return instance;
    }

    private void init(String password) {

        try {
            decryptCipher = Cipher.getInstance(algorithmStr);
            byte[] keyStr = password.getBytes();
            SecretKeySpec key = new SecretKeySpec(keyStr, KEY_ALGORITHM);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


    }

    /**
     * 解密
     */
    public byte[] decrypt(byte[] content) {
        try {
            byte[] result = decryptCipher.doFinal(content);
            return result;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
