package br.com.caelum.vraptor.boilerplate.company;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateDAO;
import br.com.caelum.vraptor.boilerplate.event.Current;

/**
 * 
 * @author Renato R. R. de Oliveira
 *
 */
@RequestScoped
public class CurrentDomainProducer {

	private CompanyDomain domain;
	
	@Inject private HibernateDAO dao;
	@Inject private HttpServletRequest request;
	
	@Produces
	@Current
	public CompanyDomain getInstance() {
		if (domain == null) {
			domain = currentDomain();
		}
		return domain;
	}

	/**
	 * Recupera a instância do objeto CompanyDomain que está ativo no momento
	 * 
	 * @return Domínio ativo no momento
	 */
	public CompanyDomain currentDomain() {
		Criteria criteria = this.dao.newCriteria(CompanyDomain.class)
				.add(Restrictions.eq("host", this.request.getHeader("Host")));
		return (CompanyDomain) criteria.uniqueResult();
	}

}
