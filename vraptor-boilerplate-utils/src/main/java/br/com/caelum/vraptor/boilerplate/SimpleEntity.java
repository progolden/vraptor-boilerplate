package br.com.caelum.vraptor.boilerplate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Simple identifiable entity. This enables some default methods that needs an ID.
 * 
 * @author Renato R. R. de Oliveira
 *
 */
@MappedSuperclass
public abstract class SimpleEntity implements SimpleIdentifiable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
