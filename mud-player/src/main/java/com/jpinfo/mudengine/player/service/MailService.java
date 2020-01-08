package com.jpinfo.mudengine.player.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.exception.GeneralException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Component
public class MailService {
	
	private static final String ACTIVATION_PREFIX = "activation-%s.txt";
	
	private static final String NON_SET_FROM= "disabled";
	
	private static final String USERNAME_TAG= "<username>";
	private static final String ACODE_TAG= "<activationCode>";
	
	@Value("${SMTP_FROM:disabled}")
	private String sender;
	
	@Autowired(required = false)
	private JavaMailSender mail;
	
	public boolean isEnabled() {
		return !sender.equals(NON_SET_FROM) && (mail!=null);
	}

	public void sendActivationEmail(Player playerData, String activationCode) {
		
			String fileName = String.format(ACTIVATION_PREFIX, 
					Locale.forLanguageTag(playerData.getLocale()).toString());
			
			internalSendMail(fileName, activationCode, playerData);
	}
	

	private void internalSendMail(String fileName, String activationCode, Player playerData) {
	
		// Prepare to read
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new ClassPathResource(fileName).getInputStream()))) {
			
			StringBuilder mailBody = new StringBuilder();
			
			while (reader.ready()) {
				
				mailBody.append(reader.readLine()
						.replace(USERNAME_TAG, playerData.getUsername())
						.replace(ACODE_TAG, activationCode)
						).append("<br/>");				
			}
			
			MimeMessageHelper helper = new MimeMessageHelper(mail.createMimeMessage());
			
			helper.setFrom(sender);
			helper.setSubject(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_ACTIVATION_SUBJECT));
			helper.setTo(playerData.getEmail());
			helper.setText(mailBody.toString(), true);
			
			// Sending
			mail.send(helper.getMimeMessage());
			
		} catch(Exception e) {
			throw new GeneralException(LocalizedMessages.API_ERROR_MESSAGE);
		}
	}
}
