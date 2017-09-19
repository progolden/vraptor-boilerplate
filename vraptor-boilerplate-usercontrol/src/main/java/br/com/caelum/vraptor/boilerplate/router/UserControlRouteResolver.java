package br.com.caelum.vraptor.boilerplate.router;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.CDI;

import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;

public class UserControlRouteResolver implements RuntimeRouteResolver {

	private RouteConfiguration config;
	
	public UserControlRouteResolver() {
		this.config = CDI.current().select(RouteConfiguration.class).get();
	}
	
	@Override
	public String[] resolve(Method javaMethod, Class<?> type, String... uris) {
		boolean isMultiTenat = javaMethod.isAnnotationPresent(MultiTenantRoute.class) || type.isAnnotationPresent(MultiTenantRoute.class);
		boolean isUserControl = isMultiTenat || javaMethod.isAnnotationPresent(UserControlRoute.class) || type.isAnnotationPresent(UserControlRoute.class);
		if (isMultiTenat && !config.isMultiTenantEnabled())
			return new String[] {};
		if (isUserControl && !config.isMultiTenantEnabled() && !config.isUserControlEnabled())
			return new String[] {};
		for (int i = 0; i < uris.length; i++) {
			if (!GeneralUtils.isEmpty(uris[i])) {
				uris[i] = this.config.getRoutesPrefix() + uris[i];
			}
		}
		return uris;
	}

}
