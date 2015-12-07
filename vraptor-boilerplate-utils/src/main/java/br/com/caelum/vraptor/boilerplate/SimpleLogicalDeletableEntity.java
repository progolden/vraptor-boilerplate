package br.com.caelum.vraptor.boilerplate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Simple entity that has a Long ID and a logical deleted field.
 * This enables some default methods that needs an ID.
 * 
 * @author Renato R. R. de Oliveira
 *
 */
@MappedSuperclass
public abstract class SimpleLogicalDeletableEntity implements SimpleIdentifiable, LogicalDeletable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Column(nullable=false)
	protected boolean deleted = false;

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	
}
