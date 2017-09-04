package br.com.caelum.vraptor.boilerplate.user.authz.permission;

import br.com.caelum.vraptor.boilerplate.user.authz.AccessLevels;
import br.com.caelum.vraptor.boilerplate.user.authz.Permission;

public class EditMessagesPermission extends Permission {

	@Override
	public String getDisplayName() {
		return "Editar Textos do Sistema";
	}

	@Override
	public int getRequiredAccessLevel() {
		return AccessLevels.COMPANY_ADMIN.getLevel();
	}

	@Override
	public String getDescription() {
		
		return "Editar textos e mensagens do sistema para esta instituição.";
	}
}
