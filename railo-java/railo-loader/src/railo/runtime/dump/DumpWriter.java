package railo.runtime.dump;

import java.io.IOException;
import java.io.Writer;

import railo.runtime.PageContext;

/**
 * writes out dumpdata to a writer
 */
public interface DumpWriter {

	public static int DEFAULT_RICH=0;
	public static int DEFAULT_PLAIN=1;
	public static int DEFAULT_NONE=2;

	
	/**
	 * @param data
	 * @param writer
	 * @throws IOException 
	 */
	public void writeOut(PageContext pc,DumpData data, Writer writer, boolean expand) throws IOException;
	
	/**
	 * cast dumpdata to a string
	 * @param data 
	 */
	public String toString(PageContext pc,DumpData data, boolean expand);
	
}
