package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.LuosimaoDuanxinUtil;
import com.baidu.shop.validate.grop.BaiDuOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 注册账户
     * @param userDTO
     * @return
     */
    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiDuBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());

        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }

    /**
     * 检验用户名手机号
     * @param value
     * @param type
     * @return
     */
    @Override
    public Result<JSONObject> checkUsernameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(type == UserConstant.USER_TYPE_USERNAME){
            criteria.andEqualTo("username",value);
        }else if(type == UserConstant.USER_TYPE_PHONE){
            criteria.andEqualTo("phone",value);
        }
        List<UserEntity> userList = userMapper.selectByExample(example);

        return this.setResultSuccess(userList);
    }

    /**
     * 给手机发送验证码
     * @param userDTO
     * @return
     */
    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //生成随机6位验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";

        //发送短信验证码
        log.debug("向手机号码:{} 发送验证码:{}",userDTO.getPhone(),code);
//        LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);

        //发送语音验证
//        LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);

        return this.setResultSuccess();
    }
}
