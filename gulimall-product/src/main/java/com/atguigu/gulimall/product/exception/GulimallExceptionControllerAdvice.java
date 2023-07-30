package com.atguigu.gulimall.product.exception;

import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

// 集中处理所有异常
@Slf4j
//@ResponseBody // 把返回的数据转化成json格式
//@ControllerAdvice(basePackages = "com.atguigu.gulimall.prduct.controller")
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.prduct.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型，{}",e.getMessage(),e.getClass());
        BindingResult bindingResult=e.getBindingResult();

        Map<String,String> errorMap=new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(400,"数据校验出现问题").put("data",errorMap);
    }

    // 任意异常
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){

        return R.error();
    }

}
