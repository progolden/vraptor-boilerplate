package br.com.caelum.vraptor.boilerplate.user.authz.permission;

import br.com.caelum.vraptor.boilerplate.user.authz.AccessLevels;
import br.com.caelum.vraptor.boilerplate.user.authz.Permission;

public class ViewUsersPermission extends Permission {

	@Override
	public String getDisplayName() {
		return "Visualizar Usuários";
	}

	@Override
	public int getRequiredAccessLevel() {
		return AccessLevels.MANAGER.getLevel();
	}

	@Override
	public String getDescription() {
		return "Listar usuários, Consultar informações de um usuário, Enviar mensagem para um usuário";
	}
}
