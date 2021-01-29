package com.ddg.meituan.common.controlleradvice;


import com.ddg.meituan.common.exception.MeituanCodeEnum;
import com.ddg.meituan.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @Author: DDG
 * @Date: 2020/5/10 12:11
 * @Description:
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public R validExceptionHandle(Exception e){
        HashMap<String, String> exceptionMap = new HashMap<>();
        if (e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = exception.getBindingResult();
            bindingResult.getFieldErrors()
                     .forEach((error) ->{
                         String field = error.getField();
                         String defaultMessage = error.getDefaultMessage();
                         exceptionMap.put(field, defaultMessage);
                     });
            return R.error(MeituanCodeEnum.VALID_EXCEPTION.getCode(), MeituanCodeEnum.VALID_EXCEPTION.getMessage()).put("data",
                    exceptionMap);
        }
        log.error("出现了异常:{} , 出现的原因是{}", e.getClass().getSimpleName(), e.getMessage());
        return R.error(MeituanCodeEnum.UN_NONE_EXCEPTION.getCode(), MeituanCodeEnum.UN_NONE_EXCEPTION.getMessage()).put("data", e.getMessage());

    }

}
