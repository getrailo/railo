package railo.runtime.converter;

import java.io.IOException;
import java.io.Writer;

import railo.runtime.PageContext;

public interface StringConverter {
	public void writeOut(PageContext pc,Object source,Writer writer) throws ConverterException,IOException;
}
