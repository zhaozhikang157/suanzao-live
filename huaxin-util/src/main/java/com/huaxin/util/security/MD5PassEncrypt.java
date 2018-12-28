package com.huaxin.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by syl on 2016/4/23.
 */
public class MD5PassEncrypt {

    static private final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    static private final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static public void main(String argv[]) throws NoSuchAlgorithmException {
        String s = "123456";
        System.out.println(crypt(s));
        System.out.println(getMD5Str("123456"));
        //System.out.println(s.substring(3) + "------");//$1$HRVO1hER$XqtRshqCkSZMLMT4MOqHS1
//        for (int i = 0; i < 1; i++) {
//            String encryptPass = crypt("111111");
//            System.out.println("第" +i + "次："+ encryptPass);
//            System.out.println(checkCrypt("123",encryptPass));
//        }
       // System.out.println("--------"+getMD5Str(s));

    }
    /**
     * 判断MD5加密   是否相同      用户登录  syl
     * @param realPass ： 明文密码
     * @param encryptPass ： MD5 已加密字符串
     * @return
     * @throws NoSuchAlgorithmException
     */
    static public  boolean checkCrypt(String realPass ,String encryptPass) throws NoSuchAlgorithmException{
        int tmpInt = encryptPass.lastIndexOf("$");
        if (tmpInt < 0) {
            return false;
        }
        String pw = realPass;
        String salt =  encryptPass.substring(0, tmpInt + 1);
        String srcPassArray = encryptPass;
        String  tmpPass = crypt (pw, salt);
        if(tmpPass.equals(srcPassArray)){
            //System.out.println(tmpPass +"\n" + srcPassArray);
            return true;
        }

        return false;
    }

    static private final String to64(long v, int size) {
        StringBuffer result = new StringBuffer();
        while (--size >= 0) {
            result.append(itoa64.charAt((int) (v & 0x3f)));
            v >>>= 6;
        }
        return result.toString();
    }
    static private final void clearbits(byte bits[]) {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = 0;
        }
    }

    /**
     * 编码的无符号字节值转换成一个int无符号值
     *
     */
    static private final int bytes2u(byte inp) {
        return (int) inp & 0xff;
    }
    /**
     * 这种方法实际上产生的OpenBSD / FreeBSD / Linux下的PAM兼容
     * MD5编码的明文密码和密码哈表
     * 由此产生的字符串将在形式'$ 1 $ <salt> $ <hashed mess>
     * @param password  明文密码
     * @return 一个OpenBSD / FreeBSD的/ Linux兼容的​​MD5哈希密码字段
     * @throws NoSuchAlgorithmException
     */
    static public final String crypt(String password)
            throws NoSuchAlgorithmException {
        StringBuffer salt = new StringBuffer();
        java.util.Random randgen = new java.util.Random();//随机取8个字符串
        while (salt.length() < 8) {
            int index = (int) (randgen.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.substring(index, index + 1));
        }
        return MD5PassEncrypt.crypt(password, salt.toString());
    }

    /**
     * 这种方法实际上产生的OpenBSD / FreeBSD / Linux下的PAM兼容
     * MD5编码的明文密码和密码哈表
     * 由此产生的字符串将在形式'$ 1 $ <salt> $ <hashed mess>
     * @param password  明文密码
     * @param salt  *将被忽略。它是明确允许通过一个预先存在的密码MD5Crypt'ed条目盐。的crypt（）将剥离盐
     * @return  一个OpenBSD / FreeBSD的/ Linux兼容的​​MD5哈希密码字段.
     * @throws NoSuchAlgorithmException
     */
    static public final String crypt(String password, String salt)
            throws NoSuchAlgorithmException {
		/*
		 * This string is magic for this algorithm. Having it this way, we can
		 * get get better later on 他的字符串是魔术这个算法。有这样，我们就可以得到更好稍后
		 */
        String magic = "$1$";
        byte finalState[];
        MessageDigest ctx, ctx1;
        long l;
		/* Refine the Salt first 首先遇到 */
		/* If it starts with the magic string, then skip that  如果它开始与魔术字符串，设置截取从第三位开始去最后*/
        if (salt.startsWith(magic)) {
            salt = salt.substring(magic.length());
        }
		/* It stops at the first '$', max 8 chars 它停在第一个' $' ，最多8个字符 */

        if (salt.indexOf('$') != -1) {//如存在$
            salt = salt.substring(0, salt.indexOf('$'));//从截取从第一位到 第一次出现$的位置，不取$
        }
        if (salt.length() > 8) {//如果大于8位字符，只去前8位
            salt = salt.substring(0, 8);
        }
        ctx = MessageDigest.getInstance("MD5");//生成MD5

        ctx.update(password.getBytes()); // 首先，因为这是最不为人知的是什么密码
        ctx.update(magic.getBytes()); // Then our magic string 那么，我们的魔术字符串
        ctx.update(salt.getBytes()); // Then the raw salt 然后原盐

		/* Then just as many characters of the MD5(pw,salt,pw) 然后，只需尽可能多的字符的MD5 */
        ctx1 = MessageDigest.getInstance("MD5");
        ctx1.update(password.getBytes());
        ctx1.update(salt.getBytes());
        ctx1.update(password.getBytes());
        finalState = ctx1.digest();

        for (int pl = password.length(); pl > 0; pl -= 16) {
            for (int i = 0; i < (pl > 16 ? 16 : pl); i++)
                ctx.update(finalState[i]);
        }
		/*
		 * the original code claimed that finalState was being cleared to keep
		 * dangerous bits out of memory, but doing this is also required in
		 * order to get the right output.
		 * 声称finalState被清除，以保持原代码
          *危险位的内存，但这样做还需要在
          *为了得到正确的输出
		 */
        clearbits(finalState);
		/* Then something really weird...   然后很奇怪的东西...*/
        for (int i = password.length(); i != 0; i >>>= 1) {
            if ((i & 1) != 0) {
                ctx.update(finalState[0]);
            } else {
                ctx.update(password.getBytes()[0]);
            }
        }
        finalState = ctx.digest();

		/*
		 * and now, just to make sure things don't run too fast On a 60 Mhz
		 * Pentium this takes 34 msec, so you would need 30 seconds to build a
		 * 1000 entry dictionary...
		 * 而现在，只是为了确保事情不运行在60 MHz的速度太快
		 *奔腾这需要34毫秒，所以你将需要30秒，以建立一个
		 * 1000条目字典...
		 * (The above timings from the C version) 从C版本以上的计时
		 */
        for (int i = 0; i < 1000; i++) {
            ctx1 = MessageDigest.getInstance("MD5");
            if ((i & 1) != 0) {
                ctx1.update(password.getBytes());
            } else {
                for (int c = 0; c < 16; c++)
                    ctx1.update(finalState[c]);
            }
            if ((i % 3) != 0) {
                ctx1.update(salt.getBytes());
            }
            if ((i % 7) != 0) {
                ctx1.update(password.getBytes());
            }
            if ((i & 1) != 0) {
                for (int c = 0; c < 16; c++)
                    ctx1.update(finalState[c]);
            } else {
                ctx1.update(password.getBytes());
            }

            finalState = ctx1.digest();
        }

		/* Now make the output string  现在，使输出的字符串*/

        StringBuffer result = new StringBuffer();

        result.append(magic);
        result.append(salt);
        result.append("$");

        l = (bytes2u(finalState[0]) << 16) | (bytes2u(finalState[6]) << 8)| bytes2u(finalState[12]);
        result.append(to64(l, 4));

        l = (bytes2u(finalState[1]) << 16) | (bytes2u(finalState[7]) << 8)| bytes2u(finalState[13]);
        result.append(to64(l, 4));
        l = (bytes2u(finalState[2]) << 16) | (bytes2u(finalState[8]) << 8)| bytes2u(finalState[14]);
        result.append(to64(l, 4));
        l = (bytes2u(finalState[3]) << 16) | (bytes2u(finalState[9]) << 8)	| bytes2u(finalState[15]);
        result.append(to64(l, 4));
        l = (bytes2u(finalState[4]) << 16) | (bytes2u(finalState[10]) << 8)| bytes2u(finalState[5]);
        result.append(to64(l, 4));
        l = bytes2u(finalState[11]);
        result.append(to64(l, 2));

		/* Don't leave anything around in vm they could use. 不要让周围的任何东西在的VM他们可以使用 */
        clearbits(finalState);

        return result.toString();
    }


    /**
     * md5 加密
     *
     * @param url
     * @return
     */
    public static String getMD5Str(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(url.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int i = b & 0xff;// 将字节转为整数
                String hexString = Integer.toHexString(i);// 将整数转为16进制
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;// 如果长度等于1, 加0补位
                }
                sb.append(hexString);
            }
            //规则 :加密的字符取前10位拼接在加密字符后 再反转
            return sb.append(sb.substring(0, 10)).reverse().toString();
        } catch (NoSuchAlgorithmException ex) {
            return url;
        }
    }
    
    
}
