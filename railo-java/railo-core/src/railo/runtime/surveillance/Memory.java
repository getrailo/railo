package railo.runtime.surveillance;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Iterator;

import railo.commons.io.FileRotation;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.lang.NumberUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTimeImpl;

public class Memory {
	
	

    public static final byte[] header=new byte[]{(byte)207,(byte)1};
	
	public final static short PAR_EDEN_SPACE=0;
	public final static short CMS_OLD_GEN=1;
	public final static short CMS_PERM_GEN=2;
	public final static short CODE_CACHE=3;
	public final static short PAR_SURVIVOR_SPACE=4;
	private final static int SIZE=5;
	
	private final static Collection.Key[] keys=new Collection.Key[]{
		KeyImpl.init("PAR_EDEN_SPACE"),
		KeyImpl.init("CMS_OLD_GEN"),
		KeyImpl.init("CMS_PERM_GEN"),
		KeyImpl.init("CODE_CACHE"),
		KeyImpl.init("PAR_SURVIVOR_SPACE"),
		KeyImpl.init("TIME")
	};
	
	private final static NameFilter filter=new NameFilter();
	
	private static final long MAX_FILE_SIZE = 10*1024*1024;

	public static final int INTERVALL = 10*1000;

	private Resource current;
	private final Resource dir;
	private long fileSize;
	
	public Memory(Resource dir) throws IOException {
		this.dir=dir;
		current = dir.getRealResource("memory.bin");
		FileRotation.checkFile(current, MAX_FILE_SIZE, 10, header);
		fileSize=current.length();
	}
	
	

	public void log() throws IOException {
		fileSize+=SIZE+8;
		if(fileSize>=MAX_FILE_SIZE) FileRotation.checkFile(current, MAX_FILE_SIZE, 10, header);
		IOUtil.write(current, getMemoryUsage(), true);
	}
	
	public Query getData(long minAge, long maxAge, int slotSize) {
		Resource[] logs = dir.listResources(filter);
		Arrays.sort(logs);
		Query qry=new QueryImpl(keys,0,"memory");
		
		long last=minAge;
		for(int i=logs.length-1;i>=0;i--){
			try {
				last=_getDataRaw(logs[i],qry,minAge,maxAge,slotSize,last);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return qry;
	}
	
	private long _getDataRaw(Resource res, Query qry, long minAge, long maxAge, int slotSize, long last) throws IOException {
		InputStream is=null;
		try{
			is=res.getInputStream();
			
			// first read and check the header
			byte[] buffer = new byte[header.length];
			int len = is.read(buffer);
			if(len!=header.length) return last;
    		for(int i=0;i<header.length;i++) {
    			if(header[i]!=buffer[i]){
    				return last;
    			}
    		}
    		
    		//read the data
    		int size=8+SIZE;
    		buffer = new byte[size];
    		long time;
    		while((len=is.read(buffer))==size) {
    			time=NumberUtil.byteArrayToLong(buffer);
    			if(time<minAge || time>maxAge) continue;
    			
    			// fill gaps
    			while(last!=0 && (time-last)>INTERVALL*2 && (time-last)>slotSize*2) {
    				last+=slotSize;
    				addRow(qry,last,0,0,0,0,0);
    			}
    			
    			if(last+slotSize>time) continue;
    			last=time;
    			addRow(qry,
    					time,
    					buffer[8+CMS_OLD_GEN]/100d,
    					buffer[8+CMS_PERM_GEN]/100d,
    					buffer[8+CODE_CACHE]/100d,
    					buffer[8+PAR_EDEN_SPACE]/100d,
    					buffer[8+PAR_SURVIVOR_SPACE]/100d
    					);
    			
    		}
			
		}
		finally{
			IOUtil.closeEL(is);
		}
		return last;
	}

	private static void addRow(Query qry, long time, double cmsOldGen, double cmsPermGen, double codeCache,double parEdenSpace, double parSurvivorSpace) {
		qry.addRow();
		qry.setAtEL(keys[SIZE], qry.getRecordcount(), new DateTimeImpl(time, false));
		qry.setAtEL(keys[CMS_OLD_GEN], qry.getRecordcount(), new Double(cmsOldGen));
		qry.setAtEL(keys[CMS_PERM_GEN], qry.getRecordcount(), new Double(cmsPermGen));
		qry.setAtEL(keys[CODE_CACHE], qry.getRecordcount(), new Double(codeCache));
		qry.setAtEL(keys[PAR_EDEN_SPACE], qry.getRecordcount(), new Double(parEdenSpace));
		qry.setAtEL(keys[PAR_SURVIVOR_SPACE], qry.getRecordcount(), new Double(parSurvivorSpace));
	}



	private static byte[] getMemoryUsage() {
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		byte[] timestamp = NumberUtil.longToByteArray(System.currentTimeMillis());
		byte[] rtn=new byte[timestamp.length+SIZE]; 
		System.arraycopy(timestamp, 0, rtn, 0, timestamp.length);
		MemoryPoolMXBean bean;
		MemoryUsage usage;
		int index;
		String name;
		while(it.hasNext()){
			bean = it.next();
			usage = bean.getUsage();
			name=bean.getName();
			if("Par Eden Space".equalsIgnoreCase(name)) index=PAR_EDEN_SPACE;
			else if("CMS Old Gen".equalsIgnoreCase(name)) index=CMS_OLD_GEN;
			else if("CMS Perm Gen".equalsIgnoreCase(name)) index=CMS_PERM_GEN;
			else if("Code Cache".equalsIgnoreCase(name)) index=CODE_CACHE;
			else if("Par Survivor Space".equalsIgnoreCase(name)) index=PAR_SURVIVOR_SPACE;
			else continue;
			rtn[timestamp.length+index]=(byte)(100D/usage.getMax()*usage.getUsed());
		}
		return rtn;
	}

	/*public static void main(String[] args) throws IOException {
		Resource res = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/temp/");
		ResourceUtil.deleteContent(res, null);
		Memory m=new Memory(res);
		
		for(int i=0;i<1000;i++)m.log();
		
		Query qry = m.getData(0,System.currentTimeMillis()+10000);

		print.o(qry);
	}*/
	
	static class NameFilter implements ResourceNameFilter {

		@Override
		public boolean accept(Resource parent, String name) {
			return name.startsWith("memory.bin");
		}
		
	}
}
