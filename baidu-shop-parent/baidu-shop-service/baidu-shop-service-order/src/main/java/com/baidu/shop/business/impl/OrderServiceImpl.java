package com.baidu.shop.business.impl;


import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.dto.CarDTO;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import com.netflix.discovery.converters.Auto;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    private JwtConfig jwtConfig;

    @Resource
    private RedisRepository redisRepository;

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);

        //detail
        OrderInfo orderInfo = BaiDuBeanUtil.copyProperties(orderEntity, OrderInfo.class);
        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderInfo.getOrderId());

        List<OrderDetailEntity> orderDetailList = orderDetailMapper.selectByExample(example);
        orderInfo.setOrderDetailList(orderDetailList);

        //status
        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderInfo.getOrderId());
        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);
    }

    @Transactional
    @Override
    public Result<String> createOrder(OrderDTO orderDTO,String token) {

        long orderId = idWorker.nextId();

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            OrderEntity orderEntity = new OrderEntity();
            Date date = new Date();

            orderEntity.setOrderId(orderId);
            orderEntity.setUserId(userInfo.getId() + "");
            orderEntity.setInvoiceType(1);//发票
            orderEntity.setSourceType(1);//pc端
            orderEntity.setBuyerRate(1);//是否评论
            orderEntity.setBuyerMessage("晴天");
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setPaymentType(orderDTO.getPayType());
            orderEntity.setCreateTime(date);

            List<Long> longs = Arrays.asList(0L);
            //detail
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuIdStr -> {
                CarDTO carDTO = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), skuIdStr, CarDTO.class);
                if (carDTO == null) {
                    throw new RuntimeException("数据异常");
                }
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setSkuId(Long.valueOf(skuIdStr));
                orderDetailEntity.setTitle(carDTO.getTitle());
                orderDetailEntity.setImage(carDTO.getImage());
                orderDetailEntity.setPrice(carDTO.getPrice());
                orderDetailEntity.setNum(carDTO.getNum());
                orderDetailEntity.setOrderId(orderId);
                longs.set(0, carDTO.getPrice() * carDTO.getNum() + longs.get(0));
                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setActualPay(longs.get(0));
            orderEntity.setTotalPay(longs.get(0));

            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1);//创建订单没支付

            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //通过用户id和skuid删除购物车中的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuIdStr -> {
                redisRepository.delHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),skuIdStr);
            });

            orderDetailList.stream().forEach(orderDetailEntity -> {
                orderMapper.updateStockBySpuId(orderDetailEntity.getSkuId(),orderDetailEntity.getNum());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResult(HTTPStatus.OK,"", orderId + "");
    }
}
