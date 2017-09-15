package br.com.caelum.vraptor.boilerplate.router;

import br.com.caelum.vraptor.Path;

public interface RuntimeRouteResolver {
	String[] getURIsFor(Path pathAnn);
}
