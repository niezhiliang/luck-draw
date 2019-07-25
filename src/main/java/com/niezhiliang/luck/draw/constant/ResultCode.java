package com.niezhiliang.luck.draw.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/04 11:19
 */
@AllArgsConstructor
@Getter
public enum ResultCode {

    /**
     * 失败状态
     */
    ERROR(0,"请求失败"),
    /**
     * 成功状态
     */
    SUCCESS(1,"请求成功"),

    LUCK_DRAW_AGAIN(10001,"该账号已经抽过奖")





    ;
    private Integer code;

    private String msg;
}
