package br.com.caelum.vraptor.boilerplate.router;

public interface PrefixedRouteConfiguration {
	public String getRoutesPrefix();
	public boolean isUserControlEnabled();
	public boolean isMultiTenantEnabled();
}
