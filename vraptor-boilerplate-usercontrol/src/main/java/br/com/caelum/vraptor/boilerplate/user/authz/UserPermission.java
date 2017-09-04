package br.com.caelum.vraptor.boilerplate.user.authz;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.caelum.vraptor.boilerplate.company.Company;
import br.com.caelum.vraptor.boilerplate.user.User;

/**
 * @author Renato R. R. de Oliveira
 * 
 */
@Entity(name = UserPermission.TABLE)
@Table(name = UserPermission.TABLE)
public class UserPermission implements Serializable {
	public static final String TABLE = "boilerplate_user_permission";
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne(targetEntity=Company.class, fetch=FetchType.EAGER, optional=false)
	private Company company;

	@Id
	@ManyToOne(targetEntity=User.class, fetch=FetchType.EAGER, optional=false)
	private User user;
	
	@Id
	@Column(nullable=false, length=255)
	private String permission;
	
	private boolean revoked = false;
	
	public UserPermission(){
		this.revoked = false;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

}
