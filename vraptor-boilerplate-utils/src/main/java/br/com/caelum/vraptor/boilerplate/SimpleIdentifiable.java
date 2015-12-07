package br.com.caelum.vraptor.boilerplate;

import java.io.Serializable;

/**
 * Simple identifiable interface. This enables some default methods that needs an ID.
 * 
 * @author Renato R. R. de Oliveira
 *
 */
public interface SimpleIdentifiable extends Serializable {

	public Long getId();
	public void setId(Long id);
	
}
