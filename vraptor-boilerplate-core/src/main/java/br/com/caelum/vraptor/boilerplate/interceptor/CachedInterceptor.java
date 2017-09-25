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
package br.com.caelum.vraptor.boilerplate.interceptor;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.boilerplate.Cached;
import br.com.caelum.vraptor.boilerplate.util.GeneralUtils;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

/**
 * 
 * @author Renato R. R. de Oliveira
 *
 */
@Intercepts(after=HeadersInterceptor.class)
@RequestScoped
@AcceptsWithAnnotations(Cached.class)
public class CachedInterceptor {

	@Inject private HttpServletResponse httpResponse;
	@Inject private ControllerMethod method;
	
	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		Cached annotation = method.getMethod().getAnnotation(Cached.class);
		String maxagestr = String.valueOf(annotation.value());
		this.httpResponse.setHeader("Pragma", "public");
		this.httpResponse.setHeader("Cache-Control", "max-age=" + maxagestr + ", must-revalidate");
		long relExpiresInMillis = System.currentTimeMillis() + annotation.value();
		this.httpResponse.setHeader("Expires", GeneralUtils.getGMTTimeString(relExpiresInMillis));
		this.httpResponse.setHeader("Last-Modified", GeneralUtils.getGMTTimeString(System.currentTimeMillis()));
		stack.next();
	}
	
	public static void addCacheHeaders(Date lastModified, HttpServletResponse response) {
		response.setHeader("Pragma", "public, max-age=" + String.valueOf(Cached.ONE_DAY));
		response.setHeader("Cache-Control", "public, max-age=" + String.valueOf(Cached.ONE_DAY));
		response.setHeader("Expires", GeneralUtils.getGMTTimeString(System.currentTimeMillis()+Cached.ONE_DAY));
		response.setHeader("Last-Modified", GeneralUtils.getGMTTimeString(lastModified.getTime()));
	}
	
}
