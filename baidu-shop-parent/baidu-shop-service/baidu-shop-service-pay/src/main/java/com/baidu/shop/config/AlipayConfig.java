package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/22
 * @Version V1.0
 **/
public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766471";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCIDhXzM7auVldM/7BVTWSJpJkiybcxD5vSAV9FqVaxHkmcJGs9zmNxvsNhhNZF160Zo3tuZ2Enb3Xhoed4YcSZcSB3HoeFJoTAJ6G5IxhKr2iSwpZfVjzNQyfuNIgfdEuM4AQuaX+udEXsECgPsFN12LkswfwKLoFCvIrHQ9GBved4LIZHTvu7lwDyu3zer32SdJu5qayNndPSBEQsHh6UaXR1aQGREk21PyGmwaQuZKDRGwviYJbCZm1NUGbKDKPcbCWAkNOwDoQh1szuBYvZeswwfp0/XlJ5J6qNpXM6oTtafx8fpayg4EZFrJs+WgYjaCoR/M3wEktae/c2C3vpAgMBAAECggEAMGw6+bT1FrVaGuiBvMpvtuFjM1NxpL92aNbeLq/nT+CEuFLp7DRxhkErzqB1lqP9UruxqYiXtm5gGS8Db//z9dyifmLaPtSHuNAiKf9InQQzBtDErifGOhPluUzvCardXMU6F76ql8+AN1VEvNi3y6CXv0tHReRpUvRBvEvx2+fNnuy8R74c+IL3Fo0g+oke5Dp1X7jNN3vQ/57j1MWTGpkSzO4FsOlwnI1yiMTWRLDIAoJR364NtrAUn71sJM0d8psgxROyh+G8WuqCFhkoNtRDgzWSbE5o05shnF53mYjJC6WJeMLf2D/UOlSKEwjOnDrgmYsCnVNertveMPr19QKBgQD4GLF1KKD17Vf2EtB+CrYszCE3OCIkqtpPO/u7XN1QZ2MHqyqSIR5UZ8lww7BU/VCV+J1rAtZ7Jv1oH5kPe8VvreVwA/ulh4eBOEvxh7k73XrPAaUA/Rj3VpFmTzGTltkfxHhiKyPrex0uGd56BUatIxaURSmuvaNUJVyEHbTpmwKBgQCMY6iOtJwUxOgtT0V+CTbWsSwjXYRyiR3iNkOAAAXsivM/JLzSEtCVZOFm7sY0YlXJucoAbLG5erS5ide91cQVh9e/LPZh3ifPALDgJ//VMTxyzPXBVb43Dxhgf/8eQQ3fJqn+v8Fqc63+m5d31YfMzT67O35QSTq730zxHZ2aywKBgDzp+zAz4IlhU/aEGhLIWIBZQlKHgsKT/HP121HedCxYphTs7s/gN80rimcYdQP5Cj84QpZSpQypvxBAqdkGX6yS+sFtCO1UgG22v9a2p0FOmOeYlMXQXr/jzmFbUmXVZMwK9i/MPc6EdN6r1VcKUpcSBWo0LQ7NWvu/DJ8XmCK/AoGAabtM4V1pB8Nc0XMYdVnfMUZyZZgy/kgCd7JCmA5sZ8AmfvHeO6Dr8vftQqamJCHDVpBUcIlsOLMPlYUZNOHgMg8DD/p+/SphdOeiTtARP7E7Q4LVu+wlvDXkOBluPIzmM6Bc+33T36aL7mukYh4xjq8thbnC4hik4YXF3vpdDo0CgYEAlTK9PkpelKjc/OJO6yzV0iR1+9XoyvzxPrcZK24DjexPpqZaaGF1SnfLCgrTN/SheBDdyNxXJ03N/Aob2KIiuqhhmd7+pMcUReUPKxLA/9uuoa3tZM19IzBcVUjrVmBPCxxhf4kclDkPqzFYOxs9Wk3JYqsMMMxNddn7zaDa6Fs=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwCi0z5p566rqHWUH5RtktyItuBKQ8YsSkwTGvJIw0Fy1++eod0DVSgxFeslKD+hkJQIgqwZVg2XvxBjesx1CozQiAW8DBC5xSh/dCu0h1lwg1+Ha9Iw3Lzw1JKhzcknQFB0S4gvYn8eRTvNCnCRgbvcEFwP/G86/mUvCKR8HH9+oswdPw+OXYv9uP0NdfIS4oe3zJAqI7qCrY/hS1Dye4qyYGJScM/uKuEBeIJRDQv/aIepZS9XQjFdeR5gVxzU7a+OKdQ/ZHafEMHG/Cb7ev6Mv8366DV0vTNAx7KdxAWmOANTwuYzKVVP60Vhcf06no+5WpvES/iYv9Ox1/iC1HQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
