package br.com.caelum.vraptor.boilerplate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jboss.logging.Logger;

public class CsvUtils {

	private static final Logger LOG = Logger.getLogger(CsvUtils.class);
	private static final int READ_BUFFER_SIZE = 4096;
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static int readFromCsv(InputStream input, Consumer<? super CSVRecord> action) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, CHARSET), READ_BUFFER_SIZE);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL
			.withDelimiter(';')
			.withRecordSeparator('\n')
		);
		RecordReaderWrapper wrapper = new RecordReaderWrapper(action);
		parser.forEach(wrapper);
		parser.close();
		reader.close();
		return wrapper.readedReacords;
	}
	
	public static void writeToCsv(OutputStream output, Iterable<? extends List<String>> dumper) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, CHARSET);
		CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL
			.withDelimiter(';')
			.withRecordSeparator('\n')
		);
		
		printer.printRecords(dumper);
		printer.flush();
		printer.close();
		writer.close();
	}
	
	private static class RecordReaderWrapper implements Consumer<CSVRecord> {

		public int readedReacords = 0;
		
		private final Consumer<? super CSVRecord> wrapped;
		
		public RecordReaderWrapper(Consumer<? super CSVRecord> wrapped) {
			this.wrapped = wrapped;
		}
		
		@Override
		public void accept(CSVRecord t) {
			this.readedReacords++;
			try {
				this.wrapped.accept(t);
			} catch (Throwable ex) {
				LOG.errorf(ex, "Exception when parsing record %d from CSV.", this.readedReacords);
			}
		}
		
	}
}
