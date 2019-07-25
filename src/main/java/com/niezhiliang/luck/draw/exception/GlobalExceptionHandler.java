package com.niezhiliang.luck.draw.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/24 13:53
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 程序中途异常处理
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = LuckDrawException.class)
    @ResponseBody
    public String ProcessExceptionHandler(LuckDrawException e) throws Exception {
        return e.getResponse();
    }
}
