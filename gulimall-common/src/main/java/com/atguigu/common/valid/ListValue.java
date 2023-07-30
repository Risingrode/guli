package com.atguigu.common.valid;

// 自定义校验注解
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author 烟雨蒙蒙
 */
@Documented
@Constraint(validatedBy = { ListValueConstraintValidator.class })
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
// 为什么interface前面要加@? 因为interface是一个类，而@ListValue是一个注解
public @interface ListValue {
    // 这个是从配置文件中读取的
    String message() default "{com.atguigu.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    int [] values() default {};

}
