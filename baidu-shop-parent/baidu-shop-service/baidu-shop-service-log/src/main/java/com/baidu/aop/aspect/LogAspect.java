package com.baidu.aop.aspect;

import com.baidu.aop.annotation.Log;
import com.baidu.aop.entity.LogEntity;
import com.baidu.aop.mapper.LogMapper;
import com.baidu.aop.utils.IPUtils;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName LogAspect
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Aspect
@Component
public class LogAspect {

    @Resource
    private LogMapper logMapper;

    @Resource
    private JwtConfig jwtConfig;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 设置操作日志切入点   在注解的位置切入代码
     */
    @Pointcut("@annotation(com.baidu.aop.annotation.Log)")
    public void operLogPoinCut() {
    }


    @AfterReturning(returning  /**
     * 记录操作日志
     * @param joinPoint 方法的执行点
     * @param result  方法返回值
     * @throws Throwable
     */ = "result", value = "operLogPoinCut()")
    public void saveOperLog(JoinPoint joinPoint, Object result) throws Throwable {

        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //从切面织入点通过反射获取织入点的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //获取到切入点所在的方法
        Method method = signature.getMethod();
        LogEntity logEntity = new LogEntity();
        //获取注解参数
        Log annotation = method.getAnnotation(Log.class);
        if (null != annotation) {
            logEntity.setModel(annotation.operModul());//操作模块
            logEntity.setDescription(annotation.operDesc());//具体操作
            logEntity.setType(annotation.operType());//操作类型
        }
        logEntity.setOperationTime(Timestamp.valueOf(sdf.format(new Date())));//操作时间
        logEntity.setIp(IPUtils.getIpAddr(request));//用户登陆的ip
        logEntity.setUrl(request.getRequestURI());//访问的方法

        String cookieValue = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        if (null != cookieValue) {
            try {
                UserInfo userInfo = JwtUtils.getInfoFromToken(cookieValue, jwtConfig.getPublicKey());
                logEntity.setUsername(userInfo.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logMapper.insertSelective(logEntity);
    }
}
