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
package br.com.caelum.vraptor.boilerplate.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Re�ne m�todos utilit�rios para tratar diversos objetos como String e Date.
 * 
 * @author Renato R. R. de Oliveira
 * 
 */
public final class GeneralUtils {

	public static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, new Locale("pt", "BR"));
	static {
		DATE_FORMAT.setLenient(false);
	}
	
	private GeneralUtils() {}
	
	/**
	 * Retorna uma data em GMT.
	 * 
	 * @param milliSeconds
	 * @return
	 */
	public static String getGMTTimeString(long milliSeconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		return sdf.format(new Date(milliSeconds));
	}
	public static Date getGMTTimeDate(String str) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		return sdf.parse(str);
	}

	public static void streamingPipe(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[2048];
		int readlen = -1;
		while ((readlen = is.read(bytes)) != -1) {
			os.write(bytes, 0, readlen);
			os.flush(); // Let us flush after bulk write
		}
	}
	
	/**
	 * Verifica se uma string � uma string vazia.
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return (string == null || string.isEmpty());
	}
	
	/**
	 * Verifica se a lista � uma lista vazia.
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		return (list == null || list.isEmpty());
	}

	public static boolean isInvalidId(Long id) {
		return ((id == null) || (id <= 0L));
	}
	
	public static Throwable getRootCause(Throwable ex) {
		if (ex == null)
			return null;
		while (ex.getCause() != null) {
			ex = ex.getCause();
		}
		return ex;
	}
	
	public static Date parseDateTime(String dateTime) throws Exception {
		Date dt;
		try{
			dt = DATE_FORMAT.parse(dateTime);
		}catch(ParseException ex){
			throw new Exception("Data inv�lida");
		}
		return dt;
	}
	
	/**
	 * Transforma um string que representa um Locale. A lingua padr�o � pt universal.
	 * @param locale String que representa o locale, Ex: pt_BR
	 * @return
	 */
	public static Locale parseLocaleString(String locale) {
		String lang = GeneralUtils.isEmpty(locale) ? "pt":locale.substring(0, 2);
		String country = GeneralUtils.isEmpty(locale) ? "":locale.substring(3, 5);
		return new Locale(lang, country);
	}
	
	/**
	 * Separa a string em palavras, baseado em uma express�o regular.
	 * @param text Texto a ser quebrado em palavras (tokens).
	 * @return Array de palavras do texto fornecido.
	 */
	public static String[] tokenize(String text) {
		if (GeneralUtils.isEmpty(text))
			return null;
		return text.split("[ ,.;:><^`�\\]\\[}{|��+_)(*&�%$#@!=\\-?�\\/\"]+", -1);
	}

	/**
	 * Converte string para title case.
	 * @param text Texto a ser convertido.
	 * @return Texto equivalente em title case.
	 */
	public static String toTitleCase(String text) {
		StringBuilder str = new StringBuilder();
		String[] words = text.trim().split("\\s");
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0) {
				if (i > 0) {
					str.append(" ");
				}
				String lower = words[i].toLowerCase(Locale.getDefault());
				if (words[i].length() == 1) {
					str.append(lower);
				} else if (words[i].length() == 2) {
					if (lower.equals("da") || lower.equals("de") || lower.equals("do") || lower.equals("di"))
						str.append(lower);
					else
						str.append(StringUtils.capitalize(lower));
				} else {
					str.append(StringUtils.capitalize(lower));
				}
			}
		}
		return str.toString();
	}

}
