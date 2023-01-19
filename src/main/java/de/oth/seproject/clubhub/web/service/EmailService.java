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

    /**
     * Sends an email with a subject and a text to a recipient
     * 
     * @param recipient Email of the recipient
     * @param text Text of the email
     * @param subject Subject of the email
     */
	public void sendEmail(String recipient, String text, String subject) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom(sender);
		mailMessage.setTo(recipient);
		mailMessage.setText(text);
		mailMessage.setSubject(subject);
		
		mailSender.send(mailMessage);
	}
}