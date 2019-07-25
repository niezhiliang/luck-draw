package com.niezhiliang.luck.draw.service;

import com.niezhiliang.luck.draw.entity.Prize;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
public interface IPrizeService extends IService<Prize> {

    boolean resultTrue(String name,String luckCode);

}
