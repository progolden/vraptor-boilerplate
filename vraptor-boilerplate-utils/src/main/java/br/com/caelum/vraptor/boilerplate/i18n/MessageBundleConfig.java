package br.com.caelum.vraptor.boilerplate.i18n;

import java.util.Locale;

/**
 * Configurations for internationalized MessageBundle.
 * @author Renato R. R. de Oliveira
 *
 */
public interface MessageBundleConfig {

	public Locale getDefaultLocale();
	public String getBundleResourceName();
	
}
