package br.com.caelum.vraptor.boilerplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.boilerplate.user.auth.UserSession;

/**
 * Especialização da classe abstrata para controladores com o objetivo de fornecer um prefixo
 * para os end points REST e injetar o objeto de sessão de usuário automaticamente nos
 * controladores do sistema. Todos os controladores devem estender dessa classe.
 * 
 * @author Renato R. R. de Oliveira <renatorro@comp.ufla.br>
 *
 */
public abstract class UserControlAbstractController extends AbstractController {

	@Inject protected UserSession userSession;
	@Inject protected HttpServletRequest request;
}
