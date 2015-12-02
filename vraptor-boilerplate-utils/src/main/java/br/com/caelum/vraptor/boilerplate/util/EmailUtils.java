package br.com.caelum.vraptor.boilerplate.util;

import java.util.regex.Pattern;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.logging.Logger;

/**
 * 
 * @author Renato R. R. de Oliveira
 *
 */
public final class EmailUtils {
	private static final Logger LOG = Logger.getLogger(EmailUtils.class);

	private static String SMTP_HOST = "localhost";
	private static Integer SMTP_PORT = 25;
	private static String SMTP_USER = null;
	private static String SMTP_PASSWORD = null;
	private static Boolean SMTP_SSL = false;
	
	
	public static final Pattern ENDL_REGEX = Pattern.compile("\\v");
	public static final Pattern DUPE_WS_REGEX = Pattern.compile("\\h+");
	
	public static String sendEmail(
			String fromMail,
			String fromName,
			String to,
			String subject,
			String body,
			boolean html) throws EmailException {
		Email email;
		if (html) {
			email = EmailUtils.getHtmlEmail();
		} else {
			email = EmailUtils.getSimpleEmail();
		}
		String msgId = null;
		
		email.setFrom(fromMail, fromName);
		email.addTo(to);
		email.setSubject(subject);
		email.setMsg(body);

		msgId = email.send();
		LOG.infof("Sent e-mail with ID: %s", msgId);
		
		return msgId;
	}

	public static SimpleEmail getSimpleEmail() {
		SimpleEmail email = new SimpleEmail();
		EmailUtils.configureConnection(email);
		return email;
	}

	public static HtmlEmail getHtmlEmail() {
		HtmlEmail email = new HtmlEmail();
		EmailUtils.configureConnection(email);
		return email;
	}

	private static void configureConnection(Email email) {
		try {
			email.setSmtpPort(SMTP_PORT);
			email.setHostName(SMTP_HOST);
			email.setCharset("UTF-8");
			if (!GeneralUtils.isEmpty(SMTP_USER)) {
				email.setAuthentication(
					SMTP_USER,
					SMTP_PASSWORD
				);
			}
			email.setSSLOnConnect(SMTP_SSL);
		} catch (Throwable ex) {
			LOG.error("Erro ao configurar o email.", ex);
			throw new RuntimeException("Error configuring smtp connection.", ex);
		}
	}
	
	public static String clearHtmlPitfalls(String pre) {
		return 
			DUPE_WS_REGEX.matcher(
				ENDL_REGEX.matcher(pre).replaceAll("")
			).replaceAll(" ")
		;
	}
}
