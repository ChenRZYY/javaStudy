package com.example.socket.util;

import net.neoremind.sshxcute.core.SSHExecUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author Haishi
 * @create 2020/6/8 16:36
 */
public class SSHExecUtilSon extends SSHExecUtil {


    public static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0) {
            return b;
        } else if (b == -1) {
            return b;
        } else {
            if (b == 1 || b == 2) {
                StringBuffer sb = new StringBuffer();

                int c;
                do {
                    c = in.read();
                    sb.append((char)c);
                } while(c != 10);

                if (b == 1) {
                    System.out.print(sb.toString());
                }

                if (b == 2) {
                    System.out.print(sb.toString());
                }
            }

            return b;
        }
    }


    public static String[] getFiles(File dir) {
        return dir.list();
    }
}
