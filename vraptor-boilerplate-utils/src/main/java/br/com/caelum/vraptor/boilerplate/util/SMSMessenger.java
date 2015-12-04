package br.com.caelum.vraptor.boilerplate.util;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.human.gateway.client.bean.Response;
import com.human.gateway.client.bean.SimpleMessage;
import com.human.gateway.client.exception.ClientHumanException;
import com.human.gateway.client.service.SimpleMessageService;

/**
 * Classe que implementa o envio de mensagens por SMS.
 * @author Renato R. R. de Oliveira
 */
public class SMSMessenger {
	
	private static final Logger LOGGER = Logger.getLogger(SMSMessenger.class);

	private static String USERNAME = null;
	private static String PASSWORD = null;
	
	public static void updateConfig(String username, String password) {
		USERNAME = username;
		PASSWORD = password;
	}
	
	public static void sendSMS(String cellPhoneNumber, String mensageText) {
		cellPhoneNumber = "55"+(cellPhoneNumber
			.replace("-", "")
			.replace("(", "")
			.replace(")", "")
			.replace(" ", "")
			.trim()
		);
		LOGGER.debug("Sending SMS to cell phone: "+cellPhoneNumber);
		
		SimpleMessageService client = new SimpleMessageService(
			USERNAME,
			PASSWORD
		);
		SimpleMessage msg = new SimpleMessage();
		msg.setTo(cellPhoneNumber);
		msg.setMessage(mensageText);
		msg.setSchedule(new Date());
		try {
			List<Response> responses = client.send(msg);
			LOGGER.debug("SMS Responses: "+String.valueOf(responses.size()));
			for (Response resp : responses) {
				String info = "Response data:\n";
				info += resp.getReturnCode() + ": " + resp.getReturnDescription();
				LOGGER.debug(info);
				if (!resp.getReturnCode().equals("000"))
					throw new RuntimeException(info);
			}
		} catch (ClientHumanException  ex) {
			LOGGER.error("Erro ao enviar SMS.", ex);
			throw new RuntimeException("Falha ao enviar SMS: "+ex.getMessage(), ex);
		}
	}
}
