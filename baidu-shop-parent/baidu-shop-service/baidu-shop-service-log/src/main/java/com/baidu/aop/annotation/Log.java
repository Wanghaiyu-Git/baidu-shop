package com.baidu.aop.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Log
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Target(ElementType.METHOD)//注解放置的目标位置即方法级别
@Retention(RetentionPolicy.RUNTIME)//注解在哪个阶段执行
@Documented
public @interface Log {
    String operModul() default ""; // 操作模块

    String operType() default "";  // 操作类型

    String operDesc() default "";  // 操作说明
}
