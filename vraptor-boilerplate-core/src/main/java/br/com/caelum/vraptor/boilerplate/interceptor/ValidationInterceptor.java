package br.com.caelum.vraptor.boilerplate.interceptor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.validator.DefaultValidator;

/**
 * Interceptor that redirects all validation errors to JSON response page.
 * @author Renato R. R. de Oliveira
 *
 */
@RequestScoped
public class ValidationInterceptor {

	private static final Logger LOG = Logger.getLogger(ValidationInterceptor.class);
	
	@Inject private DefaultValidator validator;
	
	public void intercept(@Observes br.com.caelum.vraptor.events.MethodReady event) {
		LOG.debug("Redirecting if there are validation errors.");
		this.validator.onErrorUse(GsonJSONSerialization.class).from(validator.getErrors(), "errors").recursive().serialize();
	}
	
}
