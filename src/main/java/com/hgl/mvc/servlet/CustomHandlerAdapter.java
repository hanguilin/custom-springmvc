package com.hgl.mvc.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hgl.mvc.resolver.ArgumentResolver;

/**
 * @author guilin
 * 自定义适配器
 *
 */
public interface CustomHandlerAdapter {
	
	/**
	 * 是否支持处理
	 * 是则调用本类handle方法
	 * 
	 * @param handler 处理器
	 * @return boolean
	 */
	public boolean support(Object handler);
	
	/**
	 * 具体处理方法
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
