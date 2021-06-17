package com.ergys2000.RestService.services;

import java.time.LocalDate;
import java.util.Optional;

import com.ergys2000.RestService.models.ChangePasswordRequest;
import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public User findUserById(Integer userId) throws Exception {
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			throw new Exception("User id does not exist!");
		return user.get();
	}

	public User insertUser(User user) throws Exception {
		/* Check if the types are acceptable */
		if (!user.getType().equals("user") && !user.getType().equals("admin") && !user.getType().equals("supervisor"))
			throw new Exception("Sorry, user type not supported!");

		/* Check if user of type normal user has a supervisor */
		if (user.getType().equals("user") && user.getSupervisor() == null) {
			throw new Exception("Every user must have a supervisor!");
		}

		/* Check if user of type supervisor or admin has a supervisor */
		if (user.getType().equals("admin") || user.getType().equals("supervisor")) {
			if (user.getSupervisor() != null)
				throw new Exception("Admin or supervisor cannot have a supervisor!");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	public User updateUser(User newUser) throws Exception {
		Optional<User> user = userRepository.findById(newUser.getId());
		/* Check if the user exists */
		if (user.isEmpty())
			throw new Exception("User does not exist!");

		/* Check if the types are acceptable */
		if (!newUser.getType().equals("user") && !newUser.getType().equals("admin")
				&& !newUser.getType().equals("supervisor"))
			throw new Exception("Sorry, user type not supported!");

		/* Check if user of type normal user has a supervisor */
		if (newUser.getType().equals("user") && newUser.getSupervisor() == null) {
			throw new Exception("Every user must have a supervisor!");
		}

		/* Check if user of type supervisor or admin has a supervisor */
		if (newUser.getType().equals("admin") || newUser.getType().equals("supervisor")) {
			if (newUser.getSupervisor() != null)
				throw new Exception("Admin or supervisor cannot have a supervisor!");
		}

		user.get().setType(newUser.getType());
		user.get().setFirstname(newUser.getFirstname());
		user.get().setLastname(newUser.getLastname());
		user.get().setEmail(newUser.getEmail());
		user.get().setStartDate(newUser.getStartDate());
		user.get().setSupervisor(newUser.getSupervisor());

		return userRepository.save(user.get());
	}

	public void deleteUser(User user) throws Exception {
		if (user.getType().equals("supervisor")) {
			Iterable<User> supervisedUsers = userRepository.findBySupervisorId(user.getId());
			int i = 0;
			for (User u : supervisedUsers)
				i++;
			if (i > 0)
				throw new Exception("Please remove the supervised users before removing the supervisor!");
		}

		userRepository.delete(user);
	}

	public void deleteUserById(Integer userId) throws Exception {
		User user = findUserById(userId);
		deleteUser(user);
	}

	public Iterable<User> findAllUsers() {
		return userRepository.findAll();
	}

	public Iterable<User> findUsersBySupervisorId(Integer supervisorId) {
		return userRepository.findBySupervisorId(supervisorId);
	}

	public void changePassword(Integer userId, ChangePasswordRequest chPassReq) throws Exception {
		User user = findUserById(userId);
		if (!chPassReq.getNewPassword().equals(chPassReq.getConfirmPassword()))
			throw new Exception("Sorry, new and confirm password do not match!");
		if (!passwordEncoder.matches(chPassReq.getOldPassword(), user.getPassword()))
			throw new Exception("Wrong old password!");

		user.setPassword(passwordEncoder.encode(chPassReq.getNewPassword()));
		userRepository.save(user);
	}

	public Request insertRequest(Request request, Integer userId) throws Exception {
		/* Set default values for each field */
		request.setApproved(null);
		request.setCreatedOn(LocalDate.now());

		Optional<User> user = userRepository.findById(userId);
		/* Perform sanity chekcs */
		if (user.isEmpty()) {
			throw new Exception("Your user could not be found!");
		}
		if (LocalDate.now().isBefore(user.get().getStartDate().plusDays(90))) {
			throw new Exception("Sorry you are still on probation!");
		}
		if (request.getStartDate().isAfter(request.getEndDate()))
			throw new Exception("Sorry, start date cannot be after end date!");

		/* Send an email to the supervisor */
		Optional<User> supervisor = userRepository.findUserSupervisor(userId);
		sentEmailToSupervisor(user, supervisor);

		return requestRepository.save(request);
	}

	public Request resolveRequest(Integer requestId, Integer userId, Boolean approved) throws Exception {
		Optional<Request> request = requestRepository.findById(requestId);
		/* Perform sanity chekcs */
		if (request.isEmpty()) {
			throw new Exception("Your request could not be found!");
		}
		if (request.get().getUser().getSupervisor().getId() != userId)
			throw new Exception("You do not have permission to modify this request!");

		request.get().setApproved(approved);

		/* Send an email to the supervisor */
		sendEmailToUser(approved, request);

		return requestRepository.save(request.get());
	}

	public void deleteRequest(Integer requestId) throws Exception {
		Optional<Request> request = requestRepository.findById(requestId);
		if (request.isEmpty())
			throw new Exception("Request not found!");
		requestRepository.delete(request.get());
	}

	public Iterable<Request> findRequestsByUserId(Integer userId) {
		return requestRepository.findByUserId(userId);
	}

	public Iterable<Request> findAllRequests() {
		return requestRepository.findAll();
	}

	private void sentEmailToSupervisor(Optional<User> user, Optional<User> supervisor) {
		String supervisorEmail = supervisor.get().getEmail();
		String subject = "Leave Request Notice";
		String text = user.get().getFirstname() + " " + user.get().getLastname() + " just made a leave request!";

		emailService.sendSimpleMessage(supervisorEmail, subject, text);
	}

	private void sendEmailToUser(Boolean approved, Optional<Request> request) {
		User user = request.get().getUser();
		String userEmail = user.getEmail();
		String subject = "Leave Request Update";
		String text = null;
		if (approved) {
			text = "One of your requests just got approved! Log into the system to check it.";
		} else {
			text = "One of your requests got rejected! Log into the system to check it.";
		}
		emailService.sendSimpleMessage(userEmail, subject, text);
	}
}
