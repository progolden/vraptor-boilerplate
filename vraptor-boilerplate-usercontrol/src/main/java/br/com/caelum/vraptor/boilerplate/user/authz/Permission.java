package br.com.caelum.vraptor.boilerplate.user.authz;

import br.com.caelum.vraptor.boilerplate.IdentifiableComponent;

public abstract class Permission extends IdentifiableComponent {
	
	public abstract String getDescription();

	public int getRequiredAccessLevel() {
		return AccessLevels.AUTHENTICATED.getLevel();
	}

}
