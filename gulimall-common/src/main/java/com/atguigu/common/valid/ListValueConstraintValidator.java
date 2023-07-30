package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator <ListValue,Integer>{
    private Set<Integer> set=new HashSet<>();
    // 初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        // ConstraintValidator.super.initialize(constraintAnnotation);
        int [] vals = constraintAnnotation.values();
        for (int val : vals) {
            set.add(val);
        }
    }
    // 判断是否校验成功
    // ConstraintValidator<Integer, Integer>中的第一个Integer是注解的类型，第二个Integer是被校验的数据类型
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {

        return set.contains(value);
    }
}
