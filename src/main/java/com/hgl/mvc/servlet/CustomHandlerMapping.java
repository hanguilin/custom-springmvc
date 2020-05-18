package com.hgl.mvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 自定义handlerMapping
 * 
 * @author guilin
 *
 */
public class CustomHandlerMapping{
	
	/**
	 * controller对应的bean
	 */
	private Object controller;
	
	/**
	 * 具体处理方法
	 */
	private Method method;
	
	/**
	 * 用来验证是否是当前url对应的处理方法
	 */
	private Pattern pattern;
	
	public CustomHandlerMapping(Object controller, Method method, Pattern pattern) {
		super();
		this.controller = controller;
		this.method = method;
		this.pattern = pattern;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}