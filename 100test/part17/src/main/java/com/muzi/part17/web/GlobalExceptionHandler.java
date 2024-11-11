package com.muzi.part17.web;



import com.muzi.part17.common.BusinessException;
import com.muzi.part17.common.ErrorCode;
import com.muzi.part17.common.Result;
import com.muzi.part17.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理SpringBoot参数校验异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(java.net.BindException.class)
    public Result handleBindException(BindException e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResultUtils.error(message);
    }

    /**
     * 处理其他异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        //会返回code为500的一个异常
        return ResultUtils.error(ErrorCode.SERVER_ERROR, "系统异常，请稍后重试");
    }


}
