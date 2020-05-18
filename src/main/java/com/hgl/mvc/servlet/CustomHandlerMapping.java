package com.hgl.mvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * �Զ���handlerMapping
 * 
 * @author guilin
 *
 */
public class CustomHandlerMapping{
	
	/**
	 * controller��Ӧ��bean
	 */
	private Object controller;
	
	/**
	 * ���崦����
	 */
	private Method method;
	
	/**
	 * ������֤�Ƿ��ǵ�ǰurl��Ӧ�Ĵ�����
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