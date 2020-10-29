package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.RecommendDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.mapper.RecommendMapper;
import com.baidu.shop.redis.respository.RedisRepository;
import com.baidu.shop.service.RecommendService;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.google.common.collect.EvictingQueue;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @ClassName RecommendServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/29
 * @Version V1.0
 **/
@RestController
public class RecommendServiceImpl extends BaseApiService implements RecommendService {

    @Resource
    private RedisRepository redisRepository;

    @Resource
    private RecommendMapper recommendMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Override
    public Result<JSONObject> saveRecommend(RecommendDTO recommendDTO, String token) {
        try {
            //创建一个队列
            EvictingQueue<RecommendDTO> queue = EvictingQueue.create(50);
            //获取登录的用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //从redis获取浏览记录
            String s = redisRepository.get("" + userInfo.getId());
            if(null != s){
                //将获取的数据转为List
                List<RecommendDTO> recommendList = JSONUtil.toList(s, RecommendDTO.class);
                //去重
                for(int i = 0;i < recommendList.size();i++){
                    if(recommendList.get(i).getSpuId() == recommendDTO.getSpuId()){
                        recommendList.remove(i);
                    }
                }
                //将list数据全部放到队列里
                queue.addAll(recommendList);
                //将新的浏览记录放到队列里
                queue.add(recommendDTO);
                queue.forEach(browse ->{
                    System.out.println(browse);
                });
                //重新放到redis里
                redisRepository.set("" + userInfo.getId(), JSONUtil.toJsonString(queue));
            }else{
                //是null 的话,相当于第一条浏览记录,直接新增
                queue.add(recommendDTO);
                redisRepository.set("" + userInfo.getId(), JSONUtil.toJsonString(queue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<List<RecommendDTO>> getRecommend(String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //获取redis里的数据
            String s = redisRepository.get("" + userInfo.getId());
            List<RecommendDTO> list = new ArrayList<>();

            //这块代码比较垃圾,可以优化,看看就好
            //redis里的数据不是null的话
            if(null != s){
                //转换redis里的数据
                List<RecommendDTO> recommendList = JSONUtil.toList(s, RecommendDTO.class);
                //判断浏览记录是否大于10条  ,按理说不能让下标重复
                if(recommendList.size() > 10){
                    //大于十条的话 ,随机十个下标
                    for (int i = 0;i < 10;i++){
                        Random random = new Random();
                        int item = random.nextInt(recommendList.size() - 1);
                        list.add(recommendList.get(item));
                    }
                    //将10条商品信息返回去
                    return this.setResultSuccess(list);
                }else{
                    //浏览记录小于10条的话,直接用数据库的数据   按理说不应该这样做,能力有限
                    Result<List<RecommendDTO>> goodsInfo = this.getGoodsInfo();
                    List<RecommendDTO> data = goodsInfo.getData();
                    return this.setResultSuccess(data);
                }//优化优化,全是重复
            }else{
                //是null的话 ,也用数据库的数据
                Result<List<RecommendDTO>> goodsInfo = this.getGoodsInfo();
                List<RecommendDTO> data = goodsInfo.getData();
                return this.setResultSuccess(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("错误");
    }

    @Override
    public Result<List<RecommendDTO>> getGoodsInfo() {
        List<RecommendDTO> list = new ArrayList<>();
        //查询出所有的数,.效率比较慢,看看就好,可以有更好的方法
        List<SkuEntity> skuEntityList = recommendMapper.selectAll();
        //随机十个数据
        for (int i = 0;i < 10;i++){
            Random random = new Random();
            int item = random.nextInt(skuEntityList.size() - 1);
            //copy自己需要的数据
            RecommendDTO recommendDTO = BaiDuBeanUtil.copyProperties(skuEntityList.get(item), RecommendDTO.class);
            list.add(recommendDTO);
        }
        //将十条信息返回前台
        return this.setResultSuccess(list);
    }
}
