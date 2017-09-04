package br.com.caelum.vraptor.boilerplate.company;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.caelum.vraptor.boilerplate.user.User;
import br.com.caelum.vraptor.boilerplate.user.authz.AccessLevels;;

/**
 * @author Renato R. R. de Oliveira
 * 
 */
@Entity(name = CompanyUser.TABLE)
@Table(name = CompanyUser.TABLE)
public class CompanyUser implements Serializable {
	public static final String TABLE = "boilerplate_company_user";
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne(targetEntity=Company.class, fetch=FetchType.EAGER, optional=false)
	private Company company;

	@Id
	@ManyToOne(targetEntity=User.class, fetch=FetchType.EAGER, optional=false)
	private User user;
	
	private boolean blocked = false;
	private int accessLevel = AccessLevels.AUTHENTICATED.getLevel();

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

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompanyUser other = (CompanyUser) obj;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	
	
}
