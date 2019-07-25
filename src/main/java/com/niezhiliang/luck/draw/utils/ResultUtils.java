package com.niezhiliang.luck.draw.utils;

import com.niezhiliang.luck.draw.constant.ResultCode;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/04 11:18
 */
public class ResultUtils {
    /**
     * 成功返回
     * @return
     */
    public static ResultVO success() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setMsg(ResultCode.SUCCESS.getMsg());
        return resultVO;
    }
    /**
     * 成功返回
     * @param obj
     * @return
     */
    public static ResultVO success(Object obj) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setMsg(ResultCode.SUCCESS.getMsg());
        resultVO.setData(obj);
        return resultVO;
    }


    /**
     * 成功返回
     * @param obj
     * @return
     */
    public static ResultVO success(Object obj,String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setMsg(msg);
        resultVO.setData(obj);
        return resultVO;
    }

}
