package br.com.caelum.vraptor.boilerplate.factory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.logging.Logger;

@RequestScoped
public class EntitySessionManager {

	private static final Logger LOG = Logger.getLogger(EntitySessionManager.class);
	
	private final EntityManagerFactory factory;
	private EntityManager instance;
	
	@Inject
	public EntitySessionManager(EntityManagerFactory factory) {
		this.factory = factory;
		this.instance = null;
		LOG.debugf("Hibernate entity session manager instantiated.");
	}
	@Deprecated
	protected EntitySessionManager() { this(null); }
	
	public EntityManager getSession() {
		if (this.instance == null) {
			this.instance = this.factory.createEntityManager();
			LOG.tracef("Opened new entity manager session.");
		}
		return this.instance;
	}
	
	@PreDestroy
	public void closeSession() {
		if (this.instance != null) {
			this.instance.close();
			this.instance = null;
			LOG.tracef("Closed opened entity manager session.");
		}
	}
}
