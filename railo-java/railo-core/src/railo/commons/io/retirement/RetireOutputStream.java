package railo.commons.io.retirement;

import java.io.IOException;
import java.io.OutputStream;

import railo.commons.io.res.Resource;

public class RetireOutputStream extends OutputStream {
	
	private Resource res;
	private boolean append;
	private OutputStream os;
	private long lastAccess=0;
	private long retireRange;

	/**
	 * 
	 * @param res
	 * @param append
	 * @param retireRange retire the stream after given time in minutes
	 */
	public RetireOutputStream(Resource res, boolean append, int retireRangeInMinutes){
		this.res=res;
		this.append=append;
		retireRange = retireRangeInMinutes*60000;
	}

	private OutputStream getOutputStream() throws IOException {
		if(os==null) {
			//print.e("start "+res);
			os=res.getOutputStream(append);
			RetireOutputStreamFactory.list.add(this);
			RetireOutputStreamFactory.startThread();
		}
		lastAccess=System.currentTimeMillis();
		return os;
	}
	
	public boolean retire() throws IOException{
		if(os==null || (lastAccess+retireRange)>System.currentTimeMillis()) {
			//print.e("not retire "+res);
			return false;
		}
		//print.e("retire "+res);
		append=true;
		close();
		
		return true;
	}

	@Override
	public void close() throws IOException {
		if(os!=null){
			try{
				os.close();
			}
			finally{
				RetireOutputStreamFactory.list.remove(this);
				os=null;
			}
		}
	}

	@Override
	public void flush() throws IOException {
		if(os!=null)getOutputStream().flush();
	}
	
	@Override
	public void write(int b) throws IOException {
		//print.e("write:"+((char)b));
		getOutputStream().write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		//print.e("write.barr:"+b.length);
		getOutputStream().write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		//print.e("write.barr:"+b.length+":"+off+":"+len);
		getOutputStream().write(b, off, len);
	}

}
