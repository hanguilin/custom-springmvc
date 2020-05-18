package com.hgl.mvc.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hgl.mvc.annotation.CustomRequestParam;
import com.hgl.mvc.annotation.CustomService;

/**
 * RequestParam²ÎÊý½âÎöÆ÷
 * @author guilin
 *
 */
@CustomService("requestParamArgumentResolver")
public class RequestParamArgumentResolver implements ArgumentResolver {

	@Override
	public boolean support(Class<?> type, int paramIndex, Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Annotation[] currentField = annotations[paramIndex];
		for (Annotation annotation : currentField) {
			if(CustomRequestParam.class.isAssignableFrom(annotation.getClass())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type,
			int paramIndex, Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Annotation[] currentField = annotations[paramIndex];
		for (Annotation annotation : currentField) {
			if(CustomRequestParam.class.isAssignableFrom(annotation.getClass())) {
				CustomRequestParam requestParam = (CustomRequestParam) annotation;
				String parameterName = requestParam.value();
				String parameterVal = request.getParameter(parameterName);
				return parameterVal;
			}
		}
		return null;
	}

}
