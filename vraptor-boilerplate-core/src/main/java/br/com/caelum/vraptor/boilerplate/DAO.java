package br.com.caelum.vraptor.boilerplate;

import java.io.Serializable;

public interface DAO {

	public void persist(Serializable entity);
	public Serializable save(Serializable entity);
	public void update(Serializable entity);
	public void delete(Serializable entity);
	public int bulkUpdate(String sqlStatement);
	public <E extends Serializable> E exists(Serializable id, Class<E> clazz);
}
