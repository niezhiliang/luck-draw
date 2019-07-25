package com.niezhiliang.luck.draw.controller;


import com.niezhiliang.luck.draw.constant.LuckCodeEnum;
import com.niezhiliang.luck.draw.constant.RedisKeyConstant;
import com.niezhiliang.luck.draw.constant.ResultCode;
import com.niezhiliang.luck.draw.exception.LuckDrawException;
import com.niezhiliang.luck.draw.service.IPrizeService;
import com.niezhiliang.luck.draw.service.IUserService;
import com.niezhiliang.luck.draw.utils.RedisListTools;
import com.niezhiliang.luck.draw.utils.ResultUtils;
import com.niezhiliang.luck.draw.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
@RestController
@RequestMapping("/prize")
@Slf4j
public class PrizeController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IPrizeService prizeService;

    @Autowired
    private RedisListTools<String> listTools;

    @RequestMapping(value = "test")
    public Long test() {
        return listTools.initIncreCount(RedisKeyConstant.PRIZE_COUNT);
    }

    @RequestMapping(value = "init")
    public String init() {

        //计数器初始化为0
        listTools.initIncreCount(RedisKeyConstant.PRIZE_COUNT);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_200);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_400);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_570);




        int temp = new Random().nextInt(170);
        /****************第一阶段*************************/
        List<String> list200 = new ArrayList<>();

        //三等奖 2个
        list200.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 2个
        list200.add(LuckCodeEnum.GRADE_FOUR.getCode());

        //五等奖 15个
        for (int i = 0; i< 15; i++) {
            list200.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        //幸运奖
        for (int j = 0; j < 180; j++) {
            list200.add(LuckCodeEnum.GRADE_LUCK.getCode());
            //放二等奖进去
            if (j == temp) {
                //二等奖 1个
                list200.add(LuckCodeEnum.GRADE_TWO.getCode());
                list200.add(LuckCodeEnum.GRADE_THREE.getCode());
                list200.add(LuckCodeEnum.GRADE_FOUR.getCode());
            }
        }
        /***********************************************/

        /*****************第二阶段***********************/
        List<String> list400 = new ArrayList<>();
        //二等奖 1个
        list400.add(LuckCodeEnum.GRADE_TWO.getCode());
        //三等奖 2个
        list400.add(LuckCodeEnum.GRADE_THREE.getCode());
        list400.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 2个
        list400.add(LuckCodeEnum.GRADE_FOUR.getCode());
        list400.add(LuckCodeEnum.GRADE_FOUR.getCode());

        //五等奖 15个
        for (int i = 0; i< 15; i++) {
            list400.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        //幸运奖
        for (int j = 0; j < 180; j++) {
            list400.add(LuckCodeEnum.GRADE_LUCK.getCode());
        }
        /**********************************************/

        /*****************第三阶段***********************/
        List<String> list570 = new ArrayList<>();

        //三等奖 2个
        list570.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 4个
        list570.add(LuckCodeEnum.GRADE_FOUR.getCode());


        //五等奖 15个
        for (int i = 0; i< 10; i++) {
            list570.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        int index = new Random().nextInt(130);
        //幸运奖
        for (int j = 0; j < 153; j++) {
            list570.add(LuckCodeEnum.GRADE_LUCK.getCode());
            if (index == j) {
                list570.add(LuckCodeEnum.GRADE_ONE.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());

            }
            if (index +20 == j) {
                //二等奖 1个
                list570.add(LuckCodeEnum.GRADE_TWO.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());
            }
        }

        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_200,list200);
        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_400,list400);
        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_570,list570);
        /**********************************************/

        System.out.println(listTools.getListSize(RedisKeyConstant.PRIZE_COUNT_200));
        System.out.println(listTools.getListSize(RedisKeyConstant.PRIZE_COUNT_400));
        System.out.println(listTools.getListSize(RedisKeyConstant.PRIZE_COUNT_570));

        return "success";
    }


    @RequestMapping(value = "do")
    public ResultVO invoke(String name) {
        //判断该用户是否已经中奖
        boolean flag = userService.isCanLuckDraw(name);
        if (!flag) {
            log.warn("{},已经抽过奖，无需重复抽奖",name);
            throw new LuckDrawException(ResultCode.LUCK_DRAW_AGAIN);
        }
        boolean isOk = false;
        //通过redis拿到当前的prize_count的大小
        Long prizeCount = listTools.increCount(RedisKeyConstant.PRIZE_COUNT);

        //默认幸运奖
        String luckCode = LuckCodeEnum.GRADE_LUCK.getCode();
        if (prizeCount <= 570) {
            String prizeCodeKey = null;
            if (prizeCount <= 200) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_200;
            } else if (prizeCount <= 400) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_400;
            } else if (prizeCount <= 570) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_570;
            }

            //这里写循环是为了补偿那些拿到数据库没有的库存的奖品
            synchronized (PrizeController.class) {
                for (int i = 0; i < 3; i++) {
                    Long size = listTools.getListSize(prizeCodeKey);
                    // 随机算法，获取随机下标并取出对应的奖品编码
                    int index = new Random().nextInt(size.intValue());
                    //获取到的中奖号码
                    final String code = listTools.getIndexValue(prizeCodeKey,index);
                    //将中奖号码赋予给外层的中奖号码
                    luckCode = code;
                    //执行的结果，返回true表示数据库还有余额
                    //取数据库核对该奖品是否还有余额，有自减 没有重新进行随机算法 最后将中奖信息插入数据库
                    isOk = prizeService.resultTrue(name,code);
                    if (isOk) {
                        //移除redis中对应list中的一个奖项
                        listTools.removeFirstValue(prizeCodeKey,code);
                        break;
                    }
                    log.error("{},获取{}奖品，库存余额不足 这是第几次获取:{}",name,luckCode,i+1);
                }
            }
        }
        //如果该代码执行了说明拿出的奖品数据库余额不足，直接返回幸运奖
        if (!isOk) {
            if (prizeCount > 570)
                log.warn("{}，奖品已经全部发放完毕，直接返回幸运奖",name);
            else
                log.error("{}，拿到的奖品数据库余额不足，直接返回幸运奖",name);
            prizeService.resultTrue(name, luckCode);
        }
        return ResultUtils.success("您中的奖为 ["+luckCode+"]");
    }
}
