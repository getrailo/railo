package railo.runtime.writer;

import java.io.IOException;

public interface WhiteSpaceWriter {
	
	/**
	 * write the given string without removing whitespace.
	 * @param str
	 * @throws IOException 
	 */
	public void writeRaw(String str) throws IOException;
}
