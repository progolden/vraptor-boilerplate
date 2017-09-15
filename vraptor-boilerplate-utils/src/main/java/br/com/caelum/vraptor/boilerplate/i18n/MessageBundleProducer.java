package br.com.caelum.vraptor.boilerplate.i18n;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Basic implementation of MessageBundle injection. This producer
 * can implement more complex logic to determine the bundle name and locale.
 * @author Renato R. R. de Oliveira
 *
 */
@RequestScoped
public class MessageBundleProducer {
	
	@Inject private MessageBundleConfig config;
	private MessageBundle bundle;
	
	@Produces
	public MessageBundle getInstance() {
		if (this.bundle == null) {
			this.bundle = new MessageBundle(this.config);
		}
		return this.bundle;
	}

}
