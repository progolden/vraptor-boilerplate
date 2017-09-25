package br.com.caelum.vraptor.boilerplate.company;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;
import br.com.caelum.vraptor.boilerplate.event.Current;
import br.com.caelum.vraptor.boilerplate.user.User;
import br.com.caelum.vraptor.boilerplate.user.authz.UserPermission;
import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

/**
 * Company business logic, mainly for multi tenant systems.
 * @author Renato R. R. de Oliveira
 */
@RequestScoped
public class CompanyBS extends HibernateBusiness {

	@Current @Inject private CompanyDomain domain;
	private static final int PAGESIZE = 12;
	
	/**
	 * Recupera uma instância do objeto CompanyDomain utilizando um host
	 * específico
	 * 
	 * @param host
	 *             à ser utilizado na query
	 * @return Domínio que utiliza o host epecificado
	 */
	public CompanyDomain retrieveByHost(String host) {
		Criteria criteria = this.dao.newCriteria(CompanyDomain.class).add(Restrictions.eq("host", host));
		return (CompanyDomain) criteria.uniqueResult();
	}
	
	public List<CompanyMessage> retrieveMessages(Company company) {
		Criteria criteria = this.dao.newCriteria(CompanyMessage.class);
		criteria.add(Restrictions.eq("company", company));
		return this.dao.findByCriteria(criteria, CompanyMessage.class);
	}
	
	public Map<String, String> retrieveMessagesOverlay(Company company) {
		List<CompanyMessage> messages = this.retrieveMessages(company);
		Map<String, String> overlay = new HashMap<String, String>();
		if (!GeneralUtils.isEmpty(messages)) {
			for (CompanyMessage message : messages) {
				overlay.put(message.getMessageKey(), message.getMessageValue());
			}
		}
		return overlay;
	}

	public void updateMessageOverlay(Company company, String key, String value) {
		Criteria criteria = this.dao.newCriteria(CompanyMessage.class);
		criteria.add(Restrictions.eq("company", company));
		criteria.add(Restrictions.eq("messageKey", key));
		CompanyMessage message = (CompanyMessage) criteria.uniqueResult();
		if (message == null) {
			message = new CompanyMessage();
			message.setCompany(company);
			message.setMessageKey(key);
		}
		message.setLastUpdated(new Date());
		message.setMessageValue(value);
		this.dao.persist(message);
	}

	/**
	 * Salva no banco de dados uma nova companhia
	 * 
	 * @param company instância da companhia a ser salva
	 */
	public void save(Company company) {
		company.setDeleted(false);
		this.persist(company);
	}

	/**
	 * Lista as companhias limitados a uma dada página
	 * 
	 * @param page número da página a ser listada
	 * @return PaginatedList lista de companhias
	 */
	public PaginatedList<Company> list(int page) {
		PaginatedList<Company> results = new PaginatedList<Company>();
		Criteria criteria = this.dao.newCriteria(Company.class).add(Restrictions.eq("deleted", false)).addOrder(Order.asc("name"));
		if (page > 0)
			criteria.setFirstResult((page-1) * PAGESIZE).setMaxResults(PAGESIZE);
		Criteria counting = this.dao.newCriteria(Company.class).setProjection(Projections.countDistinct("id"))
				.add(Restrictions.eq("deleted", false));
		results.setList(this.dao.findByCriteria(criteria, Company.class));
		results.setTotal((Long) counting.uniqueResult());
		return results;
	}

	/**
	 * Lista os domínios limitados a uma dada página
	 * 
	 * @param page número da página a ser listada
	 * @return PaginatedList lista de domínios
	 */
	public PaginatedList<CompanyDomain> listDomains(int page) {
		PaginatedList<CompanyDomain> results = new PaginatedList<CompanyDomain>();
		Criteria criteria = this.dao.newCriteria(CompanyDomain.class)
				.createAlias("company", "company", JoinType.INNER_JOIN)
				.addOrder(Order.asc("company.name")).addOrder(Order.asc("host"));
		if (page > 0)
			criteria.setFirstResult((page-1) * PAGESIZE).setMaxResults(PAGESIZE);
		Criteria counting = this.dao.newCriteria(CompanyDomain.class).setProjection(Projections.countDistinct("id"));
		results.setList(this.dao.findByCriteria(criteria, CompanyDomain.class));
		results.setTotal((Long) counting.uniqueResult());
		return results;
	}
	

	/**
	 * Busca Lista de permissões do usuário.
	 * 
	 * @param user Usuário buscado para listar suas permissões.
	 * 
	 * @return List Lista de permissões do usuário.
	 */
	public List<UserPermission> retrievePermissions(User user) {
		if (domain == null) {
			return new ArrayList<UserPermission>();
		}
		Criteria criteria = this.dao.newCriteria(UserPermission.class).add(Restrictions.eq("user", user))
				.add(Restrictions.eq("company", domain.getCompany())).add(Restrictions.eq("revoked", false));
		return this.dao.findByCriteria(criteria, UserPermission.class);
	}

	/**
	 * Recupera o nível de acesso do usuário para a company do domínio atual.
	 * 
	 * @param user Usuário para recuperar o nível de acesso.
	 * @return Nível de acesso para a instituição atual.
	 */
	public int retrieveAccessLevel(User user) {
		if (domain == null) {
			return user.getAccessLevel();
		}
		Criteria criteria = this.dao.newCriteria(CompanyUser.class).add(Restrictions.eq("user", user))
				.add(Restrictions.eq("company", domain.getCompany()));
		CompanyUser companyUser = (CompanyUser) criteria.uniqueResult();
		if (companyUser == null) {
			return user.getAccessLevel();
		}
		return Math.max(user.getAccessLevel(), companyUser.getAccessLevel());
	}

	/**
	 * Listar usuários da instituição do domínio acessado.
	 * 
	 * @param page Número da página.
	 * @param pageSize Tamanho da página
	 * 
	 * @return PaginatedList Lista de usuários.
	 */
	public PaginatedList<User> listFromCurrentCompany(Integer page, Integer pageSize) {

		if (page == null || page < 1) {
			page = 1;
		}
		if (pageSize == null) {
			pageSize = PAGESIZE;
		}
		PaginatedList<User> results = new PaginatedList<User>();
		if (this.domain == null) {
			Criteria criteria = this.dao.newCriteria(User.class).setFirstResult((page - 1) * pageSize)
					.setMaxResults(pageSize).addOrder(Order.asc("name"));
			Criteria counting = this.dao.newCriteria(User.class).setProjection(Projections.countDistinct("id"));
			results.setList(this.dao.findByCriteria(criteria, User.class));
			results.setTotal((Long) counting.uniqueResult());

		} else {
			Criteria criteria = this.dao.newCriteria(CompanyUser.class).setFirstResult((page - 1) * pageSize)
					.setMaxResults(pageSize).add(Restrictions.eq("company", this.domain.getCompany()))
					.createAlias("user", "user", JoinType.INNER_JOIN).addOrder(Order.asc("user.name"));
			Criteria counting = this.dao.newCriteria(CompanyUser.class)
					.add(Restrictions.eq("company", this.domain.getCompany()))
					.createAlias("user", "user", JoinType.INNER_JOIN)
					.setProjection(Projections.countDistinct("user.id"));
			List<CompanyUser> companyUsers = this.dao.findByCriteria(criteria, CompanyUser.class);
			ArrayList<User> users = new ArrayList<User>(companyUsers.size());
			for (CompanyUser companyUser : companyUsers) {
				User user = companyUser.getUser();
				user.setAccessLevel(Math.max(user.getAccessLevel(), companyUser.getAccessLevel()));
				users.add(user);
			}
			results.setList(users);
			results.setTotal((Long) counting.uniqueResult());
		}
		return results;
	}

	/**
	 * Listar usuários pela instituição.
	 * 
	 * @return PaginatedList Lista contendo os usuários.
	 */
	public PaginatedList<User> listUsersByCompany() {
		PaginatedList<User> results = new PaginatedList<User>();
		Criteria criteria = this.dao.newCriteria(CompanyUser.class);

		criteria.createAlias("user", "user", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));
		criteria.add(Restrictions.eq("blocked", false));
		// criteria.add(Restrictions.eq("user.active",false));
		criteria.addOrder(Order.asc("user.name"));

		List<CompanyUser> companyUsers = this.dao.findByCriteria(criteria, CompanyUser.class);
		ArrayList<User> users = new ArrayList<User>(companyUsers.size());
		for (CompanyUser companyUser : companyUsers) {
			users.add(companyUser.getUser());
		}
		results.setList(users);
		return results;
	}

}
