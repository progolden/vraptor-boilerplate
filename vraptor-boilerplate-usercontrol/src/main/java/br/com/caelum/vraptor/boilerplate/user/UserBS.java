package br.com.caelum.vraptor.boilerplate.user;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;
import br.com.caelum.vraptor.boilerplate.company.Company;
import br.com.caelum.vraptor.boilerplate.company.CompanyUser;
import br.com.caelum.vraptor.boilerplate.user.auth.UserAccessToken;
import br.com.caelum.vraptor.boilerplate.user.auth.UserSession;
import br.com.caelum.vraptor.boilerplate.util.CryptManager;
import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

/**
 * @author Renato R. R. de Oliveira
 */
@RequestScoped
public class UserBS extends HibernateBusiness {

	@Inject private UserSession session;
	// private static final int PAGESIZE = 20;

	/**
	 * Autenticar o usuário que está efetuando login e salva na sessão de usuário.
	 * 
	 * @param email E-mail do usuário.
	 * @param pwd Passoword do usuário.
	 * @param device ID do dispositivo de onde a autenticação veio.
	 * @return UserAccessToken Usuário existe e tem acesso ao sistema.
	 */
	public UserAccessToken authenticate(String email, String pwd, String device) {
		Criteria criteria = this.dao.newCriteria(User.class)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.or(
					Restrictions.eq("email", email),
					Restrictions.eq("username", email)
				))
				.add(Restrictions.eq("password", CryptManager.passwordHash(pwd)));
		User user = (User) criteria.uniqueResult();
		if (user != null) {
			UserAccessToken token = this.generateAccessToken(user, device);
			this.session.login(token);
			return token;
		}
		return null;
	}
	
	/**
	 * Retorna o token de acesso do usuário.
	 * 
	 * @param user Usuário para recuperar o token.
	 * @param device ID do dispositivo da token.
	 * @return token Token de acesso do usuário.
	 */
	public UserAccessToken generateAccessToken(User user, String device) {
		if (user == null)
			return null;
		Criteria criteria = this.dao.newCriteria(UserAccessToken.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eqOrIsNull("device", device))
				.add(Restrictions.gt("expiration", new Date()))
				.addOrder(Order.asc("expiration"));
		List<UserAccessToken> tokens = this.dao.findByCriteria(criteria, UserAccessToken.class);
		if (GeneralUtils.isEmpty(tokens)) {
			UserAccessToken token = new UserAccessToken();
			token.setUser(user);
			token.setDevice(device);
			token.setCreation(new Date());
			token.setCreationIp(this.request.getRemoteAddr());
			token.setExpiration(new Date(token.getCreation().getTime() + token.getTtl()));
			token.setToken(CryptManager.digest(user.getEmail() + "#" + String.valueOf(token.getCreation().getTime())
					+ "@" + String.valueOf(Math.random())));
			this.dao.persist(token);
			return token;
		}
		return tokens.get(0);
	}
	

	/**
	 * Trocar o password do usuário.
	 * 
	 * @param password
	 *            Novo password
	 * @param token
	 *            Token de identificação do usuário.
	 * @return boolean true Troca de password do usuário ocorreu com sucesso.
	 */
	public boolean resetPassword(String password, String token) {
		UserRecoverRequest req = this.retrieveRecoverRequest(token);
		if (req == null)
			return false;
		User user = req.getUser();
		user.setPassword(CryptManager.passwordHash(password));
		this.dao.persist(user);
		req.setRecover(new Date());
		req.setRecoverIp(this.request.getRemoteAddr());
		req.setUsed(true);
		this.dao.persist(req);
		return true;
	}

	/**
	 * Trocar o password do usuário.
	 * 
	 * @param password
	 *            Novo password.
	 * @param user
	 *            Usuário para trocar seu password.
	 * @return true Troca de password do usuário ocorreu com sucesso.
	 */
	public boolean changePassword(String password, User user) {
		user.setPassword(CryptManager.passwordHash(password));
		this.dao.persist(user);

		return true;
	}

	/**
	 * Requisição para recuperar usuário.
	 * 
	 * @param token
	 *            Token de identificação do usuário a ser recuperado.
	 * @return UserRecoverRequest Requisição realizada.
	 * 
	 */
	public UserRecoverRequest retrieveRecoverRequest(String token) {
		UserRecoverRequest req = this.dao.exists(token, UserRecoverRequest.class);
		if (req == null)
			return null;
		if (req.getExpiration().getTime() < System.currentTimeMillis())
			return null;
		if (req.isUsed())
			return null;
		return req;
	}



	/**
	 * Salvar o usuário no banco de dados.
	 * 
	 * @param user Usuário a ser salvo.
	 */
	public void save(User user) {
		if (this.existsByEmail(user.getEmail()) != null) {
			throw new IllegalArgumentException("Já existe um usuários cadastrado com este e-mail.");
		}
		user.setPassword(CryptManager.passwordHash(user.getPassword()));
		user.setActive(true);
		user.setDeleted(false);
		user.setCreation(new Date());
		this.persist(user);
	}

	/**
	 * Verifica se o usuário está deletado.
	 * 
	 * @param email
	 *            E-mail do usuário.
	 * @return boolean false Usuário está deletado. true Usuário não está
	 *         deletado.
	 */
	public boolean userIsDeleted(String email) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("email", email))
				.add(Restrictions.eq("deleted", false));
		User user = (User) criteria.uniqueResult();
		if (user != null)
			return false;
		return true;
	}

	/**
	 * Busca usuário pelo e-mail.
	 * 
	 * @param email
	 *            E-mail para buscar o usuário.
	 * @return User Usuário que contém o e-mail.
	 */
	public User existsByEmail(String email) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("email", email));
		return (User) criteria.uniqueResult();
	}

	/**
	 * Busca usuário pelo cpf.
	 * 
	 * @param cpf
	 *            Cpf para buscar o usuário.
	 * @return User Usuário que contém o cpf.
	 */
	public User existsByCpf(String cpf) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("cpf", cpf));
		return (User) criteria.uniqueResult();
	}

	/**
	 * Buscar usuário pelo celular.
	 * 
	 * @param cellphone
	 *            celular para buscar o usuário.
	 * @return User Usuário que contém o celular.
	 */
	public User existsByCellphone(String cellphone) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("cellphone", cellphone));
		return (User) criteria.uniqueResult();
	}

	/**
	 * Buscar usuário pelo id.
	 * 
	 * @param idUser
	 *            Id do usuário.
	 * @return User Usuário que contém o id.
	 */
	public User existsByUser(Long idUser) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("id", idUser));
		return (User) criteria.uniqueResult();
	}

	/**
	 * Buscar usuário pelo token de acesso.
	 * 
	 * @param token
	 *            token para buscar usuário.
	 * @return User Usuário que contém o token.
	 */
	public User existsByInviteToken(String token) {
		Criteria criteria = this.dao.newCriteria(User.class).add(Restrictions.eq("inviteToken", token));
		return (User) criteria.uniqueResult();
	}

	/**
	 * Verificar se usuário é de uma determinada instituição.
	 * 
	 * @param user
	 *            Usuário para verificação.
	 * @param company
	 *            Instituição para verificação.
	 * @return CompanyUser Instituição que pertence a determinado usuário.
	 */
	public CompanyUser retrieveCompanyUser(User user, Company company) {
		Criteria criteria = this.dao.newCriteria(CompanyUser.class);
		criteria.add(Restrictions.eq("company", company));
		criteria.add(Restrictions.eq("user", user));
		return (CompanyUser) criteria.uniqueResult();
	}

	/**
	 * Buscar página de usuários.
	 * 
	 * @param page Número da página de usuários a ser buscada.
	 * @return PaginatedList Página de usuários.
	 */
	public PaginatedList<User> list(int page) {
		PaginatedList<User> results = new PaginatedList<User>();
		Criteria criteria = this.dao.newCriteria(User.class).setFirstResult(page * 10).setMaxResults(10)
				.addOrder(Order.asc("name"));
		Criteria counting = this.dao.newCriteria(User.class).setProjection(Projections.countDistinct("id"));
		results.setList(this.dao.findByCriteria(criteria, User.class));
		results.setTotal((Long) counting.uniqueResult());
		return results;
	}

	/**
	 * Requisição do usuário para recuperar a senha.
	 * 
	 * @param user
	 *            Usuário para recuperar a senha.
	 * @return UserRecoverRequest Requisição relizada.
	 */
	public UserRecoverRequest requestRecover(User user) {
		if (user == null)
			return null;
		UserRecoverRequest req = new UserRecoverRequest();
		req.setUser(user);
		req.setCreation(new Date());
		req.setCreationIp(this.request.getRemoteAddr());
		req.setExpiration(new Date(System.currentTimeMillis() + 1800000L));
		req.setToken(CryptManager.digest(Math.random() + "@" + System.currentTimeMillis() + "#" + user.getEmail()));
		this.dao.persist(req);
		// TODO Send the recovering email.
		return req;
	}

	/**
	 * Buscar usuários de acordo com um nome.
	 * 
	 * @param terms Nome buscado.
	 * @return PaginatedList Lista de usuários que contém o nome buscado
	 *         em alguma parte do nome.
	 */
	public PaginatedList<User> listUsersBySearch(String terms) {
		PaginatedList<User> results = new PaginatedList<User>();
		Criteria criteria = this.dao.newCriteria(User.class);
		criteria.add(Restrictions.eq("deleted", false));
		
		Criteria count = this.dao.newCriteria(User.class).add(Restrictions.eq("deleted", false))
				.setProjection(Projections.countDistinct("id"));

		if (terms != null && !terms.isEmpty()) {
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.like("name", "%" + terms + "%").ignoreCase());
			criteria.add(or);
			count.add(or);
		}

		results.setList(this.dao.findByCriteria(criteria, User.class));
		results.setTotal((Long) count.uniqueResult());
		return results;

	}

}
