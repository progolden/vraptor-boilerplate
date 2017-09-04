package br.com.caelum.vraptor.boilerplate.filters;

import java.util.List;

public interface Filter<T> {
	public List<T> depurate(List<T> list);
}
