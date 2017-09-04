package br.com.caelum.vraptor.boilerplate.company;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.boilerplate.event.Current;

/**
 * 
 * @author Renato R. R. de Oliveira
 *
 */
@RequestScoped
public class CurrentDomainProducer {

	@Inject private CompanyBS bs;
	private CompanyDomain domain;
	
	@Produces
	@Current
	public CompanyDomain getInstance() {
		if (domain == null) {
			domain = bs.currentDomain();
		}
		return domain;
	}
}
