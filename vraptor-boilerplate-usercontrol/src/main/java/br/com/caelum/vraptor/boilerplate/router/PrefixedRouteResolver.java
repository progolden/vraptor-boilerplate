package br.com.caelum.vraptor.boilerplate.router;

import javax.enterprise.inject.spi.CDI;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

public class PrefixedRouteResolver implements RuntimeRouteResolver {

	private PrefixedRouteConfiguration config;
	
	public PrefixedRouteResolver() {
		this.config = CDI.current().select(PrefixedRouteConfiguration.class).get();
	}
	
	@Override
	public String[] getURIsFor(Path pathAnn) {
		String[] uris = pathAnn.value();
		
		for (int i = 0; i < uris.length; i++) {
			if (!GeneralUtils.isEmpty(uris[i])) {
				uris[i] = this.config.getRoutesPrefix() + uris[i];
			}
		}
		
		return uris;
	}

}
