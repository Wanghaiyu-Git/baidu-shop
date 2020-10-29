package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.RecommendDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName RecommendService
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/29
 * @Version V1.0
 **/
public interface RecommendService {

    @PostMapping(value = "recommend/saveRecommend")
    Result<JSONObject> saveRecommend(@RequestBody RecommendDTO recommendDTO, @CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getRecommend")
    Result<List<RecommendDTO>> getRecommend(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getGoodsInfo")
    Result<List<RecommendDTO>> getGoodsInfo();
}
