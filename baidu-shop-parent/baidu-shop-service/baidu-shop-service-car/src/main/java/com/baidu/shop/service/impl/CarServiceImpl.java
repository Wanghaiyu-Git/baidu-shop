package com.baidu.shop.service.impl;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.dto.CarDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private GoodsFeign goodsFeign;

    @Override
    public Result<JSONObject> carNumUpdate(Long skuId, Integer type, String token) {

        //获取当前登录用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            CarDTO car = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), skuId + "", CarDTO.class);

            if(car != null){
                if(type == 1){
                    car.setNum(car.getNum() + 1);
                }else{
                    car.setNum(car.getNum() - 1);
                }
                redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(car));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CarDTO>> carCurrentUserGoodsCar(String token) {

        //获取当前登录用户信息
        try {
            List<CarDTO> carDTOS = new ArrayList<>();
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> goodsCarMap = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId());
            goodsCarMap.forEach((key,value) -> {
                CarDTO carDTO = JSONUtil.toBean(value, CarDTO.class);
                carDTOS.add(carDTO);
            });
            return this.setResultSuccess(carDTOS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultError("内部错误");
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {

        //json字符串转换为Json对象
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);
        //json对象属性为clientCarList的数据取出来 转换成List集合
        List<CarDTO> carList = com.alibaba.fastjson.JSONObject.parseArray(jsonObject
                .getJSONArray("clientCarList").toJSONString(), CarDTO.class);

        //遍历新增到购物车
        carList.stream().forEach(car -> addCar(car,token));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(CarDTO carDTO, String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过userId和skuId获得商品数据
            CarDTO redisCar = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    carDTO.getSkuId() + "", CarDTO.class);

            CarDTO saveCar = null;

            log.debug("通过key:{},skuId:{},获取到的数据为:{}",MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    carDTO.getSkuId(), redisCar);

            if(ObjectUtil.isNotNull(redisCar)){
                //原来的用户购物车中有当前要添加到购物车中的商品
                redisCar.setNum(carDTO.getNum() + redisCar.getNum());
                saveCar = redisCar;
                log.debug("当前用户购物车中有将要新增的商品,重新设置num:{}",redisCar.getNum());
            }else{//当前用户购物车种没有将要新增的商品信息

                Result<SkuEntity> skuResult = goodsFeign.getSkuBySkuId(carDTO.getSkuId());
                if (skuResult.getCode() == 200) {

                    SkuEntity skuEntity = skuResult.getData();
                    carDTO.setTitle(skuEntity.getTitle());
                    carDTO.setImage(StringUtil.isEmpty(skuEntity.getImages()) ? "" : skuEntity.getImages().split(",")[0]);
                    carDTO.setOwnSpec(skuEntity.getOwnSpec());
                    carDTO.setPrice(Long.valueOf(skuEntity.getPrice()));
                    carDTO.setUserId(userInfo.getId());

                    redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                            carDTO.getSkuId() + "", JSONUtil.toJsonString(carDTO));

                    saveCar = carDTO;
                    log.debug("新增商品到购物车redis, KEY:{}, skuId:{}, car:{}",
                            MRshopConstant.USER_PHONE_CODE_PRE + userInfo.getId(), carDTO.getSkuId(), JSONUtil.toJsonString(carDTO));
                }
            }

            redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    carDTO.getSkuId() + "", JSONUtil.toJsonString(saveCar));

            log.debug("新增到redis数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }
}
