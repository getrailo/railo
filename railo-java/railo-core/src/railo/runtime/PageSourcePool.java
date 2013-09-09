package railo.runtime;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.collection.LongKeyList;
import railo.commons.lang.SizeOf;
import railo.commons.lang.SystemOut;
import railo.runtime.config.ConfigImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.type.Sizeable;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;

/**
 * pool to handle pages
 */
public final class PageSourcePool implements Dumpable,Sizeable {
	
	private Map<Object,PageSource> pageSources=Collections.synchronizedMap(new ReferenceMap(ReferenceMap.SOFT, ReferenceMap.SOFT));
	//timeout timeout for files
	private long timeout;
	//max size of the pool cache
	private int maxSize;
		
	/**
	 * constructor of the class
	 */
	public PageSourcePool() {
		this.timeout=10000;
		this.maxSize=1000;
	}
	
	/**
	 * return pages matching to key
	 * @param key key for the page
	 * @param updateAccesTime define if do update access time
	 * @return page
	 */
	public PageSource getPageSource(Object key,boolean updateAccesTime) {// this method is used from Morpheus
		Object o=pageSources.get(key);
		if(o==null) return null;
		
		PageSource ps=(PageSource) o;
		if(updateAccesTime)ps.setLastAccessTime();
		return ps;
	}

	
	/**
	 * sts a page object to the page pool
	 * @param key key reference to store page object
	 * @param ps pagesource to store
	 */
	public void setPage(Object key, PageSource ps) {
		ps.setLastAccessTime();
		pageSources.put(key,ps);
	}
	
	/**
	 * returns if page object exists
	 * @param key key reference to a page object
	 * @return has page object or not
	 */
	public boolean exists(Object key) {
		return pageSources.containsKey(key);
	}
	
	/**
	 * @return returns a array of all keys in the page pool
	 */
	public Object[] keys() {
		return ArrayUtil.keys(pageSources);
	}
	
	/**
	 * removes a page from the page pool
	 * @param key key reference to page object
	 * @return page object matching to key reference
	 */
	public boolean remove(Object key) {
		return pageSources.remove(key)!=null;
	}
	
	/**
	 * @return returns the size of the pool
	 */
	public int size() {
		return pageSources.size();
	}
	
	/**
	 * @return returns if pool is empty or not
	 */
	public boolean isEmpty() {
		return pageSources.isEmpty();
	}
	
	/**
	 * clear unused pages from page pool
	 */
	public void clearUnused(ConfigImpl config) {
		
		SystemOut.printDate(config.getOutWriter(),"PagePool: "+size()+">("+maxSize+")");
		if(size()>maxSize) {
			Object[] keys=keys();
			LongKeyList list=new LongKeyList();
			for(int i=0;i<keys.length;i++) {
			    PageSource ps= getPageSource(keys[i],false);
				long updateTime=ps.getLastAccessTime();
				if(updateTime+timeout<System.currentTimeMillis()) {
					long add=((ps.getAccessCount()-1)*10000);
					if(add>timeout)add=timeout;
					list.add(updateTime+add,keys[i]);
				}
			}
			while(size()>maxSize) {
				Object key = list.shift();
				if(key==null)break;
				remove(key);
			}
		}
	}

	@Override
	public DumpData toDumpData(PageContext pageContext,int maxlevel, DumpProperties dp) {
		maxlevel--;
		Iterator<Object> it = pageSources.keySet().iterator();
		
		
		DumpTable table = new DumpTable("#FFCC00","#FFFF00","#000000");
		table.setTitle("Page Source Pool");
		table.appendRow(1,new SimpleDumpData("Count"),new SimpleDumpData(pageSources.size()));
		while(it.hasNext()) {
		    PageSource ps= pageSources.get(it.next());
		    DumpTable inner = new DumpTable("#FFCC00","#FFFF00","#000000");
			inner.setWidth("100%");
			inner.appendRow(1,new SimpleDumpData("source"),new SimpleDumpData(ps.getDisplayPath()));
			inner.appendRow(1,new SimpleDumpData("last access"),DumpUtil.toDumpData(new DateTimeImpl(pageContext,ps.getLastAccessTime(),false), pageContext,maxlevel,dp));
			inner.appendRow(1,new SimpleDumpData("access count"),new SimpleDumpData(ps.getAccessCount()));
			table.appendRow(1,new SimpleDumpData("Sources"),inner);
		}
		return table;
	}
	
	/**
	 * remove all Page from Pool using this classloader
	 * @param cl 
	 */
	public void clearPages(ClassLoader cl) {
		synchronized(pageSources){
			Iterator<Entry<Object, PageSource>> it = this.pageSources.entrySet().iterator();
			PageSourceImpl entry;
			while(it.hasNext()) {
				entry = (PageSourceImpl) it.next().getValue();
				if(cl!=null)entry.clear(cl);
				else entry.clear();
			}
		}
	}

	public void clear() {
		pageSources.clear();
	}
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public long sizeOf() {
		return SizeOf.size(this.timeout)
		+SizeOf.size(this.maxSize)
		+SizeOf.size(this.pageSources);
	}
}