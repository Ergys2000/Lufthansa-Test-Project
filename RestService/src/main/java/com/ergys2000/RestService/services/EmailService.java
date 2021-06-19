package com.ergys2000.RestService.services;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/**
 * Defines the service which handles sending email requests in a seperate thread
 */
@Service
public class EmailService {
	@Autowired
	private JavaMailSender emailSender;

	private static final String FROM = "noreply@lufthansa.com";

	public void sendSimpleMessage(String to, String subject, String text) {
		Thread thread = new Thread(() -> {

			MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
				mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(to));
				mimeMessage.setFrom(new InternetAddress(FROM));
				mimeMessage.setSubject(subject);
				mimeMessage.setText(text);
			};
			emailSender.send(preparator);
		});
		thread.start();
	}
}
