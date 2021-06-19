package com.ergys2000.RestService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import com.ergys2000.RestService.models.ChangePasswordRequest;
import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {
	@InjectMocks
	UserService userService;
	@Mock
	UserRepository userRepository;
	@Mock
	RequestRepository requestRepository;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void get_all_users() {
		ArrayList<User> users = new ArrayList<User>();

		User user = new User();
		user.setId(1);
		user.setType("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword("pass");
		user.setLastname("lastname");
		user.setFirstname("lastname");
		user.setSupervisor(null);

		users.add(user);
		users.add(user);
		users.add(user);
		users.add(user);

		when(userRepository.findAll()).thenReturn(users);

		Iterable<User> userList = userService.findAllUsers();

		int size = 0;
		for (User u : userList)
			size++;

		assertThat(4).isEqualTo(size);
	}

	@Test
	public void get_user_by_id() {
		User user = new User();
		user.setId(1);
		user.setType("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword("pass");
		user.setLastname("lastname");
		user.setFirstname("lastname");
		user.setSupervisor(null);

		Optional<User> optUser = Optional.of(user);

		when(userRepository.findById(1)).thenReturn(optUser);

		User usr = null;
		try {
			usr = userService.findUserById(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(usr).isEqualTo(user);
	}

	@Test
	public void change_user_password_throws_exception() {
		User user = new User();
		user.setId(1);
		user.setType("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword("pass");
		user.setLastname("lastname");
		user.setFirstname("lastname");
		user.setSupervisor(null);
		Optional<User> optUser = Optional.of(user);

		ChangePasswordRequest req = new ChangePasswordRequest();
		req.setOldPassword("pass");
		req.setNewPassword("passwor");
		req.setConfirmPassword("password");

		when(userRepository.findById(1)).thenReturn(optUser);
		when(userRepository.save(user)).thenReturn(user);

		assertThrows(Exception.class, () -> userService.changePassword(1, req));
	}

	@Test
	public void insert_request_throws_exception() {
		User user = new User();
		user.setId(1);
		user.setType("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword("pass");
		user.setLastname("lastname");
		user.setFirstname("lastname");
		user.setSupervisor(null);
		Optional<User> optUser = Optional.of(user);

		Request request = new Request();
		request.setId(1);
		request.setEndDate(LocalDate.now());
		request.setStartDate(LocalDate.now().plusDays(2));
		request.setCreatedOn(LocalDate.now());
		request.setUser(user);

		when(userRepository.findById(1)).thenReturn(optUser);
		when(userRepository.findUserSupervisor(1)).thenReturn(null);
		when(requestRepository.save(request)).thenReturn(request);

		/* Should return exception because start date is after end date */
		assertThrows(Exception.class, () -> userService.insertRequest(request, 1));
	}

	@Test
	public void insert_request_throws_exception_user_null() {
		Request request = new Request();
		request.setId(1);
		request.setEndDate(LocalDate.now());
		request.setStartDate(LocalDate.now().minusDays(2));
		request.setCreatedOn(LocalDate.now());
		request.setUser(null);

		when(userRepository.findById(1)).thenReturn(null);
		when(userRepository.findUserSupervisor(1)).thenReturn(null);
		when(requestRepository.save(request)).thenReturn(request);

		/* Should return exception because start date is after end date */
		assertThrows(Exception.class, () -> userService.insertRequest(request, 1));
	}

	@Test
	public void resolve_request_throws_exception() {
		when(requestRepository.findById(1)).thenReturn(Optional.ofNullable(null));
		assertThrows(Exception.class, () -> userService.resolveRequest(1, 1, false));
	}
}
