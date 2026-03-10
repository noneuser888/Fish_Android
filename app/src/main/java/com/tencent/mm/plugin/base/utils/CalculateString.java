package com.tencent.mm.plugin.base.utils;

import java.security.MessageDigest;

public class CalculateString {
    public static final String getMD5(byte[] var0) {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest var2;
            (var2 = MessageDigest.getInstance("MD5")).update(var0);
            int var8;
            char[] var3 = new char[(var8 = (var0 = var2.digest()).length) * 2];
            int var4 = 0;

            for(int var5 = 0; var5 < var8; ++var5) {
                byte var6 = var0[var5];
                var3[var4++] = var1[var6 >>> 4 & 15];
                var3[var4++] = var1[var6 & 15];
            }

            return new String(var3);
        } catch (Exception var7) {
            return null;
        }
    }
}
