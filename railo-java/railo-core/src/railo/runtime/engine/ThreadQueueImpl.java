package railo.runtime.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.SystemUtil;
import railo.commons.lang.SerializableObject;
import railo.runtime.PageContext;

public class ThreadQueueImpl implements ThreadQueue {
	private final SerializableObject token=new SerializableObject();
	
	
	public final List<PageContext> list=new ArrayList<PageContext>();
	private final int max;
	private long timeout;
	
	public ThreadQueueImpl(int max, long timeout){
		this.max=max;
		this.timeout=timeout;
	}
	
	
	public void enter(PageContext pc) throws IOException {
		//print.e("enter("+Thread.currentThread().getName()+"):"+list.size());
		long start=System.currentTimeMillis();
		while(true) {
			synchronized (token) {
				if(list.size()<max) {
					//print.e("- ok("+Thread.currentThread().getName()+"):"+list.size());
					list.add(pc);
					return;
				}
			}
			if(timeout>0) SystemUtil.wait(token,timeout);
			else SystemUtil.wait(token);
			
			if(timeout>0 && (System.currentTimeMillis()-start)>=timeout)
				throw new IOException("timeout ("+(System.currentTimeMillis()-start)+") ["+timeout+"] is occured, server is busy handling requests");
		}
	}
	
	public void exit(PageContext pc){
		//print.e("exist("+Thread.currentThread().getName()+")");
		synchronized (token) {
			list.remove(pc);
			token.notify();
		}
	}
	
	public int size(){
		return list.size();
	}


	public void clear() {
		list.clear();
		token.notifyAll();
	}
	
	
	/*public static class Test extends Thread {
		private ThreadQueueImpl queue;
		public Test(ThreadQueueImpl queue){
			this.queue=queue;
		}
		public void run(){
			String name = Thread.currentThread().getName();
			try {
				queue.enter(name);
				queue.size();
				SystemUtil.sleep(50);
				queue.exit(name);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static void main(String[] args) {
		ThreadQueueImpl queue=new ThreadQueueImpl(4,1000);
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		new Test(queue).start();
		
		
	}*/
	
	
}
