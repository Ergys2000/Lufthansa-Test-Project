package com.ergys2000.RestService;

import java.time.LocalDate;
import java.util.Optional;

import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MyApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Optional<User> user = userRepository.findByEmail("rrjolligys@gmail.com");
		if (user.isEmpty()) {
			User u = new User();
			u.setId(1);
			u.setEmail("rrjolligys@gmail.com");
			u.setPassword(passwordEncoder.encode("pass"));
			u.setFirstname("Ergys");
			u.setLastname("Rrjolli");
			u.setStartDate(LocalDate.now());
			u.setType("admin");
			u.setSupervisor(null);
			userRepository.save(u);
			System.out.println("New user just created!");
		}
		//emailService.sendSimpleMessage("errjolli18@epoka.edu.al", "Sample email", "Sample text");
		//System.out.println("========================================");
		//System.out.println("The email just got sent!");
		//System.out.println("========================================");
	}

}
