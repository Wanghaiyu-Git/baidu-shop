package com.baidu.shop.utils;

/**
 * @ClassName StringUtil
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/31
 * @Version V1.0
 **/
public class StringUtil {

    public static Boolean isEmpty(String str){
        return null == str || "".equals(str);
    }

    public static Boolean isNotEmpty(Object str){

        return null != str && !"".equals(str);
    }

}
