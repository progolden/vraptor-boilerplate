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
package br.com.caelum.vraptor.boilerplate;

import java.io.Serializable;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;


/**
 * Abstracting common features of the business classes.
 * @author Renato R. R. de Oliveira
 *
 */
public abstract class JPABusiness implements Business {

	protected final Logger LOGGER;
	
	@Inject protected JPADAO dao;
	@Inject protected HttpServletRequest request;
	
	public JPABusiness() {
		LOGGER = Logger.getLogger(this.getClass());
	}
	
	@Override
	public <E extends Serializable> E exists(Serializable id, Class<E> clazz) {
		return this.dao.exists(id, clazz);
	}
	
	@Override
	public <E extends Serializable> void persist(E model) {
		this.dao.persist(model);
	}
	
	@Override
	public <E extends Serializable> void remove(E model) {
		this.dao.delete(model);
	}
	
}
