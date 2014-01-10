package railo.commons.lang;

import java.io.IOException;
import java.io.Writer;

public final class DevNullCharBuffer extends CharBuffer {

	@Override
	public void append(char[] c) {}
	@Override
	public void append(String str) {}
	@Override
	public void clear() {}
	@Override
	public int size() {
		return 0;
	}
	@Override
	public char[] toCharArray() {
		return new char[0];
	}
	@Override
	public String toString() {
		return "";
	}
	@Override
	public void writeOut(Writer writer) throws IOException {}
}