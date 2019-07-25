package com.niezhiliang.luck.draw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niezhiliang.luck.draw.entity.LuckRecode;
import com.niezhiliang.luck.draw.entity.Prize;
import com.niezhiliang.luck.draw.entity.User;
import com.niezhiliang.luck.draw.mapper.PrizeMapper;
import com.niezhiliang.luck.draw.service.ILuckRecodeService;
import com.niezhiliang.luck.draw.service.IPrizeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niezhiliang.luck.draw.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
@Service
public class PrizeServiceImpl extends ServiceImpl<PrizeMapper, Prize> implements IPrizeService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ILuckRecodeService luckRecodeService;

    @Override
    @Transactional
    public boolean resultTrue(String name, String luckCode) {

        QueryWrapper<Prize> prizeQueryWrapper = new QueryWrapper<>();
        prizeQueryWrapper.lambda().eq(Prize::getLuckCode,luckCode);
        Prize prize = this.getOne(prizeQueryWrapper);
        if (prize.getBalance() > 0) {
            int balance = prize.getBalance() - 1;
            prize.setBalance(balance);
            //修改当前奖品的剩余份数
            boolean flag = this.updateById(prize);
            if (flag) {
                //获取用户信息
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.lambda()
                        .eq(User::getName,name);

                User user = userService.getOne(userQueryWrapper);

                LuckRecode luckRecode = new LuckRecode();
                luckRecode.setCreateAt(new Date());
                luckRecode.setPrizeId(prize.getId());
                luckRecode.setUserId(user.getId());

                return luckRecodeService.save(luckRecode);
            }
        }
        return false;
    }
}
