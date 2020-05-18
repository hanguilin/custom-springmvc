package com.hgl.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �������Զ�ע��
 * @author guilin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface CustomQualifer {

	String value();
}
