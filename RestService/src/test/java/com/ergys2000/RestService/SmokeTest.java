package com.ergys2000.RestService;

import static org.assertj.core.api.Assertions.assertThat;

import com.ergys2000.RestService.controllers.AdminController;
import com.ergys2000.RestService.controllers.ResetTokenController;
import com.ergys2000.RestService.controllers.SupervisorController;
import com.ergys2000.RestService.controllers.UserController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {
	@Autowired
	private AdminController adminController;
	@Autowired
	private UserController userController;
	@Autowired
	private SupervisorController supervisorController;
	@Autowired
	private ResetTokenController resetTokenController;

	@Test
	public void contextLoads() throws Exception {
		assertThat(adminController).isNotNull();
		assertThat(userController).isNotNull();
		assertThat(supervisorController).isNotNull();
		assertThat(resetTokenController).isNotNull();
	}
}
