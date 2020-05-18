package com.hgl.mvc.servlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hgl.mvc.annotation.CustomService;
import com.hgl.mvc.resolver.ArgumentResolver;

/**
 * 对处理适配器的实现
 * 
 * @author guilin
 *
 */
@CustomService("customSimpleHandlerAdapter")
public class CustomSimpleHandlerAdapter implements CustomHandlerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomDispatcherServlet.class);
	
	@Override
	public CustomModelAndView handle(HttpServletRequest request, HttpServletResponse response, CustomHandlerMapping handler,
			Map<String, ArgumentResolver> argumentResolverMap) {
		Method method = handler.getMethod();
		Object controller = handler.getController();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterClass = parameterTypes[i];
			for (Map.Entry<String, ArgumentResolver> entry : argumentResolverMap.entrySet()) {
				ArgumentResolver argumentResolver = entry.getValue();
				if(argumentResolver.support(parameterClass, i, method)) {
					Object resolver = argumentResolver.argumentResolver(request, response, parameterClass, i, method);
					args[i] = resolver;
					break;
				}
			}
		}
		try {
			method.invoke(controller, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return new CustomModelAndView();
	}

	@Override
	public boolean support(Object handler) {
		// 暂定实现为true
		return true;
	}
	
	
}
