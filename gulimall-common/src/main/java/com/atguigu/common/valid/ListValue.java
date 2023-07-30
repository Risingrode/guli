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
// 这个类的作用是：自定义校验注解
@Documented// 作用是: 生成文档 什么文档? 就是生成javadoc
// 实体类使用@ListValue注解时，可能会传过来value数组,这个value数组中的值必须是values数组中的值
@Constraint(validatedBy = { ListValueConstraintValidator.class })// 这个是校验器,检测传过来的value值在不在实体类中定义的
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
// 为什么interface前面要加@? 因为interface是一个类，而@ListValue是一个注解
public @interface ListValue {
    // 这个是从配置文件(properties)中读取的
    String message() default "{com.atguigu.common.valid.ListValue.message}";
    // 作用是:指定校验的分组
    Class<?>[] groups() default {};
    // 作用时: 指定校验的级别 TODO　：　不会
    Class<? extends Payload>[] payload() default {};
    // 作用是: 指定多个有效值
    // 如果传过来2个值,内部是如何运行的? 通过ListValueConstraintValidator中的initialize方法
    int [] values() default {};

}
