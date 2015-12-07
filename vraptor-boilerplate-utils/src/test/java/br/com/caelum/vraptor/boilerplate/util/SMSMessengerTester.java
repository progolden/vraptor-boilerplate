package br.com.caelum.vraptor.boilerplate.util;

import org.junit.Test;

public class SMSMessengerTester {
	
	@Test
	public void sendSMSMessage() {
		String number = "(99) 99999-9999";
		String message = "Mensagem de teste.";
		SMSMessenger.sendSMS(number, message);
	}
}
