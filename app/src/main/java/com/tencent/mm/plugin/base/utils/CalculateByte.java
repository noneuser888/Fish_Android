package com.tencent.mm.plugin.base.utils;

import android.content.ComponentName;
import android.content.Intent;

import java.security.MessageDigest;

public class CalculateByte {
    public static byte[] getBytes(int sdkVersion, String packageName) {
        StringBuffer var3 = new StringBuffer();
//        if (content != null) {
//            var3.append(content);
//        }

        var3.append(sdkVersion);
        var3.append(packageName);
        var3.append("mMcShCsTr");
        return CalculateString.getMD5(var3.toString().substring(1, 9).getBytes()).getBytes();
    }

    public static Intent auth(String redirectUrl, String token, String appPackage, String state) {

        if (state == null && state.length() == 0) {
            state = "1606139357678";
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(appPackage, appPackage + ".wxapi.WXEntryActivity"));
        intent.putExtra("_mmessage_appPackage", "com.tencent.mm");
        String content = null;
        intent.putExtra("_mmessage_content", content);
        intent.putExtra("_mmessage_sdkVersion", 637928960);
        intent.putExtra("_mmessage_checksum", checkSum(intent.getStringExtra("_mmessage_content"), intent.getIntExtra("_mmessage_sdkVersion", 0), intent.getStringExtra("_mmessage_appPackage")));
        intent.putExtra("_message_token", content);
        intent.putExtra("wx_token_key", "com.tencent.mm.openapi.token");
        intent.putExtra("_wxapi_sendauth_resp_state", state);
        intent.putExtra("_wxapi_sendauth_resp_token", token);
        intent.putExtra("_wxapi_baseresp_transaction", content);
        intent.putExtra("_wxapi_sendauth_resp_lang", "zh_CN");
        intent.putExtra("_wxapi_command_type", 1);
        intent.putExtra("_wxapi_sendauth_resp_country", "CN");
        intent.putExtra("_wxapi_sendauth_resp_auth_result", false);
        intent.putExtra("_wxapi_sendauth_resp_url", redirectUrl);
        intent.putExtra("_wxapi_baseresp_errcode", 0);
        intent.putExtra("_wxapi_baseresp_errstr", content);
        intent.putExtra("_wxapi_baseresp_openId", content);
        return intent;
    }

    private static byte[] checkSum(String str, int i, String str2) {
        StringBuffer stringBuffer = new StringBuffer();
        if (str != null) {
            stringBuffer.append(str);
        }
        stringBuffer.append(i);
        stringBuffer.append(str2);
        stringBuffer.append("mMcShCsTr");
        return digest(stringBuffer.toString().substring(1, 9).getBytes()).getBytes();
    }

    private static String digest(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            byte[] digest = instance.digest();
            int length = digest.length;
            char[] cArr2 = new char[length * 2];
            int i = 0;
            for (byte b : digest) {
                int i2 = i + 1;
                cArr2[i] = cArr[(b >>> 4) & 15];
                i = i2 + 1;
                cArr2[i2] = cArr[b & 15];
            }
            return new String(cArr2);
        } catch (Exception unused) {
            return null;
        }
    }

}
