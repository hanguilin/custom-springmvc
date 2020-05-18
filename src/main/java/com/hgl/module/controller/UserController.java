package com.hgl.module.controller;

import com.hgl.module.service.UserService;
import com.hgl.mvc.annotation.CustomController;
import com.hgl.mvc.annotation.CustomQualifer;
import com.hgl.mvc.annotation.CustomRequestMapping;
import com.hgl.mvc.annotation.CustomRequestParam;

@CustomController
public class UserController {
	
	@CustomQualifer("userService")
	private UserService userService;
	
	@CustomRequestMapping("/count")
	public void doCount(@CustomRequestParam("name") String name, @CustomRequestParam("age") String age) {
		int userCount = userService.getUserCount();
		System.out.println("userCount: "+ userCount);
		System.out.println("name: " + name);
		System.out.println("age: " + age);
	}
}
