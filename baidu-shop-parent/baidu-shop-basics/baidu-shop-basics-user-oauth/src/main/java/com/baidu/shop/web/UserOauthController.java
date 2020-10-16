package com.baidu.shop.web;

import com.alibaba.fastjson.JSON;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService userOauthService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 用户登录
     * @param userEntity
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @PostMapping(value = "oauth/login")
    @ApiOperation(value = "用户登录")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity
            , HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        String toKin = userOauthService.login(userEntity,jwtConfig);

        if (ObjectUtil.isNull(toKin)) {
            return this.setResultError(HTTPStatus.VALID_USER_PASSWORD_ERROR,"用户名或密码错误");
        }

        CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtConfig.getCookieName(),toKin,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    @GetMapping(value = "oauth/verify")
    public Result<UserInfo> verifyUser(@CookieValue(value = "MRSHOP_TOKEN") String token
            ,HttpServletRequest request , HttpServletResponse response){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            return this.setResultSuccess(userInfo);
        } catch (Exception e) {//如果有异常 说明token有问题
            //e.printStackTrace();
            //应该新建http状态为用户验证失败,状态码为403
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"用户失效");
        }
    }


}
