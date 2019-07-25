package com.niezhiliang.luck.draw.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/24 14:27
 */
@AllArgsConstructor
@Getter
public enum  LuckCodeEnum {

    GRADE_ONE("99999","一等奖"),

    GRADE_TWO("88888","二等奖"),

    GRADE_THREE("77777","三等奖"),

    GRADE_FOUR("66666","四等奖"),

    GRADE_FIVE("55555","五等奖"),

    GRADE_LUCK("44444","幸运奖")

    ;
    private String code;

    private String grade;
}
