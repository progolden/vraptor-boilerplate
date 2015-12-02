/*

The MIT License (MIT)

Copyright (c) 2015 ProGolden Technology Solutions

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package br.com.caelum.vraptor.boilerplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.boilerplate.bean.ListResponse;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;
import br.com.caelum.vraptor.boilerplate.bean.Response;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.MessageList;
import br.com.caelum.vraptor.view.Results;

/**
 * Abstracting common features of the controllers.
 * @author Renato R. R. de Oliveira
 *
 */
public abstract class AbstractController {

	protected final Logger LOGGER;
	
	@Inject protected Result result;
	@Inject protected HttpServletRequest request;
	@Inject protected HttpServletResponse response;
	@Inject protected GsonSerializerBuilder gsonBuilder;
	
	public AbstractController() {
		LOGGER = Logger.getLogger(this.getClass());
	}
	
	protected void fail() {
		this.fail(null, null);
	}

	protected void fail(String message) {
		this.fail(null, message);
	}
	
	protected void fail(String error, String message) {
		this.fail(error, message, HttpServletResponse.SC_BAD_REQUEST);
	}
	
	protected void fail(MessageList messages) {
		if ((messages == null) || (messages.size() == 0)) {
			this.fail(
				"Validation errors occurred",
				"Doesn't received any validation message.",
				HttpServletResponse.SC_CONFLICT);
		} else if (messages.size() == 1) {
			this.fail("Validation errors occurred", messages.get(0).getMessage(), HttpServletResponse.SC_CONFLICT);
		} else {
			String msg = "<ul>";
			for (Message message : messages) {
				msg += "<li>"+message.getMessage()+"</li>";
			}
			msg += "</ul>";
			this.fail("Validation errors occurred", msg, HttpServletResponse.SC_CONFLICT);	
		}
	}
	
	protected void fail(String error, String message, int httpStatusCode)  {
		this.response.setStatus(httpStatusCode);
		try {
			gsonBuilder.create().toJson(
				new Response<Object>(false, message, error, null), this.response.getWriter());
			this.response.getWriter().close();
		} catch (IOException ex) {}
		this.result.nothing();
	}
	
	protected void success() {
		this.success(null, "");
	}

	protected void success(String message) {
		this.success(null, message);
	}
	
	protected <T extends Serializable> void success(T data) {
		this.success(data, null);
	}
	
	protected <T extends Serializable> void success(T data, String message) {
		this.result.use(Results.json())
			.withoutRoot()
			.from(new Response<T>(true, message, null, data))
			.recursive().serialize();
		this.result.nothing();
	}
	
	protected <T extends Serializable> void success(List<T> list, Long total) {
		this.success(list, total, null);
	}
	
	protected <T extends Serializable> void success(PaginatedList<T> list) {
		this.success(list.getList(), list.getTotal(), null);
	}
	
	protected <T extends Serializable> void success(List<T> list, Long total, String message) {
		this.result.use(Results.json())
			.withoutRoot()
			.from(new ListResponse<T>(true, message, null, list, total))
			.recursive().serialize();
		this.result.nothing();
	}
	
	protected void forbidden() {
		this.fail("Forbidden", "You do not have permission for this action.", HttpServletResponse.SC_FORBIDDEN);
	}
}
