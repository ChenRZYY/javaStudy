package com.zt.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 陈振东
 */
@Slf4j
public class GZIPUtils {
    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";
    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";


    /**
     * @param str
     * @param encoding
     * @return
     * @date 2019年2月19日
     * @author 陈振东
     */
    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        //
//		log.error("转码前: " + str);
//		try {
//			str = URLEncoder.encode(str, GZIP_ENCODE_UTF_8);
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		log.error("转码后: " + str);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(GZIP_ENCODE_UTF_8));
            gzip.close();

            // String strUTF = out.toString(GZIP_ENCODE_UTF_8);
            // String result = encodeURIComponent(strUTF);
            log.error("转码压缩后: " + Arrays.toString(out.toByteArray()));

            return out.toByteArray();
//			return out.toString(GZIP_ENCODE_UTF_8).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        // return null;
        // try {
        //// return URLEncoder.encode(out.toString(GZIP_ENCODE_ISO_8859_1),
        // GZIP_ENCODE_UTF_8).getBytes();
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // }
        return null;
    }

    public static byte[] compress(String str) throws IOException {
        return compress(str, GZIP_ENCODE_UTF_8);
    }

    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return out.toByteArray();
    }

    public static String uncompressToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uncompressToString(byte[] bytes) {
        return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
    }

    public static void main(String[] args) throws IOException {
        String s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa陈振东,陈振东,陈振东";
        System.out.println("字符串长度：" + s.length());
        //1 先压缩-->再转成byte数组
        byte[] compress = compress(s);
        System.out.println("压缩后：：" + compress(s).length);
        System.out.println(Arrays.toString(compress));
        //2 解压成 byte数组
        byte[] uncompress = uncompress(compress);
        System.out.println("解压后：" + uncompress(compress(s)).length);
        System.out.println(Arrays.toString(uncompress));
        //3 解压转成字符串
        String uncompressToString = uncompressToString(compress(s));
        System.out.println("解压字符串后：：" + uncompressToString(compress(s)).length());
        System.out.println(uncompressToString);
    }

    public static String encodeURIComponent(String s) {
        String result = null;

        try {
//        result = URLEncoder.encode(s, "UTF-8")
//                           .replaceAll("\\+", "%20")
//                           .replaceAll("\\%21", "!")
//                           .replaceAll("\\%27", "'")
//                           .replaceAll("\\%28", "(")
//                           .replaceAll("\\%29", ")")
//                           .replaceAll("\\%7E", "~");

            result = URLEncoder.encode(s, "UTF-8");

        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}