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
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.logging.Logger;

import br.com.caelum.vraptor.boilerplate.factory.EntitySessionManager;

/**
 * DAO implementation for JPA API.
 * Implements common actions to entities.
 * @author Renato R. R. de Oliveira
 *
 */
@SuppressWarnings("unchecked")
@RequestScoped
public class JPADAO implements DAO {
	
	private static final Logger LOG = Logger.getLogger(JPADAO.class);
	private static final int TRANSACTIONS_MAX_RETRIES = 50;
	
	protected final EntitySessionManager sessionManager;
	
	@Deprecated
	protected JPADAO() {
		this(null);
	}
	@Inject
	public JPADAO(EntitySessionManager sessionManager) {
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
	
	protected EntityManager getSession() {
		return this.sessionManager.getSession();
	}
	
	protected void closeSession() {
		this.sessionManager.closeSession();
	}

	public void execute(TransactionalOperation operation) {
		JDBCConnectionException last = null;
		EntityTransaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			EntityManager dbSession = this.getSession();
			try {
				tx = dbSession.getTransaction();
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
		EntityTransaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			EntityManager dbSession = this.getSession();
			try {
				tx = dbSession.getTransaction();
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
		throw new UnsupportedOperationException("JPA doesn't have save() functionality.");
	}
	
	@Override
	public void update(Serializable entity) {
		JDBCConnectionException last = null;
		EntityTransaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			EntityManager dbSession = this.getSession();
			try {
				tx = dbSession.getTransaction();
				dbSession.merge(entity);
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
		EntityTransaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			EntityManager dbSession = this.getSession();
			try {
				tx = dbSession.getTransaction();
				dbSession.remove(entity);
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
		EntityTransaction tx = null;
		for (int retry = 0; retry < TRANSACTIONS_MAX_RETRIES; retry++) {
			EntityManager dbSession = this.getSession();
			try {
				tx = dbSession.getTransaction();
				Query query = dbSession.createNativeQuery(sqlStatement);
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
		return this.getSession().find(clazz, id);
	}

	public CriteriaBuilder newCriteriaBuilder() {
		return this.getSession().getCriteriaBuilder();
	}

	public <E> E uniqueByCriteria(CriteriaQuery<E> criteriaQuery) {
		return this.getSession().createQuery(criteriaQuery).getSingleResult();
	}
	
	public <E> List<E> findByCriteria(CriteriaQuery<E> criteriaQuery) {
		return this.getSession().createQuery(criteriaQuery).getResultList();
	}

	public <E> List<E> findByCriteria(CriteriaQuery<E> criteriaQuery, int start, int limit) {
		return this.getSession().createQuery(criteriaQuery).setFirstResult(start).setMaxResults(limit).getResultList();
	}
	
	public static interface TransactionalOperation {
		public void execute(EntityManager session) throws HibernateException;
	}
	
}
