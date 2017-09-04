package br.com.caelum.vraptor.boilerplate.interceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;

import br.com.caelum.vraptor.boilerplate.ParseMultipartData;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.observer.upload.CommonsUploadMultipartObserver;
import br.com.caelum.vraptor.observer.upload.MultipartConfig;
import br.com.caelum.vraptor.validator.Validator;



@Specializes
public class MultipartInterceptor extends CommonsUploadMultipartObserver {
	
	@Override
	public void upload(@Observes ControllerFound event, MutableRequest request,
			MultipartConfig config, Validator validator) {
		
		
		if(request.getRequestURI().equals("/forpdi/api/file/upload")) {
			if (!event.getMethod().containsAnnotation(ParseMultipartData.class))
				return;
		}
		
		
		super.upload(event, request, config, validator);
	}
}