package com.baidu.shop.business.impl;


import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;

import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/21
 * @Version V1.0
 **/
@Service
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private IdWorker idWorker;

    @Resource
    private JwtUtils jwtUtils;

    @Autowired
    private RedisRepository redisRepository;


    @Transactional
    @Override
    public Result<Long> createOrder(OrderDTO orderDTO,String token) {
        System.out.println("----------");
        return this.setResultSuccess(1L);
    }
}
