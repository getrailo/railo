package railo.commons.io.retirement;

import java.util.ArrayList;
import java.util.List;

import railo.commons.io.SystemUtil;

public class RetireOutputStreamFactory {
	
	static List<RetireOutputStream> list=new ArrayList<RetireOutputStream>();
	private static RetireThread thread;
	
	static void startThread(long timeout) {
		if(timeout<1000) timeout=1000;
		if(thread==null || !thread.isAlive()) {
			thread=new RetireThread(timeout);
			thread.start();
		}
		else if(thread.sleepTime>timeout) {
			thread.sleepTime=timeout;
			SystemUtil.notify(thread);
		}
	}

	static class RetireThread extends Thread {
		
		public long sleepTime;
		
		public RetireThread(long sleepTime){
			this.sleepTime=sleepTime;
		}
		
		
		public void run(){
			//print.e("start thread");
			while(true){
				try{
					if(list.size()==0) break;
					SystemUtil.wait(this,sleepTime);
					//SystemUtil.sleep(sleepTime);
					RetireOutputStream[] arr = list.toArray(new RetireOutputStream[list.size()]); // not using iterator to avoid ConcurrentModificationException
					for(int i=0;i<arr.length;i++){
						arr[i].retire();
					}
					
				}
				catch(Throwable t){t.printStackTrace();}
			}
			//print.e("stop thread");
			thread=null;
		}
	}
}
