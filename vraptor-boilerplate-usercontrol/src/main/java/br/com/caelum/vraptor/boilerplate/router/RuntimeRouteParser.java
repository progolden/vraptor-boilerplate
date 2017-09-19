package br.com.caelum.vraptor.boilerplate.router;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor.http.route.Router;

@ApplicationScoped
@Specializes
public class RuntimeRouteParser extends PathAnnotationRoutesParser {
	
	private static final Logger LOG = Logger.getLogger(RuntimeRouteParser.class);
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected RuntimeRouteParser() {
		this(null, null);
	}

	@Inject
	public RuntimeRouteParser(Router router, ReflectionProvider reflectionProvider) {
		super(router, reflectionProvider);
	}

	@Override
	protected String[] getURIsFor(Method javaMethod, Class<?> type) {
		if (javaMethod.isAnnotationPresent(RuntimePath.class) && javaMethod.isAnnotationPresent(Path.class)) {
			Path pathAnn = javaMethod.getAnnotation(Path.class);
			RuntimePath runtimePathAnn = javaMethod.getAnnotation(RuntimePath.class);
			
			RuntimeRouteResolver resolve;
			String[] uris = pathAnn.value();
			try {
				resolve = runtimePathAnn.value().newInstance();
				uris = resolve.resolve(javaMethod, type, uris);
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.errorf(e, "Error instantiating runtime path resolver.");
				checkArgument(false, "Error instantiating runtime path resolver.");
			}

			checkArgument(uris.length > 0, "You must specify at least one path on @Path at %s", javaMethod);
			checkArgument(getUris(javaMethod).length == 0,
					"You should specify paths either in @Path(\"/path\") or @Get(\"/path\") (or @Post, @Put, @Delete), not both at %s", javaMethod);

			fixURIs(type, uris);
			return uris;
		}
		return super.getURIsFor(javaMethod, type);
	}
}
