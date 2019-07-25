package com.niezhiliang.luck.draw.exception;

import com.alibaba.fastjson.JSONObject;
import com.niezhiliang.luck.draw.constant.ResultCode;
import com.niezhiliang.luck.draw.utils.ResultVO;
import lombok.Getter;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/24 13:04
 */
public class LuckDrawException extends RuntimeException {

    @Getter
    private String response;

    public LuckDrawException() {
    }


    public LuckDrawException(String message, Throwable cause) {
        super(message, cause);
    }

    public LuckDrawException(ResultCode resultCode) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultCode.getCode());
        resultVO.setMsg(resultCode.getMsg());
        this.response = JSONObject.toJSONString(resultVO);
    }
}
