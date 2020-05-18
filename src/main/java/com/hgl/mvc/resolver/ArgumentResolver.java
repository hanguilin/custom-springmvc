package com.hgl.mvc.resolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ²ÎÊı½âÎöÆ÷
 * @author guilin
 *
 */
public interface ArgumentResolver {
	
	public boolean support(Class<?> type, int paramIndex, Method method);
	
	public Object argumentResolver(HttpServletRequest request, HttpServletResponse response,
			Class<?> type, int paramIndex, Method method);
}
