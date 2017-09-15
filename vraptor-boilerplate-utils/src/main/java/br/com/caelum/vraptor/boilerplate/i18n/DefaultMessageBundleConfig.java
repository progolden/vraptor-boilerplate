package br.com.caelum.vraptor.boilerplate.i18n;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;

/**
 * Default configurations for MessageBundle internationalized messages.
 * @author Renato R. R. de Oliveira
 * 
 */
@ApplicationScoped
public class DefaultMessageBundleConfig implements MessageBundleConfig {

	/** Caminho do bundle. */
	private String BUNDLE_NAME = "messages";
	/** Locale padrão do sistema. */
	private Locale DEFAULT_LOCALE = new Locale("pt", "BR");
	
	@Override
	public Locale getDefaultLocale() {
		return DEFAULT_LOCALE;
	}
	
	@Override
	public String getBundleResourceName() {
		return BUNDLE_NAME;
	}
	
}
