package com.niezhiliang.luck.draw.service;

import com.niezhiliang.luck.draw.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
public interface IUserService extends IService<User> {

    boolean isCanLuckDraw(String name);

}
