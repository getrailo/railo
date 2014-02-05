package railo.runtime.op;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.util.IO;

public class IOImpl implements IO {

	private static IO singelton;

	public static IO getInstance() {
		if(singelton==null)singelton=new IOImpl();
		return singelton;
	}
	
	@Override
	public void closeSilent(InputStream is) {
		IOUtil.closeEL(is);
	}
	
	@Override
	public void closeSilent(OutputStream os) {
		IOUtil.closeEL(os);
	}
	
	@Override
	public void closeSilent(InputStream is,OutputStream os) {
		IOUtil.closeEL(is,os);
	}

	@Override
	public void closeSilent(Reader r) {
		IOUtil.closeEL(r);
	}

	@Override
	public void closeSilent(Writer w) {
		IOUtil.closeEL(w);
	}

	@Override
	public void closeSilent(Object o) {
		IOUtil.closeEL(o);
	}

	@Override
	public String toString(InputStream is, Charset charset) throws IOException {
		return IOUtil.toString(is, charset);
	}

	@Override
	public String toString(Reader r) throws IOException {
		return IOUtil.toString(r);
	}

	@Override
	public String toString(byte[] barr, Charset charset) throws IOException {
		return IOUtil.toString(barr, charset);
	}

	@Override
	public String toString(Resource res, Charset charset) throws IOException {
		return IOUtil.toString(res, charset);
	}

	@Override
	public void copy(InputStream in, OutputStream out, boolean closeIS, boolean closeOS) throws IOException {
		IOUtil.copy(in, out, closeIS, closeOS);
	}

	@Override
	public void copy(Reader r, Writer w, boolean closeR, boolean closeW) throws IOException {
		IOUtil.copy(r,w,closeR,closeW);
	}

	@Override
	public void copy(Resource src, Resource trg) throws IOException {
		IOUtil.copy(src,trg);
	}

	@Override
	public BufferedInputStream toBufferedInputStream(InputStream is) {
		return IOUtil.toBufferedInputStream(is);
	}

	@Override
	public BufferedOutputStream toBufferedOutputStream(OutputStream os) {
		return IOUtil.toBufferedOutputStream(os);
	}
}
