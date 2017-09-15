package br.com.caelum.vraptor.boilerplate.router;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultPrefixedRouteConfiguration implements PrefixedRouteConfiguration {

	private static final String PREFIX = "/api";
	
	@Override
	public String getRoutesPrefix() {
		return PREFIX;
	}

	@Override
	public boolean isUserControlEnabled() {
		return true;
	}

	@Override
	public boolean isMultiTenantEnabled() {
		return true;
	}

}
