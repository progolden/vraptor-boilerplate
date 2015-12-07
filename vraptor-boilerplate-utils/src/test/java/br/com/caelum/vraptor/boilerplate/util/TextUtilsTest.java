package br.com.caelum.vraptor.boilerplate.util;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TextUtilsTest {

	@Test
	public void testTokenize() throws IOException {
		String text = "Teste de tokenização e steamer, vamos ver verbos, stop words, sinônimos, plurais. "+
			"pato patos tráfico traficantes traficante droga drogas polícia policiais policiamento "+
			"política políticos político fdsjfdsfagfagfagfagfgafgagdagafdgafdgagafdgfdsgdgdgdgdfggf";
		List<String> tokens = TextUtils.tokenizeAndNormalize(text);
		Assert.assertFalse("Nenhum token retornado.", GeneralUtils.isEmpty(tokens));
		for (String token : tokens) {
			System.out.print(token+" ");
		}
		System.out.println();
	}
	
	@Test
	public void testStem() {
		String word1 = "traficante";
		String word2 = "traficantes";
		String word3 = "politicamente";

		String stemmed1 = TextUtils.stem(word1);
		Assert.assertEquals("Stemming "+word1+" dont match trafic, leads to "+
			stemmed1, "trafic", stemmed1);
		String stemmed2 = TextUtils.stem(word2);
		Assert.assertEquals("Stemming "+word2+" dont match trafic, leads to "+
			stemmed2, "trafic", stemmed2);
		String stemmed3 = TextUtils.stem(word3);
		Assert.assertEquals("Stemming "+word3+" dont match polit, leads to "+
			stemmed3, "polit", stemmed3);
	}
}
