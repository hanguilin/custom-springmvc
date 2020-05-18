package com.hgl.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hgl.mvc.annotation.CustomController;
import com.hgl.mvc.annotation.CustomQualifer;
import com.hgl.mvc.annotation.CustomRequestMapping;
import com.hgl.mvc.annotation.CustomService;
import com.hgl.mvc.resolver.ArgumentResolver;

public class CustomDispatcherServlet extends HttpServlet{

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomDispatcherServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	private Properties contextConfig = new Properties();

	// 所有扫描类
	private List<String> classes = new ArrayList<String>();

	// 存放bean的容器ioc
	private Map<String, Object> context = new HashMap<String, Object>();
	
	// 存放参数解析器
	private Map<String, ArgumentResolver> argumentResolverMap = new HashMap<String, ArgumentResolver>();
	
	// 根据请求url找到具体的处理器
	private List<CustomHandlerMapping> handlerMapping = new ArrayList<CustomHandlerMapping>();
	
	private List<CustomHandlerAdapter> handlerAdapter = new ArrayList<CustomHandlerAdapter>();


	public CustomDispatcherServlet() {
		LOGGER.info("CustomDispatcherServlet()...");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 加载配置文件
		initConfig(config.getInitParameter("spring-mvc"));
		// 扫描类
		initBaseScanPackage(contextConfig.getProperty("spring.scanner.base.package"));
		// 生成bean实例，注入ioc
		initContext();
		// 初始化参数解析器
		initArgumentResolver();
		// 为controller层中service对象注入实例
		initInstance();
		// 建立URI与处理器的映射
		initHandlerMapping();
		// 处理器适配器
		initHandlerAdapter();
	}
	
	private void initConfig(String initParameter) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(initParameter);
		try {
			contextConfig.load(in);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	private void initBaseScanPackage(String basePackage) {
		URL resource = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
		String packagePath = resource.getFile();
		File packageFile = new File(packagePath);
		String[] listFiles = packageFile.list();
		for (String filepPath : listFiles) {
			File file = new File(packagePath + filepPath);
			if(file.isDirectory()) {
				initBaseScanPackage(basePackage + "." + filepPath);
			}else {
				classes.add(basePackage + "." + file.getName());
			}
		}
	}
	
	private void initContext() {
		if(classes.isEmpty()) {
			LOGGER.error("do scan failed.");
			return;
		}
		for (String className : classes) {
			String classPath = className.substring(0, className.lastIndexOf(".class"));
			try {
				Class<?> clazz = Class.forName(classPath);
				String simpleName = clazz.getSimpleName();
				if(clazz.isAnnotationPresent(CustomController.class)) {
					CustomController controller = clazz.getAnnotation(CustomController.class);
					String key = controller.value();
					if(StringUtils.isBlank(key)) {
						key = toLowerCaseFirstOne(simpleName);
					}
					Object instance = clazz.newInstance();
					context.put(key, instance);
				} else if(clazz.isAnnotationPresent(CustomService.class)) {
					CustomService service = clazz.getAnnotation(CustomService.class);
					String key = service.value();
					if(StringUtils.isBlank(key)) {
						key = toLowerCaseFirstOne(simpleName);
					}
					Object instance = clazz.newInstance();
					context.put(key, instance);
				} else {
					continue;
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void initArgumentResolver() {
		if(context.isEmpty()) {
			return;
		}
		for(Map.Entry<String, Object> entry : context.entrySet()) {
			Object bean = entry.getValue();
			// 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
			if(ArgumentResolver.class.isAssignableFrom(bean.getClass())) {
				argumentResolverMap.put(entry.getKey(), (ArgumentResolver) bean);
			}
		}
	}
	
	private void initInstance() {
		if(context.isEmpty()) {
			LOGGER.error("no bean is instanced.");
			return;
		}
		for(Map.Entry<String, Object> entry : context.entrySet()) {
			Object bean = entry.getValue();
			Class<? extends Object> clazz = bean.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)) {
				Field[] declaredFields = clazz.getDeclaredFields();
				for (Field field : declaredFields) {
					if(field.isAnnotationPresent(CustomQualifer.class)) {
						CustomQualifer qualifer = field.getAnnotation(CustomQualifer.class);
						String beanName = qualifer.value();
						Object value = context.get(beanName);
						try {
							if(!field.isAccessible()) {
								field.setAccessible(true);
							}
							field.set(bean, value);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	private void initHandlerMapping() {
		if(context.isEmpty()) {
			LOGGER.error("no bean is instanced.");
			return;
		}
		for(Map.Entry<String, Object> entry : context.entrySet()) {
			Object bean = entry.getValue();
			Class<? extends Object> clazz = bean.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)) {
				String classRequestMappingVal = "";
				if(clazz.isAnnotationPresent(CustomRequestMapping.class)) {
					CustomRequestMapping classRequestMapping = clazz.getAnnotation(CustomRequestMapping.class);
					classRequestMappingVal = classRequestMapping.value();
				}
				Method[] declaredMethods = clazz.getDeclaredMethods();
				List<String> uris = new ArrayList<String>();
				for (Method method : declaredMethods) {
					String methodRequestMappingVal = "";
					if(method.isAnnotationPresent(CustomRequestMapping.class)) {
						CustomRequestMapping methodRequestMapping = method.getAnnotation(CustomRequestMapping.class);
						methodRequestMappingVal = classRequestMappingVal + methodRequestMapping.value();
					}
					if(StringUtils.isNotBlank(methodRequestMappingVal)) {
						if(uris.contains(methodRequestMappingVal)) {
							throw new RuntimeException("Duplicate mapping for " + methodRequestMappingVal);
						}
						handlerMapping.add(new CustomHandlerMapping(bean, method, Pattern.compile(methodRequestMappingVal)));
						uris.add(methodRequestMappingVal);
					}
				}
				uris = null;
			}
		}
	}

	private void initHandlerAdapter() {
		if(context.isEmpty()) {
			LOGGER.error("no bean is instanced.");
			return;
		}
		for(Map.Entry<String, Object> entry : context.entrySet()) {
			Object bean = entry.getValue();
			// 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
			if(CustomHandlerAdapter.class.isAssignableFrom(bean.getClass())) {
				handlerAdapter.add((CustomHandlerAdapter) bean);
			}
		}
	}

	private String toLowerCaseFirstOne(String s){
		if(Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CustomHandlerMapping handler = getHandler(request);
		CustomHandlerAdapter handlerAdapter = getHandlerAdapter(handler);
		CustomModelAndView modelAndView = handlerAdapter.handle(request, response, handler, argumentResolverMap);
	}
	
	private CustomHandlerAdapter getHandlerAdapter(CustomHandlerMapping handler) {
		for (CustomHandlerAdapter customHandlerAdapter : handlerAdapter) {
			if(customHandlerAdapter.support(handler)) {
				return customHandlerAdapter;
			}
		}
		throw new RuntimeException("There is no handlerAdapter for " + handler);
	}

	private CustomHandlerMapping getHandler(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String path = requestURI.replaceAll(request.getContextPath(), "");
		for (CustomHandlerMapping handler : handlerMapping) {
			Pattern pattern = handler.getPattern();
			Matcher matcher = pattern.matcher(path);
			if(matcher.matches()) {
				return handler;
			}
		}
		throw new RuntimeException("There is no mapping for " + path);
	}

}
