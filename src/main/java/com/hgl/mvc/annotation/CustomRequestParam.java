package com.hgl.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ����ע��
 * @author guilin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.PARAMETER)
public @interface CustomRequestParam {

	String value() default "";
}
