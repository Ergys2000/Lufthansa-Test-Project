package com.ergys2000.RestService.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.Random;
import java.lang.Math;

import com.ergys2000.RestService.models.ChangePasswordRequest;
import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.ResetPasswordRequest;
import com.ergys2000.RestService.models.ResetToken;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.ResetTokenRepository;
import com.ergys2000.RestService.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Defines the service which handles the business logic of user operations. */
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

	@Autowired
	private ResetTokenRepository resetTokenRepository;

	/**
	 * Finds a user by it's id
	 * 
	 * @param userId the id of the user
	 * @throws Exception when the user if not found
	 * @returns the found user
	 */
	public User findUserById(Integer userId) throws Exception {
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			throw new Exception("User id does not exist!");
		return user.get();
	}

	/**
	 * Finds a user by it's email
	 * 
	 * @param email the email
	 * @throws Exception when the user if not found
	 * @returns the found user
	 */
	public User findUserByEmail(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isEmpty())
			throw new UsernameNotFoundException("User id does not exist!");
		return user.get();
	}

	/**
	 * inserts a user in the database
	 * 
	 * @param use the user object
	 * @throws Exception when the user is not valid
	 * @returns the inserted user
	 */
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

	/**
	 * updates a user in the database
	 * 
	 * @param newUser the user object
	 * @throws Exception when the user is not valid
	 * @returns the updated user
	 */
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

	/**
	 * deletes a user from the database
	 * 
	 * @param user The user to be deleted
	 * @throws Exception when the user is not found
	 */
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

	/**
	 * deletes a user by it's id
	 * 
	 * @param userId the id of the user
	 * @throws Exception when the user is not valid
	 */
	public void deleteUserById(Integer userId) throws Exception {
		User user = findUserById(userId);
		deleteUser(user);
	}

	/**
	 * finds all the users
	 * 
	 * @returns the list of users
	 */
	public Iterable<User> findAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * finds all the users of a supervisor
	 * 
	 * @param supervisorId the id of the supervisor
	 * @returns the list of users
	 */
	public Iterable<User> findUsersBySupervisorId(Integer supervisorId) {
		return userRepository.findBySupervisorId(supervisorId);
	}

	/**
	 * changes the password of a user
	 * 
	 * @param userId    the id of the user
	 * @param chPassReq the change password request object
	 * @throws Exception when the userid or password request is not valid
	 */
	public void changePassword(Integer userId, ChangePasswordRequest chPassReq) throws Exception {
		User user = findUserById(userId);
		if (!chPassReq.getNewPassword().equals(chPassReq.getConfirmPassword()))
			throw new Exception("Sorry, new and confirm password do not match!");
		if (!passwordEncoder.matches(chPassReq.getOldPassword(), user.getPassword()))
			throw new Exception("Wrong old password!");

		user.setPassword(passwordEncoder.encode(chPassReq.getNewPassword()));
		userRepository.save(user);
	}

	/**
	 * inserts a request the database
	 * 
	 * @param requestId the id of the request
	 * @param request   the request object
	 * @throws Exception when the request is not valid
	 * @returns the inserted request
	 */
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
			Period period = Period.between(LocalDate.now(), user.get().getStartDate());
			throw new Exception(
					String.format("Sorry you are still on probation! %d days left.", Math.abs(period.getDays())));
		}
		if (request.getStartDate().isAfter(request.getEndDate()))
			throw new Exception("Sorry, start date cannot be after end date!");

		/* Send an email to the supervisor */
		Optional<User> supervisor = userRepository.findUserSupervisor(userId);
		sendEmailToSupervisor(user, supervisor);

		request.setUser(user.get());

		return requestRepository.save(request);
	}

	/**
	 * changes the status of a request
	 * 
	 * @param requestId the id of the request
	 * @param userId    the id of the user
	 * @param approved  the new status
	 * @throws Exception when the information is not valid
	 * @returns the inserted user
	 */
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

	/**
	 * deletes a request
	 * 
	 * @param requestId the id of the request
	 * @throws Exception when the information is not valid
	 */
	public void deleteRequest(Integer requestId) throws Exception {
		Optional<Request> request = requestRepository.findById(requestId);
		if (request.isEmpty())
			throw new Exception("Request not found!");
		requestRepository.delete(request.get());
	}

	/**
	 * finds all the requests of a user
	 * 
	 * @param userId the id of the user
	 * @returns the request list
	 */
	public Iterable<Request> findRequestsByUserId(Integer userId) {
		return requestRepository.findByUserId(userId);
	}

	/**
	 * find all requests
	 * 
	 * @returns the request list
	 */
	public Iterable<Request> findAllRequests() {
		return requestRepository.findAll();
	}

	/**
	 * Creates a token for a user email and sends it to the email
	 * @throws Exception
	 * @param email
	 */
	public void createTokenForUser(String email) throws Exception {
		User user = findUserByEmail(email);

		/* Delete the previously created tokens for this user */
		resetTokenRepository.deleteByUserId(user.getId());

		/* Create and insert the new token */
		Integer token = (Integer) new Random().nextInt(100000);
		ResetToken resToken = new ResetToken();
		resToken.setToken(token);
		resToken.setUser(user);
		resetTokenRepository.save(resToken);

		emailService.sendSimpleMessage(email, "Reset password", "Your reset code is: " + token);
	}

	/**
	 * verifies a token
	 * @param token
	 * @param email
	 * @throws Exception
	 */
	public void verifyToken(String email, Integer token) throws Exception {
		Optional<ResetToken> resToken = resetTokenRepository.findByEmail(email);
		if (resToken.isEmpty())
			throw new Exception("Sorry no user found with that email!");
		if (!resToken.get().getToken().equals(token)) {
			User user = findUserByEmail(email);
			/* Delete the previously created tokens for this user */
			resetTokenRepository.deleteByUserId(user.getId());
			throw new Exception(
					"Sorry, wrong token! You need to create a new one, since the previous one was deleted for security reasons.");
		}
	}

	/**
	 * resets the password of a user
	 * @param token
	 * @param email
	 * @param resetPasswordRequest
	 * @throws Exception
	 */
	public void resetPassword(String email, Integer token, ResetPasswordRequest resetPasswordRequest) throws Exception {
		verifyToken(email, token);
		User user = findUserByEmail(email);

		user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
		userRepository.save(user);

		/* Remove the token now that it has been used */
		resetTokenRepository.deleteByUserId(user.getId());
	}

	/**
	 * sends an email to the supervisor
	 * 
	 * @param user       the optional user
	 * @param supervisor the optional supervisor
	 */
	private void sendEmailToSupervisor(Optional<User> user, Optional<User> supervisor) {
		String supervisorEmail = supervisor.get().getEmail();
		String subject = "Leave Request Notice";
		String text = user.get().getFirstname() + " " + user.get().getLastname() + " just made a leave request!";

		emailService.sendSimpleMessage(supervisorEmail, subject, text);
	}

	/**
	 * sends an email to the user
	 * 
	 * @param approved the status of the request
	 * @param request  the request object
	 */
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
