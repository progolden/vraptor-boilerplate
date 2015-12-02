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
package br.com.caelum.vraptor.boilerplate.bean;

import java.io.Serializable;
import java.util.List;


/**
 * Bean to serialize responses for the clients with list payloads.
 * @author Renato R. R. de Oliveira
 *
 */
public class ListResponse<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The request was successful? */
	private boolean success;
	/** Error specific/technical info. */
	private String error;
	/** Message to the client side. */
	private String message;
	/** Data list payload. */
	private List<T> data;
	/** Total size of the list, if the result is paginated. */
	private Long total;
	
	public ListResponse(boolean success, String message, String error, List<T> data, Long total) {
		this.success = success;
		this.error = error;
		this.message = message;
		this.data = data;
		this.total = total;
	}
	
	public ListResponse(boolean success, String message, String error, PaginatedList<T> list) {
		this(success, message, error, list.getList(), list.getTotal());
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	
}
