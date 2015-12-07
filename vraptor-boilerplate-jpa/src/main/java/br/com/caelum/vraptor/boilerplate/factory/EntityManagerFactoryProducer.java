package br.com.caelum.vraptor.boilerplate.factory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jboss.logging.Logger;

import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

/**
 * Hibernate EntityManagerFactory producer.
 * 
 * @author Renato R. R. de Oliveira
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

	private static final Logger LOG = Logger.getLogger(EntityManagerFactoryProducer.class);
	
	public static final String PERSISTENCE_UNIT = "MainPersistence";
	
	/** The factory. */
	private EntityManagerFactory factory;

	public void initialize(String persistenceUnit) {
		if (GeneralUtils.isEmpty(persistenceUnit)) {
			LOG.debugf("Configuring entity manager factory for default persistence unit: %s", PERSISTENCE_UNIT);
			this.factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		} else {
			LOG.debugf("Configuring entity manager factory for persistence unit: %s", persistenceUnit);
			this.factory = Persistence.createEntityManagerFactory(persistenceUnit);
		}
	}

	@Produces
	public EntityManagerFactory getInstance() {
		if (this.factory == null) {
			this.initialize(null);
		}
		return this.factory;
	}

	@PreDestroy
	public void close() {
		if (this.factory != null) {
			this.factory.close();
			this.factory = null;
		}
	}
	
}
