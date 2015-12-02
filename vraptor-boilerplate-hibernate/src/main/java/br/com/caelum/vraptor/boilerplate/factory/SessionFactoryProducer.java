/*

The MIT License (MIT)

Copyright (c) 2015 ProGolden Technology Solutions

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package br.com.caelum.vraptor.boilerplate.factory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

/**
 * Hibernate SessionFactory producer.
 * 
 * @author Renato R. R. de Oliveira
 */
@ApplicationScoped
public class SessionFactoryProducer {

	private static final Logger LOG = Logger.getLogger(SessionFactoryProducer.class);
	
	public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
	
	/** The factory. */
	private SessionFactory factory;

	public void initialize(String configurationFile) {
		Configuration config = new Configuration();
		if (GeneralUtils.isEmpty(configurationFile)) {
			LOG.debugf("Configuring session factory with file: %s", HIBERNATE_CFG_FILE);
			config.configure(HIBERNATE_CFG_FILE);
		} else {
			LOG.debugf("Configuring session factory with file: %s", configurationFile);
			config.configure(configurationFile);
		}
		StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder()
			.applySettings(config.getProperties());
		this.factory = config.buildSessionFactory(serviceRegistryBuilder.build());
	}

	@Produces
	public SessionFactory getInstance() {
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
