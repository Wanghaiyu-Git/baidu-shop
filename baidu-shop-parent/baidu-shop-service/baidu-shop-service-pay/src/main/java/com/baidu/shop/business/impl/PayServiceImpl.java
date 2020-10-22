package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.business.PayService;
import org.springframework.stereotype.Controller;


/**
 * @ClassName PayServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/22
 * @Version V1.0
 **/
@Controller
public class PayServiceImpl extends BaseApiService implements PayService {

    @Override
    public void requestPay() {
        System.out.println("------");
    }
}
