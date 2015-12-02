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
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.logging.Logger;

/**
 * Manages the opening and closing of DB sessions during a HTTP request.
 * @author Renato R. R. de Oliveira
 *
 */
@RequestScoped
public class SessionManager {

	private static final Logger LOG = Logger.getLogger(SessionManager.class);
	
	private final SessionFactory factory;
	private Session instance;
	
	@Inject
	public SessionManager(SessionFactory factory) {
		this.factory = factory;
		this.instance = null;
		LOG.debugf("Hibernate session manager instantiated.");
	}
	@Deprecated
	protected SessionManager() { this(null); }
	
	public Session getSession() {
		if (this.instance == null) {
			this.instance = this.factory.openSession();
			this.instance.setCacheMode(CacheMode.IGNORE);
			LOG.debugf("Opened new session.");
		}
		return this.instance;
	}
	
	@PreDestroy
	public void closeSession() {
		if (this.instance != null) {
			this.instance.close();
			this.instance = null;
			LOG.debugf("Closed opened session.");
		}
	}
}
