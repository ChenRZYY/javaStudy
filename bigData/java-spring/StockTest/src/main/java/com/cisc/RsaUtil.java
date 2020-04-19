package com.cisc;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Slf4j
public class RsaUtil {
    
    //public static final Charset UTF8 = Charset.forName("UTF-8");
    private static String loadKeyStr;
    
    //生成秘钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
    
    //获取公钥(Base64编码)
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byteToBase64(bytes);
    }
    
    //获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byteToBase64(bytes);
    }
    
    //将Base64编码后的公钥转换成PublicKey对象
    public static PublicKey stringToPublicKey(String pubStr) throws Exception {
        byte[] keyBytes = base64ToByte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    //将Base64编码后的私钥转换成PrivateKey对象
    public static PrivateKey stringToPrivateKey(String priStr) throws Exception {
        byte[] keyBytes = base64ToByte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
    //公钥加密
    public static byte[] publicEncrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }
    
    //私钥解密
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }
    
    /**
     * @date 2019年5月21日
     * @author 陈振东
     * @param str  需要加密的字符串
     * @param publicKey  公钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String str, String publicKey) {
        //        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        byte[] doFinal;
        try {
            //base64编码的公钥
//                    byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(base64ToByte(publicKey)));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            doFinal = cipher.doFinal(str.getBytes("UTF-8"));
            return String.valueOf(doFinal);
        }
        catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
            | IllegalBlockSizeException | BadPaddingException  | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @date 2019年5月21日
     * @author 陈振东
     * @param str 用公钥加密过后的字符串
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String str, String privateKey) {
        try {
            //64位解码加密后的字符串
            //        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            //        byte[] decoded = Base64.decodeBase64(privateKey);  
            RSAPrivateKey priKey = (RSAPrivateKey)KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey.getBytes()));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            String outStr = new String(cipher.doFinal(str.getBytes()));
            return outStr;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    //字节数组转Base64编码
    public static String byteToBase64(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }
    
    //Base64编码转字节数组
    public static byte[] base64ToByte(String base64Key) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Key);
    }
    
    //生成key文件并保存
    public static void createKeyFile() {
        try {
            //生成RSA公钥和私钥，并Base64编码
            KeyPair keyPair = getKeyPair();
            String publicKeyStr = getPublicKey(keyPair);
            String privateKeyStr = getPrivateKey(keyPair);
            System.out.println("RSA PublicKey  Base64== " + publicKeyStr);
            System.out.println("RSA PrivateKey Base64== " + privateKeyStr);
            //保存文件
            //File file = new File("src/main/resources/key.txt");
            OutputStream outputStream = new FileOutputStream("src/main/resources/key.txt");
            OutputStreamWriter osw = new OutputStreamWriter(outputStream, "UTF-8");
            osw.write(publicKeyStr + "+ToA1Z9oT+" + privateKeyStr);
            osw.close();
            System.out.println(">>> key save Success!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //读取key文件
    public static void loadKeyFile() {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = RsaUtil.class.getClassLoader().getResourceAsStream("key.txt");
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String readd = "";
            StringBuffer stringBuffer = new StringBuffer();
            while ((readd = bufferedReader.readLine()) != null) {
                stringBuffer.append(readd);
            }
            loadKeyStr = stringBuffer.toString();
            //System.out.println("key:"+loadKeyStr);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("读取文件失败，请检查文件是否存在！");
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    //获取公钥
    public static PublicKey loadPubKey() {
        try {
            //String loadKey = loadKeyFile();
            if (isNullOrEmpty(loadKeyStr)) {
                loadKeyFile();
            }
            return stringToPublicKey(loadKeyStr.substring(0, loadKeyStr.indexOf("+ToA1Z9oT+")));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //获取私钥
    public static PrivateKey loadPriKey() {
        try {
            //String loadKey = loadKeyFile();
            if (isNullOrEmpty(loadKeyStr)) {
                loadKeyFile();
            }
            return stringToPrivateKey(loadKeyStr.substring(loadKeyStr.indexOf("+ToA1Z9oT+") + 10, loadKeyStr.length()));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 加密
     * @date 2019年3月25日
     * @author 陈振东
     * @param password
     * @return
     */
    public static String rsaEncrypt(String password) {
        //password字段Base64解码
        String byteToBase64 = null;
        try {
            //读取公钥
            PublicKey pubKey = RsaUtil.loadPubKey();
            byte[] pwdDecrypt = RsaUtil.publicEncrypt(password.getBytes("utf-8"), pubKey);
            byteToBase64 = byteToBase64(pwdDecrypt);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Rsa加密失败", e);
        }
        
        return byteToBase64;
    }
    
    /**
     * 解密
     * @date 2019年3月25日
     * @author 陈振东
     * @param password
     * @return
     */
    public static String rsaDecrypt(String password) {
        //password字段Base64解码
        String pass = null;
        byte[] pwdToByte = null;
        try {
            pwdToByte = RsaUtil.base64ToByte(password);
            //解密
            PrivateKey priKey = RsaUtil.loadPriKey();
            byte[] pwdDecrypt = RsaUtil.privateDecrypt(pwdToByte, priKey);
            pass = new String(pwdDecrypt, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Rsa解密失败", e);
        }
        
        return pass;
    }
    
}