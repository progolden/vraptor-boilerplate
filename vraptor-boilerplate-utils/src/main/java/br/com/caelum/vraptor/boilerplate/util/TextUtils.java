package br.com.caelum.vraptor.boilerplate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.icu.ICUFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PortugueseStemmer;

import com.ibm.icu.text.Normalizer2;

public final class TextUtils {

	public static final Version LUCENE_MATCH_VERSION = Version.LUCENE_4_10_4;

	public static final CharArraySet PT_STOPWORDS = new CharArraySet(100, true);
	public static final CharArraySet EN_STOPWORDS = new CharArraySet(100, true);
	static {
		try {
			InputStream stream = TextUtils.class.getResourceAsStream("stopwords_pt.txt");
			if (stream != null) {
				InputStreamReader isr = new InputStreamReader(stream, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);
				while (br.ready()) {
					String line = br.readLine();
					PT_STOPWORDS.add(line);
				}
				br.close();
				isr.close();
				stream.close();
			}

			stream = TextUtils.class.getResourceAsStream("stopwords_en.txt");
			if (stream != null) {
				InputStreamReader isr = new InputStreamReader(stream, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);
				while (br.ready()) {
					String line = br.readLine();
					EN_STOPWORDS.add(line);
				}
				br.close();
				isr.close();
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final Normalizer2 FOLDING_NORMALIZER =  Normalizer2.getInstance(
	      ICUFoldingFilter.class.getResourceAsStream("utr30.nrm"),
	      "utr30", Normalizer2.Mode.COMPOSE);

	public static List<String> tokenizeAndNormalize(String text) throws IOException {
		if (GeneralUtils.isEmpty(text)) {
			return Collections.emptyList();
		}
		return tokenize(text.trim(), 32, true, false, false, false);
	}

	public static List<String> tokenizeForIndex(String text) throws IOException {
		if (GeneralUtils.isEmpty(text)) {
			return Collections.emptyList();
		}
		return tokenize(text.trim(), 64, false, true, true, true);
	}

	public static List<String> tokenizeKeywords(String text) throws IOException {
		return tokenizeKeywords(text, false);
	}
	
	public static List<String> tokenizeKeywords(String text, boolean filterStopWords) throws IOException {
		if (GeneralUtils.isEmpty(text)) {
			return Collections.emptyList();
		}
		return tokenize(text.trim(), filterStopWords ? 32:0, filterStopWords, true, true, true);
	}

	/**
	 * Utilize este m�todo apenas se for realizar um �nica chamada de stemming.
	 * Caso contr�rio instancie um Stemmer e o utilize.
	 * @param term Termo para fazer o stemming.
	 * @return Termo ap�s o stemming.
	 */
	public static String stem(String term) {
		return new Stemmer().stem(term);
	}

	public static String fold(String raw) {
		return FOLDING_NORMALIZER.normalize(raw);
	}

	public static Tokenizer getTokenizer(Reader input) {
		return new StandardTokenizer(input);
	}

	private static List<String> tokenize(String text, int maxLength, boolean stopwords, boolean stemming,
			boolean folding, boolean removeDuplicates) throws IOException {
		StringReader reader = new StringReader(text);
		Tokenizer tokenizer = getTokenizer(reader);

		TokenStream tokens = tokenizer;
		if (maxLength > 0)
			tokens = new LengthFilter(tokens, 3, maxLength);
		tokens = new LowerCaseFilter(tokens);
		tokens = new StandardFilter(tokens);
		if (stopwords)
			tokens = new StopFilter(tokens, PT_STOPWORDS);
		if (stemming)
			tokens = new SnowballFilter(tokens, new Stemmer().stemmer);
		if (folding)
			tokens = new ICUFoldingFilter(tokens);

		List<String> result = new LinkedList<String>();

	    String token;
	    tokenizer.reset();
	    if (removeDuplicates) {
	    	while (tokens.incrementToken()) {
		    	token = tokens.getAttribute(CharTermAttribute.class).toString();
		    	if (!result.contains(token))
		    		result.add(token);
		    }
		} else {
		    while (tokens.incrementToken()) {
		    	token = tokens.getAttribute(CharTermAttribute.class).toString();
		    	result.add(token);
		    }
		}
	    tokens.end();
	    tokens.close();
	    reader.close();
	    return result;
	}

	public static class Stemmer {
		PortugueseStemmer stemmer = new PortugueseStemmer();
		public String stem(String term) {
			this.stemmer.setCurrent(term);
			if (this.stemmer.stem())
				return this.stemmer.getCurrent();
			return term;
		}
	}
}
