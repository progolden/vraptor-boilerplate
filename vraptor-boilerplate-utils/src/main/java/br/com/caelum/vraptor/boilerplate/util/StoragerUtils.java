package br.com.caelum.vraptor.boilerplate.util;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StoragerUtils {

	private static String STORAGER_URL = "https://cloud.progolden.com.br/";
	private static String STORAGER_CLIENT = null;
	private static String STORAGER_SECRET = null;
	private static final String STORAGER_CLIENT_HEADER = "X-PS-Client-ID";
	private static final String STORAGER_SECRET_HEADER = "X-PS-Client-Secret";
	private static final int CONNECTION_TIMEOUT = 180000;
	
	public static void setStoragerConfig(
			String url,
			String clientId,
			String clientSecret) {
		STORAGER_URL = url;
		STORAGER_CLIENT = clientId;
		STORAGER_SECRET = clientSecret;
	}
	
	/**
	 * 
	 * @param body The input stream with the multipart body content.
	 * @param contentType The content type header content for the multipart request.
	 * @param response The HTTP response to write to.
	 * @return The uploaded file URL.
	 * @throws MalformedURLException,IOException 
	 */
	public static String pipeMultipartFile(InputStream body, String contentType, HttpServletResponse response) throws MalformedURLException,IOException {
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(STORAGER_URL + "file");
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setReadTimeout(CONNECTION_TIMEOUT);

			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Content-Type", contentType);
			httpUrlConnection.setRequestProperty(STORAGER_CLIENT_HEADER, STORAGER_CLIENT);
			httpUrlConnection.setRequestProperty(STORAGER_SECRET_HEADER, STORAGER_SECRET);

			httpUrlConnection.connect();
			outputStream = httpUrlConnection.getOutputStream();
			IOUtils.copyLarge(body, outputStream);
			
			if (httpUrlConnection.getResponseCode() != HTTP_OK) {
				inputStream = httpUrlConnection.getErrorStream();
				response.setStatus(httpUrlConnection.getResponseCode());
				response.setContentType(httpUrlConnection.getContentType());
				IOUtils.copyLarge(inputStream, response.getOutputStream());
				response.getOutputStream().close();
				return null;
			}
			inputStream = httpUrlConnection.getInputStream();
			String resp = IOUtils.toString(inputStream);
			Gson gson = new GsonBuilder().create();
			Map<String, Object> results = gson.fromJson(resp, Map.class);
			Boolean success = (Boolean) results.get("success");
			if (success) {
				Map<String, Object> data = (Map<String, Object>) results.get("data");
				Double id = (Double) data.get("id");
				return STORAGER_URL+"file/"+String.valueOf(id.longValue());
			}
			response.setContentType(httpUrlConnection.getContentType());
			IOUtils.write(resp, response.getOutputStream());
			response.getOutputStream().close();
			return null;
		} finally {
			if (httpUrlConnection != null)
				httpUrlConnection.disconnect();
		}
	}
}
