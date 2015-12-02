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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
@RequestScoped
public class HeadersInterceptor {

	@Inject private HttpServletResponse httpResponse;
	
	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		this.httpResponse.setHeader("P3P", "CP=\"This application doesn't have a P3P policy.\"");
		String allowMethods = "GET, POST, PUT, DELETE, OPTIONS";
        this.httpResponse.setHeader("Access-Control-Allow-Methods",allowMethods);
        this.httpResponse.setHeader("Access-Control-Allow-Headers",
        	"Content-Type, X-Requested-With, accept, Authorization, origin");
        this.httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		this.httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		this.httpResponse.setHeader("Access-Control-Expose-Headers", "Content-Type, Location");
		stack.next();
	}
	
}
