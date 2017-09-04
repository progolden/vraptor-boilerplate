package br.com.caelum.vraptor.boilerplate.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;

/**
 * @author Renato R. R. de Oliveira
 * 
 */
@Entity(name = Company.TABLE)
@Table(name = Company.TABLE)
public class Company extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "boilerplate_company";
	private static final long serialVersionUID = 1L;

	@Column(nullable=false, length=255)
	private String name;

	@Column(nullable=false, length=255)
	private String logo;

	@Column(nullable=true, length=10000)
	private String description;
	
	@Column(nullable=true, length=255)
	private String localization;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocalization() {
		return localization;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}
	
	
	
}
