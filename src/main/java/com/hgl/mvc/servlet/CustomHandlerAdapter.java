package com.hgl.mvc.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hgl.mvc.resolver.ArgumentResolver;

/**
 * @author guilin
 * �Զ���������
 *
 */
public interface CustomHandlerAdapter {
	
	/**
	 * �Ƿ�֧�ִ���
	 * ������ñ���handle����
	 * 
	 * @param handler ������
	 * @return boolean
	 */
	public boolean support(Object handler);
	
	/**
	 * ���崦����
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @param argumentResolverMap
	 * @return CustomModelAndView
	 */
	public CustomModelAndView handle(HttpServletRequest request, HttpServletResponse response, CustomHandlerMapping handler,
			Map<String, ArgumentResolver> argumentResolverMap);
	
}
