package com.baidu.shop.utils;

import org.springframework.beans.BeanUtils;

/**
 * @ClassName BaiDuBeanUtil
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/31
 * @Version V1.0
 **/
public class BaiDuBeanUtil<T1,T2> {
    public static <T2> T2 copyProperties(Object source, Class<T2> clazz){

        if(null == source){
            return null;
        }
        if(null == clazz){
            return null;
        }

        try {
            T2 t2 = clazz.newInstance();
            BeanUtils.copyProperties(source,t2);
            return t2;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
