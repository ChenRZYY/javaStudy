package com.cisc;
import java.security.Key;   
import java.security.KeyFactory;   
import java.security.KeyPair;   
import java.security.KeyPairGenerator;   
import java.security.PrivateKey;   
import java.security.PublicKey;   
import java.security.interfaces.RSAPrivateKey;   
import java.security.interfaces.RSAPublicKey;   
import java.security.spec.PKCS8EncodedKeySpec;   
import java.security.spec.X509EncodedKeySpec;   
    
import javax.crypto.Cipher;   
    
import sun.misc.BASE64Decoder;   
import sun.misc.BASE64Encoder;   
    
    
public class RSAHelper {   
    
        
      public static PublicKey getPublicKey(String key) throws Exception {   
            byte[] keyBytes;   
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);   
    
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);   
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
            PublicKey publicKey = keyFactory.generatePublic(keySpec);   
            return publicKey;   
      }   
        
      public static PrivateKey getPrivateKey(String key) throws Exception {   
            byte[] keyBytes;   
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);   
    
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);   
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);   
            return privateKey;   
      }   
    
        
      public static String getKeyString(Key key) throws Exception {   
            byte[] keyBytes = key.getEncoded();   
            String s = (new BASE64Encoder()).encode(keyBytes);   
            return s;   
      }   
    
    
      public static void main(String[] args) throws Exception {   
    
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");   
            //密钥位数   
            keyPairGen.initialize(1024);   
            //密钥对   
            KeyPair keyPair = keyPairGen.generateKeyPair();   
    
            // 公钥   
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();   
    
            // 私钥   
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   
    
            String publicKeyString = getKeyString(publicKey);   
            System.out.println("public:\n" + publicKeyString);   
    
            String privateKeyString = getKeyString(privateKey);   
            System.out.println("private:\n" + privateKeyString);   
    
            //加解密类   
            Cipher cipher = Cipher.getInstance("RSA");//Cipher.getInstance("RSA/ECB/PKCS1Padding");   
    
            //明文   
            byte[] plainText = "我们都很好！邮件：@sina.com".getBytes();   
            // 82808156e60b01d5234f4f3710053b7820f68a1e019f4deaec30c0e9bb7787de9a5bde5e074c2ff6aaa3f4e112dbf21a53d7a470b246e9c0fef3c378cbac599575d4fabce3da33b0d36a5485e3de45cc83d30bc2f08a4070e560e21c494e15d66d6d76d9fbf2f0bee2c0bfa1cb7d05749dd785e5b3e59b6ea086022e79d54873
           
            //加密   
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);   
            byte[] enBytes = cipher.doFinal(plainText);   
    
           //通过密钥字符串得到密钥   
            publicKey = getPublicKey(publicKeyString);   
            privateKey = getPrivateKey(privateKeyString);   
    
            //解密   
            cipher.init(Cipher.DECRYPT_MODE, privateKey);   
            byte[]deBytes = cipher.doFinal(enBytes);   
    
            publicKeyString = getKeyString(publicKey);   
            System.out.println("public:\n" +publicKeyString);   
    
            privateKeyString = getKeyString(privateKey);   
            System.out.println("private:\n" + privateKeyString);   
    
            String s = new String(deBytes);   
            System.out.println(s);   
    
    
      }   
    
}   