package com.niezhiliang.luck.draw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niezhiliang.luck.draw.entity.LuckRecode;
import com.niezhiliang.luck.draw.entity.User;
import com.niezhiliang.luck.draw.mapper.LuckRecodeMapper;
import com.niezhiliang.luck.draw.mapper.UserMapper;
import com.niezhiliang.luck.draw.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private LuckRecodeMapper luckRecodeMapper;

    @Override
    public boolean isCanLuckDraw(String name) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda()
                .eq(User::getName,name);

        User user = this.getOne(userQueryWrapper);
        //说明没抽过奖，奖用户插入到用户表中
        if (user == null) {
            user = new User();
            user.setName(name);
            return this.save(user);

        }

        //查询数据库中是否存在中奖记录
        QueryWrapper<LuckRecode> luckRecodeQueryWrapper = new QueryWrapper<>();
        luckRecodeQueryWrapper.lambda().eq(LuckRecode::getUserId,user.getId());

        return luckRecodeMapper.selectList(luckRecodeQueryWrapper).size() > 0 ? false: true;

    }
}
