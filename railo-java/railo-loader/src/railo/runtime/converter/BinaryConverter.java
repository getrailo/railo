package railo.runtime.converter;

import java.io.IOException;
import java.io.OutputStream;

import railo.runtime.PageContext;

public interface BinaryConverter {
	public void writeOut(PageContext pc,Object source,OutputStream os) throws ConverterException,IOException;
}
