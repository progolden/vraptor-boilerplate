package br.com.caelum.vraptor.boilerplate.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class GeneralUtilsTest {

	@Test
	public void getGMTTimeStringTest() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		Date date = new Date();
		
		long milliSeconds = date.getTime();
		
		Assert.assertEquals("A data n�o corresponde ao par�metro passado", dateFormat.format(date), GeneralUtils.getGMTTimeString(milliSeconds));	
	}
	
	@Test
	public void streamingPipeTest() throws IOException {
		//Cria os arquivos
		File input = new File("input.txt");
		input.createNewFile();
		
		File output = new File("output.txt");
		output.createNewFile();

		//Escreve no arquivo de INPUT
		BufferedWriter bw = new BufferedWriter(new FileWriter(input.getAbsoluteFile()));
		String inputText = "TESTE";
		bw.write(inputText);
		bw.close();		
		
		//Cria a InputStream 
		InputStream is = new FileInputStream(input);
		
		//Cria a OutputStream 
		OutputStream os = new FileOutputStream(output);

	
		//Roda o algoritmo streamingPipe
		GeneralUtils.streamingPipe(is, os);
		
		//Recupera conte�do do output file
		BufferedReader br = new BufferedReader(new FileReader(output));
		String outputText = br.readLine();
		br.close();
		
		//Compara atrav�s do assertEquals o conte�do dos arquivos
		Assert.assertEquals("Dados dos streams n�o conferem", inputText, outputText);
		
		//Finaliza as streams e limpa os arquivos criados
		is.close();
		os.close();
		
		if(input.exists())
			input.delete();
		
		if(output.exists())
			output.delete();
	}
	
	@Test
	public void emptyStringTest() {
		String myStr = null;
		Assert.assertTrue("String null n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myStr));
		myStr = "";
		Assert.assertTrue("String vazia n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myStr));
		myStr = "Alguma String";
		Assert.assertFalse("String com texto n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myStr));
	}
	
	@Test
	public void emptyListTest() {
		List<String> myList = null;
		Assert.assertTrue("Lista null n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myList));
		myList = new ArrayList<String>();
		Assert.assertTrue("Lista vazia n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myList));
		myList.add("Alguma coisa.");
		Assert.assertFalse("Lista com elementos n�o foi avaliada corretamente.", GeneralUtils.isEmpty(myList));
	}
	
	@Test
	public void parseDateTimeTest() throws Exception {
		Date parsed1 = GeneralUtils.parseDateTime("01/01/2010 00:00:00");
		Assert.assertNotNull(parsed1);
		Assert.assertEquals(1262311200000L, parsed1.getTime());
		
		Date parsed2 = GeneralUtils.parseDateTime("01/12/2010 23:59:00");
		Assert.assertNotNull(parsed2);
		Assert.assertEquals(1291255140000L, parsed2.getTime());
		
		Date parsed3 = GeneralUtils.parseDateTime("29/10/2011 23:59:59");
		Assert.assertNotNull(parsed3);
		Assert.assertEquals(1319939999000L, parsed3.getTime());
	}
	
	@Test
	public void toTitleCaseTest() throws Exception {
		Assert.assertEquals("Renato Oliveira", GeneralUtils.toTitleCase("RENATO OLIVEIRA"));
		Assert.assertEquals("Lucas de Oliveira", GeneralUtils.toTitleCase("lucas de oliveira"));
		Assert.assertEquals("Jos� da Silva", GeneralUtils.toTitleCase("jos� da silva"));
	}
	
}
