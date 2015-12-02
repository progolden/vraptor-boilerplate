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
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.logging.Logger;

import br.com.caelum.vraptor.boilerplate.factory.SessionManager;

/**
 * DAO implementation for Hibernate Criteria API and Sessions.
 * Implements common actions to entities.
 * @author Renato R. R. de Oliveira
 *
 */
@SuppressWarnings("unchecked")
@RequestScoped
public class HibernateDAO implements DAO {
	
	private static final Logger LOG = Logger.getLogger(HibernateDAO.class);
	private static final int TRANSACTIONS_MAX_RETRIES = 50;
	
	protected final SessionManager sessionManager;
	
	@Deprecated
	protected HibernateDAO() {
		this(null);
	}
	@Inject
	public HibernateDAO(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	public static <T extends Serializable> T unproxy(T entity) {
	    if (entity == null) {
	        return null;
	    }

	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
	                .getImplementation();
	    }
	    return entity;
	}
	
	protected Session getSession() {
		return this.sessionManager.getSession();
	}
	
	protected void closeSession() {
		this.sessionManager.closeSession();
	}

	public void execute(TransactionalOperation operation) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				operation.execute(dbSession);
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar executar operação transacional apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}
	
	@Override
	public void persist(Serializable entity) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				dbSession.persist(entity);
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar salvar entidade: "+entity.getClass().getCanonicalName()+" apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}
	
	@Override
	public Serializable save(Serializable entity) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				Serializable id = dbSession.save(entity);
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return id;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar salvar entidade: "+entity.getClass().getCanonicalName()+" apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}
	
	@Override
	public void update(Serializable entity) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				dbSession.update(entity);
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar atualizar entidade: "+entity.getClass().getCanonicalName()+" apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}
	
	@Override
	public void delete(Serializable entity) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				dbSession.delete(entity);
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar deletar entidade: "+entity.getClass().getCanonicalName()+" apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}
	
	@Override
	public int bulkUpdate(String sqlStatement) {
		JDBCConnectionException last = null;
		Transaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			Session dbSession = this.getSession();
			try {
				tx = dbSession.beginTransaction();
				SQLQuery query = dbSession.createSQLQuery(sqlStatement);
				int affected = query.executeUpdate();
				tx.commit();
				if (retry > 0)
					LOG.warn("Transacao realizada apos "+String.valueOf(retry+1)+" tentativas.");
				return affected;
			} catch (JDBCConnectionException ex) {
				last = ex;
				if (tx != null)
					tx.rollback();
				tx = null;
				this.closeSession();
			}
		}
		LOG.error("Erro ao tentar atualizar entidade em massa apos "+
				String.valueOf(TRANSACTIONS_MAX_RETRIES)+" tentativas.", last);
		throw new RuntimeException("Erro no banco de dados.", last);
	}

	@Override
	public <E extends Serializable> E exists(Serializable id, Class<E> clazz) {
		if (id == null)
			return null;
		return clazz.cast(this.getSession().get(clazz, id));
	}

	public <E extends Serializable> Criteria newCriteria(Class<E> clazz) {
		return this.getSession().createCriteria(clazz);
	}

	public DetachedCriteria newDetachedCriteria(Class<?> clazz, String alias) {
		return DetachedCriteria.forClass(clazz, alias);
	}

	public <E extends Serializable> List<E> findByCriteria(Criteria criteria, Class<E> dtoClass) {
		return criteria.list();
	}
	
	public SQLQuery newSQLQuery(String sql) {
		return this.getSession().createSQLQuery(sql);
	}
	
	public static interface TransactionalOperation {
		public void execute(Session session) throws HibernateException;
	}
	
}
