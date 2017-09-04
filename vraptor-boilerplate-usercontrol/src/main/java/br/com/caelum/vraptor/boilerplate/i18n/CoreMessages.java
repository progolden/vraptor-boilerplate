package br.com.caelum.vraptor.boilerplate.i18n;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Renato R. R. de Oliveira
 * 
 */
@ApplicationScoped
public class CoreMessages {

	/** Caminho do bundle. */
	public String BUNDLE_NAME = "messages";
	/** Locale padrão do sistema. */
	public Locale DEFAULT_LOCALE = new Locale("pt", "BR");
	
}
