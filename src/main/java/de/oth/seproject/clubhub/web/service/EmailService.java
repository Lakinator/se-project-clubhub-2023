package de.oth.seproject.clubhub.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}") private String sender;

	public void sendEmail(String recipient, String text, String subject) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom(sender);
		mailMessage.setTo(recipient);
		mailMessage.setText(text);
		mailMessage.setSubject(subject);
		
		mailSender.send(mailMessage);
	}
}