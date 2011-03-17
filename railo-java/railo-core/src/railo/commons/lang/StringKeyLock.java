package railo.commons.lang;

import java.util.HashMap;
import java.util.Map;

import railo.print;
import railo.commons.io.SystemUtil;

public class StringKeyLock {
	private Map<String,SerializableObject> locks=new HashMap<String, SerializableObject>();
	private int timeout;
	
	public StringKeyLock(int timeout){
		this.timeout=timeout;
	}
	
	public void lock(String key) {//print.o("+"+key);
		SerializableObject obj,last=null;
		do {
			
			synchronized (this) {
				obj=locks.get(key);
				if(obj==null) {
					locks.put(key, new SerializableObject());
					if(last!=null)SystemUtil.notifyAll(last);
					return;
				}
			}
		
			last=obj;
			wait(obj);
		}
		while(true);
	}
	
	private void wait(SerializableObject obj) {
		if(timeout>0)SystemUtil.wait(obj,timeout);
		else SystemUtil.wait(obj);
	}

	public void unlock(String key) {//print.o("-"+key);
		SerializableObject obj =locks.remove(key);
		if(obj!=null) {
        	SystemUtil.notify(obj);
        }
	}
	
	
	/*static class Test extends Thread {
		private StringKeyLock lock;
		private String key;
		private String key2;

		public Test(StringKeyLock lock, String key, String key2) {
			this.lock=lock;
			this.key=key;
			this.key2=key2;
		}

		public void run(){
			lock.lock(key2);
			print.o("+:"+key2+":"+hashCode());
			lock.lock(key);
			print.o("++:"+key+":"+hashCode());
			//SystemUtil.sleep(100);
			print.o("--:"+key+":"+hashCode());
			lock.unlock(key);
			print.o("-:"+key2+":"+hashCode());
			lock.unlock(key2);
		}
	}
	
	public static void main(String[] args) {
		StringKeyLock lock=new StringKeyLock(100);
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
		
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
		
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
		
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
		
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
		
		new Test(lock,"a","a1").start();
		new Test(lock,"a","a2").start();
		new Test(lock,"a","a3").start();
		new Test(lock,"a","a4").start();
	}*/
}