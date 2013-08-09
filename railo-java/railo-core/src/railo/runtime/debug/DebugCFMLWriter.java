package railo.runtime.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import railo.commons.lang.StringUtil;
import railo.runtime.cache.legacy.CacheItem;
import railo.runtime.op.Caster;
import railo.runtime.writer.CFMLWriter;

public class DebugCFMLWriter extends CFMLWriter implements DebugOutputLog {

	private CFMLWriter writer;
	private List<DebugTextFragment> fragments=new ArrayList<DebugTextFragment>(); 

	public DebugCFMLWriter(CFMLWriter writer) {
		super(writer.getBufferSize(), writer.isAutoFlush());
		this.writer=writer;
	}

	@Override
	public int getBufferSize() {
		return writer.getBufferSize();
	}

	@Override
	public boolean isAutoFlush() {
		return writer.isAutoFlush();
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		log(csq.toString());
		return writer.append(csq);
	}

	@Override
	public Writer append(char c) throws IOException {
		log(new String(new char[]{c}));
		return writer.append(c);
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		log(csq.subSequence(start, end).toString());
		return writer.append(csq, start, end);
	}

	@Override
	public void write(int i) throws IOException {
		print(i);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		print(cbuf);
	}

	@Override
	public void write(String str) throws IOException {
		print(str);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		log(StringUtil.substring(str,off,len));
		writer.write(str, off, len);
	}

	@Override
	public OutputStream getResponseStream() throws IOException {
		return writer.getResponseStream();
	}

	@Override
	public void setClosed(boolean b) {
		writer.setClosed(b);
	}

	@Override
	public void setBufferConfig(int interval, boolean b) throws IOException {
		writer.setBufferConfig(interval, b);
	}

	@Override
	public void appendHTMLHead(String text) throws IOException {
		writer.appendHTMLHead(text);
	}

	@Override
	public void writeHTMLHead(String text) throws IOException {
		writer.writeHTMLHead(text);
	}

	@Override
	public String getHTMLHead() throws IOException {
		return writer.getHTMLHead();
	}

	@Override
	public void resetHTMLHead() throws IOException {
		writer.resetHTMLHead();
	}

	@Override
	public void writeRaw(String str) throws IOException {
		print(str);
	}

	@Override
	public void clear() throws IOException {
		writer.clear();
	}

	@Override
	public void clearBuffer() throws IOException {
		writer.clearBuffer();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public int getRemaining() {
		return writer.getRemaining();
	}

	@Override
	public void newLine() throws IOException {
		println();
	}

	@Override
	public void print(boolean b) throws IOException {
		writer.print(b);
		log(b?"true":"false");
	}

	@Override
	public void print(char c) throws IOException {
		log(new String(new char[]{c}));
		writer.write(c);
	}

	@Override
	public void print(int i) throws IOException {
		log(Caster.toString(i));
		writer.write(i);
	}

	@Override
	public void print(long l) throws IOException {
		log(Caster.toString(l));
		writer.print(l);
	}

	@Override
	public void print(float f) throws IOException {
		log(Caster.toString(f));
		writer.print(f);
	}

	@Override
	public void print(double d) throws IOException {
		log(Caster.toString(d));
		writer.print(d);
	}

	@Override
	public void print(char[] carr) throws IOException {
		log(new String(carr));
		writer.write(carr);
	}

	@Override
	public void print(String str) throws IOException {
		log(str);
		writer.write(str);
	}

	@Override
	public void print(Object obj) throws IOException {
		log(String.valueOf(obj));
		writer.print(obj);
	}

	@Override
	public void println() throws IOException {
		print("\n");
	}

	@Override
	public void println(boolean b) throws IOException {
		print(b);
		print("\n");
	}

	@Override
	public void println(char c) throws IOException {
		print(c);
		print("\n");
	}

	@Override
	public void println(int i) throws IOException {
		print(i);
		print("\n");
	}

	@Override
	public void println(long l) throws IOException {
		print(l);
		print("\n");
	}

	@Override
	public void println(float f) throws IOException {
		print(f);
		print("\n");
	}

	@Override
	public void println(double d) throws IOException {
		print(d);
		print("\n");
	}

	@Override
	public void println(char[] carr) throws IOException {
		print(carr);
		print("\n");
	}

	@Override
	public void println(String str) throws IOException {
		print(str);
		print("\n");
	}

	@Override
	public void println(Object obj) throws IOException {
		print(obj);
		print("\n");
	}

	@Override
	public void write(char[] carr, int off, int len) throws IOException {
		log(StringUtil.substring(new String(carr),off,len));
		writer.write(carr,off,len);
	}

	private void log(String str) {
		StackTraceElement[] traces = new Throwable().getStackTrace();
		StackTraceElement trace;
		String template;
		int line;
		for(int i=0;i<traces.length;i++){
			trace=traces[i];
			template = trace.getFileName();
			line=trace.getLineNumber();
			if(line<=0 || template==null || template.endsWith(".java")) continue;
			fragments.add(new DebugTextFragment(str, template, line));
			break;
		}
	}

	@Override
	public DebugTextFragment[] getFragments() {
		return fragments.toArray(new DebugTextFragment[fragments.size()]);
	}

	@Override
	public void setAllowCompression(boolean allowCompression) {
		writer.setAllowCompression(allowCompression);
	}

	@Override
	public void doCache(CacheItem ci) {
		writer.doCache(ci);
	}

	@Override
	public CacheItem getCacheItem() {
		return writer.getCacheItem();
	}

}
