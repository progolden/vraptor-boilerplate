package br.com.caelum.vraptor.boilerplate.router;

public interface RouteConfiguration {
	public String getRoutesPrefix();
	public boolean isUserControlEnabled();
	public boolean isMultiTenantEnabled();
}
