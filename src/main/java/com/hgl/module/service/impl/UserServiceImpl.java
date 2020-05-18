package com.hgl.module.service.impl;

import com.hgl.module.service.UserService;
import com.hgl.mvc.annotation.CustomService;

@CustomService("userService")
public class UserServiceImpl implements UserService{

	public int getUserCount() {
		return 1000;
	}

}
