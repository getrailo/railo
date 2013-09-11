package railo.commons.collection;

import java.util.LinkedHashMap;

import railo.print;

public class LinkedHashMapMaxSize<K, V> extends LinkedHashMap<K, V> {
	
	private int maxSize;

	public LinkedHashMapMaxSize(int maxSize){
		this.maxSize=maxSize;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
	
	public static void main(String[] args) {
		long start;
		int maxLoop=100000, maxSize=100;
		String str=null;
		
		LinkedHashMapMaxSize<String, String> map=new LinkedHashMapMaxSize<String, String>(maxSize);
		QueueMaxSize<String> queue=new QueueMaxSize<String>(maxSize);
		SetMaxSize<String> set=new SetMaxSize<String>(maxSize);
		for(int i=0;i<maxSize;i++){
			str=""+i;
			map.put(str, str);
			queue.add(str);
			set.add(str);
		}
		
		for(int y=0;y<3;y++){
			start=System.nanoTime();
			for(int i=0;i<maxLoop;i++){
				queue.contains(str);
			}
			print.e("queue:"+(System.nanoTime()-start));
			
			start=System.nanoTime();
			for(int i=0;i<maxLoop;i++){
				map.containsKey(str);
			}
			print.e("map.key:"+(System.nanoTime()-start));
			
			start=System.nanoTime();
			for(int i=0;i<maxLoop;i++){
				map.containsValue(str);
			}
			print.e("map.value:"+(System.nanoTime()-start));
			
			start=System.nanoTime();
			for(int i=0;i<maxLoop;i++){
				set.contains(str);
			}
			print.e("set:"+(System.nanoTime()-start));
		}
	}
}
